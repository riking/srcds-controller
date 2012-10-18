/**
 * This file is part of the Source Dedicated Server Controller project.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or
 * combining it with srcds-controller (or a modified version of that library),
 * containing parts covered by the terms of GNU General Public License,
 * the licensors of this Program grant you additional permission to convey
 * the resulting work. {Corresponding Source for a non-source form of such a
 * combination shall include the source code for the parts of srcds-controller
 * used as well as that of the covered work.}
 *
 * For more information, please consult:
 *    <http://www.earthquake-clan.de/srcds/>
 *    <http://code.google.com/p/srcds-controller/>
 */
package de.eqc.srcds.rcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.eqc.srcds.rcon.exceptions.AuthenticationException;
import de.eqc.srcds.rcon.exceptions.ResponseEmptyException;
import de.eqc.srcds.rcon.exceptions.TimeoutException;

/**
 * This class implements a RCON connection to a SRCDS server. It is based on the
 * RconEd library version 0.4. See <http://rconed.sourceforge.net> for more
 * information.
 * 
 * @author DeadEd
 * @author oscahie (aka PiTaGoRaS)
 * @author David Hayes
 */
public class RconConnection {

    private final static int SERVERDATA_EXECCOMMAND = 2;
    private final static int SERVERDATA_AUTH = 3;
    private final static int SERVERDATA_AUTH_RESPONSE = 2;
    private final static int RESPONSE_TIMEOUT = 2000;
    private final static int MULTIPLE_PACKETS_TIMEOUT = 300;
    private final static int DEFAULT_RCON_PORT = 27015;
    private final static int RESPONSE_ID = 1337;

    private final Socket rconSocket;
    private final InputStream in;
    private final OutputStream out;

    public RconConnection(final String ip, final String password) throws AuthenticationException,
	    TimeoutException {

	this(ip, DEFAULT_RCON_PORT, password);
    }

    public RconConnection(final String ip, final int port, final String password)
	    throws AuthenticationException, TimeoutException {

	rconSocket = new Socket();
	try {
	    rconSocket.connect(new InetSocketAddress(ip, port), 1000);
	    rconSocket.setSoTimeout(RESPONSE_TIMEOUT);
	    out = rconSocket.getOutputStream();
	    in = rconSocket.getInputStream();
	} catch (IOException e) {
	    throw new AuthenticationException(e.getLocalizedMessage(), e);
	}

	if (!authenticate(password)) {
	    throw new AuthenticationException("Authentication failed");
	}
    }

    public void close() throws IOException {

	out.close();
	in.close();
	rconSocket.close();
    }

    public String send(final String command) throws SocketTimeoutException,
	    AuthenticationException, ResponseEmptyException {

	String response = null;
	final ByteBuffer[] resp = sendCommand(command);
	if (resp.length > 0) {
	    response = assemblePackets(resp);
	}
	if (response == null || response.length() == 0) {
	    throw new ResponseEmptyException("Response is empty");
	}
	return response;
    }

    private ByteBuffer[] sendCommand(final String command) throws SocketTimeoutException {

	final byte[] request = contructPacket(2, SERVERDATA_EXECCOMMAND, command);

	ByteBuffer[] resp = new ByteBuffer[128];
	int i = 0;
	try {
	    out.write(request);
	    resp[i] = receivePacket();
	    try {
		// We don't know how many packets will return in response, so
		// we'll read() the socket until TimeoutException occurs.
		rconSocket.setSoTimeout(MULTIPLE_PACKETS_TIMEOUT);
		while (true) {
		    resp[++i] = receivePacket();
		}
	    } catch (SocketTimeoutException e) {
		// No more packets in the response, go on
	    }
	} catch (Exception e) {
	    resp = new ByteBuffer[0];
	}
	return resp;
    }

    private static byte[] contructPacket(final int id, final int cmdtype, final String s1) {

	final ByteBuffer p = ByteBuffer.allocate(s1.length() + 16);
	p.order(ByteOrder.LITTLE_ENDIAN);

	// length of the packet
	p.putInt(s1.length() + 12);
	// request id
	p.putInt(id);
	// type of command
	p.putInt(cmdtype);
	// the command itself
	p.put(s1.getBytes());
	// two null bytes at the end
	p.put((byte) 0x00);
	p.put((byte) 0x00);
	// null string2 (see Source protocol)
	p.put((byte) 0x00);
	p.put((byte) 0x00);

	return p.array();
    }

    private ByteBuffer receivePacket() throws IOException {

	final ByteBuffer p = ByteBuffer.allocate(4120);
	p.order(ByteOrder.LITTLE_ENDIAN);

	final byte[] length = new byte[4];
	ByteBuffer ret = null;
	if (in.read(length, 0, 4) == 4) {
	    // Now we've the length of the packet, let's go read the bytes
	    p.put(length);
	    int i = 0;
	    while (i < p.getInt(0)) {
		p.put((byte) in.read());
		i++;
	    }
	    ret = p;
	}
	return ret;
    }

    private static String assemblePackets(final ByteBuffer[] packets) {

	// Return the text from all the response packets together
	final StringBuilder response = new StringBuilder();
	for (int i = 0; i < packets.length; i++) {
	    if (packets[i] != null) {
		response.append(new String(packets[i].array(), 12, packets[i].position() - 14));
	    }
	}
	return response.toString();
    }

    private boolean authenticate(final String password) throws AuthenticationException {

	final byte[] authRequest = contructPacket(RESPONSE_ID, SERVERDATA_AUTH, password);
	ByteBuffer response = ByteBuffer.allocate(64);
	boolean ret = false;
	try {
	    out.write(authRequest);
	    // junk response packet
	    response = receivePacket();
	    response = receivePacket();

	    if ((response.getInt(4) == RESPONSE_ID)
		&& (response.getInt(8) == SERVERDATA_AUTH_RESPONSE)) {
		ret = true;
	    }
	} catch (IOException e) {
	    throw new AuthenticationException(e.getLocalizedMessage(), e);
	}

	return ret;
    }

}
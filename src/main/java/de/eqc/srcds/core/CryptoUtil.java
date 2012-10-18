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
package de.eqc.srcds.core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import de.eqc.srcds.exceptions.CryptoException;

public final class CryptoUtil {

    private static final String CHARSET = "UTF-8";
    private final static String CRYPTO_KEY = "Aj17Ag%!&YJA!(.0";

    public enum Action {
	ENCRYPT, DECRYPT;
    }

    /** Hides the constructor of the utility class. */
    private CryptoUtil() {

	throw new UnsupportedOperationException();
    }

    public static String process(final Action action, final String text) throws CryptoException {

	String ret = null;
	if (action == Action.ENCRYPT) {
	    ret = encrypt(text);
	} else if (action == Action.DECRYPT) {
	    ret = decrypt(text);
	}
	return ret;
    }

    public static String encrypt(final String plain) throws CryptoException {

	try {
	    final SecretKey secret = new SecretKeySpec(CRYPTO_KEY.getBytes(CHARSET),
		    "Blowfish");
	    final Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, secret);
	    final byte[] bytes = plain.getBytes(CHARSET);
	    return new BASE64Encoder().encode(cipher.doFinal(bytes));
	} catch (Exception e) {
	    throw new CryptoException(String.format("Encryption failed: %s", e
		    .getLocalizedMessage()), e);
	}
    }

    public static String decrypt(final String encoded) throws CryptoException {

	try {
	    final SecretKey secret = new SecretKeySpec(CRYPTO_KEY.getBytes(CHARSET),
		    "Blowfish");
	    final Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, secret);
	    final byte[] bytes = new BASE64Decoder().decodeBuffer(encoded);
	    return new String(cipher.doFinal(bytes), CHARSET);
	} catch (Exception e) {
	    throw new CryptoException(String.format("Decryption failed: %s", e
		    .getLocalizedMessage()), e);
	}
    }
}
<?xml version="1.0" encoding="utf-8"?>
<!--

    This file is part of the Source Dedicated Server Controller project.

    This program is free software; you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program; if not, see <http://www.gnu.org/licenses>.
    
    Additional permission under GNU GPL version 3 section 7
    
    If you modify this Program, or any covered work, by linking or
    combining it with srcds-controller (or a modified version of that library),
    containing parts covered by the terms of GNU General Public License,
    the licensors of this Program grant you additional permission to convey
    the resulting work. {Corresponding Source for a non-source form of such a
    combination shall include the source code for the parts of srcds-controller
    used as well as that of the covered work.}

    For more information, please consult:
       <http://www.earthquake-clan.de/srcds/>
       <http://code.google.com/p/srcds-controller/>

-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="html">
	<xsl:output method="html"
		doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
		doctype-public="-//W3C//DTD XHTML 1.1//EN" />
	<xsl:template match="/ControllerResponse">
		<html>
			<head>
				<title>Source Dedicated Server Controller @ ${hostname}</title>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<link rel="icon" href="${img:favicon.ico}" type="image/x-icon"></link>
				<link rel="stylesheet" media="screen, print, projection, tv" href="${css:screen.css}"></link>
				<link rel="stylesheet" media="aural, braille, embossed, handheld, tty" href="${css:textmode.css}"></link>
			</head>
			<body>
				<div id="header">Source Dedicated Server Controller @ ${hostname} -
					Response</div>
				<br />
				<img src="${img:header_index.png}" />
				<br />
				<h2 class="response">Response from Controller</h2>
				<table border="0" class="response">
					<tr class="tableHeaders">
						<xsl:choose>
							<xsl:when test="ResponseCode = 'INFORMATION'">
								<th class="messageInfo">Information</th>
							</xsl:when>
							<xsl:when test="ResponseCode = 'RCON_RESPONSE'">
								<th class="messageInfo">RCON Response</th>
							</xsl:when>
							<xsl:otherwise>
								<th class="messageError">Error</th>
							</xsl:otherwise>
						</xsl:choose>
					</tr>
					<xsl:for-each select="Message/Item">
						<tr>
							<td class="tableData">
								<xsl:value-of select="." />
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<xsl:choose>
					<xsl:when test="ResponseCode = 'RCON_RESPONSE'">
						<br />
						<div name="rconsubmit" id="rconsubmit">
							<form action="rcon" method="get">
								<input name="command" type="text" size="40" />
								<input value="Send" type="submit" />
							</form>
						</div>
					</xsl:when>
				</xsl:choose>
				<br />
				<a href="/" class="response">[Back to main page]</a>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>

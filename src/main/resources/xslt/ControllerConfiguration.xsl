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

	<xsl:template match="/ControllerConfiguration">
		<html>
			<head>
				<title>Source Dedicated Server Controller @ ${hostname}</title>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/> 
				<link rel="icon" href="${img:favicon.ico}" type="image/x-icon"></link>
				<link rel="stylesheet" media="screen, print, projection, tv" href="${css:screen.css}"></link>
				<link rel="stylesheet" media="aural, braille, embossed, handheld, tty" href="${css:textmode.css}"></link>
			</head>		
			<body>
                <div id="header">Source Dedicated Server Controller @ ${hostname} - Edit Controller Configuration</div>
                <br/>
                <img src="${img:header_index.png}" />
                <br/>
				<h2 class="response">Edit Controller Configuration</h2>
				<form method="get">
					<table class="response" border="0">
						<tr class="tableHeaders">
							<th>Entry</th>
							<th>Value</th>
						</tr>
						<xsl:for-each select="Entry">
							<tr>
								<td class="tableData">
									<xsl:value-of select="@description" />
								</td>
								<td>
									<xsl:choose>
										<xsl:when test="Key = '${srcds-executable-key}'">
											<xsl:value-of select="Value" />
										</xsl:when>
										<xsl:when test="@type = 'Boolean'">
											<select name="{Key}">
												<xsl:choose>
													<xsl:when test="Value = 'true'">
														<option value="true" selected="selected">Enabled*</option>
														<option value="false">Disabled</option>
													</xsl:when>
													<xsl:otherwise>
														<option value="true">Enabled</option>
														<option value="false" selected="selected">Disabled*</option>
													</xsl:otherwise>
												</xsl:choose>
											</select>
										</xsl:when>
										<xsl:when test="@enumeration = 'true'">
											<xsl:variable name="Value" select="Value" />
											<xsl:variable name="EnumType" select="@type" />
											<select name="{Key}">
												<xsl:for-each select="/ControllerConfiguration/Metadata/Enumeration[@name=$EnumType]/Value">
													<xsl:choose>
														<xsl:when test="$Value = .">
															<option value="{.}" selected="selected"><xsl:value-of select="." />*</option>
														</xsl:when>
														<xsl:otherwise>
															<option value="{.}"><xsl:value-of select="." /></option>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:for-each>
											</select>
										</xsl:when>	
										<xsl:when test="@type = 'Password'">
											<xsl:variable name="minFieldLength">20</xsl:variable>
											<xsl:variable name="maxFieldLength">50</xsl:variable>
											<xsl:choose>
												<xsl:when test="string-length(Value) &lt; $minFieldLength">
													<input type="password" name="{Key}" value="{Value}" size="{$minFieldLength}" />
												</xsl:when>
												<xsl:when test="string-length(Value) &gt; $maxFieldLength - 1">
													<input type="password" name="{Key}" value="{Value}" size="{$maxFieldLength}" />
												</xsl:when>
												<xsl:otherwise>
													<input type="password" name="{Key}" value="{Value}" size="{string-length(Value) + 1}" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:when>
										<xsl:otherwise>
											<xsl:variable name="minFieldLength">30</xsl:variable>
											<xsl:variable name="maxFieldLength">80</xsl:variable>
											<xsl:choose>
												<xsl:when test="string-length(Value) &lt; $minFieldLength">
													<input type="text" name="{Key}" value="{Value}" size="{$minFieldLength}" />
												</xsl:when>
												<xsl:when test="string-length(Value) &gt; $maxFieldLength - 1">
													<input type="text" name="{Key}" value="{Value}" size="{$maxFieldLength}" />
												</xsl:when>
												<xsl:otherwise>
													<input type="text" name="{Key}" value="{Value}" size="{string-length(Value) + 1}" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					<br />
					<p class="response">
                    	<input type="submit" value="Save" />
						<input type="button" value="Revert" onclick="javascript:history.go(0)" />
						<br />
						<br />
						<a href="/">[Back to main page]</a>
					</p>					
                </form>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
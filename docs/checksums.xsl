<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:r="http://www.fcla.edu/dls/md/daitss/">

<xsl:output method="html" indent="yes" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
<xsl:strip-space elements='*'/>

<xsl:template match="/">

	<html>
	<body>


	<h3>Report Of Files Archived From Original Package <xsl:value-of select="//r:INGEST/@PACKAGE"/></h3>

	<table border="0" cellpadding="10" bgcolor="#000000">
	<tr bgcolor="#ffffff">
		<th>Original Filename</th>
		<th>FDA Identifier</th>
		<th>Checksums</th>
		<th>Preservation Level</th>
		<th>Events</th>
		<th>Warnings</th>
	</tr>

	<xsl:for-each select="//r:FILE">
		<xsl:choose>
		<xsl:when test="@ORIGIN = 'DEPOSITOR'">
			<tr>
<xsl:attribute name="bgcolor">
<xsl:choose>
<xsl:when test="position() mod 2 = 
0"><xsl:text>#c0c0c0</xsl:text></xsl:when>
<xsl:otherwise><xsl:text>white</xsl:text></xsl:otherwise>
</xsl:choose>
</xsl:attribute>


			<td valign="top"><font size="-2"><xsl:value-of select="@PATH"/></font></td>

			<td valign="top"><font size="-2"><xsl:value-of select="@DFID"/></font></td>
			
	

			<td valign="top">
				<table>
				<tr>
				<td><font size="-2"><b>SHA-1</b></font></td>
				<td><font size="-2"><xsl:value-of select="r:MESSAGE_DIGEST[@ALGORITHM = 'SHA-1']"/></font></td>
				</tr>
				
				<tr>
				<td><font size="-2"><b>MD5</b></font></td>
				<td><font size="-2"><xsl:value-of select="r:MESSAGE_DIGEST[@ALGORITHM = 'MD5']"/></font></td>
				</tr>
				</table>
			</td>

			<td valign="top"><font size="-2"><xsl:value-of select="@PRESERVATION"/></font></td>

			<td valign="top"><font size="-2">
				<xsl:for-each select="r:EVENT">
				<ul>
					<li><xsl:value-of select="./r:PROCEDURE"/><xsl:text>. </xsl:text><xsl:value-of select="./r:NOTE"/></li>
				</ul>
				</xsl:for-each>				
			</font>
			</td>

			<td valign="top"><font size="-2">
			<ul>
				<xsl:for-each select="r:WARNING">
					<li><xsl:value-of select="@CODE"/><xsl:text>. </xsl:text><xsl:value-of select="."/></li>
				</xsl:for-each>

				<xsl:for-each select="r:BROKEN_LINK">
					<li><xsl:text>Broken Link: </xsl:text><xsl:value-of select="."/></li>
				</xsl:for-each>
				
			</ul>				
			</font>
			</td>

			</tr>
		</xsl:when>
		<xsl:otherwise/>
		</xsl:choose>
	</xsl:for-each>
	</table>

	<h3>List Of All Files Ingested Into FDA As Package <xsl:value-of select="//r:INGEST/@IEID"/></h3>

	<table border="0" cellpadding="10" bgcolor="#000000">
	<tr bgcolor="#ffffff">
		<th>Filename</th>
		<th>FDA Identifier</th>
		<th>Origins</th>
		<th>Preservation Level</th>
	</tr>

	<xsl:for-each select="//r:FILE">
		<tr bgcolor="#ffffff">
		<td><font size="-2"><xsl:value-of select="@PATH"/></font></td>
		<td><font size="-2"><xsl:value-of select="@DFID"/></font></td>
		<td><font size="-2"><xsl:value-of select="@ORIGIN"/></font></td>
		<td><font size="-2"><xsl:value-of select="@PRESERVATION"/></font></td>
		</tr>		
	</xsl:for-each>

	</table>


	</body>
	</html>
</xsl:template>

</xsl:stylesheet>




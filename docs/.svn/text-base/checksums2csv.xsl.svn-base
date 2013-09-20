<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:r="http://www.fcla.edu/dls/md/daitss/">

<xsl:output method="text" indent="no" encoding="UTF-8"/>
<xsl:strip-space elements='*'/>

<xsl:template match="/">
<xsl:variable name="newline">
<xsl:text>
</xsl:text>
</xsl:variable>


<xsl:text>"ORIGINAL FILENAME","FDA IDENTIFIER","SHA-1 CHECKSUM",MD5 CHECKSUM"</xsl:text><xsl:value-of select="$newline"/>


<xsl:for-each select="//r:FILE">
<xsl:choose>
<xsl:when test="@ORIGIN = 'DEPOSITOR'">
			<xsl:text>"</xsl:text><xsl:value-of select="@PATH"/><xsl:text>",</xsl:text>
			<xsl:text>"</xsl:text><xsl:value-of select="@DFID"/><xsl:text>",</xsl:text>
			<xsl:text>"</xsl:text><xsl:value-of select="r:MESSAGE_DIGEST[@ALGORITHM = 'SHA-1']"/><xsl:text>",</xsl:text>
			<xsl:text>"</xsl:text><xsl:value-of select="r:MESSAGE_DIGEST[@ALGORITHM = 'MD5']"/><xsl:text>"</xsl:text><xsl:value-of select="$newline"/>
</xsl:when>
<xsl:otherwise/>
</xsl:choose>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>




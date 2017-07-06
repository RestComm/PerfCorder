<?xml version="1.0" encoding="windows-1252"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml"/>
    <xsl:template match="/testsuite">
        <testsuite>
            <xsl:attribute name="name">
                org.restcomm.perfcorder
            </xsl:attribute>            
            <xsl:attribute name="errors">
                <xsl:value-of select="count(//error)" />
            </xsl:attribute>
            <xsl:attribute name="failures">
                <xsl:value-of select="count(//failure)" />
            </xsl:attribute>
            <xsl:attribute name="skips">
                <xsl:value-of select="count(//skipped)" />
            </xsl:attribute>
            <xsl:attribute name="tests">
                <xsl:value-of select="count(//testcase)" />
            </xsl:attribute>
            <xsl:attribute name="time">
                <xsl:value-of select="sum(//@time)" />
            </xsl:attribute>            
            <xsl:copy-of select="/"/>
        </testsuite>
    </xsl:template>
</xsl:stylesheet>

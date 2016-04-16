<?xml version="1.0" encoding="windows-1252"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="false" omit-xml-declaration="yes"/>
    <xsl:include href="junitTestCaseTemplate.xsl"></xsl:include>
          
    <xsl:template match="/" priority="9">
        <testsuite>
        <xsl:for-each select="//key[text()='Cpu']/parent::entry/value/median">
            <xsl:call-template name="lessThanTemplate">
                <xsl:with-param name="caseName" select="'CPUMedian'" />
                <xsl:with-param name="thresholdValue"  select="'1'" />   
            </xsl:call-template>
        </xsl:for-each>
        <xsl:for-each select="//key[text()='Mem']/parent::entry/value/min">
            <xsl:call-template name="lessThanTemplate">
                <xsl:with-param name="caseName" select="'MemMin'" />
                <xsl:with-param name="thresholdValue"  select="'400'" />   
            </xsl:call-template>
        </xsl:for-each> 
        </testsuite>       
    </xsl:template>
</xsl:stylesheet>

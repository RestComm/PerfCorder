<?xml version="1.0" encoding="windows-1252"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml"/>
    <xsl:template name="lessThanTemplate" priority="0">
        <xsl:param name = "caseName" />
        <xsl:param name = "thresholdValue" />
        <testcase classname="org.restcomm.perfcorder.PerfCorderAnalyzerTest" name="{$caseName}">
            <xsl:if test="text()>$thresholdValue">
                <failure message="{$caseName} lessThan violated.">
                    <xsl:value-of select="."/>
                </failure>          
            </xsl:if>
        </testcase>
    </xsl:template>
    
    <xsl:template name="biggerThanTemplate" priority="0">
        <xsl:param name = "caseName" />
        <xsl:param name = "thresholdValue" />
        <testcase classname="org.restcomm.perfcorder.PerfCorderAnalyzerTest" name="{$caseName}">
            <xsl:if test="text() &lt; $thresholdValue">
                <failure message="{$caseName} biggerThan violated.">
                    <xsl:value-of select="."/>
                </failure>          
            </xsl:if>
        </testcase>
    </xsl:template> 
</xsl:stylesheet>

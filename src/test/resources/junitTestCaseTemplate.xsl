<?xml version="1.0" encoding="windows-1252"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml"/>
    <xsl:template name="lessThanTemplate" priority="0">
        <xsl:param name = "caseName" />
        <xsl:param name = "thresholdValue" />
        <testcase classname="org.restcomm.perfcorder.PerfCorderAnalyzerTest" name="{$caseName}">
            <xsl:attribute name="time">
                <xsl:value-of select="." />
            </xsl:attribute>            
            <xsl:if test="text()>$thresholdValue">
                <failure message="{$caseName} lessThan {$thresholdValue} violated.">
                    <xsl:value-of select="."/>
                </failure>          
            </xsl:if>
            <system-out>&lt;measurement&gt;&lt;name&gt;<xsl:value-of select="$caseName"/>&lt;/name&gt;&lt;value&gt;<xsl:value-of select="."/>&lt;/value&gt;&lt;/measurement&gt;</system-out>
        </testcase>
    </xsl:template>
    
    <xsl:template name="biggerThanTemplate" priority="0">
        <xsl:param name = "caseName" />
        <xsl:param name = "thresholdValue" />
        <testcase classname="org.restcomm.perfcorder.PerfCorderAnalyzerTest" name="{$caseName}">
            <xsl:attribute name="time">
                <xsl:value-of select="." />
            </xsl:attribute>                
            <xsl:if test="text() &lt; $thresholdValue">
                <failure message="{$caseName} biggerThan {$thresholdValue} violated.">
                    <xsl:value-of select="."/>
                </failure>          
            </xsl:if>
            <system-out>&lt;measurement&gt;&lt;name&gt;<xsl:value-of select="$caseName"/>&lt;/name&gt;&lt;value&gt;<xsl:value-of select="."/>&lt;/value&gt;&lt;/measurement&gt;</system-out>
        </testcase>
    </xsl:template> 
    
    <xsl:template name="ratioLessThanTemplate" priority="0">
        <xsl:param name = "caseName" />
        <xsl:param name = "thresholdValue" />
        <xsl:param name = "measA" />
        <xsl:param name = "measB" />        
        <testcase classname="org.restcomm.perfcorder.PerfCorderAnalyzerTest" name="{$caseName}">
            <xsl:attribute name="time">
                <xsl:value-of select="$measA div $measB" />
            </xsl:attribute>            
            <xsl:if test="$measA div $measB > $thresholdValue">
                <failure message="{$caseName} ratioLessThan {$thresholdValue} violated.">
                    <xsl:value-of select="$measA div $measB"/>
                </failure>          
            </xsl:if>
            <system-out>&lt;measurement&gt;&lt;name&gt;<xsl:value-of select="$caseName"/>&lt;/name&gt;&lt;value&gt;<xsl:value-of select="$measA div $measB"/>&lt;/value&gt;&lt;/measurement&gt;</system-out>
        </testcase>
    </xsl:template>
    
    <xsl:template name="ratioBiggerThanTemplate" priority="0">
        <xsl:param name = "caseName" />
        <xsl:param name = "thresholdValue" />
        <xsl:param name = "measA" />
        <xsl:param name = "measB" />        
        <testcase classname="org.restcomm.perfcorder.PerfCorderAnalyzerTest" name="{$caseName}">
            <xsl:attribute name="time">
                <xsl:value-of select="$measA div $measB" />
            </xsl:attribute>            
            <xsl:if test="$measA div $measB  &lt; $thresholdValue">
                <failure message="{$caseName} ratioBiggerThan {$thresholdValue} violated.">
                    <xsl:value-of select="$measA div $measB"/>
                </failure>          
            </xsl:if>
            <system-out>&lt;measurement&gt;&lt;name&gt;<xsl:value-of select="$caseName"/>&lt;/name&gt;&lt;value&gt;<xsl:value-of select="$measA div $measB"/>&lt;/value&gt;&lt;/measurement&gt;</system-out>
        </testcase>
    </xsl:template>                  
</xsl:stylesheet>

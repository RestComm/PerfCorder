<?xml version="1.0" encoding="windows-1252"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="HTML"/>
    <xsl:template name="measRowTemplate" priority="0">
        <tr>
            <td>
                <xsl:value-of select="./key/text()"/>
            </td>
            <td>
                <xsl:for-each select="./value/node()[name()!='graph']">
                    <xsl:if test="string(text())">
                        <xsl:value-of select="name()"/>=<xsl:value-of select="text()"/>
                        <br/>
                    </xsl:if>
                </xsl:for-each>                
            </td>
            <td>
                <img>             
                    <xsl:attribute name="src">data:image/png;base64,<xsl:value-of select="./value/graph/text()" /></xsl:attribute>
                </img>
            </td>
        </tr>
    </xsl:template>    
    

    <xsl:template match="/" priority="9">
        <html>
            <head>
                <title>Analisys Summary</title>
            </head>
            <body>
                Test Duration(seconds)= <xsl:value-of select="(//endTimeStamp - //startTimeStamp) div 1000"/>            
                <table border="1">
                    <thead>
                        <tr>
                            <th>Meas</th>
                            <th>Stats</th>
                            <th>Graph</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="//key[text()='Cpu']/parent::entry">
                            <xsl:call-template name="measRowTemplate">
                            </xsl:call-template>
                        </xsl:for-each>
                        <xsl:for-each select="//key[text()='Mem']/parent::entry">
                            <xsl:call-template name="measRowTemplate">
                            </xsl:call-template>
                        </xsl:for-each>
                        <xsl:for-each select="//key[text()='GcMemAfter']/parent::entry">
                            <xsl:call-template name="measRowTemplate">
                            </xsl:call-template>
                        </xsl:for-each>
                        <xsl:for-each select="//key[text()='GcPauseDuration']/parent::entry">
                            <xsl:call-template name="measRowTemplate">
                            </xsl:call-template>
                        </xsl:for-each>


                        <xsl:for-each select="//key[text()='SIPTotalCallCreated']/parent::entry">
                            <xsl:call-template name="measRowTemplate">
                            </xsl:call-template>
                        </xsl:for-each>
                        <xsl:for-each select="//key[text()='SIPFailedCalls']/parent::entry">
                            <xsl:call-template name="measRowTemplate">
                            </xsl:call-template>
                        </xsl:for-each>
                        <xsl:for-each select="//key[text()='SIPRetransmissions']/parent::entry">
                            <xsl:call-template name="measRowTemplate">
                            </xsl:call-template>
                        </xsl:for-each>
                    </tbody>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>

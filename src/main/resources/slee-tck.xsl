<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:output omit-xml-declaration="yes" indent="yes"/>

 <xsl:variable name="vText" select=
 "replace(unparsed-text('file:///C:/dev/mobicents/Perfcorder/src/main/resources/slee-summary.txt'),'\r','')"/>

 <xsl:template match="/">
  <document>
      <xsl:analyze-string select="$vText" regex="(.+?)[ \t]+([A-Z].*$)">
       <xsl:matching-substring>
           <testcase classname="{regex-group(1)}" name="{regex-group(1)}">
               
           </testcase>
       </xsl:matching-substring>
       <xsl:non-matching-substring><xsl:sequence select="."/></xsl:non-matching-substring>
      </xsl:analyze-string>
  </document>
 </xsl:template>
</xsl:stylesheet>

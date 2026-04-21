<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="skill"/>
    <xsl:output method="html" indent="yes"/>
    <xsl:template match="/recipes">
        <html>
            <head>
                <style>
                    .match { background-color: yellow; }
                    .other { background-color: lightgreen; }
                    table { border-collapse: collapse; width: 100%; }
                    td, th { border: 1px solid black; padding: 8px; }
                </style>
            </head>
            <body>
                <h2>Recipe List (XSL View)</h2>
                <h3>User skill: <xsl:value-of select="$skill"/></h3>
                <table>
                    <tr>
                        <th>Title</th>
                        <th>Cuisine</th>
                        <th>Difficulty</th>
                    </tr>
                    <xsl:for-each select="recipe">
                        <tr>
                            <xsl:attribute name="class">
                                <xsl:choose>
                                    <xsl:when test="difficulty=$skill">match</xsl:when>
                                    <xsl:otherwise>other</xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <td><xsl:value-of select="title"/></td>
                            <td>
                                <xsl:for-each select="cuisine">
                                    <xsl:value-of select="."/>
                                    <xsl:if test="position() != last()">, </xsl:if>
                                </xsl:for-each>
                            </td>
                            <td><xsl:value-of select="difficulty"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
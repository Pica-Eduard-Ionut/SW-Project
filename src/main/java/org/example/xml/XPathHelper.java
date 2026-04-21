package org.example.xml;

import org.example.model.Recipe;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import java.util.*;

public class XPathHelper {
    private Document document;
    private XPath xPath;

    public XPathHelper(Document document) {
        this.document = document;
        this.xPath = XPathFactory.newInstance().newXPath();
    }

    public List<Recipe> filter(String expression) throws Exception {
        List<Recipe> recipeList = new ArrayList<>();
        NodeList nodeList = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element recipeElement = (Element) nodeList.item(i);
            String recipeId = recipeElement.getAttribute("id");
            String title = recipeElement.getElementsByTagName("title").item(0).getTextContent();
            List<String> cuisineList = new ArrayList<>();
            NodeList cuisineNodes = recipeElement.getElementsByTagName("cuisine");

            for (int j = 0; j < cuisineNodes.getLength(); j++) {
                cuisineList.add(cuisineNodes.item(j).getTextContent());
            }

            String difficulty = recipeElement.getElementsByTagName("difficulty").item(0).getTextContent();
            recipeList.add(new Recipe(recipeId, title, cuisineList, difficulty));
        }

        return recipeList;
    }
}
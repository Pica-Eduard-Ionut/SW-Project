package org.example.xml;

import org.example.model.Recipe;
import org.example.model.User;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLManager {
    private Document recipesDocument;
    private Document usersDocument;

    public XMLManager() throws Exception {
        File recipesFile = new File("data/recipes.xml");
        if (!recipesFile.exists() || recipesFile.length() == 0) {
            List<Recipe> scrapedRecipes = RecipeScraper.scrape();
            recipesDocument = createEmptyDocument("recipes");
            for (Recipe recipe : scrapedRecipes) {
                appendRecipe(recipe);
            }
            save(recipesDocument, "data/recipes.xml");
        } else {
            recipesDocument = loadDocument("data/recipes.xml");
        }
        usersDocument = loadDocument("data/users.xml");
    }

    private Document loadDocument(String path) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return documentBuilder.parse(new File(path));
    }

    private Document createEmptyDocument(String rootName) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement(rootName);
        document.appendChild(rootElement);
        return document;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipeList = new ArrayList<>();
        NodeList recipeNodes = recipesDocument.getElementsByTagName("recipe");
        for (int i = 0; i < recipeNodes.getLength(); i++) {
            Element recipeElement = (Element) recipeNodes.item(i);
            String recipeId = recipeElement.getAttribute("id");
            String title = recipeElement.getElementsByTagName("title").item(0).getTextContent();
            NodeList cuisineNodes = recipeElement.getElementsByTagName("cuisine");
            List<String> cuisineList = new ArrayList<>();

            for (int j = 0; j < cuisineNodes.getLength(); j++) {
                cuisineList.add(cuisineNodes.item(j).getTextContent());
            }

            String difficulty = recipeElement.getElementsByTagName("difficulty").item(0).getTextContent();
            recipeList.add(new Recipe(recipeId, title, cuisineList, difficulty));
        }
        return recipeList;
    }

    public void addRecipe(Recipe recipe) throws Exception {
        Element rootElement = recipesDocument.getDocumentElement();
        Element recipeElement = recipesDocument.createElement("recipe");
        int recipeId = recipesDocument.getElementsByTagName("recipe").getLength() + 1;
        recipeElement.setAttribute("id", String.valueOf(recipeId));
        Element titleElement = recipesDocument.createElement("title");
        titleElement.setTextContent(recipe.getTitle());
        Element difficultyElement = recipesDocument.createElement("difficulty");
        difficultyElement.setTextContent(recipe.getDifficulty());
        recipeElement.appendChild(titleElement);
        List<String> cuisineList = recipe.getCuisines();
        if (cuisineList.size() < 2) {
            throw new IllegalArgumentException("A recipe must have at least 2 cuisines.");
        }

        String firstCuisine = cuisineList.get(0);
        String secondCuisine = cuisineList.get(1);
        if (firstCuisine.equals(secondCuisine)) {
            throw new IllegalArgumentException("Cuisines must be different.");
        }

        for (int i = 0; i < 2; i++) {
            Element cuisineElement = recipesDocument.createElement("cuisine");
            cuisineElement.setTextContent(cuisineList.get(i));
            recipeElement.appendChild(cuisineElement);
        }
        recipeElement.appendChild(difficultyElement);
        rootElement.appendChild(recipeElement);
        save(recipesDocument, "data/recipes.xml");
    }

    private void appendRecipe(Recipe recipe) {
        Element rootElement = recipesDocument.getDocumentElement();
        Element recipeElement = recipesDocument.createElement("recipe");
        recipeElement.setAttribute("id", recipe.getId());
        Element titleElement = recipesDocument.createElement("title");
        titleElement.setTextContent(recipe.getTitle());
        Element difficultyElement = recipesDocument.createElement("difficulty");
        difficultyElement.setTextContent(recipe.getDifficulty());
        recipeElement.appendChild(titleElement);
        List<String> cuisineList = recipe.getCuisines();

        if (cuisineList.size() >= 2 && !cuisineList.get(0).equals(cuisineList.get(1))) {
            for (int i = 0; i < 2; i++) {
                Element cuisineElement = recipesDocument.createElement("cuisine");
                cuisineElement.setTextContent(cuisineList.get(i));
                recipeElement.appendChild(cuisineElement);
            }
        }
        recipeElement.appendChild(difficultyElement);
        rootElement.appendChild(recipeElement);
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        NodeList userNodes = usersDocument.getElementsByTagName("user");

        for (int i = 0; i < userNodes.getLength(); i++) {
            Element userElement = (Element) userNodes.item(i);
            userList.add(new User(
                    userElement.getElementsByTagName("name").item(0).getTextContent(),
                    userElement.getElementsByTagName("surname").item(0).getTextContent(),
                    userElement.getElementsByTagName("skill").item(0).getTextContent(),
                    userElement.getElementsByTagName("preferredCuisine").item(0).getTextContent()
            ));
        }
        return userList;
    }

    public void addUser(User user) throws Exception {
        Element rootElement = usersDocument.getDocumentElement();
        Element userElement = usersDocument.createElement("user");
        int userId = usersDocument.getElementsByTagName("user").getLength() + 1;
        userElement.setAttribute("id", String.valueOf(userId));
        Element nameElement = usersDocument.createElement("name");
        nameElement.setTextContent(user.getName());
        Element surnameElement = usersDocument.createElement("surname");
        surnameElement.setTextContent(user.getSurname());
        Element skillElement = usersDocument.createElement("skill");
        skillElement.setTextContent(user.getSkill());
        Element cuisineElement = usersDocument.createElement("preferredCuisine");
        cuisineElement.setTextContent(user.getPreferredCuisine());
        userElement.appendChild(nameElement);
        userElement.appendChild(surnameElement);
        userElement.appendChild(skillElement);
        userElement.appendChild(cuisineElement);
        rootElement.appendChild(userElement);
        save(usersDocument, "data/users.xml");
    }

    public void reloadUsers() throws Exception {
        usersDocument = loadDocument("data/users.xml");
    }

    private void save(Document document, String path) throws Exception {
        removeWhitespaceNodes(document);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(document), new StreamResult(new File(path)));
    }

    public Document getRecipesDocument() {
        return recipesDocument;
    }

    public void exportXsl(String skill) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Source xsltSource = new StreamSource("data/recipes.xsl");
        Transformer transformer = transformerFactory.newTransformer(xsltSource);
        transformer.setParameter("skill", skill);
        transformer.transform(new DOMSource(recipesDocument),new StreamResult("data/output.html"));
    }

    private void removeWhitespaceNodes(Node node) {
        NodeList children = node.getChildNodes();

        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes(child);
            }
        }
    }
}
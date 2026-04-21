package org.example.xml;

import org.example.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class RecipeScraper {
    private static final String url = "https://www.bbcgoodfood.com/recipes/collection/budget-autumn";
    private static final String[] cuisines = {"Italian", "Asian", "European", "Indian", "American", "French"};
    private static final String[] difficulties = {"Beginner", "Intermediate", "Advanced"};

    public static List<Recipe> scrape() throws Exception {
        List<Recipe> recipeList = new ArrayList<>();
        Random random = new Random();
        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get();
        Elements cardElements = document.select("article.card[data-item-type=recipe]");
        int recipeId = 1;

        for (Element cardElement : cardElements) {
            if (cardElement.selectFirst(".premium-identifier") != null) continue;

            Element titleElement = cardElement.selectFirst("h2.heading-4");
            if (titleElement == null) continue;

            String title = titleElement.text();
            if (title.toLowerCase().contains("app only")) continue;

            int firstIndex = random.nextInt(cuisines.length);
            int secondIndex;

            do {
                secondIndex = random.nextInt(cuisines.length);
            } while (secondIndex == firstIndex);

            List<String> cuisineList = List.of(cuisines[firstIndex], cuisines[secondIndex]);
            String difficulty = difficulties[random.nextInt(difficulties.length)];
            recipeList.add(new Recipe(String.valueOf(recipeId++), title, cuisineList, difficulty));
            if (recipeList.size() == 20) break;
        }

        return recipeList;
    }
}
package org.example.ui;

import org.example.model.Recipe;
import org.example.model.User;
import org.example.xml.RecipeScraper;
import org.example.xml.XMLManager;
import org.example.xml.XPathHelper;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainUI {
    private XMLManager manager;
    private DefaultListModel<Recipe> listModel;
    private JList<Recipe> list;
    private JComboBox<String> cuisineFilter;
    private JComboBox<User> userBox;

    public MainUI(XMLManager manager) {
        this.manager = manager;
        JFrame frame = new JFrame("Recipe Recommender System");
        frame.setSize(950, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        refreshList(manager.getAllRecipes());
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Recipe r = (Recipe) value;
                label.setOpaque(true);
                if (!isSelected) {
                    switch (r.getDifficulty()) {
                        case "Beginner" -> label.setBackground(Color.GREEN);
                        case "Intermediate" -> label.setBackground(Color.YELLOW);
                        case "Advanced" -> label.setBackground(Color.RED);
                    }
                }

                return label;
            }
        });

        JPanel topPanel = new JPanel();
        userBox = new JComboBox<>();
        for (User u : manager.getAllUsers()) {
            userBox.addItem(u);
        }

        JButton recommendBtn = new JButton("Recommend");
        JButton resetBtn = new JButton("All Recipes");
        JButton addRecipeBtn = new JButton("Add Recipe");
        JButton addUserBtn = new JButton("Add User");
        JButton scrapeBtn = new JButton("Scrape Recipes");
        JButton xslBtn = new JButton("Export XSL");

        cuisineFilter = new JComboBox<>(new String[]{"All", "Italian", "Asian", "European", "American", "Indian", "French"});
        JButton filterBtn = new JButton("Filter");

        // connectors
        recommendBtn.addActionListener(e -> {
            try {
                User user = (User) userBox.getSelectedItem();
                if (user == null) return;
                XPathHelper helper = new XPathHelper(manager.getRecipesDocument());
                String expr = "//recipe[difficulty='" + user.getSkill() + "' and cuisine[contains(., '" + user.getPreferredCuisine() + "')]]";
                refreshList(helper.filter(expr));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        resetBtn.addActionListener(e -> refreshList(manager.getAllRecipes()));

        filterBtn.addActionListener(e -> {
            try {
                String selected = (String) cuisineFilter.getSelectedItem();
                if ("All".equals(selected)) {
                    refreshList(manager.getAllRecipes());
                } else {
                    XPathHelper helper = new XPathHelper(manager.getRecipesDocument());
                    refreshList(helper.filter("//recipe[cuisine='" + selected + "']"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        scrapeBtn.addActionListener(e -> {
            try {
                List<Recipe> scraped = RecipeScraper.scrape();
                for (Recipe r : scraped) {
                    manager.addRecipe(r);
                }
                refreshList(manager.getAllRecipes());
                JOptionPane.showMessageDialog(frame, "Scraped " + scraped.size() + " recipes!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Scraping failed: " + ex.getMessage());
            }
        });
        addRecipeBtn.addActionListener(e -> openAddRecipeForm());
        addUserBtn.addActionListener(e -> openAddUserForm());

        xslBtn.addActionListener(e -> {
            try {
                User user = (User) userBox.getSelectedItem();
                if (user == null) return;
                manager.exportXsl(user.getSkill()); // ⭐ PASS SKILL
                File file = new File("data/output.html");
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(file.toURI());
                }
                JOptionPane.showMessageDialog(frame, "Recipes exported for " + user.getSkill() + " user!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Recipe r = list.getSelectedValue();
                if (r != null) {
                    JOptionPane.showMessageDialog(frame, "Title: " + r.getTitle() + "\nDifficulty: " + r.getDifficulty() + "\nCuisines: " + r.getCuisines());
                }
            }
        });
        topPanel.add(userBox);
        topPanel.add(recommendBtn);
        topPanel.add(resetBtn);
        topPanel.add(cuisineFilter);
        topPanel.add(filterBtn);
        topPanel.add(addRecipeBtn);
        topPanel.add(addUserBtn);
        topPanel.add(scrapeBtn);
        topPanel.add(xslBtn);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(list), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void refreshList(List<Recipe> recipes) {
        listModel.clear();
        for (Recipe r : recipes) {
            listModel.addElement(r);
        }
    }

    private void openAddRecipeForm() {
        JFrame f = new JFrame("Add Recipe");
        f.setSize(350, 250);
        f.setLayout(new GridLayout(6, 2));
        JTextField title = new JTextField();
        JComboBox<String> c1 = new JComboBox<>(new String[]{"Italian", "Asian", "European", "American", "Indian"});
        JComboBox<String> c2 = new JComboBox<>(new String[]{"Italian", "Asian", "European", "American", "Indian"});
        JComboBox<String> diff = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
        JButton save = new JButton("Save");

        save.addActionListener(e -> {
            try {
                if (title.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(f, "Title is required!");
                    return;
                }
                Recipe r = new Recipe(String.valueOf(System.currentTimeMillis()), title.getText(), List.of( (String) c1.getSelectedItem(), (String) c2.getSelectedItem()), (String) diff.getSelectedItem());
                manager.addRecipe(r);
                refreshList(manager.getAllRecipes());
                f.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        f.add(new JLabel("Title"));
        f.add(title);
        f.add(new JLabel("Cuisine 1"));
        f.add(c1);
        f.add(new JLabel("Cuisine 2"));
        f.add(c2);
        f.add(new JLabel("Difficulty"));
        f.add(diff);
        f.add(save);

        f.setVisible(true);
    }

    private void openAddUserForm() {
        JFrame f = new JFrame("Add User");
        f.setSize(300, 200);
        f.setLayout(new GridLayout(5, 2));
        JTextField name = new JTextField();
        JTextField surname = new JTextField();
        JComboBox<String> skill = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
        JComboBox<String> cuisine = new JComboBox<>(new String[]{"Italian", "Asian", "European", "American", "Indian"});
        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            try {
                if (name.getText().isEmpty() || surname.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(f, "Name and surname required!");
                    return;
                }
                User u = new User(name.getText(), surname.getText(), (String) skill.getSelectedItem(), (String) cuisine.getSelectedItem());
                manager.addUser(u);
                manager.reloadUsers();
                userBox.removeAllItems();

                for (User us : manager.getAllUsers()) {
                    userBox.addItem(us);
                }
                f.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        f.add(new JLabel("Name"));
        f.add(name);
        f.add(new JLabel("Surname"));
        f.add(surname);
        f.add(new JLabel("Skill"));
        f.add(skill);
        f.add(new JLabel("Cuisine"));
        f.add(cuisine);
        f.add(save);

        f.setVisible(true);
    }
}
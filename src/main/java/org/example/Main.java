package org.example;

import org.example.ui.MainUI;
import org.example.xml.XMLManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            XMLManager xmlManager = new XMLManager();
            SwingUtilities.invokeLater(() -> new MainUI(xmlManager));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
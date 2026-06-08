package raven.main;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import raven.login.Login;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    public Application() {
        init();
    }

    private void init() {
        setTitle("EducTech");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1200, 700));
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setContentPane(new Login());
    }

    public static void main(String[] args) {
        FlatMacDarkLaf.registerCustomDefaultsSource("raven.themes");
        UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}
package raven.main;

import com.formdev.flatlaf.FlatLightLaf;
import raven.login.Login;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    public Application() {
        init();
    }

    private void init() {
        setTitle("EducTech Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1500, 900));
        setMinimumSize(new Dimension(1280, 760));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(new Login());
    }

    public static void main(String[] args) {
        FlatLightLaf.registerCustomDefaultsSource("raven.themes");
        UIManager.put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        FlatLightLaf.setup();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}

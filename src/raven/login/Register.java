package raven.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.db.UsuarioDAO;

import javax.swing.*;
import java.awt.*;

public class Register extends JPanel {

    private JTextField     txtNome;
    private JTextField     txtUsername;
    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton        cmdRegister;

    public Register() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill, 256:300"));
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:28;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        txtNome            = new JTextField();
        txtUsername        = new JTextField();
        txtEmail           = new JTextField();
        txtPassword        = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        cmdRegister        = new JButton("Cadastrar");

        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        txtConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");

        txtNome.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,            "Seu nome completo");
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,        "Escolha um usuário");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,           "seu@email.com");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,        "Mínimo 6 caracteres");
        txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Repita a senha");

        cmdRegister.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");

        cmdRegister.addActionListener(e -> cadastrar());

        JLabel lbTitle      = new JLabel("Criar conta");
        JLabel description  = new JLabel("Preencha os dados abaixo");

        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +19");
        description.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Nome completo"),    "gapy 8");
        panel.add(txtNome);
        panel.add(new JLabel("Usuário"),          "gapy 8");
        panel.add(txtUsername);
        panel.add(new JLabel("E-mail"),           "gapy 8");
        panel.add(txtEmail);
        panel.add(new JLabel("Senha"),            "gapy 8");
        panel.add(txtPassword);
        panel.add(new JLabel("Confirmar senha"),  "gapy 8");
        panel.add(txtConfirmPassword);
        panel.add(cmdRegister,       "gapy 14");
        panel.add(createBackToLogin(),"gapy 6");

        add(panel);
    }

    private void cadastrar() {
        String nome  = txtNome.getText().trim();
        String user  = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String pass  = new String(txtPassword.getPassword());
        String conf  = new String(txtConfirmPassword.getPassword());

        // Validações
        if (nome.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "A senha deve ter no mínimo 6 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(this,
                    "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtConfirmPassword.setText("");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                    "Informe um e-mail válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        if (dao.loginOuEmailExiste(user, email)) {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou e-mail já cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = dao.cadastrar(nome, user, email, pass, "ALUNO");
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Conta criada com sucesso!\nFaça login para continuar.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            voltarLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível cadastrar. Tente novamente.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voltarLogin() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new Login());
        frame.revalidate();
        frame.repaint();
    }

    private Component createBackToLogin() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "background:null");

        JLabel label = new JLabel("Já tem uma conta?");
        label.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        JButton cmdBack = new JButton("<html><a href=\"#\">Entrar</a></html>");
        cmdBack.putClientProperty(FlatClientProperties.STYLE, "border:3,3,3,3");
        cmdBack.setContentAreaFilled(false);
        cmdBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdBack.addActionListener(e -> voltarLogin());

        panel.add(label);
        panel.add(cmdBack);
        return panel;
    }
}

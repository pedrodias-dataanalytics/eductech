package raven.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.db.UsuarioDAO;
import raven.main.Dashboard;

import javax.swing.*;
import java.awt.*;

public class Register extends JPanel {

    private JTextField txtNome;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton cmdRegister;

    public Register() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 42", "[52%,grow][48%,grow]", "[center]"));
        setBackground(Dashboard.COR_FUNDO);

        add(criarResumo(), "grow, push");
        add(criarFormulario(), "grow, push, w 430:480:540");
    }

    private JPanel criarFormulario() {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 38 46 34 46", "[grow]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1));

        txtNome = new JTextField();
        txtUsername = new JTextField();
        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        cmdRegister = new JButton("Cadastrar");

        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        txtConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");

        txtNome.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Seu nome completo");
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Escolha um usuario");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "seu@email.com");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Minimo 6 caracteres");
        txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Repita a senha");

        estilizarCampo(txtNome);
        estilizarCampo(txtUsername);
        estilizarCampo(txtEmail);
        estilizarCampo(txtPassword);
        estilizarCampo(txtConfirmPassword);

        cmdRegister.setFont(new Font("Dialog", Font.BOLD, 14));
        cmdRegister.setForeground(Color.WHITE);
        cmdRegister.setBackground(Dashboard.COR_ATIVO);
        cmdRegister.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        cmdRegister.setFocusPainted(false);
        cmdRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdRegister.addActionListener(e -> cadastrar());

        JLabel lbTitle = new JLabel("Criar conta");
        lbTitle.setFont(new Font("Dialog", Font.BOLD, 26));
        lbTitle.setForeground(Dashboard.COR_TEXTO);
        JLabel description = new JLabel("Novos usuarios entram com perfil ALUNO.");
        description.setFont(new Font("Dialog", Font.PLAIN, 14));
        description.setForeground(Dashboard.COR_TEXTO_MUTED);

        panel.add(lbTitle);
        panel.add(description, "gapy 0 16");
        panel.add(new JLabel("Nome completo"));
        panel.add(txtNome, "growx, h 40!");
        panel.add(new JLabel("Usuario"), "gapy 6");
        panel.add(txtUsername, "growx, h 40!");
        panel.add(new JLabel("E-mail"), "gapy 6");
        panel.add(txtEmail, "growx, h 40!");
        panel.add(new JLabel("Senha"), "gapy 6");
        panel.add(txtPassword, "growx, h 40!");
        panel.add(new JLabel("Confirmar senha"), "gapy 6");
        panel.add(txtConfirmPassword, "growx, h 40!");
        panel.add(cmdRegister, "growx, gapy 16, h 44!");
        panel.add(createBackToLogin(), "gapy 8");
        return panel;
    }

    private JPanel criarResumo() {
        JPanel panel = new JPanel(new MigLayout("fill, wrap, insets 44", "[grow]", "[grow][][grow]"));
        panel.setBackground(Dashboard.COR_SIDEBAR);
        panel.setBorder(BorderFactory.createLineBorder(new Color(42, 52, 70), 1));

        JLabel titulo = new JLabel("EducTech Manager");
        titulo.setFont(new Font("Dialog", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);

        panel.add(Box.createVerticalGlue(), "grow, push, wrap");
        panel.add(titulo, "align center, wrap");
        panel.add(Box.createVerticalGlue(), "grow, push");
        return panel;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(Color.WHITE);
        campo.setForeground(Dashboard.COR_TEXTO);
        campo.setCaretColor(Dashboard.COR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    }

    private void cadastrar() {
        String nome = txtNome.getText().trim();
        String user = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String conf = new String(txtConfirmPassword.getPassword());

        if (nome.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "A senha deve ter no minimo 6 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(this,
                    "As senhas nao coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtConfirmPassword.setText("");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                    "Informe um e-mail valido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        if (dao.loginOuEmailExiste(user, email)) {
            JOptionPane.showMessageDialog(this,
                    "Usuario ou e-mail ja cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = dao.cadastrar(nome, user, email, pass, "ALUNO");
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Conta criada com sucesso!\nFaca login para continuar.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            voltarLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nao foi possivel cadastrar. Tente novamente.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voltarLogin() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new Login());
        frame.revalidate();
        frame.repaint();
    }

    private Component createBackToLogin() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel("Ja tem uma conta?");
        label.setForeground(Dashboard.COR_TEXTO_MUTED);

        JButton cmdBack = new JButton("<html><a href=\"#\">Entrar</a></html>");
        cmdBack.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 3));
        cmdBack.setContentAreaFilled(false);
        cmdBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdBack.addActionListener(e -> voltarLogin());

        panel.add(label);
        panel.add(cmdBack);
        return panel;
    }
}

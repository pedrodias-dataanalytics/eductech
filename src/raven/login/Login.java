package raven.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.db.UsuarioDAO;
import raven.main.Dashboard;

import javax.swing.*;
import java.awt.*;

public class Login extends JPanel {

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton cmdEntrar;

    public Login() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 42", "[52%,grow][48%,grow]", "[center]"));
        setBackground(Dashboard.COR_FUNDO);

        JPanel hero = criarHero();
        JPanel form = criarFormulario();
        add(hero, "grow, push");
        add(form, "grow, push, w 420:460:520");
    }

    private JPanel criarHero() {
        JPanel hero = new JPanel(new MigLayout("fill, wrap, insets 44", "[grow]", "[grow][][grow]"));
        hero.setBackground(Dashboard.COR_SIDEBAR);
        hero.setBorder(BorderFactory.createLineBorder(new Color(42, 52, 70), 1));

        JLabel marca = new JLabel("EducTech Manager");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("Dialog", Font.BOLD, 34));

        hero.add(Box.createVerticalGlue(), "grow, push, wrap");
        hero.add(marca, "align center, wrap");
        hero.add(Box.createVerticalGlue(), "grow, push");
        return hero;
    }

    private JPanel criarFormulario() {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 42 46 38 46", "[grow]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1));

        txtUsuario = new JTextField();
        txtSenha = new JPasswordField();
        cmdEntrar = new JButton("Entrar");

        txtSenha.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        txtUsuario.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Usuario ou e-mail");
        txtSenha.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Senha");
        estilizarCampo(txtUsuario);
        estilizarCampo(txtSenha);

        cmdEntrar.setFont(new Font("Dialog", Font.BOLD, 14));
        cmdEntrar.setForeground(Color.WHITE);
        cmdEntrar.setBackground(Dashboard.COR_ATIVO);
        cmdEntrar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        cmdEntrar.setFocusPainted(false);
        cmdEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdEntrar.addActionListener(e -> autenticar());
        txtSenha.addActionListener(e -> autenticar());

        JLabel lbTitulo = new JLabel("Bem-vindo de volta");
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 26));
        lbTitulo.setForeground(Dashboard.COR_TEXTO);
        JLabel descricao = new JLabel("Acesse sua conta para continuar.");
        descricao.setFont(new Font("Dialog", Font.PLAIN, 14));
        descricao.setForeground(Dashboard.COR_TEXTO_MUTED);

        panel.add(lbTitulo);
        panel.add(descricao, "gapy 0 20");
        panel.add(new JLabel("Usuario ou e-mail"));
        panel.add(txtUsuario, "growx, h 42!");
        panel.add(new JLabel("Senha"), "gapy 8");
        panel.add(txtSenha, "growx, h 42!");
        panel.add(cmdEntrar, "growx, gapy 18, h 44!");
        panel.add(createCadastroLabel(), "gapy 8");
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

    private void autenticar() {
        String login = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        String[] usuario = dao.autenticar(login, senha);

        if (usuario != null) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new Dashboard(usuario[0], usuario[1]));
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario ou senha invalidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    private Component createCadastroLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);

        JLabel label = new JLabel("Nao tem uma conta?");
        label.setForeground(Dashboard.COR_TEXTO_MUTED);

        JButton cmdCadastrar = new JButton("<html><a href=\"#\">Cadastre-se</a></html>");
        cmdCadastrar.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 3));
        cmdCadastrar.setContentAreaFilled(false);
        cmdCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdCadastrar.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new Register());
            frame.revalidate();
            frame.repaint();
        });

        panel.add(label);
        panel.add(cmdCadastrar);
        return panel;
    }
}

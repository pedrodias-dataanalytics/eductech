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
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        txtUsuario = new JTextField();
        txtSenha   = new JPasswordField();
        cmdEntrar  = new JButton("Entrar");

        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill, 256:300"));
        panel.putClientProperty(FlatClientProperties.STYLE,
                "arc:28;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        txtSenha.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        txtUsuario.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Usuário ou e-mail");
        txtSenha.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,   "Senha");

        cmdEntrar.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");

        cmdEntrar.addActionListener(e -> autenticar());

        // Também permite Enter no campo senha
        txtSenha.addActionListener(e -> autenticar());

        JLabel lbTitulo  = new JLabel("Bem-vindo de volta");
        JLabel descricao = new JLabel("Acesse sua conta no EducTech");

        lbTitulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +19");
        descricao.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        panel.add(lbTitulo);
        panel.add(descricao);
        panel.add(new JLabel("Usuário"), "gapy 8");
        panel.add(txtUsuario);
        panel.add(new JLabel("Senha"), "gapy 8");
        panel.add(txtSenha);
        panel.add(cmdEntrar, "gapy 14");
        panel.add(createCadastroLabel(), "gapy 6");

        add(panel);
    }

    private void autenticar() {
        String login = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioDAO dao    = new UsuarioDAO();
        String[]   usuario = dao.autenticar(login, senha);

        if (usuario != null) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new Dashboard(usuario[0], usuario[1]));
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }

    private Component createCadastroLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "background:null");

        JLabel label = new JLabel("Não tem uma conta?");
        label.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        JButton cmdCadastrar = new JButton("<html><a href=\"#\">Cadastre-se</a></html>");
        cmdCadastrar.putClientProperty(FlatClientProperties.STYLE, "border:3,3,3,3");
        cmdCadastrar.setContentAreaFilled(false);
        cmdCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdCadastrar.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new Register());
            frame.revalidate();
            frame.repaint();
        });

        panel.add(label);
        panel.add(cmdCadastrar);
        return panel;
    }
}

package raven.main;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.aluno.TelaAlunos;
import raven.curso.TelaCursos;
import raven.db.Conexao;
import raven.login.Login;
import raven.matricula.TelaMatriculas;
import raven.usuario.TelaUsuarios;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JPanel {

    private final String nomeUsuario;
    private final String perfil;

    public Dashboard(String nomeUsuario, String perfil) {
        this.nomeUsuario = nomeUsuario;
        this.perfil      = perfil;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 0", "[200!][grow]", "[grow]"));

        // ── Sidebar ────────────────────────────────────────────────
        JPanel sidebar = criarSidebar();
        add(sidebar, "growy, pushy");

        // ── Painel de conteúdo ─────────────────────────────────────
        JPanel conteudo = criarPainelInicial();
        add(conteudo, "grow, push");
    }

    // ── Sidebar com menu ──────────────────────────────────────────
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel(new MigLayout("wrap, fillx, insets 20 16 20 16", "[grow]", "[][][][grow][]"));
        sidebar.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%)");

        // Logo / título
        JLabel logo = new JLabel("EducTech");
        logo.putClientProperty(FlatClientProperties.STYLE, "font:bold +14");
        sidebar.add(logo, "gapy 4 16");

        // Separador
        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:darken(@background,15%);" +
                "[dark]foreground:lighten(@background,15%)");
        sidebar.add(sep, "growx, gapy 0 10");

        // Botões de menu
        sidebar.add(criarBtnMenu("Dashboard",        "dash"),    "growx");
        sidebar.add(criarBtnMenu("Alunos",           "alunos"),  "growx");
        sidebar.add(criarBtnMenu("Cursos",           "cursos"),  "growx");
        sidebar.add(criarBtnMenu("Matrículas",       "mats"),    "growx");

        if ("ADMIN".equals(perfil)) {
            sidebar.add(criarBtnMenu("Usuários",     "users"),   "growx");
        }

        // Rodapé do sidebar
        sidebar.add(new JLabel(), "growy, pushy");

        JLabel lbNome = new JLabel(nomeUsuario);
        lbNome.putClientProperty(FlatClientProperties.STYLE, "font:bold +1");
        sidebar.add(lbNome, "growx");

        JLabel lbPerfil = new JLabel(perfil);
        lbPerfil.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,35%);" +
                "[dark]foreground:darken(@foreground,35%);font:-1");
        sidebar.add(lbPerfil, "growx");

        JButton btnSair = new JButton("Sair");
        btnSair.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,8%);" +
                "[dark]background:lighten(@background,8%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> sair());
        sidebar.add(btnSair, "growx, gapy 8 0");

        return sidebar;
    }

    private JButton criarBtnMenu(String texto, String chave) {
        JButton btn = new JButton(texto);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0;" +
                "arc:10");
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navegarPara(chave));
        return btn;
    }

    // ── Painel inicial (dashboard) ────────────────────────────────
    private JPanel criarPainelInicial() {
        JPanel p = new JPanel(new MigLayout("fill, insets 30", "[grow]", "[][][][grow]"));
        p.setOpaque(false);

        JLabel lbBemVindo = new JLabel("Olá, " + nomeUsuario + "!");
        JLabel lbSub      = new JLabel("Painel de Controle — EducTech");

        lbBemVindo.putClientProperty(FlatClientProperties.STYLE, "font:bold +18");
        lbSub.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        p.add(lbBemVindo, "wrap");
        p.add(lbSub,      "gapy 2 20, wrap");

        // Cards de totais
        JPanel cards = new JPanel(new MigLayout("insets 0, gap 16", "[grow][grow][grow][grow]", "[grow]"));
        cards.setOpaque(false);

        cards.add(criarCard("Alunos",     buscarTotal("SELECT COUNT(*) FROM alunos WHERE ativo=1"),     new Color(33, 150, 243)));
        cards.add(criarCard("Cursos",     buscarTotal("SELECT COUNT(*) FROM cursos WHERE ativo=1"),     new Color(76, 175, 80)));
        cards.add(criarCard("Matrículas", buscarTotal("SELECT COUNT(*) FROM matriculas"),               new Color(255, 152, 0)));
        cards.add(criarCard("Usuários",   buscarTotal("SELECT COUNT(*) FROM usuarios WHERE ativo=1"),   new Color(156, 39, 176)));

        p.add(cards, "growx, wrap");

        // Atalhos rápidos
        JLabel lbAtalhos = new JLabel("Acesso rápido");
        lbAtalhos.putClientProperty(FlatClientProperties.STYLE, "font:bold +3");
        p.add(lbAtalhos, "gapy 24 12, wrap");

        JPanel atalhos = new JPanel(new MigLayout("insets 0, gap 12", "[][][][]"));
        atalhos.setOpaque(false);
        atalhos.add(criarBotaoAtalho("Novo Aluno",     "alunos"));
        atalhos.add(criarBotaoAtalho("Novo Curso",     "cursos"));
        atalhos.add(criarBotaoAtalho("Nova Matrícula", "mats"));
        if ("ADMIN".equals(perfil)) {
            atalhos.add(criarBotaoAtalho("Novo Usuário", "users"));
        }
        p.add(atalhos, "wrap");

        return p;
    }

    private JPanel criarCard(String titulo, int total, Color cor) {
        JPanel card = new JPanel(new MigLayout("wrap, fillx, insets 20", "[center]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc:16;" +
                "[light]background:darken(@background,4%);" +
                "[dark]background:lighten(@background,4%)");

        String hex = String.format("#%02x%02x%02x", cor.getRed(), cor.getGreen(), cor.getBlue());

        JLabel lbTotal  = new JLabel(String.valueOf(total));
        JLabel lbTitulo = new JLabel(titulo);

        lbTotal.putClientProperty(FlatClientProperties.STYLE,
                "font:bold +26; foreground:" + hex);
        lbTitulo.putClientProperty(FlatClientProperties.STYLE,
                "[light]foreground:lighten(@foreground,20%);" +
                "[dark]foreground:darken(@foreground,20%)");

        card.add(lbTotal);
        card.add(lbTitulo);
        return card;
    }

    private JButton criarBotaoAtalho(String texto, String chave) {
        JButton btn = new JButton(texto);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,8%);" +
                "[dark]background:lighten(@background,8%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:10");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navegarPara(chave));
        return btn;
    }

    // ── Navegação entre telas ─────────────────────────────────────
    private void navegarPara(String chave) {
        JPanel tela = switch (chave) {
            case "alunos" -> new TelaAlunos(perfil);
            case "cursos" -> new TelaCursos(perfil);
            case "mats"   -> new TelaMatriculas(perfil);
            case "users"  -> "ADMIN".equals(perfil) ? new TelaUsuarios() : painelAcessoNegado();
            default       -> criarPainelInicial();
        };

        removeAll();
        setLayout(new MigLayout("fill, insets 0", "[200!][grow]", "[grow]"));
        add(criarSidebar(), "growy, pushy");
        add(tela, "grow, push");
        revalidate();
        repaint();
    }

    private JPanel painelAcessoNegado() {
        JPanel p = new JPanel(new MigLayout("fill", "[center]", "[center]"));
        p.setOpaque(false);
        JLabel lb = new JLabel("Acesso negado para este perfil.");
        lb.putClientProperty(FlatClientProperties.STYLE,
                "foreground:#e74c3c;font:bold +4");
        p.add(lb);
        return p;
    }

    private void sair() {
        int resp = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Sair",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new Login());
            frame.revalidate();
            frame.repaint();
        }
    }

    private int buscarTotal(String query) {
        try (Connection con = Conexao.getConnexion();
             Statement st   = con.createStatement();
             ResultSet rs   = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("Erro ao buscar total: " + e.getMessage());
        }
        return 0;
    }
}

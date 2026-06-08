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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Dashboard extends JPanel {

    private final String nomeUsuario;
    private final String perfil;
    private JPanel conteudoPanel;
    private JPanel sidebar;
    private String abaAtiva = "dash";

    public static final Color COR_SIDEBAR      = new Color(15, 15, 25);
    public static final Color COR_SIDEBAR_HOVER= new Color(28, 28, 45);
    public static final Color COR_ATIVO        = new Color(99, 102, 241);
    public static final Color COR_ATIVO_BG     = new Color(99, 102, 241, 35);
    public static final Color COR_TEXTO_MUTED  = new Color(130, 130, 160);
    public static final Color COR_FUNDO        = new Color(10, 10, 18);
    public static final Color COR_CARD         = new Color(18, 18, 30);
    public static final Color COR_BORDA        = new Color(35, 35, 55);
    public static final Color COR_TEXTO        = Color.WHITE;

    public Dashboard(String nomeUsuario, String perfil) {
        this.nomeUsuario = nomeUsuario;
        this.perfil      = perfil;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        sidebar       = buildSidebar();
        conteudoPanel = new JPanel(new BorderLayout());
        conteudoPanel.setBackground(COR_FUNDO);
        conteudoPanel.add(buildHome(), BorderLayout.CENTER);

        add(sidebar,       BorderLayout.WEST);
        add(conteudoPanel, BorderLayout.CENTER);
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────
    public JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(COR_SIDEBAR);
        sb.setPreferredSize(new Dimension(220, 0));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 18));
        logoPanel.setBackground(COR_SIDEBAR);
        logoPanel.setMaximumSize(new Dimension(220, 64));
        JLabel icone = new JLabel("◈");
        icone.setFont(new Font("Dialog", Font.BOLD, 20));
        icone.setForeground(COR_ATIVO);
        JLabel logo = new JLabel("EducTech");
        logo.setFont(new Font("Dialog", Font.BOLD, 16));
        logo.setForeground(COR_TEXTO);
        logoPanel.add(icone);
        logoPanel.add(logo);
        sb.add(logoPanel);
        sb.add(criarSeparador());

        // Menu principal
        sb.add(criarSecaoLabel("MENU"));
        sb.add(criarItemMenu("⊞  Dashboard",  "dash"));
        sb.add(criarItemMenu("◉  Alunos",     "alunos"));
        sb.add(criarItemMenu("▦  Cursos",     "cursos"));
        sb.add(criarItemMenu("⊟  Matrículas", "mats"));

        if ("ADMIN".equals(perfil)) {
            sb.add(criarSeparador());
            sb.add(criarSecaoLabel("ADMINISTRAÇÃO"));
            sb.add(criarItemMenu("⊕  Usuários", "users"));
        }

        sb.add(Box.createVerticalGlue());
        sb.add(criarSeparador());
        sb.add(buildRodape());
        return sb;
    }

    private JPanel criarSecaoLabel(String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        p.setBackground(COR_SIDEBAR);
        p.setMaximumSize(new Dimension(220, 32));
        JLabel lb = new JLabel(texto);
        lb.setFont(new Font("Dialog", Font.BOLD, 10));
        lb.setForeground(COR_TEXTO_MUTED);
        p.add(lb);
        return p;
    }

    private JPanel criarSeparador() {
        JPanel sep = new JPanel();
        sep.setBackground(COR_BORDA);
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setPreferredSize(new Dimension(220, 1));
        return sep;
    }

    public JPanel criarItemMenu(String texto, String chave) {
        boolean ativo = chave.equals(abaAtiva);
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 11));
        item.setBackground(ativo ? COR_ATIVO_BG : COR_SIDEBAR);
        item.setMaximumSize(new Dimension(220, 46));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setBorder(BorderFactory.createMatteBorder(0, ativo ? 3 : 3, 0, 0,
                ativo ? COR_ATIVO : COR_SIDEBAR));

        JLabel lb = new JLabel(texto);
        lb.setFont(new Font("Dialog", ativo ? Font.BOLD : Font.PLAIN, 13));
        lb.setForeground(ativo ? COR_TEXTO : COR_TEXTO_MUTED);
        item.add(lb);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!chave.equals(abaAtiva)) item.setBackground(COR_SIDEBAR_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (!chave.equals(abaAtiva)) item.setBackground(COR_SIDEBAR);
            }
            public void mouseClicked(MouseEvent e) { navegarPara(chave); }
        });
        return item;
    }

    private JPanel buildRodape() {
        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBackground(COR_SIDEBAR);
        rodape.setBorder(new EmptyBorder(14, 16, 16, 16));
        rodape.setMaximumSize(new Dimension(220, 110));

        JPanel usuario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        usuario.setBackground(COR_SIDEBAR);

        JLabel avatar = new JLabel(String.valueOf(nomeUsuario.charAt(0)).toUpperCase());
        avatar.setFont(new Font("Dialog", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(COR_ATIVO);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel nomePanel = new JPanel();
        nomePanel.setLayout(new BoxLayout(nomePanel, BoxLayout.Y_AXIS));
        nomePanel.setBackground(COR_SIDEBAR);
        JLabel lbNome = new JLabel(nomeUsuario);
        lbNome.setFont(new Font("Dialog", Font.BOLD, 12));
        lbNome.setForeground(COR_TEXTO);
        JLabel lbPerfil = new JLabel(perfil);
        lbPerfil.setFont(new Font("Dialog", Font.PLAIN, 11));
        lbPerfil.setForeground(COR_TEXTO_MUTED);
        nomePanel.add(lbNome);
        nomePanel.add(lbPerfil);

        usuario.add(avatar);
        usuario.add(nomePanel);
        rodape.add(usuario);
        rodape.add(Box.createVerticalStrut(10));

        JButton btnSair = new JButton("↩  Sair do sistema");
        btnSair.setFont(new Font("Dialog", Font.PLAIN, 12));
        btnSair.setForeground(new Color(248, 113, 113));
        btnSair.setBackground(new Color(248, 113, 113, 20));
        btnSair.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(248, 113, 113, 60), 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSair.setMaximumSize(new Dimension(188, 36));
        btnSair.addActionListener(e -> sair());
        rodape.add(btnSair);
        return rodape;
    }

    // ── HOME ─────────────────────────────────────────────────────────
    private JPanel buildHome() {
        JPanel p = new JPanel(new MigLayout("fill, insets 36 36 36 36", "[grow]", "[][110!][][][grow]"));
        p.setBackground(COR_FUNDO);

        JLabel lbTitulo = new JLabel("Painel de Controle");
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 28));
        lbTitulo.setForeground(COR_TEXTO);
        JLabel lbSub = new JLabel("Bem-vindo, " + nomeUsuario + "! Aqui está o resumo do sistema.");
        lbSub.setFont(new Font("Dialog", Font.PLAIN, 13));
        lbSub.setForeground(COR_TEXTO_MUTED);
        p.add(lbTitulo, "wrap");
        p.add(lbSub,    "gapy 4 28, wrap");

        JPanel cards = new JPanel(new MigLayout("insets 0, gap 16", "[grow][grow][grow][grow]", "[grow]"));
        cards.setOpaque(false);
        cards.add(criarCard("Alunos",     buscarTotal("SELECT COUNT(*) FROM alunos WHERE ativo=1"),   new Color(99,102,241),  "◉"), "grow");
        cards.add(criarCard("Cursos",     buscarTotal("SELECT COUNT(*) FROM cursos WHERE ativo=1"),   new Color(16,185,129),  "▦"), "grow");
        cards.add(criarCard("Matrículas", buscarTotal("SELECT COUNT(*) FROM matriculas"),             new Color(245,158,11),  "⊟"), "grow");
        cards.add(criarCard("Usuários",   buscarTotal("SELECT COUNT(*) FROM usuarios WHERE ativo=1"), new Color(236,72,153),  "⊕"), "grow");
        p.add(cards, "growx, wrap");

        JLabel lbAcoes = new JLabel("Ações Rápidas");
        lbAcoes.setFont(new Font("Dialog", Font.BOLD, 17));
        lbAcoes.setForeground(COR_TEXTO);
        p.add(lbAcoes, "gapy 28 14, wrap");

        JPanel acoes = new JPanel(new MigLayout("insets 0, gap 12", "[grow][grow][grow][grow]", "[56!]"));
        acoes.setOpaque(false);
        acoes.add(criarBotaoAcao("+ Novo Aluno",     new Color(99,102,241), "alunos"), "grow");
        acoes.add(criarBotaoAcao("+ Novo Curso",     new Color(16,185,129), "cursos"), "grow");
        acoes.add(criarBotaoAcao("+ Nova Matrícula", new Color(245,158,11), "mats"),   "grow");
        if ("ADMIN".equals(perfil))
            acoes.add(criarBotaoAcao("+ Novo Usuário", new Color(236,72,153), "users"), "grow");
        p.add(acoes, "growx");
        return p;
    }

    public static JPanel criarCard(String titulo, int total, Color cor, String icone) {
        JPanel card = new JPanel(new MigLayout("insets 20 20 20 20", "[grow]", "[][][grow]"));
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, cor),
                BorderFactory.createLineBorder(COR_BORDA, 1)));

        JLabel lbIcone = new JLabel(icone);
        lbIcone.setFont(new Font("Dialog", Font.BOLD, 20));
        lbIcone.setForeground(cor);
        lbIcone.setOpaque(true);
        lbIcone.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 25));
        lbIcone.setHorizontalAlignment(SwingConstants.CENTER);
        lbIcone.setPreferredSize(new Dimension(42, 42));
        lbIcone.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel lbTotal = new JLabel(String.valueOf(total));
        lbTotal.setFont(new Font("Dialog", Font.BOLD, 38));
        lbTotal.setForeground(COR_TEXTO);

        JLabel lbTitulo = new JLabel(titulo);
        lbTitulo.setFont(new Font("Dialog", Font.PLAIN, 13));
        lbTitulo.setForeground(COR_TEXTO_MUTED);

        card.add(lbIcone,  "wrap, gapy 0 10");
        card.add(lbTotal,  "wrap, gapy 0 2");
        card.add(lbTitulo, "wrap");
        return card;
    }

    public static JButton criarBotaoAcao(String texto, Color cor, String chave) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setForeground(COR_TEXTO);
        btn.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 40));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 90), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 80));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 40));
            }
        });
        return btn;
    }

    // ── NAVEGAÇÃO ────────────────────────────────────────────────────
    private void navegarPara(String chave) {
        abaAtiva = chave;
        JPanel tela = switch (chave) {
            case "alunos" -> new TelaAlunos(perfil);
            case "cursos" -> new TelaCursos(perfil);
            case "mats"   -> new TelaMatriculas(perfil);
            case "users"  -> "ADMIN".equals(perfil) ? new TelaUsuarios() : painelAcessoNegado();
            default       -> buildHome();
        };
        remove(sidebar);
        sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);
        conteudoPanel.removeAll();
        conteudoPanel.add(tela, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel painelAcessoNegado() {
        JPanel p = new JPanel(new MigLayout("fill", "[center]", "[center]"));
        p.setBackground(COR_FUNDO);
        JLabel lb = new JLabel("Acesso negado para este perfil.");
        lb.setFont(new Font("Dialog", Font.BOLD, 18));
        lb.setForeground(new Color(248, 113, 113));
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
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return 0;
    }
}

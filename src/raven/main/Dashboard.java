package raven.main;

import net.miginfocom.swing.MigLayout;
import raven.aluno.TelaAlunos;
import raven.curso.TelaCursos;
import raven.db.Conexao;
import raven.login.Login;
import raven.matricula.TelaMatriculas;
import raven.usuario.TelaUsuarios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Dashboard extends JPanel {

    private final String nomeUsuario;
    private final String perfil;
    private JPanel conteudoPanel;
    private JPanel sidebar;
    private String abaAtiva = "dash";

    public static final Color COR_SIDEBAR = new Color(18, 24, 38);
    public static final Color COR_SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color COR_ATIVO = new Color(37, 99, 235);
    public static final Color COR_ATIVO_BG = new Color(37, 99, 235, 34);
    public static final Color COR_TEXTO_MUTED = new Color(116, 128, 146);
    public static final Color COR_FUNDO = new Color(243, 247, 252);
    public static final Color COR_CARD = Color.WHITE;
    public static final Color COR_BORDA = new Color(220, 226, 235);
    public static final Color COR_TEXTO = new Color(20, 28, 42);
    private static final Color COR_TEXTO_CLARO = new Color(226, 232, 240);

    public Dashboard(String nomeUsuario, String perfil) {
        this.nomeUsuario = nomeUsuario;
        this.perfil = perfil;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(COR_FUNDO);

        sidebar = buildSidebar();
        conteudoPanel = new JPanel(new BorderLayout());
        conteudoPanel.setBackground(COR_FUNDO);
        conteudoPanel.add(buildHome(), BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(conteudoPanel, BorderLayout.CENTER);
    }

    public JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(COR_SIDEBAR);
        sb.setPreferredSize(new Dimension(220, 0));

        JPanel logoPanel = new JPanel(new MigLayout("insets 22 22 18 22", "[][grow]", "[]2[]"));
        logoPanel.setBackground(COR_SIDEBAR);
        logoPanel.setMaximumSize(new Dimension(220, 96));

        JLabel marca = new JLabel("ET");
        marca.setFont(new Font("Dialog", Font.BOLD, 15));
        marca.setForeground(Color.WHITE);
        marca.setHorizontalAlignment(SwingConstants.CENTER);
        marca.setOpaque(true);
        marca.setBackground(COR_ATIVO);
        marca.setPreferredSize(new Dimension(42, 42));

        JLabel logo = new JLabel("EducTech");
        logo.setFont(new Font("Dialog", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("ODS 4 - Educacao");
        subtitulo.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(148, 163, 184));

        JPanel nomes = new JPanel();
        nomes.setOpaque(false);
        nomes.setLayout(new BoxLayout(nomes, BoxLayout.Y_AXIS));
        nomes.add(logo);
        nomes.add(subtitulo);

        logoPanel.add(marca, "spany 2");
        logoPanel.add(nomes, "growx");
        sb.add(logoPanel);
        sb.add(criarSeparador());

        sb.add(criarSecaoLabel("NAVEGACAO"));
        sb.add(criarItemMenu("Dashboard", "dash"));
        sb.add(criarItemMenu("Alunos", "alunos"));
        sb.add(criarItemMenu("Cursos", "cursos"));
        sb.add(criarItemMenu("Matriculas", "mats"));

        if ("ADMIN".equals(perfil)) {
            sb.add(criarSeparador());
            sb.add(criarSecaoLabel("ADMINISTRACAO"));
            sb.add(criarItemMenu("Usuarios", "users"));
        }

        sb.add(Box.createVerticalGlue());
        sb.add(criarSeparador());
        sb.add(buildRodape());
        return sb;
    }

    private JPanel criarSecaoLabel(String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 10));
        p.setBackground(COR_SIDEBAR);
        p.setMaximumSize(new Dimension(220, 38));
        JLabel lb = new JLabel(texto);
        lb.setFont(new Font("Dialog", Font.BOLD, 11));
        lb.setForeground(new Color(148, 163, 184));
        p.add(lb);
        return p;
    }

    private JPanel criarSeparador() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(42, 52, 70));
        sep.setMaximumSize(new Dimension(220, 1));
        return sep;
    }

    public JPanel criarItemMenu(String texto, String chave) {
        boolean ativo = chave.equals(abaAtiva);
        JPanel item = new JPanel(new MigLayout("insets 0 18 0 18", "[grow]", "[44!]"));
        item.setBackground(ativo ? COR_ATIVO_BG : COR_SIDEBAR);
        item.setMaximumSize(new Dimension(220, 48));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ativo ? COR_ATIVO : COR_SIDEBAR));

        JLabel lb = new JLabel(texto);
        lb.setFont(new Font("Dialog", ativo ? Font.BOLD : Font.PLAIN, 14));
        lb.setForeground(ativo ? Color.WHITE : COR_TEXTO_CLARO);
        item.add(lb, "growx");

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!chave.equals(abaAtiva)) item.setBackground(COR_SIDEBAR_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                if (!chave.equals(abaAtiva)) item.setBackground(COR_SIDEBAR);
            }

            public void mouseClicked(MouseEvent e) {
                navegarPara(chave);
            }
        });
        return item;
    }

    private JPanel buildRodape() {
        JPanel rodape = new JPanel(new MigLayout("wrap, fillx, insets 16 18 20 18", "[grow]"));
        rodape.setBackground(COR_SIDEBAR);
        rodape.setMaximumSize(new Dimension(220, 138));

        JLabel lbNome = new JLabel(nomeUsuario);
        lbNome.setFont(new Font("Dialog", Font.BOLD, 13));
        lbNome.setForeground(Color.WHITE);
        JLabel lbPerfil = new JLabel("Perfil: " + perfil);
        lbPerfil.setFont(new Font("Dialog", Font.PLAIN, 12));
        lbPerfil.setForeground(new Color(148, 163, 184));

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Dialog", Font.BOLD, 12));
        btnSair.setForeground(new Color(185, 28, 28));
        btnSair.setBackground(new Color(254, 226, 226));
        btnSair.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> sair());

        rodape.add(lbNome);
        rodape.add(lbPerfil, "gapy 0 10");
        rodape.add(btnSair, "growx");
        return rodape;
    }

    private JPanel buildHome() {
        int totalAlunos = buscarTotal("SELECT COUNT(*) FROM alunos WHERE ativo=1");
        int totalCursos = buscarTotal("SELECT COUNT(*) FROM cursos WHERE ativo=1");
        int totalMatriculas = buscarTotal("SELECT COUNT(*) FROM matriculas");
        int totalUsuarios = buscarTotal("SELECT COUNT(*) FROM usuarios WHERE ativo=1");
        int matriculasAtivas = buscarTotal("SELECT COUNT(*) FROM matriculas WHERE status='ATIVA'");
        int matriculasConcluidas = buscarTotal("SELECT COUNT(*) FROM matriculas WHERE status='CONCLUIDA'");
        int matriculasCanceladas = buscarTotal("SELECT COUNT(*) FROM matriculas WHERE status='CANCELADA'");

        JPanel p = new JPanel(new MigLayout("fill, insets 32 38 32 38, gap 18", "[grow]", "[][][][grow]"));
        p.setBackground(COR_FUNDO);

        JPanel topo = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]4[]"));
        topo.setOpaque(false);
        JLabel lbTitulo = new JLabel("Painel de Controle");
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 30));
        lbTitulo.setForeground(COR_TEXTO);
        JLabel lbSub = new JLabel("Resumo do EducTech Manager para acompanhar alunos, cursos e matriculas.");
        lbSub.setFont(new Font("Dialog", Font.PLAIN, 14));
        lbSub.setForeground(COR_TEXTO_MUTED);
        JLabel pill = new JLabel("ODS 4 - Educacao de Qualidade");
        pill.setOpaque(true);
        pill.setForeground(new Color(22, 101, 52));
        pill.setBackground(new Color(220, 252, 231));
        pill.setBorder(new EmptyBorder(8, 14, 8, 14));

        topo.add(lbTitulo, "growx");
        topo.add(pill, "wrap");
        topo.add(lbSub, "span, growx");
        p.add(topo, "growx, wrap");

        JPanel cards = new JPanel(new MigLayout("insets 0, gap 16", "[grow][grow][grow][grow]", "[120!]"));
        cards.setOpaque(false);
        cards.add(criarCard("Alunos ativos", totalAlunos, new Color(37, 99, 235), "Pessoas cadastradas"), "grow");
        cards.add(criarCard("Cursos ativos", totalCursos, new Color(5, 150, 105), "Trilhas disponiveis"), "grow");
        cards.add(criarCard("Matriculas", totalMatriculas, new Color(217, 119, 6), "Vinculos criados"), "grow");
        cards.add(criarCard("Usuarios", totalUsuarios, new Color(147, 51, 234), "Acessos ao sistema"), "grow");
        p.add(cards, "growx, wrap");

        JPanel acoes = new JPanel(new MigLayout("insets 0, gap 12", "[grow][grow][grow][grow]", "[52!]"));
        acoes.setOpaque(false);
        acoes.add(criarBotaoAcao("Novo aluno", new Color(37, 99, 235), "alunos"), "grow");
        acoes.add(criarBotaoAcao("Novo curso", new Color(5, 150, 105), "cursos"), "grow");
        acoes.add(criarBotaoAcao("Nova matricula", new Color(217, 119, 6), "mats"), "grow");
        if ("ADMIN".equals(perfil)) {
            acoes.add(criarBotaoAcao("Novo usuario", new Color(147, 51, 234), "users"), "grow");
        }
        p.add(acoes, "growx, wrap");

        JPanel detalhe = new JPanel(new MigLayout("fill, insets 0, gap 18", "[38%,grow][62%,grow]", "[grow]"));
        detalhe.setOpaque(false);
        detalhe.add(criarPainelStatus(matriculasAtivas, matriculasConcluidas, matriculasCanceladas), "grow");
        detalhe.add(criarPainelRecentes(), "grow");
        p.add(detalhe, "grow, push");

        return p;
    }

    public static JPanel criarCard(String titulo, int total, Color cor, String descricao) {
        JPanel card = new JPanel(new MigLayout("insets 18 20 18 20", "[grow]", "[]10[]4[]"));
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, cor),
                BorderFactory.createLineBorder(COR_BORDA, 1)));

        JLabel lbTitulo = new JLabel(titulo);
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 13));
        lbTitulo.setForeground(COR_TEXTO_MUTED);
        JLabel lbTotal = new JLabel(String.valueOf(total));
        lbTotal.setFont(new Font("Dialog", Font.BOLD, 34));
        lbTotal.setForeground(COR_TEXTO);
        JLabel lbDesc = new JLabel(descricao);
        lbDesc.setFont(new Font("Dialog", Font.PLAIN, 12));
        lbDesc.setForeground(COR_TEXTO_MUTED);

        card.add(lbTitulo, "wrap");
        card.add(lbTotal, "wrap");
        card.add(lbDesc);
        return card;
    }

    private JPanel criarPainelStatus(int ativas, int concluidas, int canceladas) {
        JPanel panel = criarPainel("Status das matriculas");
        int total = Math.max(ativas + concluidas + canceladas, 1);
        panel.add(criarLinhaStatus("Ativas", ativas, total, new Color(37, 99, 235)), "growx, wrap");
        panel.add(criarLinhaStatus("Concluidas", concluidas, total, new Color(5, 150, 105)), "growx, wrap");
        panel.add(criarLinhaStatus("Canceladas", canceladas, total, new Color(220, 38, 38)), "growx, wrap");

        JLabel nota = new JLabel("<html>Use este painel para acompanhar rapidamente a ocupacao dos cursos e a evolucao dos alunos.</html>");
        nota.setForeground(COR_TEXTO_MUTED);
        nota.setFont(new Font("Dialog", Font.PLAIN, 13));
        panel.add(nota, "growx, gapy 14 0");
        return panel;
    }

    private JPanel criarLinhaStatus(String titulo, int valor, int total, Color cor) {
        JPanel linha = new JPanel(new MigLayout("fillx, insets 8 0 8 0", "[grow][]", "[]6[]"));
        linha.setOpaque(false);
        JLabel lb = new JLabel(titulo);
        lb.setForeground(COR_TEXTO);
        lb.setFont(new Font("Dialog", Font.BOLD, 13));
        JLabel qtd = new JLabel(String.valueOf(valor));
        qtd.setForeground(COR_TEXTO_MUTED);
        qtd.setFont(new Font("Dialog", Font.BOLD, 13));
        JProgressBar bar = new JProgressBar(0, total);
        bar.setValue(valor);
        bar.setStringPainted(false);
        bar.setForeground(cor);
        bar.setBackground(new Color(229, 235, 245));
        bar.setBorderPainted(false);

        linha.add(lb, "growx");
        linha.add(qtd, "wrap");
        linha.add(bar, "span, growx, h 10!");
        return linha;
    }

    private JPanel criarPainelRecentes() {
        JPanel panel = criarPainel("Matriculas recentes");
        String[] colunas = {"Aluno", "Curso", "Inicio", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String sql = "SELECT a.nome aluno, c.nome curso, DATE_FORMAT(m.data_inicio, '%d/%m/%Y') inicio, m.status " +
                "FROM matriculas m JOIN alunos a ON a.id=m.aluno_id JOIN cursos c ON c.id=m.curso_id " +
                "ORDER BY m.id DESC LIMIT 8";
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                modelo.addRow(new String[]{
                        rs.getString("aluno"),
                        rs.getString("curso"),
                        rs.getString("inicio"),
                        rs.getString("status")
                });
            }
        } catch (Exception e) {
            modelo.addRow(new String[]{"Banco indisponivel", "Verifique a conexao", "-", "-"});
        }

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(34);
        tabela.setFont(new Font("Dialog", Font.PLAIN, 13));
        tabela.setForeground(COR_TEXTO);
        tabela.setBackground(COR_CARD);
        tabela.setGridColor(COR_BORDA);
        tabela.setShowVerticalLines(false);
        tabela.getTableHeader().setBackground(new Color(248, 250, 252));
        tabela.getTableHeader().setForeground(COR_TEXTO_MUTED);
        tabela.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        tabela.setDefaultRenderer(Object.class, renderer);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        scroll.getViewport().setBackground(COR_CARD);
        panel.add(scroll, "grow, push");
        return panel;
    }

    private JPanel criarPainel(String titulo) {
        JPanel panel = new JPanel(new MigLayout("fill, wrap, insets 22", "[grow]", "[][grow]"));
        panel.setBackground(COR_CARD);
        panel.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        JLabel lb = new JLabel(titulo);
        lb.setFont(new Font("Dialog", Font.BOLD, 17));
        lb.setForeground(COR_TEXTO);
        panel.add(lb, "gapy 0 12");
        return panel;
    }

    public JButton criarBotaoAcao(String texto, Color cor, String chave) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navegarPara(chave));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(cor.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(cor);
            }
        });
        return btn;
    }

    private void navegarPara(String chave) {
        abaAtiva = chave;
        JPanel tela = switch (chave) {
            case "alunos" -> new TelaAlunos(perfil);
            case "cursos" -> new TelaCursos(perfil);
            case "mats" -> new TelaMatriculas(perfil);
            case "users" -> "ADMIN".equals(perfil) ? new TelaUsuarios() : painelAcessoNegado();
            default -> buildHome();
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
        lb.setForeground(new Color(185, 28, 28));
        p.add(lb);
        return p;
    }

    private void sair() {
        int resp = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Sair",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new Login());
            frame.revalidate();
            frame.repaint();
        }
    }

    private int buscarTotal(String query) {
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }
}

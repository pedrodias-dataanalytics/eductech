package raven.aluno;

import net.miginfocom.swing.MigLayout;
import raven.db.AlunoDAO;
import raven.main.Dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaAlunos extends JPanel {

    private final String perfil;
    private final AlunoDAO dao = new AlunoDAO();
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtNome, txtEmail, txtCpf, txtTelefone;
    private JButton btnSalvar, btnExcluir;
    private int idSelecionado = -1;

    private static final Color COR = new Color(99, 102, 241);

    public TelaAlunos(String perfil) {
        this.perfil = perfil;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(Dashboard.COR_FUNDO);

        // Cabeçalho
        JPanel header = new JPanel(new MigLayout("insets 28 36 16 36", "[grow][]"));
        header.setBackground(Dashboard.COR_FUNDO);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setLayout(new BoxLayout(tituloPanel, BoxLayout.Y_AXIS));
        tituloPanel.setOpaque(false);
        JLabel lbTitulo = new JLabel("Gerenciar Alunos");
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 26));
        lbTitulo.setForeground(Dashboard.COR_TEXTO);
        JLabel lbSub = new JLabel("Cadastre, edite e gerencie os alunos do sistema.");
        lbSub.setFont(new Font("Dialog", Font.PLAIN, 13));
        lbSub.setForeground(Dashboard.COR_TEXTO_MUTED);
        tituloPanel.add(lbTitulo);
        tituloPanel.add(Box.createVerticalStrut(4));
        tituloPanel.add(lbSub);

        JButton btnNovo = criarBotaoPrimario("+ Novo Aluno", COR);
        btnNovo.addActionListener(e -> limparFormulario());
        btnNovo.setVisible(podeEditar());

        header.add(tituloPanel, "grow");
        header.add(btnNovo);
        add(header, BorderLayout.NORTH);

        // Corpo
        JPanel corpo = new JPanel(new MigLayout("fill, insets 0 28 28 28, gap 18", "[700,grow,fill][300!,fill]", "[grow]"));
        corpo.setBackground(Dashboard.COR_FUNDO);

        // Tabela
        JPanel tabelaPanel = criarPainelTabela();
        corpo.add(tabelaPanel, "grow, push");
        corpo.add(criarFormulario(), "growx, aligny top");
        add(corpo, BorderLayout.CENTER);

        carregarTabela();
    }

    private JPanel criarPainelTabela() {
        JPanel p = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        p.setOpaque(false);

        // Barra de busca
        JTextField txtBusca = criarCampoBusca("Buscar por nome ou e-mail...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });
        p.add(txtBusca, "growx, gapy 0 12, wrap");

        String[] colunas = {"ID", "Nome", "E-mail", "CPF", "Telefone"};
        modelo  = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = criarTabela(modelo);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scroll = criarScrollPane(tabela);
        p.add(scroll, "grow, push");
        return p;
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new MigLayout("wrap, fillx, insets 18", "[grow]"));
        form.setBackground(Dashboard.COR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, COR),
                BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1)));

        JLabel lbForm = new JLabel("Dados do Aluno");
        lbForm.setFont(new Font("Dialog", Font.BOLD, 15));
        lbForm.setForeground(Dashboard.COR_TEXTO);

        txtNome     = criarCampo("Nome completo");
        txtEmail    = criarCampo("E-mail");
        txtCpf      = criarCampo("000.000.000-00");
        txtTelefone = criarCampo("(00) 00000-0000");

        btnSalvar  = criarBotaoPrimario("Salvar", COR);
        btnExcluir = criarBotaoPerigo("Excluir");
        btnExcluir.setEnabled(false);

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        aplicarPermissao();

        form.add(lbForm,                 "gapy 0 10");
        form.add(criarLabel("Nome"),     "gapy 4");
        form.add(txtNome,   "growx, h 38!");
        form.add(criarLabel("E-mail"),   "gapy 8");
        form.add(txtEmail,  "growx, h 38!");
        form.add(criarLabel("CPF"),      "gapy 8");
        form.add(txtCpf,    "growx, h 38!");
        form.add(criarLabel("Telefone"), "gapy 8");
        form.add(txtTelefone, "growx, h 38!");
        form.add(btnSalvar,  "growx, gapy 12 6, h 42!");
        form.add(btnExcluir, "growx, h 40!");
        return form;
    }

    private void carregarTabela() { carregarTabela(null); }
    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);
        for (String[] l : dao.listar()) {
            if (filtro == null || filtro.isBlank()
                    || l[1].toLowerCase().contains(filtro.toLowerCase())
                    || l[2].toLowerCase().contains(filtro.toLowerCase()))
                modelo.addRow(l);
        }
    }
    private void filtrar(String t) { carregarTabela(t); }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = Integer.parseInt(modelo.getValueAt(row, 0).toString());
        txtNome.setText(modelo.getValueAt(row, 1).toString());
        txtEmail.setText(modelo.getValueAt(row, 2).toString());
        txtCpf.setText(modelo.getValueAt(row, 3).toString());
        txtTelefone.setText(modelo.getValueAt(row, 4).toString());
        btnExcluir.setEnabled(podeEditar());
    }

    private void limparFormulario() {
        idSelecionado = -1;
        txtNome.setText(""); txtEmail.setText("");
        txtCpf.setText(""); txtTelefone.setText("");
        tabela.clearSelection();
        btnExcluir.setEnabled(false);
        txtNome.requestFocus();
    }

    private void salvar() {
        if (!podeEditar()) {
            mostrarAcessoConsulta();
            return;
        }
        String nome = txtNome.getText().trim(), email = txtEmail.getText().trim(),
                cpf  = txtCpf.getText().trim(),  tel   = txtTelefone.getText().trim();
        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, e-mail e CPF são obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE); return;
        }
        boolean ok = idSelecionado == -1 ? dao.inserir(nome, email, cpf, tel) : dao.atualizar(idSelecionado, nome, email, cpf, tel);
        if (ok) { JOptionPane.showMessageDialog(this, "Aluno salvo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
        else    { JOptionPane.showMessageDialog(this, "Erro ao salvar. CPF ou e-mail já cadastrado.", "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void excluir() {
        if (!podeEditar()) {
            mostrarAcessoConsulta();
            return;
        }
        if (idSelecionado < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Excluir este aluno?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) { JOptionPane.showMessageDialog(this, "Aluno excluído.", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
            else JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean podeEditar() {
        return "ADMIN".equals(perfil);
    }

    private void aplicarPermissao() {
        boolean editar = podeEditar();
        txtNome.setEditable(editar);
        txtEmail.setEditable(editar);
        txtCpf.setEditable(editar);
        txtTelefone.setEditable(editar);
        btnSalvar.setVisible(editar);
        btnExcluir.setVisible(editar);
    }

    private void mostrarAcessoConsulta() {
        JOptionPane.showMessageDialog(this,
                "Seu perfil permite apenas consulta.", "Acesso limitado", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helpers de UI ────────────────────────────────────────────────
    public static JTextField criarCampo(String placeholder) {
        JTextField f = new JTextField();
        f.setBackground(Color.WHITE);
        f.setForeground(Dashboard.COR_TEXTO);
        f.setCaretColor(Dashboard.COR_TEXTO);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    public static JTextField criarCampoBusca(String placeholder) {
        JTextField f = criarCampo(placeholder);
        f.setFont(new Font("Dialog", Font.PLAIN, 13));
        return f;
    }

    public static JLabel criarLabel(String texto) {
        JLabel lb = new JLabel(texto);
        lb.setFont(new Font("Dialog", Font.PLAIN, 12));
        lb.setForeground(Dashboard.COR_TEXTO_MUTED);
        return lb;
    }

    public static JButton criarBotaoPrimario(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(cor.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(cor); }
        });
        return btn;
    }

    public static JButton criarBotaoPerigo(String texto) {
        Color cor = new Color(239, 68, 68);
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Dialog", Font.PLAIN, 13));
        btn.setForeground(cor);
        btn.setBackground(new Color(254, 226, 226));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(239, 68, 68, 80), 1),
                BorderFactory.createEmptyBorder(9, 20, 9, 20)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTable criarTabela(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setBackground(Dashboard.COR_CARD);
        t.setForeground(Dashboard.COR_TEXTO);
        t.setSelectionBackground(new Color(219, 234, 254));
        t.setSelectionForeground(Dashboard.COR_TEXTO);
        t.setGridColor(Dashboard.COR_BORDA);
        t.setRowHeight(34);
        t.setFont(new Font("Dialog", Font.PLAIN, 13));
        t.getTableHeader().setBackground(new Color(248, 250, 252));
        t.getTableHeader().setForeground(Dashboard.COR_TEXTO_MUTED);
        t.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Dashboard.COR_BORDA));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setShowVerticalLines(false);
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setBorder(new EmptyBorder(0, 12, 0, 12));
        t.setDefaultRenderer(Object.class, cr);
        return t;
    }

    public static JScrollPane criarScrollPane(JTable tabela) {
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBackground(Dashboard.COR_CARD);
        scroll.getViewport().setBackground(Dashboard.COR_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1));
        return scroll;
    }
}

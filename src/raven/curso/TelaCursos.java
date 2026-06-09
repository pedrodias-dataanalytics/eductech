package raven.curso;

import net.miginfocom.swing.MigLayout;
import raven.aluno.TelaAlunos;
import raven.db.CursoDAO;
import raven.main.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCursos extends JPanel {

    private final String perfil;
    private final CursoDAO dao = new CursoDAO();
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtNome;
    private JTextArea  txtDescricao;
    private JSpinner   spnCarga;
    private JButton    btnSalvar, btnExcluir;
    private int idSelecionado = -1;

    private static final Color COR = new Color(16, 185, 129);

    public TelaCursos(String perfil) {
        this.perfil = perfil;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBackground(Dashboard.COR_FUNDO);

        JPanel header = new JPanel(new MigLayout("insets 28 36 16 36", "[grow][]"));
        header.setBackground(Dashboard.COR_FUNDO);
        JPanel tituloPanel = new JPanel();
        tituloPanel.setLayout(new BoxLayout(tituloPanel, BoxLayout.Y_AXIS));
        tituloPanel.setOpaque(false);
        JLabel lbTitulo = new JLabel("Gerenciar Cursos");
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 26));
        lbTitulo.setForeground(Dashboard.COR_TEXTO);
        JLabel lbSub = new JLabel("Cadastre, edite e gerencie os cursos disponíveis.");
        lbSub.setFont(new Font("Dialog", Font.PLAIN, 13));
        lbSub.setForeground(Dashboard.COR_TEXTO_MUTED);
        tituloPanel.add(lbTitulo);
        tituloPanel.add(Box.createVerticalStrut(4));
        tituloPanel.add(lbSub);
        JButton btnNovo = TelaAlunos.criarBotaoPrimario("+ Novo Curso", COR);
        btnNovo.addActionListener(e -> limparFormulario());
        btnNovo.setVisible(podeEditar());
        header.add(tituloPanel, "grow");
        header.add(btnNovo);
        add(header, BorderLayout.NORTH);

        JPanel corpo = new JPanel(new MigLayout("fill, insets 0 28 28 28, gap 18", "[700,grow,fill][300!,fill]", "[grow]"));
        corpo.setBackground(Dashboard.COR_FUNDO);

        // Tabela
        JPanel tabelaPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        tabelaPanel.setOpaque(false);
        JTextField txtBusca = TelaAlunos.criarCampoBusca("Buscar curso...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });
        tabelaPanel.add(txtBusca, "growx, gapy 0 12, wrap");

        String[] colunas = {"ID", "Nome", "Descrição", "Carga (h)"};
        modelo = new DefaultTableModel(colunas, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tabela = TelaAlunos.criarTabela(modelo);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(3).setMaxWidth(100);
        tabela.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) preencherFormulario(); });
        tabelaPanel.add(TelaAlunos.criarScrollPane(tabela), "grow, push");
        corpo.add(tabelaPanel, "grow, push");
        corpo.add(criarFormulario(), "growx, aligny top");
        add(corpo, BorderLayout.CENTER);
        carregarTabela();
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new MigLayout("wrap, fillx, insets 18", "[grow]"));
        form.setBackground(Dashboard.COR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, COR),
                BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1)));

        JLabel lbForm = new JLabel("Dados do Curso");
        lbForm.setFont(new Font("Dialog", Font.BOLD, 15));
        lbForm.setForeground(Dashboard.COR_TEXTO);

        txtNome      = TelaAlunos.criarCampo("Nome do curso");
        txtDescricao = new JTextArea(4, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setBackground(Color.WHITE);
        txtDescricao.setForeground(Dashboard.COR_TEXTO);
        txtDescricao.setCaretColor(Dashboard.COR_TEXTO);
        txtDescricao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        spnCarga = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        spnCarga.setBackground(Color.WHITE);

        btnSalvar  = TelaAlunos.criarBotaoPrimario("Salvar", COR);
        btnExcluir = TelaAlunos.criarBotaoPerigo("Excluir");
        btnExcluir.setEnabled(false);
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        aplicarPermissao();

        form.add(lbForm, "gapy 0 10");
        form.add(TelaAlunos.criarLabel("Nome"),             "gapy 4");
        form.add(txtNome, "growx, h 38!");
        form.add(TelaAlunos.criarLabel("Descrição"),        "gapy 8");
        form.add(new JScrollPane(txtDescricao), "growx, h 76!");
        form.add(TelaAlunos.criarLabel("Carga horária (h)"),"gapy 8");
        form.add(spnCarga, "growx, h 38!");
        form.add(btnSalvar,  "growx, gapy 12 6, h 42!");
        form.add(btnExcluir, "growx, h 40!");
        return form;
    }

    private void carregarTabela() { carregarTabela(null); }
    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);
        for (String[] l : dao.listar())
            if (filtro == null || filtro.isBlank() || l[1].toLowerCase().contains(filtro.toLowerCase()))
                modelo.addRow(l);
    }
    private void filtrar(String t) { carregarTabela(t); }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = Integer.parseInt(modelo.getValueAt(row, 0).toString());
        txtNome.setText(modelo.getValueAt(row, 1).toString());
        txtDescricao.setText(modelo.getValueAt(row, 2).toString());
        spnCarga.setValue(Integer.parseInt(modelo.getValueAt(row, 3).toString()));
        btnExcluir.setEnabled(podeEditar());
    }

    private void limparFormulario() {
        idSelecionado = -1;
        txtNome.setText(""); txtDescricao.setText(""); spnCarga.setValue(0);
        tabela.clearSelection(); btnExcluir.setEnabled(false); txtNome.requestFocus();
    }

    private void salvar() {
        if (!podeEditar()) {
            mostrarAcessoConsulta();
            return;
        }
        String nome = txtNome.getText().trim(), desc = txtDescricao.getText().trim();
        int carga = (int) spnCarga.getValue();
        if (nome.isEmpty()) { JOptionPane.showMessageDialog(this, "Nome obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        boolean ok = idSelecionado == -1 ? dao.inserir(nome, desc, carga) : dao.atualizar(idSelecionado, nome, desc, carga);
        if (ok) { JOptionPane.showMessageDialog(this, "Curso salvo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
        else    { JOptionPane.showMessageDialog(this, "Erro ao salvar.", "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void excluir() {
        if (!podeEditar()) {
            mostrarAcessoConsulta();
            return;
        }
        if (idSelecionado < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Excluir este curso?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) { JOptionPane.showMessageDialog(this, "Curso excluído.", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
            else JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean podeEditar() {
        return "ADMIN".equals(perfil);
    }

    private void aplicarPermissao() {
        boolean editar = podeEditar();
        txtNome.setEditable(editar);
        txtDescricao.setEditable(editar);
        spnCarga.setEnabled(editar);
        btnSalvar.setVisible(editar);
        btnExcluir.setVisible(editar);
    }

    private void mostrarAcessoConsulta() {
        JOptionPane.showMessageDialog(this,
                "Seu perfil permite apenas consulta.", "Acesso limitado", JOptionPane.INFORMATION_MESSAGE);
    }
}

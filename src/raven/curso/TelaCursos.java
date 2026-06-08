package raven.curso;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.db.CursoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCursos extends JPanel {

    private final String perfil;
    private final CursoDAO dao = new CursoDAO();

    private JTable             tabela;
    private DefaultTableModel  modelo;

    private JTextField  txtNome;
    private JTextArea   txtDescricao;
    private JSpinner    spnCarga;
    private JButton     btnSalvar;
    private JButton     btnNovo;
    private JButton     btnExcluir;

    private int idSelecionado = -1;

    public TelaCursos(String perfil) {
        this.perfil = perfil;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 24", "[grow][320!]", "[][][grow]"));
        setOpaque(false);

        JLabel titulo = new JLabel("Gerenciar Cursos");
        titulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +14");
        add(titulo, "span 2, wrap");

        JTextField txtBusca = new JTextField();
        txtBusca.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar curso...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });

        btnNovo = new JButton("+ Novo Curso");
        btnNovo.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,8%);" +
                "[dark]background:lighten(@background,8%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:8");
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> limparFormulario());

        add(txtBusca, "growx");
        add(btnNovo,  "wrap");

        String[] colunas = {"ID", "Nome", "Descrição", "Carga (h)"};
        modelo  = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela  = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(3).setMaxWidth(90);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;" +
                "[light]border:1,1,1,1,darken(@background,10%);" +
                "[dark]border:1,1,1,1,lighten(@background,10%)");
        add(scroll, "grow, push");
        add(criarFormulario(), "growy, pushy, aligny top");

        carregarTabela();
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new MigLayout("wrap, fillx, insets 20", "[grow]"));
        form.putClientProperty(FlatClientProperties.STYLE,
                "arc:14;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        JLabel lbForm = new JLabel("Dados do Curso");
        lbForm.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        txtNome      = new JTextField();
        txtDescricao = new JTextArea(4, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        spnCarga     = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));

        txtNome.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nome do curso");

        btnSalvar = new JButton("Salvar");
        btnSalvar.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e -> salvar());

        btnExcluir = new JButton("Excluir");
        btnExcluir.putClientProperty(FlatClientProperties.STYLE,
                "foreground:#e74c3c;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(e -> excluir());

        form.add(lbForm, "gapy 0 12");
        form.add(new JLabel("Nome"),            "gapy 4");
        form.add(txtNome, "growx");
        form.add(new JLabel("Descrição"),       "gapy 4");
        form.add(new JScrollPane(txtDescricao), "growx, h 90!");
        form.add(new JLabel("Carga horária (h)"), "gapy 4");
        form.add(spnCarga, "growx");
        form.add(btnSalvar,  "growx, gapy 14 4");
        form.add(btnExcluir, "growx");

        return form;
    }

    private void carregarTabela() { carregarTabela(null); }

    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);
        for (String[] linha : dao.listar()) {
            if (filtro == null || filtro.isBlank()
                    || linha[1].toLowerCase().contains(filtro.toLowerCase())) {
                modelo.addRow(linha);
            }
        }
    }

    private void filtrar(String texto) { carregarTabela(texto); }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = Integer.parseInt(modelo.getValueAt(row, 0).toString());
        txtNome.setText(modelo.getValueAt(row, 1).toString());
        txtDescricao.setText(modelo.getValueAt(row, 2).toString());
        spnCarga.setValue(Integer.parseInt(modelo.getValueAt(row, 3).toString()));
        btnExcluir.setEnabled(true);
    }

    private void limparFormulario() {
        idSelecionado = -1;
        txtNome.setText("");
        txtDescricao.setText("");
        spnCarga.setValue(0);
        tabela.clearSelection();
        btnExcluir.setEnabled(false);
        txtNome.requestFocus();
    }

    private void salvar() {
        String nome     = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();
        int    carga    = (int) spnCarga.getValue();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do curso é obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = (idSelecionado == -1)
                ? dao.inserir(nome, descricao, carga)
                : dao.atualizar(idSelecionado, nome, descricao, carga);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Curso salvo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar curso.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado < 0) return;
        int resp = JOptionPane.showConfirmDialog(this,
                "Excluir este curso?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Curso excluído.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

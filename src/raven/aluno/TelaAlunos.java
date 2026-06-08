package raven.aluno;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.db.AlunoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaAlunos extends JPanel {

    private final String perfil;
    private final AlunoDAO dao = new AlunoDAO();

    private JTable          tabela;
    private DefaultTableModel modelo;

    private JTextField txtNome;
    private JTextField txtEmail;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JButton    btnSalvar;
    private JButton    btnNovo;
    private JButton    btnExcluir;

    private int idSelecionado = -1;

    public TelaAlunos(String perfil) {
        this.perfil = perfil;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 24", "[grow][320!]", "[][][grow]"));
        setOpaque(false);

        // Título
        JLabel titulo = new JLabel("Gerenciar Alunos");
        titulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +14");
        add(titulo, "span 2, wrap");

        // Barra de busca + botão novo
        JTextField txtBusca = new JTextField();
        txtBusca.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar por nome ou e-mail...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });

        btnNovo = new JButton("+ Novo Aluno");
        btnNovo.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,8%);" +
                "[dark]background:lighten(@background,8%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:8");
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> limparFormulario());

        add(txtBusca, "growx");
        add(btnNovo,  "wrap");

        // Tabela
        String[] colunas = {"ID", "Nome", "E-mail", "CPF", "Telefone"};
        modelo  = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela  = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
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

        // Formulário lateral
        add(criarFormulario(), "growy, pushy, aligny top");

        carregarTabela();
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new MigLayout("wrap, fillx, insets 20", "[grow]"));
        form.putClientProperty(FlatClientProperties.STYLE,
                "arc:14;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        JLabel lbForm = new JLabel("Dados do Aluno");
        lbForm.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        txtNome     = new JTextField();
        txtEmail    = new JTextField();
        txtCpf      = new JTextField();
        txtTelefone = new JTextField();

        txtNome.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,     "Nome completo");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,    "E-mail");
        txtCpf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,      "000.000.000-00");
        txtTelefone.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "(00) 00000-0000");

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
        form.add(new JLabel("Nome"),     "gapy 4");
        form.add(txtNome,     "growx");
        form.add(new JLabel("E-mail"),   "gapy 4");
        form.add(txtEmail,    "growx");
        form.add(new JLabel("CPF"),      "gapy 4");
        form.add(txtCpf,      "growx");
        form.add(new JLabel("Telefone"), "gapy 4");
        form.add(txtTelefone, "growx");
        form.add(btnSalvar,   "growx, gapy 14 4");
        form.add(btnExcluir,  "growx");

        return form;
    }

    private void carregarTabela() {
        carregarTabela(null);
    }

    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);
        List<String[]> lista = dao.listar();
        for (String[] linha : lista) {
            if (filtro == null || filtro.isBlank()
                    || linha[1].toLowerCase().contains(filtro.toLowerCase())
                    || linha[2].toLowerCase().contains(filtro.toLowerCase())) {
                modelo.addRow(linha);
            }
        }
    }

    private void filtrar(String texto) {
        carregarTabela(texto);
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = Integer.parseInt(modelo.getValueAt(row, 0).toString());
        txtNome.setText(modelo.getValueAt(row, 1).toString());
        txtEmail.setText(modelo.getValueAt(row, 2).toString());
        txtCpf.setText(modelo.getValueAt(row, 3).toString());
        txtTelefone.setText(modelo.getValueAt(row, 4).toString());
        btnExcluir.setEnabled(true);
    }

    private void limparFormulario() {
        idSelecionado = -1;
        txtNome.setText("");
        txtEmail.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        tabela.clearSelection();
        btnExcluir.setEnabled(false);
        txtNome.requestFocus();
    }

    private void salvar() {
        String nome     = txtNome.getText().trim();
        String email    = txtEmail.getText().trim();
        String cpf      = txtCpf.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, e-mail e CPF são obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok;
        if (idSelecionado == -1) {
            ok = dao.inserir(nome, email, cpf, telefone);
        } else {
            ok = dao.atualizar(idSelecionado, nome, email, cpf, telefone);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Aluno salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar. Verifique se CPF ou e-mail já estão cadastrados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado < 0) return;
        int resp = JOptionPane.showConfirmDialog(this,
                "Deseja excluir este aluno?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Aluno excluído.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

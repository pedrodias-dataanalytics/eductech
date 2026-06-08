package raven.matricula;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.db.MatriculaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaMatriculas extends JPanel {

    private final String perfil;
    private final MatriculaDAO dao = new MatriculaDAO();

    private JTable            tabela;
    private DefaultTableModel modelo;

    private JComboBox<String> cmbAluno;
    private JComboBox<String> cmbCurso;
    private JComboBox<String> cmbStatus;
    private JTextField        txtData;
    private JButton           btnSalvar;
    private JButton           btnNovo;
    private JButton           btnExcluir;

    private List<String[]> listaAlunos;
    private List<String[]> listaCursos;
    private int idSelecionado = -1;

    public TelaMatriculas(String perfil) {
        this.perfil = perfil;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 24", "[grow][320!]", "[][][grow]"));
        setOpaque(false);

        JLabel titulo = new JLabel("Gerenciar Matrículas");
        titulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +14");
        add(titulo, "span 2, wrap");

        JTextField txtBusca = new JTextField();
        txtBusca.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar por aluno ou curso...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });

        btnNovo = new JButton("+ Nova Matrícula");
        btnNovo.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,8%);" +
                "[dark]background:lighten(@background,8%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:8");
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> limparFormulario());

        add(txtBusca, "growx");
        add(btnNovo,  "wrap");

        String[] colunas = {"ID", "Aluno", "Curso", "Data Início", "Status"};
        modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(3).setMaxWidth(120);
        tabela.getColumnModel().getColumn(4).setMaxWidth(110);
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

        JLabel lbForm = new JLabel("Dados da Matrícula");
        lbForm.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        listaAlunos = dao.listarAlunos();
        listaCursos = dao.listarCursos();

        cmbAluno  = new JComboBox<>();
        cmbCurso  = new JComboBox<>();
        cmbStatus = new JComboBox<>(new String[]{"ATIVA", "CONCLUIDA", "CANCELADA"});
        txtData   = new JTextField(LocalDate.now().toString());

        for (String[] a : listaAlunos) cmbAluno.addItem(a[1]);
        for (String[] c : listaCursos) cmbCurso.addItem(c[1]);

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

        form.add(lbForm,            "gapy 0 12");
        form.add(new JLabel("Aluno"),       "gapy 4");
        form.add(cmbAluno,          "growx");
        form.add(new JLabel("Curso"),       "gapy 4");
        form.add(cmbCurso,          "growx");
        form.add(new JLabel("Data início (AAAA-MM-DD)"), "gapy 4");
        form.add(txtData,           "growx");
        form.add(new JLabel("Status"),      "gapy 4");
        form.add(cmbStatus,         "growx");
        form.add(btnSalvar,         "growx, gapy 14 4");
        form.add(btnExcluir,        "growx");

        return form;
    }

    private void carregarTabela() { carregarTabela(null); }

    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);
        for (String[] linha : dao.listar()) {
            if (filtro == null || filtro.isBlank()
                    || linha[1].toLowerCase().contains(filtro.toLowerCase())
                    || linha[2].toLowerCase().contains(filtro.toLowerCase())) {
                modelo.addRow(linha);
            }
        }
    }

    private void filtrar(String texto) { carregarTabela(texto); }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = Integer.parseInt(modelo.getValueAt(row, 0).toString());
        String alunoNome = modelo.getValueAt(row, 1).toString();
        String cursoNome = modelo.getValueAt(row, 2).toString();
        txtData.setText(modelo.getValueAt(row, 3).toString());
        cmbStatus.setSelectedItem(modelo.getValueAt(row, 4).toString());

        for (int i = 0; i < cmbAluno.getItemCount(); i++) {
            if (cmbAluno.getItemAt(i).equals(alunoNome)) { cmbAluno.setSelectedIndex(i); break; }
        }
        for (int i = 0; i < cmbCurso.getItemCount(); i++) {
            if (cmbCurso.getItemAt(i).equals(cursoNome)) { cmbCurso.setSelectedIndex(i); break; }
        }
        btnExcluir.setEnabled(true);
    }

    private void limparFormulario() {
        idSelecionado = -1;
        cmbAluno.setSelectedIndex(0);
        cmbCurso.setSelectedIndex(0);
        txtData.setText(LocalDate.now().toString());
        cmbStatus.setSelectedIndex(0);
        tabela.clearSelection();
        btnExcluir.setEnabled(false);
    }

    private void salvar() {
        if (listaAlunos.isEmpty() || listaCursos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre alunos e cursos antes de criar matrículas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    alunoIdx = cmbAluno.getSelectedIndex();
        int    cursoIdx = cmbCurso.getSelectedIndex();
        String data     = txtData.getText().trim();
        String status   = cmbStatus.getSelectedItem().toString();

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a data de início.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok;
        if (idSelecionado == -1) {
            int alunoId = Integer.parseInt(listaAlunos.get(alunoIdx)[0]);
            int cursoId = Integer.parseInt(listaCursos.get(cursoIdx)[0]);
            ok = dao.inserir(alunoId, cursoId, data);
        } else {
            ok = dao.atualizarStatus(idSelecionado, status);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Matrícula salva!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar matrícula.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado < 0) return;
        int resp = JOptionPane.showConfirmDialog(this,
                "Excluir esta matrícula?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Matrícula excluída.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

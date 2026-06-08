package raven.matricula;

import net.miginfocom.swing.MigLayout;
import raven.aluno.TelaAlunos;
import raven.db.MatriculaDAO;
import raven.main.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaMatriculas extends JPanel {

    private final String perfil;
    private final MatriculaDAO dao = new MatriculaDAO();
    private JTable tabela;
    private DefaultTableModel modelo;
    private JComboBox<String> cmbAluno, cmbCurso, cmbStatus;
    private JTextField txtData;
    private JButton btnSalvar, btnExcluir;
    private List<String[]> listaAlunos, listaCursos;
    private int idSelecionado = -1;

    private static final Color COR = new Color(245, 158, 11);

    public TelaMatriculas(String perfil) {
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
        JLabel lbTitulo = new JLabel("Gerenciar Matrículas");
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 26));
        lbTitulo.setForeground(Dashboard.COR_TEXTO);
        JLabel lbSub = new JLabel("Vincule alunos a cursos e gerencie as matrículas.");
        lbSub.setFont(new Font("Dialog", Font.PLAIN, 13));
        lbSub.setForeground(Dashboard.COR_TEXTO_MUTED);
        tituloPanel.add(lbTitulo);
        tituloPanel.add(Box.createVerticalStrut(4));
        tituloPanel.add(lbSub);
        JButton btnNovo = TelaAlunos.criarBotaoPrimario("+ Nova Matrícula", COR);
        btnNovo.addActionListener(e -> limparFormulario());
        header.add(tituloPanel, "grow");
        header.add(btnNovo);
        add(header, BorderLayout.NORTH);

        JPanel corpo = new JPanel(new MigLayout("fill, insets 0 36 36 36, gap 20", "[grow][320!]", "[grow]"));
        corpo.setBackground(Dashboard.COR_FUNDO);

        JPanel tabelaPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        tabelaPanel.setOpaque(false);
        JTextField txtBusca = TelaAlunos.criarCampoBusca("Buscar por aluno ou curso...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });
        tabelaPanel.add(txtBusca, "growx, gapy 0 12, wrap");

        String[] colunas = {"ID", "Aluno", "Curso", "Data Início", "Status"};
        modelo = new DefaultTableModel(colunas, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tabela = TelaAlunos.criarTabela(modelo);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(3).setMaxWidth(120);
        tabela.getColumnModel().getColumn(4).setMaxWidth(110);
        tabela.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) preencherFormulario(); });
        tabelaPanel.add(TelaAlunos.criarScrollPane(tabela), "grow, push");
        corpo.add(tabelaPanel, "grow, push");
        corpo.add(criarFormulario(), "growy, pushy, aligny top");
        add(corpo, BorderLayout.CENTER);
        carregarTabela();
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel(new MigLayout("wrap, fillx, insets 24", "[grow]"));
        form.setBackground(Dashboard.COR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, COR),
                BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1)));

        JLabel lbForm = new JLabel("Dados da Matrícula");
        lbForm.setFont(new Font("Dialog", Font.BOLD, 15));
        lbForm.setForeground(Dashboard.COR_TEXTO);

        listaAlunos = dao.listarAlunos();
        listaCursos = dao.listarCursos();

        cmbAluno  = criarCombo(); for (String[] a : listaAlunos) cmbAluno.addItem(a[1]);
        cmbCurso  = criarCombo(); for (String[] c : listaCursos) cmbCurso.addItem(c[1]);
        cmbStatus = criarCombo(new String[]{"ATIVA", "CONCLUIDA", "CANCELADA"});
        txtData   = TelaAlunos.criarCampo(LocalDate.now().toString());
        txtData.setText(LocalDate.now().toString());

        btnSalvar  = TelaAlunos.criarBotaoPrimario("Salvar", COR);
        btnExcluir = TelaAlunos.criarBotaoPerigo("Excluir");
        btnExcluir.setEnabled(false);
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());

        form.add(lbForm, "gapy 0 16");
        form.add(TelaAlunos.criarLabel("Aluno"),  "gapy 4"); form.add(cmbAluno,  "growx");
        form.add(TelaAlunos.criarLabel("Curso"),  "gapy 8"); form.add(cmbCurso,  "growx");
        form.add(TelaAlunos.criarLabel("Data início (AAAA-MM-DD)"), "gapy 8"); form.add(txtData, "growx");
        form.add(TelaAlunos.criarLabel("Status"), "gapy 8"); form.add(cmbStatus, "growx");
        form.add(btnSalvar,  "growx, gapy 16 6");
        form.add(btnExcluir, "growx");
        return form;
    }

    private JComboBox<String> criarCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(new Color(25, 25, 40));
        cb.setForeground(Dashboard.COR_TEXTO);
        return cb;
    }

    private JComboBox<String> criarCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(new Color(25, 25, 40));
        cb.setForeground(Dashboard.COR_TEXTO);
        return cb;
    }

    private void carregarTabela() { carregarTabela(null); }
    private void carregarTabela(String filtro) {
        modelo.setRowCount(0);
        for (String[] l : dao.listar())
            if (filtro == null || filtro.isBlank()
                    || l[1].toLowerCase().contains(filtro.toLowerCase())
                    || l[2].toLowerCase().contains(filtro.toLowerCase()))
                modelo.addRow(l);
    }
    private void filtrar(String t) { carregarTabela(t); }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = Integer.parseInt(modelo.getValueAt(row, 0).toString());
        String an = modelo.getValueAt(row, 1).toString(), cn = modelo.getValueAt(row, 2).toString();
        txtData.setText(modelo.getValueAt(row, 3).toString());
        cmbStatus.setSelectedItem(modelo.getValueAt(row, 4).toString());
        for (int i = 0; i < cmbAluno.getItemCount(); i++) if (cmbAluno.getItemAt(i).equals(an)) { cmbAluno.setSelectedIndex(i); break; }
        for (int i = 0; i < cmbCurso.getItemCount(); i++) if (cmbCurso.getItemAt(i).equals(cn)) { cmbCurso.setSelectedIndex(i); break; }
        btnExcluir.setEnabled(true);
    }

    private void limparFormulario() {
        idSelecionado = -1;
        if (cmbAluno.getItemCount() > 0) cmbAluno.setSelectedIndex(0);
        if (cmbCurso.getItemCount() > 0) cmbCurso.setSelectedIndex(0);
        txtData.setText(LocalDate.now().toString());
        cmbStatus.setSelectedIndex(0);
        tabela.clearSelection(); btnExcluir.setEnabled(false);
    }

    private void salvar() {
        if (listaAlunos.isEmpty() || listaCursos.isEmpty()) { JOptionPane.showMessageDialog(this, "Cadastre alunos e cursos primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        boolean ok;
        if (idSelecionado == -1) {
            int ai = Integer.parseInt(listaAlunos.get(cmbAluno.getSelectedIndex())[0]);
            int ci = Integer.parseInt(listaCursos.get(cmbCurso.getSelectedIndex())[0]);
            ok = dao.inserir(ai, ci, txtData.getText().trim());
        } else {
            ok = dao.atualizarStatus(idSelecionado, cmbStatus.getSelectedItem().toString());
        }
        if (ok) { JOptionPane.showMessageDialog(this, "Matrícula salva!", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
        else    { JOptionPane.showMessageDialog(this, "Erro ao salvar.", "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void excluir() {
        if (idSelecionado < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Excluir esta matrícula?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) { JOptionPane.showMessageDialog(this, "Matrícula excluída.", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
            else JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

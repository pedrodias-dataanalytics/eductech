package raven.usuario;

import net.miginfocom.swing.MigLayout;
import raven.aluno.TelaAlunos;
import raven.db.UsuarioDAO;
import raven.main.Dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TelaUsuarios extends JPanel {

    private final UsuarioDAO dao = new UsuarioDAO();
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTextField txtNome, txtLogin, txtEmail;
    private JComboBox<String> cmbPerfil;
    private JPasswordField txtSenha;
    private JButton btnSalvar, btnExcluir, btnAlterarSenha;
    private int idSelecionado = -1;

    private static final Color COR = new Color(236, 72, 153);

    public TelaUsuarios() {
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
        JLabel lbTitulo = new JLabel("Gerenciar Usuários");
        lbTitulo.setFont(new Font("Dialog", Font.BOLD, 26));
        lbTitulo.setForeground(Dashboard.COR_TEXTO);
        JLabel lbSub = new JLabel("Controle os acessos e perfis de usuários do sistema.");
        lbSub.setFont(new Font("Dialog", Font.PLAIN, 13));
        lbSub.setForeground(Dashboard.COR_TEXTO_MUTED);
        tituloPanel.add(lbTitulo);
        tituloPanel.add(Box.createVerticalStrut(4));
        tituloPanel.add(lbSub);
        JButton btnNovo = TelaAlunos.criarBotaoPrimario("+ Novo Usuário", COR);
        btnNovo.addActionListener(e -> limparFormulario());
        header.add(tituloPanel, "grow");
        header.add(btnNovo);
        add(header, BorderLayout.NORTH);

        JPanel corpo = new JPanel(new MigLayout("fill, insets 0 36 36 36, gap 20", "[grow][340!]", "[grow]"));
        corpo.setBackground(Dashboard.COR_FUNDO);

        JPanel tabelaPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        tabelaPanel.setOpaque(false);
        JTextField txtBusca = TelaAlunos.criarCampoBusca("Buscar usuário...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });
        tabelaPanel.add(txtBusca, "growx, gapy 0 12, wrap");

        String[] colunas = {"ID", "Nome", "Login", "E-mail", "Perfil", "Ativo"};
        modelo = new DefaultTableModel(colunas, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tabela = TelaAlunos.criarTabela(modelo);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(4).setMaxWidth(80);
        tabela.getColumnModel().getColumn(5).setMaxWidth(60);
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

        JLabel lbForm = new JLabel("Dados do Usuário");
        lbForm.setFont(new Font("Dialog", Font.BOLD, 15));
        lbForm.setForeground(Dashboard.COR_TEXTO);

        txtNome   = TelaAlunos.criarCampo("Nome completo");
        txtLogin  = TelaAlunos.criarCampo("Login");
        txtEmail  = TelaAlunos.criarCampo("E-mail");
        cmbPerfil = new JComboBox<>(new String[]{"ALUNO", "ADMIN"});
        cmbPerfil.setBackground(new Color(25, 25, 40));
        cmbPerfil.setForeground(Dashboard.COR_TEXTO);
        txtSenha  = new JPasswordField();
        txtSenha.setBackground(new Color(25, 25, 40));
        txtSenha.setForeground(Dashboard.COR_TEXTO);
        txtSenha.setCaretColor(Dashboard.COR_TEXTO);
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Dashboard.COR_BORDA, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        btnSalvar       = TelaAlunos.criarBotaoPrimario("Salvar", COR);
        btnAlterarSenha = TelaAlunos.criarBotaoPrimario("Alterar senha", new Color(99, 102, 241));
        btnExcluir      = TelaAlunos.criarBotaoPerigo("Desativar");
        btnAlterarSenha.setEnabled(false);
        btnExcluir.setEnabled(false);

        btnSalvar.addActionListener(e -> salvar());
        btnAlterarSenha.addActionListener(e -> alterarSenha());
        btnExcluir.addActionListener(e -> excluir());

        form.add(lbForm, "gapy 0 16");
        form.add(TelaAlunos.criarLabel("Nome"),   "gapy 4");  form.add(txtNome,   "growx");
        form.add(TelaAlunos.criarLabel("Login"),  "gapy 8");  form.add(txtLogin,  "growx");
        form.add(TelaAlunos.criarLabel("E-mail"), "gapy 8");  form.add(txtEmail,  "growx");
        form.add(TelaAlunos.criarLabel("Perfil"), "gapy 8");  form.add(cmbPerfil, "growx");
        form.add(TelaAlunos.criarLabel("Senha (novo usuário)"), "gapy 8"); form.add(txtSenha, "growx");
        form.add(btnSalvar,       "growx, gapy 16 6");
        form.add(btnAlterarSenha, "growx, gapy 0 6");
        form.add(btnExcluir,      "growx");
        return form;
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
        txtNome.setText(modelo.getValueAt(row, 1).toString());
        txtLogin.setText(modelo.getValueAt(row, 2).toString());
        txtEmail.setText(modelo.getValueAt(row, 3).toString());
        cmbPerfil.setSelectedItem(modelo.getValueAt(row, 4).toString());
        txtSenha.setText("");
        btnExcluir.setEnabled(true);
        btnAlterarSenha.setEnabled(true);
    }

    private void limparFormulario() {
        idSelecionado = -1;
        txtNome.setText(""); txtLogin.setText(""); txtEmail.setText(""); txtSenha.setText("");
        cmbPerfil.setSelectedIndex(0);
        tabela.clearSelection(); btnExcluir.setEnabled(false); btnAlterarSenha.setEnabled(false);
        txtNome.requestFocus();
    }

    private void salvar() {
        String nome = txtNome.getText().trim(), login = txtLogin.getText().trim(),
               email = txtEmail.getText().trim(), perfil = cmbPerfil.getSelectedItem().toString(),
               senha = new String(txtSenha.getPassword());
        if (nome.isEmpty() || login.isEmpty() || email.isEmpty()) { JOptionPane.showMessageDialog(this, "Nome, login e e-mail obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        boolean ok;
        if (idSelecionado == -1) {
            if (senha.length() < 6) { JOptionPane.showMessageDialog(this, "Senha mínimo 6 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
            ok = dao.cadastrar(nome, login, email, senha, perfil);
        } else {
            ok = dao.atualizar(idSelecionado, nome, login, email, perfil);
        }
        if (ok) { JOptionPane.showMessageDialog(this, "Usuário salvo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
        else    { JOptionPane.showMessageDialog(this, "Erro. Login ou e-mail já em uso.", "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void alterarSenha() {
        String nova = JOptionPane.showInputDialog(this, "Nova senha (mínimo 6 caracteres):", "Alterar Senha", JOptionPane.PLAIN_MESSAGE);
        if (nova != null && nova.length() >= 6) {
            if (dao.alterarSenha(idSelecionado, nova)) JOptionPane.showMessageDialog(this, "Senha alterada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Erro ao alterar senha.", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (nova != null) JOptionPane.showMessageDialog(this, "Senha muito curta.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private void excluir() {
        if (idSelecionado < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Desativar este usuário?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) { JOptionPane.showMessageDialog(this, "Usuário desativado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE); limparFormulario(); carregarTabela(); }
            else JOptionPane.showMessageDialog(this, "Erro ao desativar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

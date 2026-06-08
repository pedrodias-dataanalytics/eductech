package raven.usuario;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.db.UsuarioDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaUsuarios extends JPanel {

    private final UsuarioDAO dao = new UsuarioDAO();

    private JTable            tabela;
    private DefaultTableModel modelo;

    private JTextField     txtNome;
    private JTextField     txtLogin;
    private JTextField     txtEmail;
    private JComboBox<String> cmbPerfil;
    private JPasswordField txtSenha;
    private JButton        btnSalvar;
    private JButton        btnNovo;
    private JButton        btnExcluir;
    private JButton        btnAlterarSenha;

    private int idSelecionado = -1;

    public TelaUsuarios() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 24", "[grow][340!]", "[][][grow]"));
        setOpaque(false);

        JLabel titulo = new JLabel("Gerenciar Usuários");
        titulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +14");
        add(titulo, "span 2, wrap");

        JTextField txtBusca = new JTextField();
        txtBusca.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar usuário...");
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(txtBusca.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(txtBusca.getText()); }
        });

        btnNovo = new JButton("+ Novo Usuário");
        btnNovo.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,8%);" +
                "[dark]background:lighten(@background,8%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0;arc:8");
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> limparFormulario());

        add(txtBusca, "growx");
        add(btnNovo,  "wrap");

        String[] colunas = {"ID", "Nome", "Login", "E-mail", "Perfil", "Ativo"};
        modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(28);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(4).setMaxWidth(80);
        tabela.getColumnModel().getColumn(5).setMaxWidth(60);
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

        JLabel lbForm = new JLabel("Dados do Usuário");
        lbForm.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        txtNome   = new JTextField();
        txtLogin  = new JTextField();
        txtEmail  = new JTextField();
        cmbPerfil = new JComboBox<>(new String[]{"ALUNO", "ADMIN"});
        txtSenha  = new JPasswordField();
        txtSenha.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");

        txtNome.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,  "Nome completo");
        txtLogin.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Login");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "E-mail");
        txtSenha.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Deixe em branco para não alterar");

        btnSalvar = new JButton("Salvar");
        btnSalvar.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e -> salvar());

        btnAlterarSenha = new JButton("Alterar senha");
        btnAlterarSenha.putClientProperty(FlatClientProperties.STYLE,
                "[light]background:darken(@background,6%);" +
                "[dark]background:lighten(@background,6%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");
        btnAlterarSenha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAlterarSenha.setEnabled(false);
        btnAlterarSenha.addActionListener(e -> alterarSenha());

        btnExcluir = new JButton("Desativar");
        btnExcluir.putClientProperty(FlatClientProperties.STYLE,
                "foreground:#e74c3c;" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +
                "borderWidth:0;focusWidth:0;innerFocusWidth:0");
        btnExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(e -> excluir());

        form.add(lbForm,               "gapy 0 12");
        form.add(new JLabel("Nome"),          "gapy 4");
        form.add(txtNome,              "growx");
        form.add(new JLabel("Login"),         "gapy 4");
        form.add(txtLogin,             "growx");
        form.add(new JLabel("E-mail"),        "gapy 4");
        form.add(txtEmail,             "growx");
        form.add(new JLabel("Perfil"),        "gapy 4");
        form.add(cmbPerfil,            "growx");
        form.add(new JLabel("Senha (novo usuário)"), "gapy 4");
        form.add(txtSenha,             "growx");
        form.add(btnSalvar,            "growx, gapy 14 4");
        form.add(btnAlterarSenha,      "growx, gapy 0 4");
        form.add(btnExcluir,           "growx");

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
        txtNome.setText(""); txtLogin.setText("");
        txtEmail.setText(""); txtSenha.setText("");
        cmbPerfil.setSelectedIndex(0);
        tabela.clearSelection();
        btnExcluir.setEnabled(false);
        btnAlterarSenha.setEnabled(false);
        txtNome.requestFocus();
    }

    private void salvar() {
        String nome   = txtNome.getText().trim();
        String login  = txtLogin.getText().trim();
        String email  = txtEmail.getText().trim();
        String perfil = cmbPerfil.getSelectedItem().toString();
        String senha  = new String(txtSenha.getPassword());

        if (nome.isEmpty() || login.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, login e e-mail são obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok;
        if (idSelecionado == -1) {
            if (senha.length() < 6) {
                JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 6 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ok = dao.cadastrar(nome, login, email, senha, perfil);
        } else {
            ok = dao.atualizar(idSelecionado, nome, login, email, perfil);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Usuário salvo!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar. Login ou e-mail pode já estar em uso.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarSenha() {
        if (idSelecionado < 0) return;
        String nova = JOptionPane.showInputDialog(this, "Nova senha (mínimo 6 caracteres):", "Alterar Senha", JOptionPane.PLAIN_MESSAGE);
        if (nova != null && nova.length() >= 6) {
            if (dao.alterarSenha(idSelecionado, nova)) {
                JOptionPane.showMessageDialog(this, "Senha alterada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao alterar senha.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else if (nova != null) {
            JOptionPane.showMessageDialog(this, "Senha muito curta (mínimo 6 caracteres).", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado < 0) return;
        int resp = JOptionPane.showConfirmDialog(this,
                "Desativar este usuário?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            if (dao.excluir(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Usuário desativado.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao desativar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

package raven.db;

import raven.util.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ---------------------------------------------------------------
    //  Autenticação
    // ---------------------------------------------------------------
    /** Retorna {nome, perfil} se as credenciais forem válidas, null caso contrário. */
    public String[] autenticar(String login, String senha) {
        String sql = "SELECT nome, perfil FROM usuarios WHERE (login = ? OR email = ?) AND senha = ? AND ativo = 1";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String hash = HashUtil.sha256(senha);
            ps.setString(1, login);
            ps.setString(2, login);
            ps.setString(3, hash);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{ rs.getString("nome"), rs.getString("perfil") };
            }
        } catch (Exception e) {
            System.err.println("Erro ao autenticar: " + e.getMessage());
        }
        return null;
    }

    // ---------------------------------------------------------------
    //  Cadastro
    // ---------------------------------------------------------------
    /** Retorna true se o cadastro foi bem-sucedido. */
    public boolean cadastrar(String nome, String login, String email, String senha, String perfil) {
        String sql = "INSERT INTO usuarios (nome, login, email, senha, perfil) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setString(2, login);
            ps.setString(3, email);
            ps.setString(4, HashUtil.sha256(senha));
            ps.setString(5, perfil);

            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Login ou e-mail já cadastrado.");
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    //  Listagem
    // ---------------------------------------------------------------
    /** Retorna lista de usuários: {id, nome, login, email, perfil, ativo}. */
    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, nome, login, email, perfil, ativo FROM usuarios ORDER BY nome";
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getString("login"),
                    rs.getString("email"),
                    rs.getString("perfil"),
                    rs.getInt("ativo") == 1 ? "Sim" : "Não"
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return lista;
    }

    // ---------------------------------------------------------------
    //  Atualização
    // ---------------------------------------------------------------
    public boolean atualizar(int id, String nome, String login, String email, String perfil) {
        String sql = "UPDATE usuarios SET nome=?, login=?, email=?, perfil=? WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, login);
            ps.setString(3, email);
            ps.setString(4, perfil);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }

    public boolean alterarSenha(int id, String novaSenha) {
        String sql = "UPDATE usuarios SET senha=? WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, HashUtil.sha256(novaSenha));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao alterar senha: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    //  Exclusão lógica
    // ---------------------------------------------------------------
    public boolean excluir(int id) {
        String sql = "UPDATE usuarios SET ativo=0 WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------
    //  Verificação de duplicidade
    // ---------------------------------------------------------------
    public boolean loginOuEmailExiste(String login, String email) {
        String sql = "SELECT id FROM usuarios WHERE login=? OR email=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
}

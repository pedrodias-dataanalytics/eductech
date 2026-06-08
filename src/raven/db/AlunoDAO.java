package raven.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, nome, email, cpf, telefone FROM alunos WHERE ativo=1 ORDER BY nome";
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("cpf"),
                    rs.getString("telefone") == null ? "" : rs.getString("telefone")
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar alunos: " + e.getMessage());
        }
        return lista;
    }

    public boolean inserir(String nome, String email, String cpf, String telefone) {
        String sql = "INSERT INTO alunos (nome, email, cpf, telefone) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, cpf);
            ps.setString(4, telefone);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao inserir aluno: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(int id, String nome, String email, String cpf, String telefone) {
        String sql = "UPDATE alunos SET nome=?, email=?, cpf=?, telefone=? WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, cpf);
            ps.setString(4, telefone);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar aluno: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "UPDATE alunos SET ativo=0 WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir aluno: " + e.getMessage());
            return false;
        }
    }
}

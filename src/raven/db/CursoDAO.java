package raven.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, carga_horaria FROM cursos WHERE ativo=1 ORDER BY nome";
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("id"),
                    rs.getString("nome"),
                    rs.getString("descricao") == null ? "" : rs.getString("descricao"),
                    rs.getString("carga_horaria")
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar cursos: " + e.getMessage());
        }
        return lista;
    }

    public boolean inserir(String nome, String descricao, int cargaHoraria) {
        String sql = "INSERT INTO cursos (nome, descricao, carga_horaria) VALUES (?, ?, ?)";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, descricao);
            ps.setInt(3, cargaHoraria);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao inserir curso: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(int id, String nome, String descricao, int cargaHoraria) {
        String sql = "UPDATE cursos SET nome=?, descricao=?, carga_horaria=? WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, descricao);
            ps.setInt(3, cargaHoraria);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar curso: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "UPDATE cursos SET ativo=0 WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir curso: " + e.getMessage());
            return false;
        }
    }
}

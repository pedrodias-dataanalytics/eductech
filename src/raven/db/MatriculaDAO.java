package raven.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatriculaDAO {

    public List<String[]> listar() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
                SELECT m.id, a.nome AS aluno, c.nome AS curso,
                       m.data_inicio, m.status
                FROM matriculas m
                JOIN alunos  a ON a.id = m.aluno_id
                JOIN cursos  c ON c.id = m.curso_id
                WHERE a.ativo = 1 AND c.ativo = 1
                ORDER BY m.id DESC
                """;
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("id"),
                    rs.getString("aluno"),
                    rs.getString("curso"),
                    rs.getString("data_inicio"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar matrículas: " + e.getMessage());
        }
        return lista;
    }

    public boolean inserir(int alunoId, int cursoId, String dataInicio) {
        String sql = "INSERT INTO matriculas (aluno_id, curso_id, data_inicio) VALUES (?, ?, ?)";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, alunoId);
            ps.setInt(2, cursoId);
            ps.setString(3, dataInicio);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao inserir matrícula: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarStatus(int id, String status) {
        String sql = "UPDATE matriculas SET status=? WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar matrícula: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM matriculas WHERE id=?";
        try (Connection con = Conexao.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir matrícula: " + e.getMessage());
            return false;
        }
    }

    // Retorna {id, nome} de todos os alunos ativos para o combo
    public List<String[]> listarAlunos() {
        List<String[]> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nome FROM alunos WHERE ativo=1 ORDER BY nome")) {
            while (rs.next()) lista.add(new String[]{ rs.getString("id"), rs.getString("nome") });
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return lista;
    }

    // Retorna {id, nome} de todos os cursos ativos para o combo
    public List<String[]> listarCursos() {
        List<String[]> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nome FROM cursos WHERE ativo=1 ORDER BY nome")) {
            while (rs.next()) lista.add(new String[]{ rs.getString("id"), rs.getString("nome") });
        } catch (Exception e) { System.err.println(e.getMessage()); }
        return lista;
    }
}

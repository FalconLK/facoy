package br.gov.ba.mam.banco.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import br.gov.ba.mam.banco.IF_Persistencia;
import br.gov.ba.mam.beans.Artista;
import br.gov.ba.mam.beans.Categoria;
import br.gov.ba.mam.beans.Endereco;
import br.gov.ba.mam.beans.Obra;

public class GerenteMySQL implements IF_Persistencia {

    public GerenteMySQL() {
    }

    private Integer nextKeyArtista() {
        Connection conn = null;
        PreparedStatement ps = null;
        int valor = 0;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "select max(artista.numeroinscricao) from artista";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                valor = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }
        return valor + 1;
    }

    private void close(Connection conn, PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * @throws Exception  */
    private void alterarCategoria(Categoria cat) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "UPDATE categoria SET nome_categoria = ? where id_categoria = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cat.getNome());
            ps.setInt(2, cat.getCodigo());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            close(conn, ps);
        }
    }

    private void salvarCategoria(Categoria cat) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "insert into categoria VALUES (?,?)";
            ps = conn.prepareStatement(sql);
            ps.setNull(1, Types.INTEGER);
            ps.setString(2, cat.getNome());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            close(conn, ps);
        }
    }

    /** Cria uma categoria de acordo com o ResultSet passado */
    private Categoria getCategoria(ResultSet rs) throws SQLException {
        Categoria retorno = new Categoria();
        retorno.setCodigo(rs.getInt(1));
        retorno.setNome(rs.getString(2));
        return retorno;
    }

    @Override
    public void addCategoria(Categoria cat) throws Exception {
        if (cat.getCodigo() == null) {
            salvarCategoria(cat);
        } else alterarCategoria(cat);
    }

    @Override
    public void addCategorias(Collection<Categoria> values) {
        for (Categoria categoria : values) {
            try {
                addCategoria(categoria);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delCategoria(Integer codigo) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "delete from categoria where id_categoria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, codigo);
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            close(conn, ps);
        }
    }

    @Override
    public Categoria getCategoria(Integer codigo) {
        Connection conn = null;
        PreparedStatement ps = null;
        Categoria cat = null;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "select * from categoria where id_categoria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                try {
                    cat = getCategoria(rs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }
        return cat;
    }

    @Override
    public List<Categoria> getListaCategorias() {
        Connection conn = null;
        PreparedStatement ps = null;
        HashMap<String, Categoria> lista = new HashMap<String, Categoria>();
        try {
            conn = C3P0Pool.getConnection();
            String sql = "select * from categoria";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    Categoria cat = this.getCategoria(rs);
                    lista.put(cat.getNome(), cat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }
        return new ArrayList<Categoria>(lista.values());
    }

    @Override
    public void addArtista(Artista artista) throws Exception {
        if (artista.getNumeroInscricao() == null) {
            artista.setNumeroInscricao(this.nextKeyArtista());
            salvarArtista(artista);
        } else {
            alterarArtista(artista);
        }
    }

    private void alterarArtista(Artista artista) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "UPDATE artista SET nome = ?,sexo = ?,email = ?,obs = ?,telefone = ? where numeroinscricao = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, artista.getNome());
            ps.setBoolean(2, artista.isSexo());
            ps.setString(3, artista.getEmail());
            ps.setString(4, artista.getObs());
            ps.setString(5, artista.getTelefone());
            ps.setInt(6, artista.getNumeroInscricao());
            ps.executeUpdate();
            alterarEndereco(conn, ps, artista);
            delObras(conn, ps, artista.getNumeroInscricao());
            sql = "insert into obra VALUES (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            for (Obra obra : artista.getListaObras()) {
                salvarObra(conn, ps, obra, artista.getNumeroInscricao());
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            close(conn, ps);
        }
    }

    private void alterarEndereco(Connection conn, PreparedStatement ps, Artista artista) throws Exception {
        String sql = "UPDATE endereco SET logradouro = ?,cep = ?,estado = ?,bairro = ?,cidade = ?,pais = ? where fk_artista = ?";
        ps = conn.prepareStatement(sql);
        ps.setString(1, artista.getEndereco().getLogradouro());
        ps.setString(2, artista.getEndereco().getCep());
        ps.setInt(3, artista.getEndereco().getEstado());
        ps.setString(4, artista.getEndereco().getBairro());
        ps.setString(5, artista.getEndereco().getCidade());
        ps.setString(6, artista.getEndereco().getPais());
        ps.setInt(7, artista.getNumeroInscricao());
        ps.executeUpdate();
    }

    private void salvarArtista(Artista artista) throws Exception {
        System.out.println("GerenteMySQL.salvarArtista()" + artista.toString2());
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "insert into artista VALUES (?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, artista.getNumeroInscricao());
            ps.setString(2, artista.getNome());
            ps.setBoolean(3, artista.isSexo());
            ps.setString(4, artista.getEmail());
            ps.setString(5, artista.getObs());
            ps.setString(6, artista.getTelefone());
            ps.setNull(7, Types.INTEGER);
            ps.executeUpdate();
            salvarEndereco(conn, ps, artista);
            sql = "insert into obra VALUES (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            for (Obra obra : artista.getListaObras()) {
                salvarObra(conn, ps, obra, artista.getNumeroInscricao());
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            close(conn, ps);
        }
    }

    private void salvarObra(Connection conn, PreparedStatement ps, Obra obra, Integer numeroInscricao) throws Exception {
        System.out.println("GerenteMySQL.salvarObra() salvando obra " + numeroInscricao);
        try {
            ps.setNull(1, Types.INTEGER);
            ps.setString(2, obra.getTitulo());
            ps.setInt(3, obra.getSelec());
            ps.setInt(4, obra.getCategoria().getCodigo());
            ps.setInt(5, numeroInscricao);
            ps.setInt(6, obra.getCodigo());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void salvarEndereco(Connection conn, PreparedStatement ps, Artista artista) throws Exception {
        System.out.println("GerenteMySQL.salvarEndereco() salvando endereco " + artista.getNumeroInscricao());
        try {
            String sql = "insert into endereco VALUES (?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setNull(1, Types.INTEGER);
            ps.setString(2, artista.getEndereco().getLogradouro());
            ps.setString(3, artista.getEndereco().getCep());
            ps.setInt(4, artista.getEndereco().getEstado());
            ps.setString(5, artista.getEndereco().getBairro());
            ps.setString(6, artista.getEndereco().getCidade());
            ps.setString(7, artista.getEndereco().getPais());
            ps.setInt(8, artista.getNumeroInscricao());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void delArtista(Integer numeroInscricao) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "delete from artista where numeroinscricao = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, numeroInscricao);
            ps.executeUpdate();
            delEndereco(conn, ps, numeroInscricao);
            delObras(conn, ps, numeroInscricao);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            close(conn, ps);
        }
    }

    private void delEndereco(Connection conn, PreparedStatement ps, Integer numeroInscricao) throws SQLException {
        String sql = "delete from endereco where fk_artista = ?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, numeroInscricao);
        ps.executeUpdate();
    }

    private void delObras(Connection conn, PreparedStatement ps, Integer numeroInscricao) throws SQLException {
        String sql = "delete from obra where fk_artista = ?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, numeroInscricao);
        ps.executeUpdate();
    }

    /** Retorna a lista de artistas cadastrados no banco */
    @Override
    public List<Artista> getListaArtistas() {
        Connection conn = null;
        PreparedStatement ps = null;
        HashMap<Integer, Artista> lista = new HashMap<Integer, Artista>();
        try {
            conn = C3P0Pool.getConnection();
            String sql = "select " + "a.numeroinscricao, a.nome, a.sexo, a.email, a.obs, a.telefone, " + "e.logradouro, e.cep, e.estado, e.bairro, e.cidade, e.pais " + "from artista a join endereco e on (a.numeroinscricao = e.fk_artista) " + "order by numeroinscricao;";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    Artista artista = this.getArtista(rs);
                    lista.put(artista.getNumeroInscricao(), artista);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }
        return new ArrayList<Artista>(lista.values());
    }

    /** Cria uma categoria de acordo com o ResultSet passado */
    private Artista getArtista(ResultSet rs) throws SQLException {
        Artista artista = new Artista();
        artista.setNumeroInscricao(rs.getInt("numeroinscricao"));
        artista.setNome(rs.getString("nome"));
        artista.setSexo(rs.getBoolean("sexo"));
        artista.setEmail(rs.getString("email"));
        artista.setObs(rs.getString("obs"));
        artista.setTelefone(rs.getString("telefone"));
        Endereco endereco = new Endereco();
        endereco.setLogradouro(rs.getString("logradouro"));
        endereco.setCep(rs.getString("cep"));
        endereco.setEstado(rs.getInt("estado"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setPais(rs.getString("pais"));
        artista.setEndereco(endereco);
        List<Obra> listaObras = getListaObras(rs.getInt("numeroinscricao"));
        artista.setListaObras(listaObras);
        return artista;
    }

    /** Retorna a lista de obras de um determinado artista */
    private List<Obra> getListaObras(int id_artista) {
        Connection conn = null;
        PreparedStatement ps = null;
        List<Obra> listaObras = new ArrayList<Obra>();
        try {
            conn = C3P0Pool.getConnection();
            String sql = "select * from obra o join categoria c on (c.id_categoria = o.fk_categoria) where (o.fk_artista = ?);";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id_artista);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    Obra obra = new Obra();
                    obra.setTitulo(rs.getString("titulo"));
                    obra.setSelec(rs.getInt("selecao"));
                    obra.setCodigo(rs.getInt("codigo"));
                    Categoria c = new Categoria();
                    c.setCodigo(rs.getInt("id_categoria"));
                    c.setNome(rs.getString("nome_categoria"));
                    obra.setCategoria(c);
                    listaObras.add(obra);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }
        return listaObras;
    }

    @Override
    public Integer getNumeroTotalArtista() {
        Connection conn = null;
        PreparedStatement ps = null;
        int contador = 0;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "select count(*) from artista;";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            rs.next();
            contador = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }
        return contador;
    }

    public Integer getNumeroTotalObras(int selecao) {
        Connection conn = null;
        PreparedStatement ps = null;
        int contador = 0;
        try {
            conn = C3P0Pool.getConnection();
            String sql = "select count(*) from obra where selecao = ?;";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, selecao);
            ResultSet rs = ps.executeQuery();
            rs.next();
            contador = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);
        }
        return contador;
    }
}

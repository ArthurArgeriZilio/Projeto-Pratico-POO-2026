package repository;

import database.DatabaseConnection;
import model.Aposta;
import model.Campeonato;
import model.Clube;
import model.Grupo;
import model.Participante;
import model.Partida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class JdbcSistemaRepository implements SistemaRepository {
    private final DatabaseConnection databaseConnection;

    public JdbcSistemaRepository() {
        this.databaseConnection = DatabaseConnection.getInstance();
    }

    @Override
    public EstadoSistema carregar() throws Exception {
        EstadoSistema estado = new EstadoSistema();

        try (Connection conn = databaseConnection.getConnection()) {
            Map<Integer, Clube> clubesPorId = carregarClubes(conn, estado);
            Map<Integer, Participante> participantesPorId = carregarParticipantes(conn, estado);
            Map<Integer, Campeonato> campeonatosPorId = carregarCampeonatos(conn, estado);
            carregarClubesDosCampeonatos(conn, campeonatosPorId, clubesPorId);
            Map<Integer, Partida> partidasPorId = carregarPartidas(conn, campeonatosPorId, clubesPorId);
            carregarGrupos(conn, estado, participantesPorId);
            carregarApostas(conn, participantesPorId, partidasPorId);
        }

        return estado;
    }

    @Override
    public void salvar(EstadoSistema estado) throws Exception {
        try (Connection conn = databaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                limparBanco(conn);

                Map<Clube, Integer> clubeIds = salvarClubes(conn, estado);
                Map<Participante, Integer> participanteIds = salvarParticipantes(conn, estado);
                Map<Campeonato, Integer> campeonatoIds = salvarCampeonatos(conn, estado, clubeIds);
                Map<Partida, Integer> partidaIds = salvarPartidas(conn, estado, campeonatoIds, clubeIds);
                salvarGrupos(conn, estado, participanteIds);
                salvarApostas(conn, estado, participanteIds, partidaIds);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean testarConexao() {
        try (Connection conn = databaseConnection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    private Map<Integer, Clube> carregarClubes(Connection conn, EstadoSistema estado) throws Exception {
        Map<Integer, Clube> clubesPorId = new HashMap<>();
        String sql = "SELECT id, nome, sigla FROM clubes ORDER BY id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Clube clube = new Clube(rs.getString("nome"), rs.getString("sigla"));
                estado.getClubes().add(clube);
                clubesPorId.put(rs.getInt("id"), clube);
            }
        }
        return clubesPorId;
    }

    private Map<Integer, Participante> carregarParticipantes(Connection conn, EstadoSistema estado) throws Exception {
        Map<Integer, Participante> participantesPorId = new HashMap<>();
        String sql = "SELECT id, nome, email FROM participantes ORDER BY id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Participante participante = new Participante(rs.getString("nome"), rs.getString("email"));
                estado.getParticipantes().add(participante);
                participantesPorId.put(rs.getInt("id"), participante);
            }
        }
        return participantesPorId;
    }

    private Map<Integer, Campeonato> carregarCampeonatos(Connection conn, EstadoSistema estado) throws Exception {
        Map<Integer, Campeonato> campeonatosPorId = new HashMap<>();
        String sql = "SELECT id, nome FROM campeonatos ORDER BY id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Campeonato campeonato = new Campeonato(rs.getString("nome"));
                estado.getCampeonatos().add(campeonato);
                campeonatosPorId.put(rs.getInt("id"), campeonato);
            }
        }
        return campeonatosPorId;
    }

    private void carregarClubesDosCampeonatos(Connection conn, Map<Integer, Campeonato> campeonatosPorId,
            Map<Integer, Clube> clubesPorId) throws Exception {
        String sql = "SELECT campeonato_id, clube_id FROM campeonato_clubes ORDER BY campeonato_id, id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Campeonato campeonato = campeonatosPorId.get(rs.getInt("campeonato_id"));
                Clube clube = clubesPorId.get(rs.getInt("clube_id"));
                if (campeonato != null && clube != null) {
                    campeonato.adicionarClube(clube);
                }
            }
        }
    }

    private Map<Integer, Partida> carregarPartidas(Connection conn, Map<Integer, Campeonato> campeonatosPorId,
            Map<Integer, Clube> clubesPorId) throws Exception {
        Map<Integer, Partida> partidasPorId = new HashMap<>();
        String sql = "SELECT id, campeonato_id, clube_casa_id, clube_visitante_id, data_hora, gols_casa, "
                + "gols_visitante, resultado_registrado FROM partidas ORDER BY id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Clube casa = clubesPorId.get(rs.getInt("clube_casa_id"));
                Clube visitante = clubesPorId.get(rs.getInt("clube_visitante_id"));
                Campeonato campeonato = campeonatosPorId.get(rs.getInt("campeonato_id"));
                Timestamp dataHora = rs.getTimestamp("data_hora");
                if (casa != null && visitante != null && campeonato != null && dataHora != null) {
                    Partida partida = new Partida(casa, visitante, dataHora.toLocalDateTime());
                    if (rs.getBoolean("resultado_registrado")) {
                        partida.registrarResultado(rs.getInt("gols_casa"), rs.getInt("gols_visitante"));
                    }
                    campeonato.adicionarPartida(partida);
                    partidasPorId.put(rs.getInt("id"), partida);
                }
            }
        }
        return partidasPorId;
    }

    private void carregarGrupos(Connection conn, EstadoSistema estado, Map<Integer, Participante> participantesPorId)
            throws Exception {
        Map<Integer, Grupo> gruposPorId = new HashMap<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT id, nome FROM grupos ORDER BY id")) {
            while (rs.next()) {
                Grupo grupo = new Grupo(rs.getString("nome"));
                estado.getGrupos().add(grupo);
                gruposPorId.put(rs.getInt("id"), grupo);
            }
        }

        String sql = "SELECT grupo_id, participante_id FROM grupo_participantes ORDER BY grupo_id, id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Grupo grupo = gruposPorId.get(rs.getInt("grupo_id"));
                Participante participante = participantesPorId.get(rs.getInt("participante_id"));
                if (grupo != null && participante != null) {
                    grupo.adicionarParticipante(participante);
                }
            }
        }
    }

    private void carregarApostas(Connection conn, Map<Integer, Participante> participantesPorId,
            Map<Integer, Partida> partidasPorId) throws Exception {
        String sql = "SELECT participante_id, partida_id, gols_casa_aposta, gols_visitante_aposta FROM apostas ORDER BY id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Participante participante = participantesPorId.get(rs.getInt("participante_id"));
                Partida partida = partidasPorId.get(rs.getInt("partida_id"));
                if (participante != null && partida != null) {
                    participante.adicionarAposta(new Aposta(participante, partida,
                            rs.getInt("gols_casa_aposta"), rs.getInt("gols_visitante_aposta")));
                }
            }
        }
    }

    private void limparBanco(Connection conn) throws Exception {
        String[] tabelas = {
            "apostas", "grupo_participantes", "grupos", "partidas",
            "campeonato_clubes", "campeonatos", "participantes", "clubes"
        };
        for (String tabela : tabelas) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM " + tabela);
            }
        }
    }

    private Map<Clube, Integer> salvarClubes(Connection conn, EstadoSistema estado) throws Exception {
        Map<Clube, Integer> ids = new IdentityHashMap<>();
        String sql = "INSERT INTO clubes (nome, sigla) VALUES (?, ?)";
        for (Clube clube : estado.getClubes()) {
            ids.put(clube, inserir(conn, sql, clube.getNome(), clube.getSigla()));
        }
        return ids;
    }

    private Map<Participante, Integer> salvarParticipantes(Connection conn, EstadoSistema estado) throws Exception {
        Map<Participante, Integer> ids = new IdentityHashMap<>();
        String sql = "INSERT INTO participantes (nome, email) VALUES (?, ?)";
        for (Participante participante : estado.getParticipantes()) {
            ids.put(participante, inserir(conn, sql, participante.getNome(), participante.getEmail()));
        }
        return ids;
    }

    private Map<Campeonato, Integer> salvarCampeonatos(Connection conn, EstadoSistema estado,
            Map<Clube, Integer> clubeIds) throws Exception {
        Map<Campeonato, Integer> ids = new IdentityHashMap<>();
        String sqlCamp = "INSERT INTO campeonatos (nome) VALUES (?)";
        String sqlVinculo = "INSERT INTO campeonato_clubes (campeonato_id, clube_id) VALUES (?, ?)";
        for (Campeonato campeonato : estado.getCampeonatos()) {
            int campeonatoId = inserir(conn, sqlCamp, campeonato.getNome());
            ids.put(campeonato, campeonatoId);
            for (Clube clube : campeonato.getClubes()) {
                inserir(conn, sqlVinculo, campeonatoId, clubeIds.get(clube));
            }
        }
        return ids;
    }

    private Map<Partida, Integer> salvarPartidas(Connection conn, EstadoSistema estado,
            Map<Campeonato, Integer> campeonatoIds, Map<Clube, Integer> clubeIds) throws Exception {
        Map<Partida, Integer> ids = new IdentityHashMap<>();
        String sql = "INSERT INTO partidas (campeonato_id, clube_casa_id, clube_visitante_id, data_hora, gols_casa, "
                + "gols_visitante, resultado_registrado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        for (Campeonato campeonato : estado.getCampeonatos()) {
            for (Partida partida : campeonato.getPartidas()) {
                int id = inserir(conn, sql, campeonatoIds.get(campeonato), clubeIds.get(partida.getClubeCasa()),
                        clubeIds.get(partida.getClubeVisitante()), Timestamp.valueOf(partida.getDataHora()),
                        partida.getGolsCasa(), partida.getGolsVisitante(), partida.isResultadoRegistrado());
                ids.put(partida, id);
            }
        }
        return ids;
    }

    private void salvarGrupos(Connection conn, EstadoSistema estado, Map<Participante, Integer> participanteIds)
            throws Exception {
        String sqlGrupo = "INSERT INTO grupos (nome) VALUES (?)";
        String sqlVinculo = "INSERT INTO grupo_participantes (grupo_id, participante_id) VALUES (?, ?)";
        for (Grupo grupo : estado.getGrupos()) {
            int grupoId = inserir(conn, sqlGrupo, grupo.getNome());
            for (Participante participante : grupo.getParticipantes()) {
                inserir(conn, sqlVinculo, grupoId, participanteIds.get(participante));
            }
        }
    }

    private void salvarApostas(Connection conn, EstadoSistema estado, Map<Participante, Integer> participanteIds,
            Map<Partida, Integer> partidaIds) throws Exception {
        String sql = "INSERT INTO apostas (participante_id, partida_id, gols_casa_aposta, gols_visitante_aposta) "
                + "VALUES (?, ?, ?, ?)";
        for (Participante participante : estado.getParticipantes()) {
            for (Aposta aposta : participante.getApostas()) {
                inserir(conn, sql, participanteIds.get(participante), partidaIds.get(aposta.getPartida()),
                        aposta.getGolsCasaAposta(), aposta.getGolsVisitanteAposta());
            }
        }
    }

    private int inserir(Connection conn, String sql, Object... parametros) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}

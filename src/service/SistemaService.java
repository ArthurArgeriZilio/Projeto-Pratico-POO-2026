package service;

import model.*;
import repository.EstadoSistema;
import repository.JdbcSistemaRepository;
import repository.SistemaRepository;

import java.util.ArrayList;
import java.util.List;

// Classe que controla toda a logica do sistema
public class SistemaService {
    private List<Clube> clubes;
    private List<Campeonato> campeonatos;
    private List<Grupo> grupos;
    private List<Participante> participantes;
    private SistemaRepository repository;
    private boolean bancoDisponivel;
    private String mensagemPersistencia;

    public SistemaService() {
        this.clubes = new ArrayList<>();
        this.campeonatos = new ArrayList<>();
        this.grupos = new ArrayList<>();
        this.participantes = new ArrayList<>();
        this.repository = new JdbcSistemaRepository();
        carregarDados();
    }

    // cadastra um novo clube
    public String cadastrarClube(String nome, String sigla) {
        for (Clube clube : clubes) {
            if (clube.getSigla().equalsIgnoreCase(sigla)) {
                return "Ja existe clube cadastrado com essa sigla.";
            }
        }
        Clube c = new Clube(nome, sigla);
        clubes.add(c);
        salvarDados();
        return null;
    }

    public List<Clube> getClubes() {
        return clubes;
    }

    // cria um campeonato
    public Campeonato cadastrarCampeonato(String nome) {
        Campeonato c = new Campeonato(nome);
        campeonatos.add(c);
        salvarDados();
        return c;
    }

    public List<Campeonato> getCampeonatos() {
        return campeonatos;
    }

    // cria um grupo (maximo 5)
    public boolean cadastrarGrupo(String nome) {
        if (grupos.size() >= 5) {
            return false;
        }
        Grupo g = new Grupo(nome);
        grupos.add(g);
        salvarDados();
        return true;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    // cadastra participante
    public Participante cadastrarParticipante(String nome, String email) {
        Participante p = new Participante(nome, email);
        participantes.add(p);
        salvarDados();
        return p;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    // registra uma aposta, retorna mensagem de erro ou null se deu certo
    public String registrarAposta(Participante participante, Partida partida, int golsCasa, int golsVisitante) {
        if (!partida.podeApostar()) {
            return "Nao e possivel apostar. Prazo encerrou (20 min antes da partida).";
        }
        if (partida.isResultadoRegistrado()) {
            return "Nao e possivel apostar em partida ja encerrada.";
        }

        // verifica se ja apostou nessa partida
        for (int i = 0; i < participante.getApostas().size(); i++) {
            if (participante.getApostas().get(i).getPartida() == partida) {
                return "Voce ja apostou nessa partida.";
            }
        }

        Aposta aposta = new Aposta(participante, partida, golsCasa, golsVisitante);
        participante.adicionarAposta(aposta);
        salvarDados();
        return null;
    }

    // registra o resultado real de uma partida
    public void registrarResultado(Partida partida, int golsCasa, int golsVisitante) {
        partida.registrarResultado(golsCasa, golsVisitante);
        salvarDados();
    }

    public String adicionarClubeAoCampeonato(Campeonato campeonato, Clube clube) {
        if (campeonato == null || clube == null) {
            return "Selecione campeonato e clube.";
        }
        if (campeonato.getClubes().contains(clube)) {
            return "Esse clube ja esta no campeonato.";
        }
        if (!campeonato.adicionarClube(clube)) {
            return "Campeonato ja tem 8 clubes.";
        }
        salvarDados();
        return null;
    }

    public String cadastrarPartida(Campeonato campeonato, Clube casa, Clube visitante, java.time.LocalDateTime dataHora) {
        if (campeonato == null || casa == null || visitante == null) {
            return "Preencha todos os campos.";
        }
        if (casa == visitante) {
            return "Escolha clubes diferentes.";
        }
        Partida partida = new Partida(casa, visitante, dataHora);
        campeonato.adicionarPartida(partida);
        salvarDados();
        return null;
    }

    public String adicionarParticipanteAoGrupo(Grupo grupo, Participante participante) {
        if (grupo == null || participante == null) {
            return "Selecione grupo e participante.";
        }
        if (grupo.getParticipantes().contains(participante)) {
            return "Participante ja esta nesse grupo.";
        }
        if (!grupo.adicionarParticipante(participante)) {
            return "Grupo ja tem 5 participantes.";
        }
        salvarDados();
        return null;
    }

    public void salvarDados() {
        if (!bancoDisponivel) {
            return;
        }
        try {
            EstadoSistema estado = criarEstadoAtual();
            repository.salvar(estado);
            mensagemPersistencia = "Banco conectado - dados salvos automaticamente.";
        } catch (Exception e) {
            bancoDisponivel = false;
            mensagemPersistencia = "Banco indisponivel: " + e.getMessage();
        }
    }

    public boolean isBancoDisponivel() {
        return bancoDisponivel;
    }

    public String getMensagemPersistencia() {
        return mensagemPersistencia;
    }

    private void carregarDados() {
        try {
            bancoDisponivel = repository.testarConexao();
            if (!bancoDisponivel) {
                mensagemPersistencia = "Banco nao conectado - usando memoria.";
                return;
            }
            EstadoSistema estado = repository.carregar();
            this.clubes = estado.getClubes();
            this.campeonatos = estado.getCampeonatos();
            this.grupos = estado.getGrupos();
            this.participantes = estado.getParticipantes();
            mensagemPersistencia = "Banco conectado - dados carregados.";
        } catch (Exception e) {
            bancoDisponivel = false;
            mensagemPersistencia = "Banco indisponivel: " + e.getMessage();
        }
    }

    private EstadoSistema criarEstadoAtual() {
        EstadoSistema estado = new EstadoSistema();
        estado.getClubes().addAll(clubes);
        estado.getCampeonatos().addAll(campeonatos);
        estado.getGrupos().addAll(grupos);
        estado.getParticipantes().addAll(participantes);
        return estado;
    }
}

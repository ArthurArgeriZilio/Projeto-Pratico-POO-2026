package gui;

import model.*;
import service.SistemaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private SistemaService service;
    private JTabbedPane abas;
    private JLabel lblPersistencia;

    // tabelas
    private DefaultTableModel modeloClubes;
    private DefaultTableModel modeloCampClubes;
    private DefaultTableModel modeloPartidas;
    private DefaultTableModel modeloGruposPart;
    private DefaultTableModel modeloParticipantes;
    private DefaultTableModel modeloApostas;
    private DefaultTableModel modeloResultados;
    private DefaultTableModel modeloClassificacao;

    // combos que precisam ser atualizados
    private JComboBox<Campeonato> cbCampeonato;
    private JComboBox<Clube> cbClubeCamp;
    private JComboBox<Campeonato> cbCampPartida;
    private JComboBox<Clube> cbCasaPartida;
    private JComboBox<Clube> cbVisitantePartida;
    private JComboBox<Grupo> cbGrupoAdd;
    private JComboBox<Participante> cbPartGrupo;
    private JComboBox<Participante> cbPartAposta;
    private JComboBox<Campeonato> cbCampAposta;
    private JComboBox<Partida> cbPartidaAposta;
    private JComboBox<Campeonato> cbCampResultado;
    private JComboBox<Partida> cbPartidaResultado;
    private JComboBox<Grupo> cbGrupoClass;

    public TelaPrincipal() {
        service = new SistemaService();

        setTitle("Sistema de Apostas - Campeonato de Futebol");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        abas = new JTabbedPane();
        abas.addTab("Clubes", criarAbaClubes());
        abas.addTab("Campeonatos", criarAbaCampeonatos());
        abas.addTab("Partidas", criarAbaPartidas());
        abas.addTab("Grupos", criarAbaGrupos());
        abas.addTab("Participantes", criarAbaParticipantes());
        abas.addTab("Apostas", criarAbaApostas());
        abas.addTab("Resultados", criarAbaResultados());
        abas.addTab("Classificacao", criarAbaClassificacao());

        // quando troca de aba, atualiza os combos
        abas.addChangeListener(e -> atualizarCombos());

        lblPersistencia = new JLabel(service.getMensagemPersistencia());
        lblPersistencia.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.add(abas, BorderLayout.CENTER);
        conteudo.add(lblPersistencia, BorderLayout.SOUTH);
        add(conteudo);

        atualizarTabelas();
        atualizarCombos();
    }

    // ==================== ABA CLUBES ====================

    private JPanel criarAbaClubes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Cadastrar Clube"));

        JTextField txtNome = new JTextField();
        JTextField txtSigla = new JTextField();
        JButton btnCadastrar = new JButton("Cadastrar");

        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Sigla:"));
        form.add(txtSigla);
        form.add(new JLabel(""));
        form.add(btnCadastrar);

        modeloClubes = new DefaultTableModel(new String[]{"Nome", "Sigla"}, 0);
        JTable tabela = new JTable(modeloClubes);

        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = txtNome.getText().trim();
                String sigla = txtSigla.getText().trim();
                if (nome.isEmpty() || sigla.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                    return;
                }
                String erro = service.cadastrarClube(nome, sigla);
                if (erro != null) {
                    JOptionPane.showMessageDialog(null, erro);
                    return;
                }
                txtNome.setText("");
                txtSigla.setText("");
                atualizarTabelas();
                atualizarCombos();
            }
        });

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ABA CAMPEONATOS ====================

    private JPanel criarAbaCampeonatos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // painel pra criar campeonato
        JPanel pnlCriar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlCriar.setBorder(BorderFactory.createTitledBorder("Criar Campeonato"));
        JTextField txtNome = new JTextField(20);
        JButton btnCriar = new JButton("Criar");
        pnlCriar.add(new JLabel("Nome:"));
        pnlCriar.add(txtNome);
        pnlCriar.add(btnCriar);

        // painel pra adicionar clube no campeonato
        JPanel pnlAdd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAdd.setBorder(BorderFactory.createTitledBorder("Adicionar Clube ao Campeonato (max 8)"));
        cbCampeonato = new JComboBox<>();
        cbClubeCamp = new JComboBox<>();
        JButton btnAdd = new JButton("Adicionar");
        pnlAdd.add(new JLabel("Campeonato:"));
        pnlAdd.add(cbCampeonato);
        pnlAdd.add(new JLabel("Clube:"));
        pnlAdd.add(cbClubeCamp);
        pnlAdd.add(btnAdd);

        JPanel topo = new JPanel(new GridLayout(2, 1));
        topo.add(pnlCriar);
        topo.add(pnlAdd);

        modeloCampClubes = new DefaultTableModel(new String[]{"Campeonato", "Clube"}, 0);
        JTable tabela = new JTable(modeloCampClubes);

        btnCriar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = txtNome.getText().trim();
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Informe o nome!");
                    return;
                }
                service.cadastrarCampeonato(nome);
                txtNome.setText("");
                atualizarTabelas();
                atualizarCombos();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Campeonato camp = (Campeonato) cbCampeonato.getSelectedItem();
                Clube clube = (Clube) cbClubeCamp.getSelectedItem();
                if (camp == null || clube == null) {
                    JOptionPane.showMessageDialog(null, "Selecione campeonato e clube!");
                    return;
                }
                String erro = service.adicionarClubeAoCampeonato(camp, clube);
                if (erro != null) {
                    JOptionPane.showMessageDialog(null, erro);
                    return;
                }
                atualizarTabelas();
                atualizarCombos();
            }
        });

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ABA PARTIDAS ====================

    private JPanel criarAbaPartidas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Cadastrar Partida"));

        cbCampPartida = new JComboBox<>();
        cbCasaPartida = new JComboBox<>();
        cbVisitantePartida = new JComboBox<>();
        JTextField txtData = new JTextField("dd/MM/yyyy HH:mm");
        JButton btnCadastrar = new JButton("Cadastrar Partida");

        // placeholder simples
        txtData.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtData.getText().equals("dd/MM/yyyy HH:mm")) {
                    txtData.setText("");
                }
            }
        });

        // quando seleciona o campeonato, carrega os clubes dele
        cbCampPartida.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbCasaPartida.removeAllItems();
                cbVisitantePartida.removeAllItems();
                Campeonato camp = (Campeonato) cbCampPartida.getSelectedItem();
                if (camp != null) {
                    for (int i = 0; i < camp.getClubes().size(); i++) {
                        cbCasaPartida.addItem(camp.getClubes().get(i));
                        cbVisitantePartida.addItem(camp.getClubes().get(i));
                    }
                }
            }
        });

        form.add(new JLabel("Campeonato:"));
        form.add(cbCampPartida);
        form.add(new JLabel("Clube Casa:"));
        form.add(cbCasaPartida);
        form.add(new JLabel("Clube Visitante:"));
        form.add(cbVisitantePartida);
        form.add(new JLabel("Data/Hora:"));
        form.add(txtData);
        form.add(new JLabel(""));
        form.add(btnCadastrar);

        modeloPartidas = new DefaultTableModel(new String[]{"Campeonato", "Casa", "Visitante", "Data/Hora"}, 0);
        JTable tabela = new JTable(modeloPartidas);

        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Campeonato camp = (Campeonato) cbCampPartida.getSelectedItem();
                Clube casa = (Clube) cbCasaPartida.getSelectedItem();
                Clube visitante = (Clube) cbVisitantePartida.getSelectedItem();

                if (camp == null || casa == null || visitante == null) {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                    return;
                }
                if (casa == visitante) {
                    JOptionPane.showMessageDialog(null, "Escolha clubes diferentes!");
                    return;
                }

                LocalDateTime dataHora;
                try {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    dataHora = LocalDateTime.parse(txtData.getText().trim(), fmt);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Data invalida! Use: dd/MM/yyyy HH:mm");
                    return;
                }

                String erro = service.cadastrarPartida(camp, casa, visitante, dataHora);
                if (erro != null) {
                    JOptionPane.showMessageDialog(null, erro);
                    return;
                }
                txtData.setText("dd/MM/yyyy HH:mm");
                atualizarTabelas();
                atualizarCombos();
            }
        });

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ABA GRUPOS ====================

    private JPanel criarAbaGrupos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlCriar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlCriar.setBorder(BorderFactory.createTitledBorder("Criar Grupo (max 5)"));
        JTextField txtNome = new JTextField(20);
        JButton btnCriar = new JButton("Criar");
        pnlCriar.add(new JLabel("Nome:"));
        pnlCriar.add(txtNome);
        pnlCriar.add(btnCriar);

        JPanel pnlAdd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAdd.setBorder(BorderFactory.createTitledBorder("Adicionar Participante ao Grupo (max 5)"));
        cbGrupoAdd = new JComboBox<>();
        cbPartGrupo = new JComboBox<>();
        JButton btnAdd = new JButton("Adicionar");
        pnlAdd.add(new JLabel("Grupo:"));
        pnlAdd.add(cbGrupoAdd);
        pnlAdd.add(new JLabel("Participante:"));
        pnlAdd.add(cbPartGrupo);
        pnlAdd.add(btnAdd);

        JPanel topo = new JPanel(new GridLayout(2, 1));
        topo.add(pnlCriar);
        topo.add(pnlAdd);

        modeloGruposPart = new DefaultTableModel(new String[]{"Grupo", "Participante"}, 0);
        JTable tabela = new JTable(modeloGruposPart);

        btnCriar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = txtNome.getText().trim();
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Informe o nome do grupo!");
                    return;
                }
                if (!service.cadastrarGrupo(nome)) {
                    JOptionPane.showMessageDialog(null, "Ja tem 5 grupos! Limite atingido.");
                    return;
                }
                List<Grupo> grupos = service.getGrupos();
                txtNome.setText("");
                atualizarTabelas();
                atualizarCombos();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Grupo grupo = (Grupo) cbGrupoAdd.getSelectedItem();
                Participante part = (Participante) cbPartGrupo.getSelectedItem();
                if (grupo == null || part == null) {
                    JOptionPane.showMessageDialog(null, "Selecione grupo e participante!");
                    return;
                }
                String erro = service.adicionarParticipanteAoGrupo(grupo, part);
                if (erro != null) {
                    JOptionPane.showMessageDialog(null, erro);
                    return;
                }
                atualizarTabelas();
                atualizarCombos();
            }
        });

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ABA PARTICIPANTES ====================

    private JPanel criarAbaParticipantes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Cadastrar Participante"));

        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JButton btnCadastrar = new JButton("Cadastrar");

        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("E-mail:"));
        form.add(txtEmail);
        form.add(new JLabel(""));
        form.add(btnCadastrar);

        modeloParticipantes = new DefaultTableModel(new String[]{"Nome", "E-mail", "Tipo"}, 0);
        JTable tabela = new JTable(modeloParticipantes);

        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = txtNome.getText().trim();
                String email = txtEmail.getText().trim();
                if (nome.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                    return;
                }
                Participante p = service.cadastrarParticipante(nome, email);
                txtNome.setText("");
                txtEmail.setText("");
                atualizarTabelas();
                atualizarCombos();
            }
        });

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ABA APOSTAS ====================

    private JPanel criarAbaApostas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Registrar Aposta"));

        cbPartAposta = new JComboBox<>();
        cbCampAposta = new JComboBox<>();
        cbPartidaAposta = new JComboBox<>();
        JSpinner spnCasa = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        JSpinner spnVisitante = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        JButton btnApostar = new JButton("Registrar Aposta");

        // quando seleciona campeonato, carrega as partidas
        cbCampAposta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPartidaAposta.removeAllItems();
                Campeonato camp = (Campeonato) cbCampAposta.getSelectedItem();
                if (camp != null) {
                    for (int i = 0; i < camp.getPartidas().size(); i++) {
                        Partida p = camp.getPartidas().get(i);
                        if (!p.isResultadoRegistrado()) {
                            cbPartidaAposta.addItem(p);
                        }
                    }
                }
            }
        });

        form.add(new JLabel("Participante:"));
        form.add(cbPartAposta);
        form.add(new JLabel("Campeonato:"));
        form.add(cbCampAposta);
        form.add(new JLabel("Partida:"));
        form.add(cbPartidaAposta);
        form.add(new JLabel("Gols Casa (aposta):"));
        form.add(spnCasa);
        form.add(new JLabel("Gols Visitante (aposta):"));
        form.add(spnVisitante);
        form.add(new JLabel(""));
        form.add(btnApostar);

        modeloApostas = new DefaultTableModel(new String[]{"Participante", "Partida", "Gols Casa", "Gols Visitante"}, 0);
        JTable tabela = new JTable(modeloApostas);

        btnApostar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Participante part = (Participante) cbPartAposta.getSelectedItem();
                Partida partida = (Partida) cbPartidaAposta.getSelectedItem();
                if (part == null || partida == null) {
                    JOptionPane.showMessageDialog(null, "Selecione participante e partida!");
                    return;
                }
                int golsC = (int) spnCasa.getValue();
                int golsV = (int) spnVisitante.getValue();

                String erro = service.registrarAposta(part, partida, golsC, golsV);
                if (erro != null) {
                    JOptionPane.showMessageDialog(null, erro);
                    return;
                }
                atualizarTabelas();
                atualizarCombos();
                JOptionPane.showMessageDialog(null, "Aposta registrada!");
            }
        });

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ABA RESULTADOS ====================

    private JPanel criarAbaResultados() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Registrar Resultado (Administrador)"));

        cbCampResultado = new JComboBox<>();
        cbPartidaResultado = new JComboBox<>();
        JSpinner spnCasa = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        JSpinner spnVisitante = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        JButton btnRegistrar = new JButton("Registrar Resultado");

        cbCampResultado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPartidaResultado.removeAllItems();
                Campeonato camp = (Campeonato) cbCampResultado.getSelectedItem();
                if (camp != null) {
                    for (int i = 0; i < camp.getPartidas().size(); i++) {
                        Partida p = camp.getPartidas().get(i);
                        if (!p.isResultadoRegistrado()) {
                            cbPartidaResultado.addItem(p);
                        }
                    }
                }
            }
        });

        form.add(new JLabel("Campeonato:"));
        form.add(cbCampResultado);
        form.add(new JLabel("Partida:"));
        form.add(cbPartidaResultado);
        form.add(new JLabel("Gols Casa (real):"));
        form.add(spnCasa);
        form.add(new JLabel("Gols Visitante (real):"));
        form.add(spnVisitante);
        form.add(new JLabel(""));
        form.add(btnRegistrar);

        modeloResultados = new DefaultTableModel(new String[]{"Partida", "Gols Casa", "Gols Visitante", "Resultado"}, 0);
        JTable tabela = new JTable(modeloResultados);

        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Partida partida = (Partida) cbPartidaResultado.getSelectedItem();
                if (partida == null) {
                    JOptionPane.showMessageDialog(null, "Selecione uma partida!");
                    return;
                }
                int golsC = (int) spnCasa.getValue();
                int golsV = (int) spnVisitante.getValue();
                service.registrarResultado(partida, golsC, golsV);

                // monta a descricao do resultado
                String desc;
                String res = partida.getResultado();
                if (res.equals("CASA")) {
                    desc = "Vitoria " + partida.getClubeCasa().getNome();
                } else if (res.equals("VISITANTE")) {
                    desc = "Vitoria " + partida.getClubeVisitante().getNome();
                } else {
                    desc = "Empate";
                }

                atualizarTabelas();
                atualizarCombos();
                JOptionPane.showMessageDialog(null, "Resultado registrado!");
            }
        });

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ABA CLASSIFICACAO ====================

    private JPanel criarAbaClassificacao() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Classificacao do Grupo"));
        cbGrupoClass = new JComboBox<>();
        JButton btnAtualizar = new JButton("Ver Classificacao");
        JButton btnExportar = new JButton("Exportar CSV");
        form.add(new JLabel("Grupo:"));
        form.add(cbGrupoClass);
        form.add(btnAtualizar);
        form.add(btnExportar);

        modeloClassificacao = new DefaultTableModel(new String[]{"Posicao", "Participante", "Pontuacao"}, 0);
        JTable tabela = new JTable(modeloClassificacao);

        btnAtualizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Grupo grupo = (Grupo) cbGrupoClass.getSelectedItem();
                if (grupo == null) {
                    JOptionPane.showMessageDialog(null, "Selecione um grupo!");
                    return;
                }
                modeloClassificacao.setRowCount(0);
                List<Participante> classificacao = grupo.getClassificacao();
                for (int i = 0; i < classificacao.size(); i++) {
                    Participante p = classificacao.get(i);
                    modeloClassificacao.addRow(new Object[]{(i + 1) + "o", p.getNome(), p.getPontuacaoTotal()});
                }
            }
        });

        btnExportar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Grupo grupo = (Grupo) cbGrupoClass.getSelectedItem();
                if (grupo == null) {
                    JOptionPane.showMessageDialog(null, "Selecione um grupo!");
                    return;
                }
                exportarClassificacao(grupo);
            }
        });

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    private void atualizarTabelas() {
        lblPersistencia.setText(service.getMensagemPersistencia());

        modeloClubes.setRowCount(0);
        for (Clube clube : service.getClubes()) {
            modeloClubes.addRow(new Object[]{clube.getNome(), clube.getSigla()});
        }

        modeloCampClubes.setRowCount(0);
        modeloPartidas.setRowCount(0);
        modeloResultados.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Campeonato campeonato : service.getCampeonatos()) {
            for (Clube clube : campeonato.getClubes()) {
                modeloCampClubes.addRow(new Object[]{campeonato.getNome(), clube.getNome()});
            }
            for (Partida partida : campeonato.getPartidas()) {
                modeloPartidas.addRow(new Object[]{
                    campeonato.getNome(),
                    partida.getClubeCasa().getNome(),
                    partida.getClubeVisitante().getNome(),
                    partida.getDataHora().format(fmt)
                });
                if (partida.isResultadoRegistrado()) {
                    modeloResultados.addRow(new Object[]{
                        partida.getClubeCasa().getSigla() + " vs " + partida.getClubeVisitante().getSigla(),
                        partida.getGolsCasa(),
                        partida.getGolsVisitante(),
                        getDescricaoResultado(partida)
                    });
                }
            }
        }

        modeloGruposPart.setRowCount(0);
        for (Grupo grupo : service.getGrupos()) {
            for (Participante participante : grupo.getParticipantes()) {
                modeloGruposPart.addRow(new Object[]{grupo.getNome(), participante.getNome()});
            }
        }

        modeloParticipantes.setRowCount(0);
        modeloApostas.setRowCount(0);
        for (Participante participante : service.getParticipantes()) {
            modeloParticipantes.addRow(new Object[]{participante.getNome(), participante.getEmail(), participante.getTipo()});
            for (Aposta aposta : participante.getApostas()) {
                modeloApostas.addRow(new Object[]{
                    participante.getNome(),
                    aposta.getPartida().toString(),
                    aposta.getGolsCasaAposta(),
                    aposta.getGolsVisitanteAposta()
                });
            }
        }
    }

    private String getDescricaoResultado(Partida partida) {
        String resultado = partida.getResultado();
        if ("CASA".equals(resultado)) {
            return "Vitoria " + partida.getClubeCasa().getNome();
        }
        if ("VISITANTE".equals(resultado)) {
            return "Vitoria " + partida.getClubeVisitante().getNome();
        }
        return "Empate";
    }

    private void exportarClassificacao(Grupo grupo) {
        String nomeArquivo = "classificacao_" + grupo.getNome().replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
        File arquivo = new File(nomeArquivo);
        try (PrintWriter writer = new PrintWriter(arquivo)) {
            writer.println("posicao;participante;pontuacao");
            List<Participante> classificacao = grupo.getClassificacao();
            for (int i = 0; i < classificacao.size(); i++) {
                Participante participante = classificacao.get(i);
                writer.println((i + 1) + ";" + participante.getNome() + ";" + participante.getPontuacaoTotal());
            }
            JOptionPane.showMessageDialog(null, "Arquivo gerado: " + arquivo.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao exportar classificacao: " + ex.getMessage());
        }
    }

    // ==================== ATUALIZAR COMBOS ====================

    private void atualizarCombos() {
        // atualiza clubes
        cbClubeCamp.removeAllItems();
        for (int i = 0; i < service.getClubes().size(); i++) {
            cbClubeCamp.addItem(service.getClubes().get(i));
        }

        // atualiza campeonatos em todos os combos
        cbCampeonato.removeAllItems();
        cbCampPartida.removeAllItems();
        cbCampAposta.removeAllItems();
        cbCampResultado.removeAllItems();
        for (int i = 0; i < service.getCampeonatos().size(); i++) {
            Campeonato c = service.getCampeonatos().get(i);
            cbCampeonato.addItem(c);
            cbCampPartida.addItem(c);
            cbCampAposta.addItem(c);
            cbCampResultado.addItem(c);
        }

        // atualiza participantes
        cbPartGrupo.removeAllItems();
        cbPartAposta.removeAllItems();
        for (int i = 0; i < service.getParticipantes().size(); i++) {
            Participante participante = service.getParticipantes().get(i);
            cbPartGrupo.addItem(participante);
            cbPartAposta.addItem(participante);
        }

        // atualiza grupos
        cbGrupoAdd.removeAllItems();
        cbGrupoClass.removeAllItems();
        for (int i = 0; i < service.getGrupos().size(); i++) {
            Grupo grupo = service.getGrupos().get(i);
            cbGrupoAdd.addItem(grupo);
            cbGrupoClass.addItem(grupo);
        }
    }
}

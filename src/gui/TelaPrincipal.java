package gui;

import model.*;
import service.SistemaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private SistemaService service;
    private JTabbedPane abas;

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

        add(abas);
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
                service.cadastrarClube(nome, sigla);
                modeloClubes.addRow(new Object[]{nome, sigla});
                txtNome.setText("");
                txtSigla.setText("");
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
                Campeonato c = service.cadastrarCampeonato(nome);
                cbCampeonato.addItem(c);
                txtNome.setText("");
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
                if (!camp.adicionarClube(clube)) {
                    JOptionPane.showMessageDialog(null, "Campeonato ja tem 8 clubes!");
                    return;
                }
                modeloCampClubes.addRow(new Object[]{camp.getNome(), clube.getNome()});
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

                Partida partida = new Partida(casa, visitante, dataHora);
                camp.adicionarPartida(partida);
                modeloPartidas.addRow(new Object[]{camp.getNome(), casa.getNome(), visitante.getNome(), txtData.getText().trim()});
                txtData.setText("dd/MM/yyyy HH:mm");
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
                Grupo novo = grupos.get(grupos.size() - 1);
                cbGrupoAdd.addItem(novo);
                txtNome.setText("");
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
                if (!grupo.adicionarParticipante(part)) {
                    JOptionPane.showMessageDialog(null, "Grupo ja tem 5 participantes!");
                    return;
                }
                modeloGruposPart.addRow(new Object[]{grupo.getNome(), part.getNome()});
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
                modeloParticipantes.addRow(new Object[]{nome, email, p.getTipo()});
                cbPartGrupo.addItem(p);
                txtNome.setText("");
                txtEmail.setText("");
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
                modeloApostas.addRow(new Object[]{part.getNome(), partida.toString(), golsC, golsV});
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

                modeloResultados.addRow(new Object[]{
                    partida.getClubeCasa().getSigla() + " vs " + partida.getClubeVisitante().getSigla(),
                    golsC, golsV, desc
                });
                cbPartidaResultado.removeItem(partida);
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
        form.add(new JLabel("Grupo:"));
        form.add(cbGrupoClass);
        form.add(btnAtualizar);

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

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return painel;
    }

    // ==================== ATUALIZAR COMBOS ====================

    private void atualizarCombos() {
        // atualiza clubes
        cbClubeCamp.removeAllItems();
        for (int i = 0; i < service.getClubes().size(); i++) {
            cbClubeCamp.addItem(service.getClubes().get(i));
        }

        // atualiza campeonatos em todos os combos
        cbCampPartida.removeAllItems();
        cbCampAposta.removeAllItems();
        cbCampResultado.removeAllItems();
        for (int i = 0; i < service.getCampeonatos().size(); i++) {
            Campeonato c = service.getCampeonatos().get(i);
            cbCampPartida.addItem(c);
            cbCampAposta.addItem(c);
            cbCampResultado.addItem(c);
        }

        // atualiza participantes
        cbPartAposta.removeAllItems();
        for (int i = 0; i < service.getParticipantes().size(); i++) {
            cbPartAposta.addItem(service.getParticipantes().get(i));
        }

        // atualiza grupos
        cbGrupoClass.removeAllItems();
        for (int i = 0; i < service.getGrupos().size(); i++) {
            cbGrupoClass.addItem(service.getGrupos().get(i));
        }
    }
}

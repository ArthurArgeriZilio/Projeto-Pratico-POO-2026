# Sistema de Apostas - Campeonato de Futebol

Sistema de gerenciamento de apostas entre participantes de um grupo relacionado às partidas de um campeonato de futebol.

## Requisitos
- Java JDK 11 ou superior
- MySQL/MariaDB
- Driver JDBC do MySQL (`mysql-connector-j`)

## Compilação e Execução

### Banco de dados
1. Executar o arquivo `database/schema.sql` no MySQL.
2. Conferir usuario e senha no arquivo `database.properties`.

### Compilar
```bash
javac -d bin -sourcepath src src/Main.java
```

### Executar
```bash
java -cp bin Main
```

Se for rodar usando o banco, colocar o driver junto:
```bash
java -cp "bin;mysql-connector-j.jar" Main
```

## Estrutura do Projeto

```
src/
├── Main.java                    # Classe principal (ponto de entrada)
├── model/
│   ├── Pontuavel.java           # Interface de pontuação
│   ├── Pessoa.java              # Classe abstrata (base para Participante e Administrador)
│   ├── Participante.java        # Herança + Polimorfismo + Interface
│   ├── Administrador.java       # Herança + Polimorfismo
│   ├── Clube.java               # Classe concreta
│   ├── Campeonato.java          # Classe concreta (máx. 8 clubes)
│   ├── Partida.java             # Classe concreta
│   ├── Aposta.java              # Classe concreta (sobrecarga de métodos)
│   └── Grupo.java               # Classe concreta (máx. 5 participantes)
├── service/
│   └── SistemaService.java      # Lógica principal do sistema
├── database/
│   ├── DatabaseConfig.java      # Configuracoes do banco
│   └── DatabaseConnection.java  # Conexao JDBC
├── repository/
│   ├── SistemaRepository.java   # Interface do repositorio
│   ├── JdbcSistemaRepository.java # Repositorio com JDBC
│   └── EstadoSistema.java       # Guarda as listas do sistema
└── gui/
    └── TelaPrincipal.java       # Interface gráfica (Swing)
```

Arquivos de apoio:
- `database/schema.sql`: cria as tabelas do banco.
- `database.properties`: configura URL, usuario e senha do banco.

## Conceitos de POO Aplicados

| Conceito | Onde |
|---|---|
| **Encapsulamento** | Atributos `private` + getters/setters em todas as classes |
| **Construtores** | Construtor padrão e sobrecarregado em `Pessoa`, `Clube`, `Partida`, `Aposta`, etc. |
| **Herança Simples** | `Participante extends Pessoa`, `Administrador extends Pessoa` |
| **Polimorfismo (Sobrescrita)** | Método `getTipo()` sobrescrito em `Participante` e `Administrador` |
| **Polimorfismo (Sobrecarga)** | Método `calcularPontos()` / `calcularPontos(int, int)` em `Aposta` |
| **Classe Abstrata** | `Pessoa` (com método abstrato `getTipo()`) |
| **Interface** | `Pontuavel` (implementada por `Participante`) |
| **Singleton** | `DatabaseConnection` |
| **DAO/Repository** | `SistemaRepository` e `JdbcSistemaRepository` |
| **GUI Swing** | `TelaPrincipal` com JTabbedPane, JTable, JComboBox, etc. |

## Regras de Pontuação
- Acertar apenas o resultado (vencedor/empate): **5 pontos**
- Acertar resultado + placar exato: **10 pontos**

## Funcionalidades
1. Cadastro de clubes
2. Cadastro de campeonatos (com até 8 clubes)
3. Cadastro de partidas com data/hora
4. Cadastro de grupos de apostas (máx. 5 grupos)
5. Cadastro de participantes (máx. 5 por grupo)
6. Registro de apostas (até 20 min antes da partida)
7. Registro de resultado real pelo administrador
8. Classificação automática por grupo
9. Salvar dados no banco usando JDBC
10. Carregar os dados ao abrir o sistema
11. Salvar automaticamente depois dos cadastros
12. Exportar classificacao do grupo para CSV

## Observacao sobre o banco
Se o banco nao estiver configurado, o sistema ainda abre e funciona em memoria. Quando o MySQL estiver certo, ele carrega e salva os dados automaticamente.

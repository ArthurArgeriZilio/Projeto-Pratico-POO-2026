# Sistema de Apostas - Campeonato de Futebol

Sistema de gerenciamento de apostas entre participantes de um grupo relacionado às partidas de um campeonato de futebol.

## Requisitos
- Java JDK 11 ou superior

## Compilação e Execução

### Compilar
```bash
javac -d bin -sourcepath src src/Main.java
```

### Executar
```bash
java -cp bin Main
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
│   └── SistemaService.java      # Lógica de negócio centralizada
└── gui/
    └── TelaPrincipal.java       # Interface gráfica (Swing)
```

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

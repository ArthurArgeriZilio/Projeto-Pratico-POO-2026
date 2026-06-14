CREATE DATABASE IF NOT EXISTS sistema_apostas;
USE sistema_apostas;

CREATE TABLE IF NOT EXISTS clubes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    sigla VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS participantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS campeonatos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS campeonato_clubes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campeonato_id INT NOT NULL,
    clube_id INT NOT NULL,
    UNIQUE (campeonato_id, clube_id),
    FOREIGN KEY (campeonato_id) REFERENCES campeonatos(id) ON DELETE CASCADE,
    FOREIGN KEY (clube_id) REFERENCES clubes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS partidas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campeonato_id INT NOT NULL,
    clube_casa_id INT NOT NULL,
    clube_visitante_id INT NOT NULL,
    data_hora DATETIME NOT NULL,
    gols_casa INT NOT NULL DEFAULT 0,
    gols_visitante INT NOT NULL DEFAULT 0,
    resultado_registrado BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (campeonato_id) REFERENCES campeonatos(id) ON DELETE CASCADE,
    FOREIGN KEY (clube_casa_id) REFERENCES clubes(id),
    FOREIGN KEY (clube_visitante_id) REFERENCES clubes(id)
);

CREATE TABLE IF NOT EXISTS grupos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS grupo_participantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grupo_id INT NOT NULL,
    participante_id INT NOT NULL,
    UNIQUE (grupo_id, participante_id),
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE,
    FOREIGN KEY (participante_id) REFERENCES participantes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS apostas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    participante_id INT NOT NULL,
    partida_id INT NOT NULL,
    gols_casa_aposta INT NOT NULL,
    gols_visitante_aposta INT NOT NULL,
    UNIQUE (participante_id, partida_id),
    FOREIGN KEY (participante_id) REFERENCES participantes(id) ON DELETE CASCADE,
    FOREIGN KEY (partida_id) REFERENCES partidas(id) ON DELETE CASCADE
);

CREATE DATABASE machry_database
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE machry_database;

CREATE TABLE item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    unidade_medida VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE fichaTecnica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    rendimento DECIMAL(12,3) NOT NULL,
    unidade_rendimento VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE lote (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    data_producao DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_entrada DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_validade DATE,
    FOREIGN KEY (item_id) REFERENCES item(id)
) ENGINE=InnoDB;

CREATE TABLE ingredienteFicha (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ficha_id INT NOT NULL,
    item_id INT NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    FOREIGN KEY (ficha_id) REFERENCES fichaTecnica(id),
    FOREIGN KEY (item_id) REFERENCES item(id)
) ENGINE=InnoDB;

CREATE TABLE processamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    processo VARCHAR(100) NOT NULL,
    rendimento DECIMAL(12,3) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE itemProcessamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lote_id INT NOT NULL,
    processamento_id INT NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    quantidade DECIMAL(12,3) NOT NULL,
    FOREIGN KEY (lote_id) REFERENCES lote(id),
    FOREIGN KEY (processamento_id) REFERENCES processamento(id)
) ENGINE=InnoDB;

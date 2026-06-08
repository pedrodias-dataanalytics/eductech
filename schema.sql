-- =============================================================
--  EducTech Manager — Script completo do banco de dados
-- =============================================================

CREATE DATABASE IF NOT EXISTS eductech_manager
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE eductech_manager;

-- -------------------------------------------------------------
--  Tabela: usuarios
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id      INT          NOT NULL AUTO_INCREMENT,
    nome    VARCHAR(100) NOT NULL,
    login   VARCHAR(60)  NOT NULL UNIQUE,
    email   VARCHAR(100) NOT NULL UNIQUE,
    senha   VARCHAR(64)  NOT NULL,   -- SHA-256 hex
    perfil  ENUM('ADMIN','ALUNO')    NOT NULL DEFAULT 'ALUNO',
    ativo   TINYINT(1)               NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- -------------------------------------------------------------
--  Tabela: alunos
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS alunos (
    id       INT          NOT NULL AUTO_INCREMENT,
    nome     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE,
    cpf      VARCHAR(14)  NOT NULL UNIQUE,
    telefone VARCHAR(20),
    ativo    TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- -------------------------------------------------------------
--  Tabela: cursos
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cursos (
    id       INT          NOT NULL AUTO_INCREMENT,
    nome     VARCHAR(100) NOT NULL,
    descricao TEXT,
    carga_horaria INT     NOT NULL DEFAULT 0,
    ativo    TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- -------------------------------------------------------------
--  Tabela: matriculas
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS matriculas (
    id         INT  NOT NULL AUTO_INCREMENT,
    aluno_id   INT  NOT NULL,
    curso_id   INT  NOT NULL,
    data_inicio DATE NOT NULL,
    status     ENUM('ATIVA','CONCLUIDA','CANCELADA') NOT NULL DEFAULT 'ATIVA',
    PRIMARY KEY (id),
    FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE,
    FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
--  Dados iniciais
-- -------------------------------------------------------------

-- Administrador padrão  (senha: admin123  → SHA-256)
INSERT INTO usuarios (nome, login, email, senha, perfil) VALUES
('Administrador', 'admin', 'admin@eductech.com',
 SHA2('admin123', 256), 'ADMIN');

-- Alunos de exemplo
INSERT INTO alunos (nome, email, cpf, telefone) VALUES
('Ana Paula Silva',   'ana@email.com',   '111.222.333-44', '(11) 91111-2222'),
('Bruno Costa',       'bruno@email.com', '222.333.444-55', '(11) 92222-3333'),
('Carla Mendes',      'carla@email.com', '333.444.555-66', '(11) 93333-4444');

-- Cursos de exemplo
INSERT INTO cursos (nome, descricao, carga_horaria) VALUES
('Java Básico',       'Fundamentos da linguagem Java',            60),
('Banco de Dados',    'SQL, modelagem e administração MySQL',     40),
('Desenvolvimento Web','HTML, CSS, JavaScript e frameworks',      80);

-- Matrículas de exemplo
INSERT INTO matriculas (aluno_id, curso_id, data_inicio, status) VALUES
(1, 1, CURDATE(), 'ATIVA'),
(1, 2, CURDATE(), 'ATIVA'),
(2, 1, CURDATE(), 'ATIVA'),
(3, 3, CURDATE(), 'ATIVA');

-- Usuário padrão tipo ALUNO (senha: aluno123)
INSERT INTO usuarios (nome, login, email, senha, perfil) VALUES
('Aluno Padrão', 'aluno', 'aluno@eductech.com',
 SHA2('aluno123', 256), 'ALUNO');

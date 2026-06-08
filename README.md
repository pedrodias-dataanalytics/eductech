# EducTech Manager

> Sistema desktop de gestão educacional desenvolvido em Java Swing + MySQL.  
> **ODS 4 — Educação de Qualidade** | Projeto UC Programação de Soluções Computacionais 2026/1

---

## Sobre o projeto

O **EducTech Manager** é um sistema de gestão educacional desenvolvido como projeto da UC Programação de Soluções Computacionais. O sistema está alinhado ao **ODS 4 — Educação de Qualidade** da ONU, contribuindo para a organização e o acesso à informação em instituições de ensino.

O sistema permite o gerenciamento completo de alunos, cursos, matrículas e usuários, com controle de acesso por perfil.

---

## Funcionalidades

- **Login seguro** com autenticação por usuário ou e-mail e senha criptografada (SHA-256)
- **Cadastro de novos usuários** diretamente pela tela de registro
- **Dashboard** com painel de controle exibindo totais de alunos, cursos, matrículas e usuários
- **Gerenciar Alunos** — cadastro, edição, busca e exclusão
- **Gerenciar Cursos** — cadastro, edição, busca e exclusão
- **Gerenciar Matrículas** — vinculação de alunos a cursos, atualização de status
- **Gerenciar Usuários** — exclusivo para ADMIN, com controle de perfis
- **Controle de acesso por perfil**:
  - **ADMIN** → acesso completo a todas as funcionalidades
  - **ALUNO** → acesso limitado ao dashboard e consultas

---

## Tecnologias utilizadas

| Tecnologia | Versão |
|---|---|
| Java | JDK 24 |
| Java Swing | javax.swing |
| MySQL | 8.0 |
| FlatLaf | 3.5.4 |
| MigLayout | 11.4.2 |
| MySQL Connector/J | 9.7.0 |

---

## Como executar

### Pré-requisitos
- JDK 17 ou superior
- MySQL 8.0 ou superior
- IntelliJ IDEA (ou outra IDE Java)

### 1. Configurar o banco de dados

Execute o script SQL no MySQL Workbench ou terminal:

```bash
mysql -u root -p < schema.sql
```

### 2. Configurar a conexão

Edite o arquivo `src/raven/db/Conexao.java`:

```java
private static final String SENHA = "sua_senha_mysql";
```

### 3. Adicionar dependências (JARs)

Adicione ao classpath do projeto:
- `flatlaf-3.5.4.jar`
- `miglayout-swing-11.4.2.jar`
- `miglayout-core-11.4.2.jar`
- `mysql-connector-j-9.7.0.jar`

### 4. Executar

Rode a classe principal: `raven.main.Application`

---

## Usuários padrão

| Login | Senha | Perfil |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `aluno` | `aluno123` | ALUNO |

---

## Estrutura do projeto

```
src/
 raven/
  main/        → Application.java, Dashboard.java
  login/       → Login.java, Register.java
  db/          → Conexao.java, UsuarioDAO.java, AlunoDAO.java,
                 CursoDAO.java, MatriculaDAO.java
  util/        → HashUtil.java
  aluno/       → TelaAlunos.java
  curso/       → TelaCursos.java
  matricula/   → TelaMatriculas.java
  usuario/     → TelaUsuarios.java
```

---

## ODS 4 — Educação de Qualidade

Este projeto está alinhado ao **Objetivo de Desenvolvimento Sustentável 4** da ONU, que busca assegurar a educação inclusiva, equitativa e de qualidade. O EducTech Manager contribui facilitando a gestão de instituições de ensino, organizando informações de alunos, cursos e matrículas de forma eficiente e acessível.

---

## Professores responsáveis

Cristiane Fidelix e Erica Lopes  
UC Programação de Soluções Computacionais — 2026/1

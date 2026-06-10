# EducTech Manager

Vídeo: https://youtu.be/HFY7xcb4hzc?si=plLilTg0EfAvo_6D

Sistema desktop de gestao educacional desenvolvido em Java Swing + MySQL.

**ODS 4 - Educacao de Qualidade**  
Projeto da UC Programacao de Solucoes Computacionais 2026/1

## Sobre o projeto

O EducTech Manager organiza alunos, cursos, matriculas e usuarios em uma interface desktop com controle de acesso por perfil. O sistema atende aos requisitos do projeto: interface grafica em Java Swing, banco de dados MySQL, autenticacao, dois perfis de usuario, CRUDs e dashboard com totalizadores.

## Funcionalidades

- Login seguro com autenticacao por usuario/e-mail e senha criptografada com SHA-256.
- Cadastro de usuarios com perfil inicial ALUNO.
- Dashboard com indicadores de alunos, cursos, matriculas, usuarios e status das matriculas.
- CRUD de alunos, cursos, matriculas e usuarios.
- Controle de acesso:
  - ADMIN: acesso completo.
  - ALUNO: acesso limitado conforme telas permitidas.
- Banco MySQL com dados iniciais para demonstracao.

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Plataforma da aplicacao |
| Java Swing | Interface grafica desktop |
| Maven | Build e dependencias |
| MySQL 8 | Banco de dados |
| FlatLaf | Aparencia moderna |
| MigLayout | Layout das telas |

## Como executar

### Pre-requisitos

- JDK 21 configurado no `PATH`.
- Apache Maven configurado no `PATH`.
- MySQL Server 8 em execucao.

Verifique:

```powershell
java -version
javac -version
mvn -version
```

### 1. Criar o banco

No PowerShell, dentro da pasta do projeto:

```powershell
mysql -u root -p < schema.sql
```

Se o comando `mysql` nao estiver no `PATH`, use o caminho completo do MySQL.

### 2. Configurar conexao

Por padrao, a aplicacao usa:

- Banco: `eductech_manager`
- Usuario: `root`
- Senha: vazia, ou o valor informado na variavel `DB_PASSWORD`

Tambem e possivel configurar por variaveis de ambiente:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="sua_senha_mysql"
$env:DB_URL="jdbc:mysql://localhost:3306/eductech_manager?useSSL=false&serverTimezone=America/Sao_Paulo"
```

### 3. Rodar pelo Maven

```powershell
mvn exec:java
```

### 4. Gerar JAR executavel

```powershell
mvn package
java -jar target/eductech-manager-1.0.0.jar
```

## Usuarios padrao

| Login | Senha | Perfil |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `aluno` | `aluno123` | ALUNO |

## Estrutura

```text
src/
  raven/
    main/        Application.java, Dashboard.java
    login/       Login.java, Register.java
    db/          Conexao.java e DAOs
    aluno/       TelaAlunos.java
    curso/       TelaCursos.java
    matricula/   TelaMatriculas.java
    usuario/     TelaUsuarios.java
    util/        HashUtil.java
```

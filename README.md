# EducTech Manager

Sistema desktop de gestão educacional desenvolvido em Java Swing + MySQL.

---

## Requisitos

| Ferramenta | Versão mínima |
|------------|---------------|
| JDK        | 17+           |
| MySQL      | 8.0+          |
| IDE        | IntelliJ IDEA / Eclipse / NetBeans |

**Dependências JAR** (adicione ao classpath do projeto):
- `flatlaf-3.x.jar`
- `flatlaf-fonts-roboto-x.x.jar`
- `miglayout-swing-x.x.jar`
- `mysql-connector-j-8.x.jar`

---

## Configuração do banco de dados

### 1. Execute o script SQL

```sql
mysql -u root -p < schema.sql
```

Ou abra o arquivo `schema.sql` no MySQL Workbench e execute.

### 2. Ajuste a senha do MySQL

Edite o arquivo `src/raven/db/Conexao.java`:

```java
private static final String SENHA = "SUA_SENHA_AQUI";
```

---

## Usuários padrão (criados pelo script)

| Login   | Senha     | Perfil |
|---------|-----------|--------|
| `admin` | `admin123`| ADMIN  |
| `aluno` | `aluno123`| ALUNO  |

> **Importante:** As senhas são armazenadas com hash SHA-256, igual ao `SHA2()` do MySQL.

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

## Funcionalidades

### Tela de Login
- Autenticação por login ou e-mail + senha (SHA-256)
- Link para cadastro de nova conta

### Tela de Cadastro
- Cria conta com perfil ALUNO
- Validação de campos e verificação de duplicidade

### Dashboard
- Sidebar com menu de navegação
- Cards com totais de alunos, cursos, matrículas e usuários
- Atalhos rápidos para as telas de gerenciamento
- Botão de sair (volta ao login)

### Gerenciar Alunos / Cursos / Matrículas / Usuários
- Listagem em tabela com busca em tempo real
- Formulário lateral para inserir/editar
- Exclusão com confirmação (exclusão lógica para alunos, usuários e cursos)

### Controle de acesso
- **ADMIN**: acesso completo a todas as telas
- **ALUNO**: acesso ao dashboard e telas de consulta; sem acesso à tela de Usuários

---

## Ponto de entrada

Classe principal: `raven.main.Application`

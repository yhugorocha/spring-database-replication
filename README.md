# PostgreSQL Replication Study

Projeto criado para estudar replicação de banco de dados com PostgreSQL usando uma topologia simples de `primary` + `replica` e uma API em Spring Boot para demonstrar escrita no nó principal e leitura no nó replicado.

## Objetivo

A ideia deste repositório é servir como laboratório de estudo para:

- entender o básico de replicação física no PostgreSQL;
- subir um ambiente local com `docker-compose`;
- separar leitura e escrita na aplicação;
- testar fallback de leitura quando a réplica estiver indisponível.

## Arquitetura

O ambiente possui 3 partes principais:

- `pg-primary`: banco principal, responsável por receber escritas;
- `pg-replica`: banco em modo réplica, sincronizado a partir do `primary`;
- aplicação Spring Boot: expõe endpoints HTTP e usa dois `DataSource`, um para escrita e outro para leitura.

Fluxo da aplicação:

1. `POST /items` grava no banco `primary` (`localhost:5432`);
2. `GET /items` tenta ler da `replica` (`localhost:5433`);
3. se a `replica` falhar, a aplicação faz fallback e lê do `primary`.

## Tecnologias

- Java 21
- Spring Boot 3
- Spring Web
- Spring JDBC
- PostgreSQL 16
- Docker Compose

## Estrutura do projeto

```text
.
|-- docker-compose.yml
|-- primary/
|   |-- init/01-init.sql
|   |-- pg_hba.conf
|   `-- postgresql.conf
|-- replica/
|   `-- setup-replica.sh
`-- src/
    `-- main/
        |-- java/.../controller
        |-- java/.../service
        |-- java/.../repository
        `-- resources/application.yaml
```

## Como a replicação foi configurada

O `primary` está configurado com:

- `wal_level = replica`
- `max_wal_senders = 10`
- `max_replication_slots = 10`
- `hot_standby = on`

Além disso:

- o arquivo `pg_hba.conf` libera conexão para cliente comum e para o usuário de replicação;
- o script [`primary/init/01-init.sql`](./primary/init/01-init.sql) cria o usuário `replicator` e inicializa tabelas de estudo;
- o script [`replica/setup-replica.sh`](./replica/setup-replica.sh) usa `pg_basebackup` para clonar o `primary` e iniciar a réplica em modo standby.

Isso caracteriza um cenário de replicação física simples, útil para laboratório local.

## Pré-requisitos

Para rodar o projeto localmente, você precisa de:

- Docker e Docker Compose
- Java 21

Opcional:

- `psql` para inspecionar os bancos manualmente

## Credenciais padrão

Banco principal e réplica:

- database: `appdb`
- usuário da aplicação: `app`
- senha da aplicação: `app123`

Usuário de replicação:

- usuário: `replicator`
- senha: `replica123`

## Como executar

### 1. Suba os bancos

```bash
docker compose up -d
```

### 2. Rode a aplicação

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação sobe na porta `8080`.

## Endpoints

### Criar item

```bash
curl -X POST http://localhost:8080/items \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Notebook\"}"
```

### Listar itens

```bash
curl http://localhost:8080/items
```

Resposta esperada:

```json
[
  {
    "id": 1,
    "name": "item inicial"
  },
  {
    "id": 2,
    "name": "Notebook"
  }
]
```

## Como validar a replicação

Você pode conferir o comportamento do ambiente diretamente no PostgreSQL.

### Verificar se cada nó está no papel correto

Primary:

```bash
psql -h localhost -p 5432 -U app -d appdb -c "SELECT pg_is_in_recovery();"
```

Replica:

```bash
psql -h localhost -p 5433 -U app -d appdb -c "SELECT pg_is_in_recovery();"
```

Resultado esperado:

- `false` no `primary`
- `true` na `replica`

### Conferir os dados nos dois nós

Primary:

```bash
psql -h localhost -p 5432 -U app -d appdb -c "SELECT * FROM items ORDER BY id;"
```

Replica:

```bash
psql -h localhost -p 5433 -U app -d appdb -c "SELECT * FROM items ORDER BY id;"
```

Os registros devem aparecer nos dois bancos após a replicação.

## Configuração da aplicação

Os `DataSource` estão definidos em [`src/main/resources/application.yaml`](./src/main/resources/application.yaml):

- `writer`: aponta para `localhost:5432`
- `reader`: aponta para `localhost:5433`

No código:

- a escrita usa o `writerJdbcTemplate`;
- a leitura usa o `readerJdbcTemplate`;
- se a leitura na réplica falhar, o serviço usa o banco principal como fallback.

O schema usado pela API não é criado automaticamente pelo Spring. Neste projeto, a tabela `items` é criada pelo script de inicialização do container PostgreSQL.

## Observações importantes

- As credenciais do projeto são fixas e existem apenas para estudo local. Não use essa configuração em produção.
- O script de inicialização do Postgres roda apenas quando o volume do container é criado pela primeira vez.
- Se você alterar scripts de `init` e quiser recriar tudo do zero, use:

```bash
docker compose down -v
docker compose up -d
```

- Este projeto demonstra um cenário simples de replicação. Ele não cobre tópicos como failover automático, eleição de líder, balanceamento de leitura ou observabilidade de atraso de réplica.


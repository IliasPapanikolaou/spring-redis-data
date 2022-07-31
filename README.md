# Redis data
Redis can act as database, message broker or cache

> Source: https://developer.redis.com/develop/java

## Mock Data
> Use app.generateData=true in application.properties to generate mock data

## Redis CLI

Delete Redis DB

```Bash
FLUSHDB
```

Get All keys from redis CLI

```Bash
KEYS com.unipi.ipap.springdatarediscrud.entity.Role*
```

Get types from keys

```Bash
TYPE com.unipi.ipap.springdatarediscrud.entity.Role:0bb80747-ac0d-458e-8811-a9d5c0f331a8 # hash
TYPE com.unipi.ipap.springdatarediscrud.entity.Role # set
```

Get details from a hash key
```Bash
HGETALL com.unipi.ipap.springdatarediscrud.entity.Role:915ef3ba-42b2-437e-9432-736d600bf4e5
```
Result:
```
1) "_class"
2) "com.unipi.ipap.springdatarediscrud.entity.Role"
3) "id"
4) "915ef3ba-42b2-437e-9432-736d600bf4e5"
5) "name"
6) "admin"
```

Get members of a set
```Bash
SMEMBERS com.unipi.ipap.springdatarediscrud.entity.Role
```
Result:
```
1) "0bb80747-ac0d-458e-8811-a9d5c0f331a8"
2) "915ef3ba-42b2-437e-9432-736d600bf4e5"
```

## Search with RediSearch (CLI)

You can see the index information with the following command in the Redis CLI:
```Bash
FT.INFO "books-idx"
````
Full-text Search Queries
RediSearch is a full-text search engine, allowing the application to run powerful queries. For example, to search all books that contain “networking”-related information, you would run the following command:
```Bash
FT.SEARCH books-idx "networking" RETURN 1 title
````
```Bash
FT.SEARCH books-idx "@title:networking" RETURN 1 title
````
Prefix matches:
```Bash
FT.SEARCH books-idx "clo*" RETURN 4 title subtitle authors.[0] authors.[1]
````
Fuzzy search:
```Bash
FT.SEARCH books-idx "%scal%" RETURN 2 title subtitle
```
Unions:
```Bash
FT.SEARCH books-idx "rust | %scal%" RETURN 3 title subtitle authors.[0]
```

## Run Redis Stack on Docker

How to install Redis Stack using Docker
To get started with Redis Stack using Docker, you first need to select a Docker image:

- redis/redis-stack contains both Redis Stack server and RedisInsight. This container is best for local development because you can use the embedded RedisInsight to visualize your data.

- redis/redis-stack-server provides Redis Stack server only. This container is best for production deployment.

## Getting started

### redis/redis-stack-server

To start Redis Stack server using the redis-stack-server image, run the following command in your terminal:

```bash
docker run -d --name redis-stack-server -p 6379:6379 redis/redis-stack-server:latest
```

You can connect the Redis Stack server database to your RedisInsight desktop application.

### redis/redis-stack

To start Redis Stack developer container using the redis-stack image, run the following command in your terminal:

```bash
docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
```

The docker run command above also exposes RedisInsight on port 8001. You can use RedisInsight by pointing your browser to localhost:8001.

## Connect with redis-cli

You can then connect to the server using redis-cli, just as you connect to any Redis instance.

If you don’t have redis-cli installed locally, you can run it from the Docker container:

```bash
docker exec -it redis-stack redis-cli
```

## Configuration

### Persistence

To persist your Redis data to a local path, specify -v to configure a local volume. This command stores all data in the local directory local-data:

```bash
docker run -v /local-data/:/data redis/redis-stack:latest
```

### Ports

If you want to expose Redis Stack server or RedisInsight on a different port, update the left hand of portion of the -p argument. This command exposes Redis Stack server on port 10001 and RedisInsight on port 13333:

```bash
docker run -p 10001:6379 -p 13333:8001 redis/redis-stack:latest
```

### Config files

By default, the Redis Stack Docker containers use internal configuration files for Redis. To start Redis with local configuration file, you can use the -v volume options:

```bash
docker run -v `pwd`/local-redis-stack.conf:/redis-stack.conf -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
```

### Environment variables

To pass in arbitrary configuration changes, you can set any of these environment variables:

- REDIS_ARGS: extra arguments for Redis

- REDISEARCH_ARGS: arguments for RediSearch

- REDISJSON_ARGS: arguments for RedisJSON

- REDISGRAPH_ARGS: arguments for RedisGraph

- REDISTIMESERIES_ARGS: arguments for RedisTimeSeries

- REDISBLOOM_ARGS: arguments for RedisBloom

For example, here's how to use the REDIS_ARGS environment variable to pass the requirepass directive to Redis:

```bash
docker run -e REDIS_ARGS="--requirepass redis-stack" redis/redis-stack:latest
```

Here's how to set a retention policy for RedisTimeSeries:

```bash
docker run -e REDISTIMESERIES_ARGS="RETENTION_POLICY=20" redis/redis-stack:latest
```

### Docker Compose
```yaml
version: '3.9'
services:
  redis:
    image: 'redislabs/redismod:edge'
    ports:
      - '6379:6379'
    volumes:
      - ./data:/data
    entrypoint: >
      redis-server
        --loadmodule /usr/lib/redis/modules/redisai.so
        --loadmodule /usr/lib/redis/modules/redisearch.so
        --loadmodule /usr/lib/redis/modules/redisgraph.so
        --loadmodule /usr/lib/redis/modules/redistimeseries.so
        --loadmodule /usr/lib/redis/modules/rejson.so
        --loadmodule /usr/lib/redis/modules/redisbloom.so
        --loadmodule /var/opt/redislabs/lib/modules/redisgears.so
        --appendonly yes
    deploy:
      replicas: 1
      restart_policy:
        condition: on-failure
```
## CRUD:

#### POST Request:
```Bash
{
"productId": 101,
"name": "Playstation 5",
"quantity": 4,
"price": 600.0
}
```

```Bash
{
  "password": "9yNvIO4GLBdboI",
  "name": "Georgia Spencer",
  "id": -5035019007718357598,
  "email": "georgia.spencer@example.com"
}
```
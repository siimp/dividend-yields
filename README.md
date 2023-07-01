# Nasdaq Baltic Dividend Yields

#### website  
https://dividend-yields.siimp.ee/
#### backend
https://github.com/siimp/dividend-yields-scraper.git
#### frontend
https://github.com/siimp/dividend-yields-frontend.git
#### server config
https://github.com/siimp/siimp.ee.git


## Api description

### Stock
* http://localhost:8080/stock

### Stock info
* http://localhost:8080/stock-info

### Dividend
* http://localhost:8080/dividend

### DividendYield
* http://localhost:8080/dividend-yield?year=2019
* http://localhost:8080/dividend-yield/future

## Actuator management endpoints
* http://localhost:8080/actuator/health
* http://localhost:8080/actuator/caches
* http://localhost:8080/actuator/dividend-update-job
* http://localhost:8080/actuator/stock-price-job
* http://localhost:8080/actuator/stock-info-job


## Development
```
docker compose up
```

## With podman
```
podman run --name dividend-yields-postgres --rm -p 5432:5432 \
-e POSTGRES_USER=dividend-yields \
-e POSTGRES_PASSWORD=dividend-yields \
-e POSTGRES_DB=dividend-yields \
-v ./volumes/postgresql:/var/lib/postgresql/data \
docker.io/library/postgres:14.3-alpine
```


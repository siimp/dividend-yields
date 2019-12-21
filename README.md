# Nasdaq Baltic Dividend Yields

#### website  
https://dividend-yields.siimp.ee
#### code for scraper
https://github.com/siimp/dividend-yields-scraper
#### code for frontend
https://github.com/siimp/dividend-yields-frontend
#### server config
https://github.com/siimp/siimp.ee.git

## Actuator endpoints to manually start scheduled jobs
* http://localhost:8080/actuator/dividend-update-job
* http://localhost:8080/actuator/stock-price-job
* http://localhost:8080/actuator/stock-info-job


## Api request samples

### Stock
http://localhost:8080/stock

### Dividend
http://localhost:8080/dividend

### DividendYield
http://localhost:8080/dividend-yield?year=2019
http://localhost:8080/dividend-yield/future





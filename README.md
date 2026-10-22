# WPC.EE backend

Spring Boot REST API WPC.EE kodulehele, e-poele ja B2B partnerikontole. Frontend on eraldi repos [wpc-frontend](https://github.com/sandramusta/wpc-frontend).

Projekt on IT-kursuse lõputöö ja põhineb kursusel tehtud veebipoe projektil ([veebipood](https://github.com/sandramusta/veebipood)).

## Mida see teeb

- **E-pood:** tooted ja variandid (kõrgus × värv) koos hinna, laoseisu ja eeldatava tarneajaga.
- **Tellimused:** päring (`INQUIRY`) või ost (`PURCHASE`). Ost vähendab laoseisu.
- **Makse:** EveryPay demo. Kui makse ebaõnnestub, läheb kaup lattu tagasi.
- **B2B partnerid:** partneri tase ja allahindlus (Partner −10%, Pro −15%), ettevõtte andmed ja logo, paigalduse tunnihind.
- **Partneri projektid:** kliendi projekt → hinnapakkumine → klient kinnitab → materjalide tellimine → arve kliendile.
- **Vahemälu:** tootenimekiri on Redises 10 minutit, laoseisu muutudes tühjendatakse.

Ülesehitus, olemid ja endpointid: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Tehnoloogiad

| | |
| --- | --- |
| Keel | Java 21 |
| Raamistik | Spring Boot 4.1 (Web MVC, Data JPA, Cache) |
| Andmebaas | PostgreSQL |
| Vahemälu | Redis (Dockeris) |
| Makse | EveryPay demo API |
| API dokumentatsioon | Swagger (springdoc-openapi) |
| Muu | Lombok, Maven |

## Käivitamine

### 1. Andmebaas

Loo PostgreSQL-is andmebaas `wpc`. Tabelid loob Hibernate ise (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Seadistus

Loo fail `src/main/resources/application-dev.properties`. **See fail on `.gitignore`-s ja ei lähe GitHubi**, sest seal on paroolid.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wpc
spring.datasource.username=postgres
spring.datasource.password=SINU_PAROOL
spring.jpa.hibernate.ddl-auto=update

everypay.url=https://igw-demo.every-pay.com/api/v4
everypay.api-username=SINU_EVERYPAY_KASUTAJA
everypay.api-secret=SINU_EVERYPAY_VÕTI
everypay.account-name=EUR3D1
everypay.customer-url=http://lvh.me:4200/makse
```

### 3. Redis

```bash
docker run -d --name redis -p 6379:6379 redis
```

Hiljem piisab käsust `docker start redis`. Kui Redis ei tööta, käib rakendus ikka edasi: logisse tuleb hoiatus ja andmed võetakse otse andmebaasist.

### 4. Käivita backend

IntelliJ-s käivita `WpcApplication` või käsurealt:

```bash
./mvnw spring-boot:run
```

Backend töötab aadressil `http://localhost:8080`.

### 5. Swagger

Kõik endpointid ja nende proovimine: `http://localhost:8080/swagger-ui/index.html`

## Miks `lvh.me`, mitte `localhost`

EveryPay ei luba makse järel tagasi suunata `localhost` aadressile (viga *customer_url is invalid*). `lvh.me` on domeen, mis suunab samuti sinu enda arvutisse (`127.0.0.1`), aga EveryPay võtab selle vastu. Seepärast:

- `everypay.customer-url` on `http://lvh.me:4200/makse`;
- `CorsConfig` lubab päringuid aadressidelt `http://localhost:4200` ja `http://lvh.me:4200`.

Päris serveris on siin päris domeen, näiteks `https://wpc.ee/makse`.

## Kaustad

```
src/main/java/ee/sandra/wpc/
  controller/   REST endpointid
  service/      äriloogika: tellimus, makse, projektid, arved
  repository/   andmebaasi päringud (Spring Data JPA)
  entity/       andmebaasi tabelid
  dto/          päringute ja vastuste kuju
  CorsConfig    millised frontendi aadressid tohivad backendiga rääkida
  CacheConfig   mis juhtub, kui Redis ei tööta
```

## Teadaolevad piirangud

- Sisselogimist veel ei ole. Partneri vaates valitakse partner demoribalt.
- Raha on `double` tüüpi, päris rakenduses peaks olema `BigDecimal`.
- `ddl-auto=update` sobib arenduseks, päris serveris tuleks kasutada andmebaasi migratsioone (nt Flyway).
- Arvele lisatakse alati 24% käibemaks, ka siis, kui partneril pole KMKR numbrit.

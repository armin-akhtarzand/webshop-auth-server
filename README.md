# Webshop Auth Service

Auth Service ansvarar för användarkonton och autentisering i webshoppen.
Tjänsten lagrar användare i PostgreSQL, kontrollerar inloggningsuppgifter
och utfärdar signerade JWT-token.

## Ansvar och samverkan

- Hanterar registrering och användaruppgifter.
- Lagrar lösenord som BCrypt-hashar.
- Utfärdar JWT-token med användarens identitet och roller.
- Tillhandahåller aktuell användare via /auth/me.
- Publicerar den publika JWT-nyckeln via /auth/jwks.

Frontend skickar sina API-anrop till Gateway.
Gateway vidarebefordrar autentiseringsanrop till Auth Service.

HttpOnly-cookien hanteras av Gateway. Auth Service verifierar
JWT-token som skickas vidare i Authorization-headern.

Product Service hanterar produkter, Order Service hanterar beställningar
och betalningar, och Mail Service skickar orderbekräftelser.

## Tekniker

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Security och OAuth2 Resource Server
- Spring Data JPA och PostgreSQL
- Bean Validation
- Springdoc OpenAPI 3.0.2 och Swagger UI
- Lombok
- Maven och Maven Wrapper

## Förutsättningar

- JDK 17.
- Tillgång till en PostgreSQL-databas.
- Ett matchande RSA-nyckelpar för signering och verifiering av JWT.
- Git för att klona projektet.

Maven Wrapper ingår, så Maven behöver inte installeras separat.

## Hämta projektet

```bash
git clone https://github.com/armin-akhtarzand/webshop-auth-server.git
cd webshop-auth-server
```

## Miljövariabler

Konfigurationen finns i src/main/resources/application.properties.

Följande variabler måste anges:

- DB_URL: JDBC-adress till PostgreSQL.
  Exempel: jdbc:postgresql://localhost:5432/webshop_auth
- DB_USERNAME: databasens användarnamn.
- DB_PASSWORD: databasens lösenord.
- JWT_PUBLIC_KEY: Base64-kodad publik RSA-nyckel i X.509 DER-format.
- JWT_PRIVATE_KEY: Base64-kodad privat RSA-nyckel i PKCS#8 DER-format.

Följande variabler har standardvärden:

- PORT: 8080.
- JWT_ISSUER: http://localhost:8080
- JWT_EXPIRATION_MINUTES: 60.
- JWT_KEY_ID: jwt-key-1.

JWT-nycklarna ska anges som Base64-text utan PEM-rubriker.
Den privata och publika nyckeln måste tillhöra samma nyckelpar.
JWT_ISSUER måste stämma med den issuer som används vid verifiering.

Ange variablerna i terminalen eller i IntelliJ under
Run → Edit Configurations → Environment variables.

En .env-fil läses inte automatiskt av den visade konfigurationen.
Lösenord och privata nycklar ska inte sparas i Git.

## Databas

Tjänsten använder PostgreSQL och lagrar användarnas ID, e-postadress,
lösenordshash och roll. E-postadressen måste vara unik.
Standardrollen för nya användare är ROLE_USER.

Projektet är konfigurerat med:

```properties
spring.jpa.hibernate.ddl-auto=none
```

Det innebär att databasen och tabellerna måste finnas innan
funktionerna används. Ingen SQL-skriptfil har identifierats
i det granskade repot.

För lokal utveckling med en ny, tom utvecklingsdatabas kan Hibernate
skapa tabellerna genom att tillfälligt ange:

```text
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Starta tjänsten en gång med denna inställning. Stoppa sedan tjänsten,
ta bort variabeln och starta igen med standardinställningen none.

Använd detta endast för den egna utvecklingsdatabasen.
För en befintlig gemensam databas används teamets förberedda schema.

## Starta lokalt

Ange miljövariablerna innan tjänsten startas.

### macOS och Linux

```bash
./mvnw spring-boot:run
```

Om skriptet saknar körbehörighet:

```bash
bash ./mvnw spring-boot:run
```

### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

Tjänsten körs som standard på:

```text
http://localhost:8080
```

Om PORT ändras ska motsvarande adress också användas i Gateway.
För att använda hela webshoppen behöver även Gateway, Frontend
och de tjänster som respektive funktion anropar vara igång.

## API-dokumentation

När tjänsten är startad med standardporten finns dokumentationen här:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI: http://localhost:8080/v3/api-docs

Skyddade anrop direkt till Auth Service kräver en giltig
Bearer-token. Frontend använder i stället Gateway och dess cookie-flöde.

## Test och bygge

Kör tester:

```bash
./mvnw test
```

Bygg och verifiera projektet:

```bash
./mvnw clean verify
```

På Windows används mvnw.cmd i stället för ./mvnw.

## Felsökning

- Saknad miljövariabel: kontrollera databasvariabler och JWT-nycklar.
- Fel vid inläsning av nycklar: kontrollera Base64-format,
  nyckelformat och att nycklarna hör ihop.
- Databasanslutningen misslyckas: kontrollera adress,
  användarnamn, lösenord och att databasen är tillgänglig.
- Tabell saknas: kontrollera att databasschemat har förberetts.
- Porten används redan: stoppa processen eller ange en annan PORT.
- Status 401: kontrollera tokens giltighet, signatur och issuer.

## Systemets repositories

- [Auth Service](https://github.com/armin-akhtarzand/webshop-auth-server)
- [Product Service](https://github.com/armin-akhtarzand/webshop-product-service)
- [Order Service](https://github.com/armin-akhtarzand/webshop-order-service)
- [Mail Service](https://github.com/armin-akhtarzand/webshop-mail-service)
- [Gateway](https://github.com/SofiaTekle/webshop-gateway)
- [Frontend](https://github.com/SofiaTekle/webshop-frontend)
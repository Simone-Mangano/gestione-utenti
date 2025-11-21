## Gestione Utenti – Spring Boot Full REST Application

Applicazione completa per la gestione degli utenti con CRUD, ricerca avanzata, filtri, ordinamento, sicurezza Basic Auth, interfaccia frontend statica, Swagger/OpenAPI, H2 Database e script SQL per MySQL.


---

##  Tecnologie utilizzate
        backend
- Java 17  
- Spring Boot 3.x  
- Spring Web  
- Spring Data JPA  
- Spring Security (Basic Auth)  
- H2 Database  
- MySQL
- Lombok  
- OpenAPI / Swagger UI  

        frontend
- HTML5  
- CSS3  
- JavaScript (Fetch API)  
- Filtri, sorting , paginazione  

---

##  Autenticazione

L’app usa HTTP Basic Authentication con utenti in-memory:

| Ruolo | Username |  Password   |   Permessi   |
|-------|----------|-------------|--------------|
| ADMIN | `admin`  | `admin1234` | CRUD completo|
|  USER | `user`   | `user1234`  | Solo lettura |

---
##  Funzionalità principali

     API REST
- Creazione utente  
- Lettura singola e lista  
- Ricerca   
- Ricerca esatta  
- Modifica  
- Eliminazione  

      FRONTEND
- Login popup 
- Dashboard  
- Tabella utenti dinamica  
- Ricerca live  
- Filtri per intervallo date  
- Ordinamento:
  - per cognome  
  - per nome  
  - cronologico  
- Reset filtri  
- Paginazione  
- Modale creazione  
- Modale modifica  
- Logout  

---



## Test automatici

Presenti in:
src/test/java/org/exi/demo/

Coprono:
	•	Repository
	•	Service
	•	Eccezioni
	•	Vincoli duplicati

## Frontend

Il frontend si trova in:
src/main/resources/static/

##  Build & Run

---

### 🔨 Build del progetto (da terminale, dentro la cartella del progetto)

build del progetto
      
      ./mvnw clean install

run del progetto
   
      ./mvnw spring-boot:run

---

## Accesso all’applicazione

Una volta avviata l'app, aprire

    http://localhost:8080/index.html
    
##  Documentazione API

Swagger UI disponibile su:

http://localhost:8080/swagger-ui/index.html

 API REST (Sintesi)

GET
	•	/api/utenti
	•	/api/utenti/{id}
	•	/api/utenti/search?q=...
	•	/api/utenti/searchExact?cf=...

POST
	•	/api/utenti (ADMIN)

PUT
	•	/api/utenti/{id} (ADMIN)

DELETE
	•	/api/utenti/{id} (ADMIN)

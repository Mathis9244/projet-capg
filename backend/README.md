# Empreinte Carbone — Hackathon 2026 Capgemini

Application fullstack pour **calculer l’empreinte carbone d’un site physique** (bâtiments, matériaux, consommation énergétique, exploitation), avec indicateurs, comparaison de sites et historisation.

Référence : **HACKATHON 2026 - Cahier des charges-Projet Dev.pdf**

---

## Conformité au cahier des charges

| Exigence | Statut | Détail |
|----------|--------|--------|
| **Front web Angular** | ✅ | Dashboard, formulaire de saisie, KPIs, thème Capgemini |
| **Backend Java Spring Boot, API REST** | ✅ | Contrôleurs REST, services, JPA |
| **Base de données** | ✅ | PostgreSQL (prod) ; H2 en mémoire (dev par défaut) |
| **Authentification JWT** | ✅ | Login, guard, interceptor côté front ; SecurityConfig + JwtService côté back |
| **Saisie des caractéristiques du site** | ✅ | Surface, parking, conso énergétique, matériaux (béton, acier, verre, bois), employés, année |
| **Calcul CO₂ construction + exploitation** | ✅ | Facteurs d’émission par matériau et énergie (DataInitializer) |
| **KPIs** | ✅ | CO₂ total, CO₂/m², CO₂/employé, répartition construction / exploitation |
| **Comparaison de plusieurs sites** | ✅ | Page « Comparer des sites » + API `GET /api/sites/compare` |
| **Historisation** | ✅ | Sites et matériaux persistés en base, liste des sites + dernier calcul |
| **Déploiement Docker** | ✅ | Dockerfile + docker-compose pour le backend |
| **Documentation API (Swagger)** | ✅ | springdoc-openapi, `/swagger-ui.html` |
| **Application mobile React Native** | ⏳ | Prévue (non livrée dans ce dépôt) |
| **Graphiques dynamiques** | ⏳ | KPIs et tableaux présents ; graphiques (charts) à enrichir |
| **Export PDF** | ⏳ | Non implémenté |
| **API externe type ADEME** | ⏳ | Facteurs d’émission en base ; pas d’appel API ADEME |

**Paliers :**  
- **Palier 1** : ✅ Socle technique, formulaire, calcul CO₂, affichage Angular, historique en base.  
- **Palier 2** : ✅ Dashboard avec KPIs et comparaison ; ⏳ mobile et graphiques avancés.  
- **Palier 3** : ✅ Comparaison ; ⏳ export PDF, courbes d’évolution, API ADEME.

---

## Architecture et séparation Backend / Frontend

Le dépôt est structuré en **deux dossiers racine** :

```
projet-capg/
├── backend/                 # ce module (Spring Boot)
│   ├── pom.xml
│   ├── src/main/java/com/capg/hackathon/
│   │   ├── config/          # Sécurité, JWT, CORS, OpenAPI, données initiales
│   │   ├── site/            # Entités, DTOs, repository, service, contrôleur sites
│   │   └── user/            # Entités, auth, contrôleur login
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── application-postgres.properties
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── .dockerignore
│
├── frontend/
│   └── web/                 # Angular (UI + appels REST)
│       ├── angular.json
│       ├── package.json
│       └── src/app/...
│
└── README.md                 # README racine (démarrage rapide)
```

- **Backend** : `backend/` (API REST, calcul CO₂, persistance, JWT, Swagger).  
- **Frontend** : `frontend/web/` (UI Angular, auth guard + interceptor, appels HTTP).

La communication se fait **uniquement par API REST** (JSON) ; le frontend ne contient pas de base de données ni de calcul d’empreinte.

---

## Prérequis

- **Backend** : Java 17+, Maven 3.x  
- **Frontend** : Node.js 18+, npm  
- **Docker** (optionnel) : pour lancer le backend en conteneur

---

## Lancer le projet

### 1. Backend (obligatoire)

**Option A — En local (sans Docker)**  
À la racine du projet :

```bash
cd "c:\Users\mathi\OneDrive\Bureau\projet cap g\backend"
mvn spring-boot:run
```

- API : **http://localhost:8080**  
- Swagger : **http://localhost:8080/swagger-ui.html**  
- Health : **http://localhost:8080/actuator/health**  
- Base : H2 en mémoire (données perdues à l’arrêt). Compte de démo : **admin** / **admin**.

**Option B — Avec Docker**

```bash
cd "c:\Users\mathi\OneDrive\Bureau\projet cap g\backend"
docker compose up -d --build
```

Même URLs que ci-dessus. La base reste H2 en mémoire dans le conteneur.

### 2. Frontend (Angular)

Dans un **autre** terminal :

```bash
cd "c:\Users\mathi\OneDrive\Bureau\projet cap g\frontend\web"
npm install
npm start
```

- Application : **http://localhost:4200**  
- Connexion : **admin** / **admin**, puis accès au dashboard Sites et à la page Comparer.

### 3. Utilisation rapide

1. Ouvrir http://localhost:4200 → redirection vers la page de connexion si besoin.  
2. Se connecter avec **admin** / **admin**.  
3. Sur la page **Sites** : remplir le formulaire (nom, localisation, surface, conso, matériaux…), cliquer sur **Calculer & enregistrer**.  
4. Consulter la liste des sites et le **Dernier calcul**.  
5. Aller sur **Comparer des sites** : choisir deux sites, cliquer sur **Comparer**.

---

## API REST (résumé)

| Méthode | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/login` | Connexion (body: `username`, `password`) → JWT |
| GET | `/api/sites` | Liste des sites (authentifié) |
| GET | `/api/sites/{id}` | Détail d’un site |
| POST | `/api/sites` | Création d’un site + calcul CO₂ (body: SiteRequest) |
| GET | `/api/sites/compare?siteA=&siteB=` | Comparaison de deux sites |

Documentation interactive : **http://localhost:8080/swagger-ui.html** (après démarrage du backend).

---

## Configuration backend

- **Base de données** : `src/main/resources/application.properties`. Par défaut : H2 en mémoire. Pour PostgreSQL, modifier `spring.datasource.*` et activer le driver PostgreSQL.  
- **JWT** : `app.jwt.secret` et `app.jwt.expiration-ms` dans `application.properties`.  
- **CORS** : autorisé pour `http://localhost:4200` (voir `SecurityConfig`).

---

## Frontend (Angular)

- **Entrée** : `web/src/app/app.config.ts` (HTTP, router, intercepteur auth).  
- **Routes** : `web/src/app/app.routes.ts` (login, sites, compare, guard).  
- **API** : `web/src/app/sites/site-api.service.ts` (baseUrl `http://localhost:8080/api`).  
- **Thème** : couleurs Capgemini dans `web/src/styles.scss` (variables `--cap-*`).

Voir aussi **web/README.md** pour le détail du frontend.

---

## Licence et contexte

Projet réalisé dans le cadre du **Hackathon 2026 Capgemini**.  
Code fourni à titre de démonstration et de base pour les paliers suivants (mobile, graphiques, export PDF, API ADEME).

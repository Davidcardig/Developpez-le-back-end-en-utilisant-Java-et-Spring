# ChâTop - Portail de Location Immobilière

## 📋 Description du Projet

ChâTop est une application web full-stack de gestion de locations immobilières. Le projet comprend :
- **Back-end** : API REST développée avec Spring Boot (Java 17)
- **Front-end** : Application Angular 14
- **Base de données** : MySQL pour la persistance des données

L'application permet aux utilisateurs de :
- S'inscrire et se connecter de manière sécurisée (JWT)
- Consulter les annonces de location
- Créer et gérer des annonces
- Envoyer des messages aux propriétaires

---

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé les outils suivants :

### Pour le Back-end
- **Java Development Kit (JDK)** : Version 17
  - [Télécharger JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  - Vérifiez l'installation : `java -version`
  
- **Maven** : Version 3.6+ (ou utilisez le wrapper Maven inclus)
  - Vérifiez l'installation : `mvn -version`

### Pour le Front-end
- **Node.js** : Version 14.x ou supérieure
  - [Télécharger Node.js](https://nodejs.org/)
  - Vérifiez l'installation : `node -v`
  
- **npm** : Version 6.x ou supérieure (inclus avec Node.js)
  - Vérifiez l'installation : `npm -v`

### Pour la Base de données
- **MySQL Server** : Version 8.0 ou supérieure
  - [Télécharger MySQL](https://dev.mysql.com/downloads/mysql/)
  - Vérifiez l'installation : `mysql --version`

---

## 🗄️ Installation de la Base de Données

### Étape 1 : Créer la base de données

1. Connectez-vous à MySQL :
```bash
mysql -u root -p
```

2. Créez la base de données `chatop` :
```sql
CREATE DATABASE chatop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Vérifiez la création :
```sql
SHOW DATABASES;
```

4. Quittez MySQL :
```sql
EXIT;
```

### Étape 2 : Exécuter le script SQL

Le script SQL se trouve dans : `Front-end/ressources/sql/script.sql`

1. Exécutez le script pour créer les tables :
```bash
mysql -u root -p chatop < "Front-end/ressources/sql/script.sql"
```

Ou depuis MySQL :
```sql
USE chatop;
SOURCE Front-end/ressources/sql/script.sql;
```

### Étape 3 : Vérifier les tables créées

```sql
USE chatop;
SHOW TABLES;
```

Vous devriez voir les tables suivantes :
- **USERS** : Gestion des utilisateurs
- **RENTALS** : Gestion des annonces de location
- **MESSAGES** : Gestion des messages

---

## 🚀 Installation et Lancement du Projet

### PARTIE 1 : Configuration du Back-end

#### Étape 1 : Naviguer vers le dossier Back-end

```bash
cd "C:\Users\david\Desktop\OCR\Projet 3 bis\Developpez-le-back-end-en-utilisant-Java-et-Spring - Copie\Back-end"
```

#### Étape 2 : Configurer les variables d'environnement

Créez un fichier `.env` à la racine du dossier `Back-end` avec le contenu suivant :

```properties
# Configuration Base de données
DB_USERNAME=root
DB_PASSWORD=votre_mot_de_passe_mysql

# Configuration JWT
JWT_SECRET_KEY=votre_cle_secrete_jwt_minimum_32_caracteres
JWT_TOKEN_EXPIRATION=86400000

# Configuration Répertoire Images
IMAGES_DIRECTORY=src/main/resources/static/images
```

**⚠️ Important :**
- Remplacez `votre_mot_de_passe_mysql` par votre mot de passe MySQL
- Remplacez `votre_cle_secrete_jwt_minimum_32_caracteres` par une clé secrète de votre choix (minimum 32 caractères)
- L'expiration du token est définie en millisecondes (86400000 ms = 24 heures)

#### Étape 3 : Installer les dépendances Maven

Sur Windows PowerShell :
```powershell
.\mvnw.cmd clean install
```

Sur Linux/Mac :
```bash
./mvnw clean install
```

Ou si Maven est installé globalement :
```bash
mvn clean install
```

#### Étape 4 : Créer le dossier pour les images

Créez le dossier où seront stockées les images uploadées :
```powershell
mkdir -Force src\main\resources\static\images
```

#### Étape 5 : Lancer l'application Back-end

Sur Windows PowerShell :
```powershell
.\mvnw.cmd spring-boot:run
```

Sur Linux/Mac :
```bash
./mvnw spring-boot:run
```

Ou avec Maven :
```bash
mvn spring-boot:run
```

L'API démarre sur : **http://localhost:8080**

✅ **Vérification :** Vous devriez voir dans la console :
```
Started ChatopApplication in X.XXX seconds
```

---

### PARTIE 2 : Configuration du Front-end

#### Étape 1 : Naviguer vers le dossier Front-end

Ouvrez un **nouveau terminal** et exécutez :

```bash
cd "C:\Users\david\Desktop\OCR\Projet 3 bis\Developpez-le-back-end-en-utilisant-Java-et-Spring - Copie\Front-end"
```

#### Étape 2 : Installer les dépendances npm

```bash
npm install
```

Cette commande peut prendre quelques minutes.

#### Étape 3 : Lancer l'application Front-end

```bash
npm start
```

Ou avec Angular CLI :
```bash
ng serve
```

L'application Angular démarre sur : **http://localhost:4200**

✅ **Vérification :** Vous devriez voir :
```
✔ Compiled successfully.
```

## 📚 Documentation de l'API (Swagger)

Une fois le back-end lancé, la documentation interactive de l'API est accessible via **Swagger UI** :

### 🔗 URL Swagger
```
http://localhost:8080/swagger-ui/index.html
```

### Endpoints principaux

#### 🔐 Authentification (`/api/auth`)
- `POST /api/auth/register` : Inscription d'un nouvel utilisateur
- `POST /api/auth/login` : Connexion et récupération du token JWT
- `GET /api/auth/me` : Récupération des informations de l'utilisateur connecté

#### 🏠 Locations (`/api/rentals`)
- `GET /api/rentals` : Liste de toutes les locations
- `GET /api/rentals/{id}` : Détails d'une location
- `POST /api/rentals` : Créer une nouvelle location
- `PUT /api/rentals/{id}` : Modifier une location

#### 💬 Messages (`/api/messages`)
- `POST /api/messages` : Envoyer un message

#### 👤 Utilisateurs (`/api/user`)
- `GET /api/user/{id}` : Informations d'un utilisateur

#### 🖼️ Images (`/api/images`)
- `GET /api/images/{filename}` : Récupérer une image uploadée

---

## 📁 Structure du Projet

### Back-end
```
Back-end/
├── src/
│   ├── main/
│   │   ├── java/com/chatop/
│   │   │   ├── configuration/      # Configuration Spring Security, JWT, etc.
│   │   │   ├── controllers/        # Contrôleurs REST
│   │   │   ├── dtos/               # Data Transfer Objects
│   │   │   ├── exceptions/         # Gestion des exceptions
│   │   │   ├── mappers/            # Mappers Entity ↔ DTO
│   │   │   ├── models/             # Entités JPA
│   │   │   ├── repositories/       # Repositories JPA
│   │   │   └── services/           # Logique métier
│   │   └── resources/
│   │       ├── application.properties  # Configuration de l'application
│   │       └── static/images/          # Stockage des images
│   └── test/                       # Tests unitaires
├── pom.xml                         # Dépendances Maven
└── .env                            # Variables d'environnement (à créer)
```

### Front-end
```
Front-end/
├── src/
│   ├── app/
│   │   ├── components/             # Composants réutilisables
│   │   ├── features/               # Modules fonctionnels
│   │   │   ├── auth/               # Authentification
│   │   │   └── rentals/            # Gestion des locations
│   │   ├── guards/                 # Guards de navigation
│   │   ├── interceptors/           # Intercepteurs HTTP (JWT)
│   │   ├── services/               # Services Angular
│   │   └── interfaces/             # Interfaces TypeScript
│   └── environments/               # Configuration des environnements
├── ressources/
│   ├── sql/script.sql              # Script de création de la BDD
│   ├── mockoon/                    # Configuration Mockoon
│   └── postman/                    # Collection Postman
└── package.json                    # Dépendances npm
```

---

## 👨‍💻 Auteur

Projet réalisé dans le cadre de la formation OpenClassrooms - Développeur Full Stack Java et Angular



# TESTING

Ce projet a fait l'objet d'une série de tests pour assurer la redondance, l'embriquement et la validation dans les cas extrêmes de chaque fonctionnalité. Différents outils, détaillés ci-dessous, ont été utilisés pour mener à bien ces tests, aussi bien sur le **frontend** que le **backend**. Ils se divisent en trois groupes : les tests **unitaires**, **d'intégration** et **end-to-end**.

## Instructions d'exécution

### Frontend (Unitaires + Intégration)
```
npm test
```

### Frontend (E2E)
```
npm run cy:open
```

### Backend (Unitaires + Intégration)
```
.\mvnw verify
```

## Couverture de code
L'application devait avoir au moins **70%** de son code couvert par ces tests, aussi bien avec le frontend que la backend :

#### Backend

![backend_coverage_85%](/img/p3coverage-back.png "P3BackCoverage.")

#### Frontend
![frontend_coverage_95%](/img/p3coverage-front.png "P3FrontCoverage.")


## Critères de validation

### Unitaires

Cette série de tests sert à valider le bon fonctionnement de chaque partie de l'application lorsqu'elle est isolée du reste. Voici quelques exemples de cas de tests :

| Entité | Test | Résultat attendu |
|:-------------:|:-------------:|:-------------:|
| UserController (Backend) | Envoi d'une requête HTTP POST sur l'endpoint "/login" avec des données valides | Une réponse HTTP 200 OK. |
| UserService (Backend) | Authentification d'un utilisateur via un email et un mot de passe | Validation du mot de passe avec celui de la BDD (un String préconfiguré) et renvoi d'un String. |
| DashboardComponent (Frontend) | Chargement de la liste des fichiers de l'utilisateur (un tableau) | Appel de la méthode getFiles() de son FileService et modification de la variable filesList. |

### Intégration

Cette série de tests sert à valider le bon fonctionnement de l'application lorsque ses fonctionnalités sont réunies et embriquées (ex : UserController, UserService et UserRepository pour le backend). Voici quelques exemples de cas de tests :

| Entité | Test | Résultat attendu |
|:-------------:|:-------------:|:-------------:|
| UserController (Backend) | Envoi d'une requête HTTP POST sur l'endpoint "/register" avec des données valides, puis recherche de l'utilisateur dans la BDD | Une réponse HTTP 200 OK ; l'utilisateur est présent dans la BDD |
| FileController (Backend) | Envoi d'une requête HTTP POST sur l'endpoint "/upload" avec des données valides, puis recherche du fichier dans la BDD | Une réponse HTTP 200 OK ; les métadonnées du fichier sont présentes dans la BDD |

### E2E

Cette série de tests valide le bon fonctionnement de l'application avec toutes ses parties connectées - front et back. Voici quelques exemples de cas de tests :

| Entité | Test | Résultat attendu |
|:-------------:|:-------------:|:-------------:|
| DownloadComponent | Clic sur le bouton "Télécharger" | Démarre le téléchargement du fichier sélectionné dans le navigateur ; on le retrouve dans le dossier des téléchargements. |
| UploadComponent | Sélection d'un fichier à upload, puis clic sur le bouton "Téléverser" | Redirection vers la page "Dashboard" avec une nouvelle entrée dans la liste des fichiers téléversés. |
| LoginComponent | Saisie d'un email et d'un mot de passe valide, puis clic sur le bouton "Connexion" | Redirection vers la page "Dashboard" de l'utilisateur en question. |

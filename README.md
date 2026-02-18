# DataShare

Une application qui simplifie le partage de fichiers !

## Préquis

Cette application nécessite plusieurs éléments :
* Node 10.8.2
* Docker 28.3.3 (avec Docker Desktop au besoin)

## Procédures de démarrage

Suivez ces instructions pour démarrer l'application :

1. Ouvrir Docker Desktop (sans quoi, le backend ne fonctionnera pas.)
2. À la racine du projet (/dataShare/), ouvrir un terminal de commande.
3. Démarrer l'**API** :
```
..\dataShare> cd .\api\
..\dataShare\api> .\mvnw spring-boot:run
```
4. Démarrer le **Frontend** :
```
..\dataShare> cd .\clientui\
..\dataShare\clientui> ng serve
```

## Utilisation

Par défaut, l'API se lancera sur **localhost:8080** ; le Frontend, lui, sur **localhost:4200**. Pour utiliser l'application, il suffit de se rendre sur l'adresse du Frontend. Cela redirigera l'utilisateur sur la page d'authentification.

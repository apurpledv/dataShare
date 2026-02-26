# MAINTENANCE

En prenant en compte l'architecture de l'application, certaines dépendances ou frameworks critiques (Angular CLI par exemple) **doivent être mis à jour avant la mise en production**. Une nouvelle version de Jest compatible devra être fournie.

Pour mettre à jour Angular, utilisez cette commande :

```
npm audit fix --force
```

## Évolution

Pour assurer la stabilité et la sécurité du projet, il faudra penser à mettre à jour les frameworks utilisés (Spring Boot et Angular) à chaque nouvelle version majeure.

# SECURITY

Afin d'assurer la robustesse de l'application, un scan de sécurité a été réalisé.

## Instructions d'exécution

```
npm audit
```

## Résultat du scan
```
# npm audit report

tar  <=7.5.6
Severity: high
node-tar is Vulnerable to Arbitrary File Overwrite and Symlink Poisoning via Insufficient Path Sanitization - https://github.com/advisories/GHSA-8qq5-rm4j-mr97
Race Condition in node-tar Path Reservations via Unicode Ligature Collisions on macOS APFS - https://github.com/advisories/GHSA-r6q2-hw4h-h46w
node-tar Vulnerable to Arbitrary File Creation/Overwrite via Hardlink Path Traversal - https://github.com/advisories/GHSA-34x7-hfp2-rc4v
fix available via `npm audit fix --force`
Will install @angular/cli@21.1.4, which is a breaking change
node_modules/tar
  pacote  5.0.0 - 21.0.0
  Depends on vulnerable versions of tar
  node_modules/pacote
    @angular/cli  6.2.9 || 7.3.0-beta.0 - 20.3.14 || 21.0.0-next.0 - 21.0.0-rc.6
    Depends on vulnerable versions of pacote
    node_modules/@angular/cli

3 high severity vulnerabilities

To address all issues (including breaking changes), run:
  npm audit fix --force
```

## Interprétation et décision(s) prise(s)

Notre application est exposée à plusieurs vulnérabilités dites "de haute sévérité" à cause de certains modules installés. Ce problème est lié à la version d'Angular CLI utilisée - la version 19. Certaines dépendances, comme Jest, fonctionnent avec cette version d'Angular et non une autre. Ainsi, il a été choisi de ne pas mettre à jour Angular.

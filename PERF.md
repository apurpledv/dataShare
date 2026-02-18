# PERF

Certaines fonctionnalités diffèrent grandement dans leur temps d'exécution en fonction de ce que l'utilisateur fait (ex : téléchargement et téléversement). Pour assurer que notre application répond à nos besoins de manière efficace, des critères d'acceptation des performances ont été établis :

### Téléversement

* Cas 1 "Fichiers de moins de 1 Ko" : <= 100 ms

![cas1perfUpload1](img/p3perfA1.png "cas1perfUpload1")

![cas1perfUpload2](img/p3perfA2.png "cas1perfUpload2")

* Cas 2 "Fichiers de moins de 1 Mo" : <= 3000 ms

![cas2perfUpload1](img/p3perfB1.png "cas2perfUpload1")

![cas2perfUpload2](img/p3perfB2.png "cas2perfUpload2")

* Cas 3 "Fichiers de moins de 20 Mo" : <= 5000 ms

![cas3perfUpload1](img/p3perfC1.png "cas3perfUpload1")

![cas3perfUpload2](img/p3perfC2.png "cas3perfUpload2")

### Interprétation

Comme vu sur ces captures d'écran, notre endpoint "/upload" rentre bien dans nos critères d'acceptation. Pour réitérer d'autres tests sur d'autres endpoints, une simple inspection de la page (F12) et de l'onglet "Network" lors d'une interaction avec l'API révèle les temps de réponses.

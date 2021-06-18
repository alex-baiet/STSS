
                         README.txt

############################################################
    
    FONCTIONNALITES INPLEMENTEES

- Mis à part les amélioration, et certaines cartes, tous le
  contenu nécessaire pour le rendu 2 a été implémenté.

- Seul les cartes nécessitant de choisir une autre carte
  dans la main/pioche/défausse, les cartes dont les effets
  étaient basés sur l'amélioration de cartes, ainsi que
  certaines cartes aux effets uniques les plus compliqués à
  intégrer n'ont pas été intégrés. 
  Au total :
    - 66 / 75 cartes d'Ironclad ont été intégrées,
    - 58 / 75 cartes de Silent ont été intégrées,
    - 26 / 35 cartes Colorless ont été intégrées,
    - 4 / 5 cartes Status ont été intégrées (Aucun effet
    existant ne peut donner la carte Void, donc il n'y a
    aucun intérêt à l'intégrer),
    - aucune cartes Curse n'a été intégrée (aucun effet
    dans le jeu permet de récupérer des Curses).

- Le système d'utilisation des potions a été intégré, ainsi
  que 25 / 35 potions. 

############################################################
    
    ORGANISATION DU PROGRAMME

La class RunManager est la classe mère contenant la fonction 
main du jeu.
Il contient aussi : 
- un Data où sont fais tous les calculs, et où sont traitées
  toutes les actions du joueurs.
- un UI ne s'occupant que d'afficher le jeu. Aucune 
  interaction avec les données du jeu n'est faite dedans.

Pour le reste de l'organisation, allez voir l'uml STSS.uml.

############################################################
    
    CHOIX TECHNIQUES / ALGORITHMIQUES

Les adversaires (classe Enemy) utilisent des classe Strategy 
différentes, permettant de faire varier leur pattern en 
fonction des besoins.
En raison des pattern trop différents, les données des
Enemy ont été intégrées directement dans le code.

Les cartes n'utilisent pas de strategie : tous les effets
possibles sont contenus dans des champs, et sont tous 
appliqués lors de la résolution de l'effet de la carte.
Les cartes sont chargées à partir des fichiers textes dans
"STSS/data/cards".

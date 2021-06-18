package fr.silex.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import fr.silex.RunManager;
import fr.silex.RunManager.Scene;
import fr.silex.action.Card;
import fr.silex.action.Potion;
import fr.silex.data.Datas.EnemyCategory;
import fr.silex.entity.Enemy;
import fr.silex.entity.Hero;
import fr.silex.room.Drop;
import fr.silex.ui.Button;
import fr.silex.ui.UI;
import fr.umlv.zen5.Event;

public class DataBattleRoom implements Data {
	public static enum BattleState {
		FIGHTING, LOOTING, DEFEAT
	}
	public static enum TypeChoice {
		CARD, POTION
	}
	
	private static ArrayList<String> log = new ArrayList<>(); // Contient le texte a afficher en combat.
	
	private Hero hero;
	private BattleState state = BattleState.FIGHTING;
	private int numChoice = -1; /* En fonction de l'etat du combat, correspond a la selection de la cartes de la main, 
	                               la potion a utiliser, ou la carte  choisi du drop de fin de combat. */
	private int width; // Résolution de l'écran (utilisé pour créer les boutons)
	private int height;
	private EnemyCategory enemyCategory;
	
	// variables de combat
	private ArrayList<Enemy> enemies;
	private Button[] handButs; // liste de bouton contenant les images des cartes et la zone de clic
	private Button endBut;
	private Button[] enemiesBut; // liste de boutons invisibles pour selectionner l'ennemie.
	private TypeChoice typeChoice = null;
	private Button[] potionsButs = new Button[3];
	private Button usePotionBut;

	// Variables de la phase de loot
	private Drop drop = null; // Butin de fin de combat
	private Button goldLootBut;
	private Button potionLootBut;
	private Button cardLootBut;
	private Button[] cardChoiceButs;
	private boolean choosingCard = false;
	private Button skipCard;

	public DataBattleRoom(Hero hero, EnemyCategory category) {
		this(hero, category, RunManager.getWidthScreen(), RunManager.getHeightScreen());
	}	

	public DataBattleRoom(Hero hero, EnemyCategory category, int widthScreen, int heightScreen) {
		log = new ArrayList<>();
		this.hero= hero;
		this.enemyCategory = category;
		
		switch (category) {
		case NORMAL:
			this.enemies = Datas.getRandomEnemies(2, category);
			break;
		case ELITE:
			this.enemies = Datas.getRandomEnemies(1, category);
			break;
		case BOSS: // ok la c'est pareil que ELITE mais ca risque de changer
			this.enemies = Datas.getRandomEnemies(1, category);
			break;
		default:
			break;
		}
		printlnEnemies();
		width = widthScreen;
		height = heightScreen;
		drop = new Drop(hero, category);
		
		// Initialistaion du combat
		prepareBattle();

		// Chargement des boutons
		loadHandButtons();
		loadPotionsButtons();
		loadEnemiesButtons();
		loadEndButton();
	}

	public DataBattleRoom(Hero hero, ArrayList<Enemy> enemies) {
		log = new ArrayList<>();
		this.hero= hero;
		this.enemyCategory = EnemyCategory.NORMAL;
		this.enemies = new ArrayList<>(enemies);
		
		printlnEnemies();
		width = RunManager.getWidthScreen();
		height = RunManager.getHeightScreen();
		drop = new Drop(hero, enemyCategory);
		
		// Initialistaion du combat
		prepareBattle();

		// Chargement des boutons
		loadHandButtons();
		loadPotionsButtons();
		loadEnemiesButtons();
		loadEndButton();
	}
	
	/**
	 * Recupere le message a afficher en combat, en plus de l'afficher dans la console.
	 */
	public static void println(String message) {
		System.out.println(message);
		String[] lines = message.split("\n");
		for (String line : lines) {
			log.add(line);
		}
	}
	
	public static ArrayList<String> getLog() {
		return new ArrayList<>(log);
	}
	
	/**
	 * Résout tous les effets de combat en fonction de l'action du joueur.
	 * @return True si le joueur a demandé a sortir de la BattleRoom.
	 */
	@Override
	public Scene resolvePlayerAction(Event event) {

		switch (state) {
		case FIGHTING:
			tryPlayCard(event);
			tryUsePotion(event);
			tryEndTurn(event);
			tryEndFight();
			updateEnemies();
			return null;
		case LOOTING:
			tryLootItem(event);
			if (tryExitRoom(event)) { 
				if (enemyCategory == EnemyCategory.BOSS || RunManager.inDebugMod()) return Scene.MAIN_MENU;
				else return Scene.MAP;
			}
			return null;
		case DEFEAT:
			if (tryExitRoom(event)) return Scene.MAIN_MENU;
			return null;
		default:
			throw new RuntimeException("L'état "+state+" n'a pas été intégré.");
		}
		
	}
	
	/** Supprime / Met à  jour la strategy des ennemies */
	private void updateEnemies() {
		int i=0;
		// Suppression des ennemies morts (Inutilisé pour le moment, géré par BattleRoom)
		while (i<enemies.size()) {
			if (enemies.get(i).getLife() <= 0) {
				enemies.remove(i);
			} else {
				i++;
			}
		}
		// Changement de stratégiedes ennemies restants.
		for (Enemy enemy : enemies) {
			enemy.checkChangeStrategy(enemies);
		}
	}
	
	private void printlnEnemies() {
		if (enemies.size() == 1) {
			println("Un "+enemies.get(0).name()+" vous fait face !");
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append("Un "+enemies.get(0).name());
			for (int i=1; i<enemies.size()-1; i++) {
				builder.append(",\nun "+enemies.get(i).name());
			}
			builder.append("\net un "+enemies.get(enemies.size()-1).name()+" vous font face !");
			println(builder.toString());
		}
	}
	
	/**
	 * Préparation des entités au combat
	 */
	private void prepareBattle() {
		hero.prepareBattle(enemies);
		for (Enemy enemy : enemies) {
			enemy.prepareAct();
		}
		hero.newTurn();
	}
	
	private void endPlayerTurn() {
		hero.endTurn();
		removeDeadEnemies();
		println("----- Tour des ennemies -----");
		
		// Tour des ennemies
		for (Enemy enemy : enemies) {
			enemy.newTurn();
		}
		removeDeadEnemies();
		for (Enemy enemy : enemies) {
			enemy.act(hero, enemies);
		}
		removeDeadEnemies();
		for (Enemy enemy : enemies) {
			enemy.endTurn();
			enemy.prepareAct();
		}
		removeDeadEnemies();

		println("----- Tour du joueur -----");
		hero.newTurn();
		removeDeadEnemies();
		numChoice=-1;
		typeChoice=null;
		loadHandButtons();
		loadPotionsButtons();
		loadEnemiesButtons();
	}
	
	/**
	 * Joue un carte du joueur.
	 */
	private void playCard(int targetEnemy) {
		hero.deck().playCard(enemies, numChoice, targetEnemy);
		
		numChoice = -1;
		typeChoice = null;
		removeDeadEnemies();
		loadHandButtons();
		loadPotionsButtons();
	}
	
	/**
	 * Supprime les ennemies morts de la liste d'ennemies.
	 */
	private void removeDeadEnemies() {
		int i = 0;
		while (i<enemies.size()) {
			if (enemies.get(i).getLife() <= 0) {
				DataBattleRoom.println(enemies.get(i).name() + " est vaincu.");
				Enemy dead = enemies.remove(i);
				if (dead.explodeOnDeath()) {
					DataBattleRoom.println(dead.name()+" explose !");
					for (int j=0; j<enemies.size(); j++) {
						enemies.get(j).damage(dead.getMaxLife());
					}
					removeDeadEnemies(); // On refait la suppression d'ennemies pour ne pas oublier les nouvelles victimes.
					return;
				}
			} else {
				i++;
			}
		}
		loadEnemiesButtons();
	}
	
	/**
	 * Charge tous les boutons pour les cartes de la main.
	 */
	private void loadHandButtons() {
		int ax=width/8, ay=height*3/4, bx=width*7/8, by=height; // Définition de la zone d'affichage
		float cardRatio = 0.8f; // = largeur image carte / hauteur image carte
		int widthCard = (int)((by-ay) * cardRatio); // Largeur pris par chaque carte sur l'ecran.
		
		ArrayList<Card> hand = hero.deck().hand();
		handButs = new Button[hand.size()];
		
		// ici on positionne les cartes dans la zone d'affichage.
		if (widthCard * hand.size() < bx-ax) { 
			// Si il y a assez d'espace en largeur pour afficher 
			//les cartes sur toutes la hauteur de la zone de carte, alors...
			for (int i=0; i<hand.size(); i++) {
				handButs[i] = new Button(ax + (bx-ax)/2 - widthCard*hand.size()/2 + widthCard*i, ay, widthCard, by-ay, 
						null, (numChoice==i && typeChoice==TypeChoice.CARD ? UI.SELECT_COLOR : null), hand.get(i).img()); // bonne chance pour comprendre ca ^.^
			}
		} 
		else {
			for (int i=0; i<hand.size(); i++) {
				handButs[i] = new Button(ax + (bx-ax)*i/hand.size(), ay, (bx-ax)/hand.size(), by-ay, 
						null, (numChoice==i && typeChoice==TypeChoice.CARD ? UI.SELECT_COLOR : null), hand.get(i).img());
			}
		}
	}
	
	/**
	 * Charge la zone de sélection des ennemies.
	 */
	private void loadEnemiesButtons() {
		int ax=width/2, ay=0, bx=width, by=height/2; // Définition de la zone d'affichage
		enemiesBut = new Button[enemies.size()];
		
		for (int i=0; i<enemies.size(); i++) {
			enemiesBut[i] = new Button(ax + (bx-ax)*i/enemies.size(), ay, (bx-ax)/enemies.size(), by-ay, null, UI.BUTTON_COLOR, null); // Création des zones de clics invisibles pour le joueur.
		}
	}
	
	/**
	 * Charge le bouton de fin de tour.
	 */
	private void loadEndButton() {
		int x = width*7/8;
		int y = height*3/4;
		int widthBut = width/8;
		int heightBut = height/4;
		
		switch (state) {
		case FIGHTING:
			endBut = new Button(x, y, widthBut, heightBut, "Fin du tour", UI.BUTTON_COLOR, null);
			break;
		case LOOTING:
			if (enemyCategory != EnemyCategory.BOSS) {
				endBut = new Button(x, y, widthBut, heightBut, "Continuer", UI.BUTTON_COLOR, null);
			} else {
				endBut = new Button(x, y, widthBut, heightBut, "Terminer votre\naventure... <(^-^)>", UI.BUTTON_COLOR, null);
			}
			break;
		case DEFEAT:
			endBut = new Button(x, y, widthBut, heightBut, "Terminer votre\naventure... (T_T)", UI.BUTTON_COLOR, null);
			break;
		}
	}
	
	private void loadPotionsButtons() {
		int ax=width/4, ay=0, bx=width*2/4, by=height/8; // Définition de la zone d'affichage
		Potion[] potions = hero.getPotions();
		int spaceWidth = (bx-ax) / potions.length;
		
		for (int i=0; i<potionsButs.length; i++) {
			if (potions[i] != null) {
				potionsButs[i] = new Button(ax + spaceWidth * i, ay, spaceWidth, by-ay, 
						null, (numChoice==i && typeChoice==TypeChoice.POTION ? UI.SELECT_COLOR : null), potions[i].img());
			} else {
				potionsButs[i] = null;
			}
		}

		if (numChoice != -1 && typeChoice == TypeChoice.POTION) {
			usePotionBut = new Button(width/4, height/8, width/4, height/12, 
					"use "+potions[numChoice].name()+" : \n"+potions[numChoice].description(), 
					potions[numChoice].needTarget() ? Color.BLACK : UI.BUTTON_COLOR, 
					null);
		}
		else {
			usePotionBut = new Button(width/4, height/8, width/4, height/12, 
					"use Potion", Color.BLACK, null);
		}
	}
	
	private void loadLootsButtons() {
		int gold = drop.getGold();
		Potion potion = drop.getPotion();
		Card[] cards = drop.getCards();
		
		int heightBut = height/12;
		int marginBut = height/24;
		int yPos = height/8;

		goldLootBut = gold > 0 ? 
				new Button(width/2, yPos + (heightBut+marginBut)*0, width/2, heightBut, gold+" or", UI.BUTTON_COLOR, null) :
				null;
		potionLootBut = potion != null ? 
				new Button(width/2, yPos + (heightBut+marginBut)*1, width/2, heightBut, potion.name()+"\n"+potion.description(), UI.BUTTON_COLOR, null) :
				null;
		cardLootBut = cards != null ? 
				new Button(width/2, yPos + (heightBut+marginBut)*2, width/2, heightBut, "Carte au choix", UI.BUTTON_COLOR, null) :
				null;
		if (cardLootBut != null) {
			cardChoiceButs = new Button[cards.length];

			for (int i = 0; i < cards.length; i++) {
				cardChoiceButs[i] = new Button(
						width/8 + (width*3/4)*i/cards.length,
						height/8,
						(width*3/4)/cards.length,
						height*3/4, 
						null, null, cards[i].img());
			}
		}
		
		skipCard = new Button(width/3, height*7/8, width/3, height/8, "Ne prendre aucune carte", UI.BUTTON_COLOR, null);
	}

	/**
	 * Termine le tour du joueur si il clic sur le bouton fin de tour.
	 */
	private void tryEndTurn(Event event) {
		if (endBut.isClicked(event)) {
			endPlayerTurn();
		}
	}

	/**
	 * Joue un carte de la main du joueur si le joueur a ciblé une carte et un ennemie.
	 */
	private void tryPlayCard(Event event) {
		ArrayList<Card> hand = hero.deck().hand();
		
		// Le joueur choisi une carte
		for (int i=0; i<handButs.length; i++) {
			if (handButs[i].isClicked(event) && hand.get(i).isUsable(hero)) { // Si le joueur clic sur la carte et peut jouer la carte, ...
				numChoice = i;
				typeChoice = TypeChoice.CARD;
				if (hand.get(i).needTarget()) {
					loadHandButtons();
					loadPotionsButtons();
				} else {
					playCard(0);
				}
				break;
			}
		} 
		
		if (numChoice != -1 && typeChoice == TypeChoice.CARD) { // Le joueur cible un ennemie
			for (int i=0; i<enemiesBut.length; i++) {
				if (enemiesBut[i].isClicked(event)) {
					playCard(i);
				}
			}
		}
	}
	
	/**
	 * Utilise ou sélectionne la potion.
	 */
	private void tryUsePotion(Event event) {
		// Le joueur choisi une potion
		for (int i=0; i<potionsButs.length; i++) {
			if (potionsButs[i] != null && potionsButs[i].isClicked(event)) { // Si le joueur clic sur la carte et peut jouer la potion, ...
				numChoice = i;
				typeChoice = TypeChoice.POTION;
				loadHandButtons();
				loadPotionsButtons();
			}
		}
		
		if (numChoice != -1 && typeChoice == TypeChoice.POTION) { // Le joueur cible un ennemie (ou pas)
			if (hero.getPotions()[numChoice].needTarget()) {
				for (int i=0; i<enemiesBut.length; i++) {
					if (enemiesBut[i].isClicked(event)) {
						usePotion(i);
					}
				}
			} else if (usePotionBut.isClicked(event)) {
				usePotion(0);
			}
		}
	}
	
	private void tryLootItem(Event event) {
		if (!choosingCard) { // On choisit le loot a recuperer
			if (goldLootBut != null && goldLootBut.isClicked(event)) {
				hero.addGold(drop.lootGold());
				loadLootsButtons();
			}
			if (potionLootBut != null && potionLootBut.isClicked(event)) {
				hero.addPotion(drop.lootPotion());
				loadLootsButtons();
				loadPotionsButtons();
			}
			if (cardLootBut != null && cardLootBut.isClicked(event)) {
				choosingCard = true;
			}
		} else { // On choisi la carte a recuperer
			for (int i = 0; i < cardChoiceButs.length; i++) {
				if (cardChoiceButs[i].isClicked(event)) {
					hero.deck().addCard(drop.lootCard(i));
					choosingCard = false;
					loadLootsButtons();
				}
			}
			if (skipCard.isClicked(event)) {
				choosingCard = false;
			}
		}
	}
	
	private boolean tryExitRoom(Event event) {
		if (endBut.isClicked(event) && (state==BattleState.DEFEAT || state==BattleState.LOOTING)) {
			return true;
		}
		return false;
	}

	/**
	 * Termine le combat si le joueur ou les ennemies sont vaincus.
	 */
	private void tryEndFight() {
		if (hero.getLife() <= 0) {
			state = BattleState.DEFEAT;
			println("Vous avez perdu...");
			loadEndButton();
		} else if (enemies.size() == 0) {
			state = BattleState.LOOTING;
			println("Victoire ! Récupérez vos récompenses.");
			loadEndButton();
			loadLootsButtons();
			numChoice = -1; // Va servir pour choisir une carte dans loot
		}
	}
	
	/**
	 * Utilise une potion du Hero sur l'ennemie à la position indiqué dans l'array.
	 */
	private void usePotion(int numTarget) {
		hero.usePotion(numChoice, numTarget);
		
		numChoice = -1;
		typeChoice = null;
		removeDeadEnemies();
		loadPotionsButtons();
		loadHandButtons();
	}
	
	// getters
	public Hero getHero() { return hero; }
	public ArrayList<Enemy> getEnemies() { return new ArrayList<Enemy>(enemies); }
	public Button[] getHandButtons() { return Arrays.copyOf(handButs, handButs.length); }
	public Button getEndButton() { return endBut; }
	public Button[] getEnemiesButtons() { return Arrays.copyOf(enemiesBut, enemiesBut.length); }
	public Button[] getPotionsButtons() { return Arrays.copyOf(potionsButs, potionsButs.length); }
	public Button getUsePotionButton() { return usePotionBut; }
	public BattleState getBattleState() { return state; }
	public Button getGoldLootButton() { return goldLootBut; }
	public Button getPotionLootButton() { return potionLootBut; }
	public Button getCardLootButton() { return cardLootBut; }
	public Button[] getCardChoiceButton() { return Arrays.copyOf(cardChoiceButs, cardChoiceButs.length); }
	public boolean isChoosingCard() { return choosingCard; }
	public EnemyCategory getEnemyCategory() { return enemyCategory; }
	public Button getSkipCardButton() { return skipCard; }
}

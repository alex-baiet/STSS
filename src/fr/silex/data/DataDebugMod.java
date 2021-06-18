package fr.silex.data;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import fr.silex.RunManager;
import fr.silex.RunManager.Scene;
import fr.silex.action.Card;
import fr.silex.action.Potion;
import fr.silex.entity.Enemy;
import fr.silex.ui.Button;
import fr.silex.ui.UI;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
import fr.umlv.zen5.KeyboardKey;

public class DataDebugMod implements Data {
	public static enum StateDebug {
		CHOOSING_CARDS, CHOOSING_POTIONS, CHOOSING_ENEMIES, FIGHT
	}
	
	private final int width;
	private final int height;
	private final int marginTop;
	private final int heightResult;
	private final int marginLeft;
	private final int widthResult;
	
	// Toutes les choses choisis a tester
	private final Card[] chosenCards = new Card[10];
	private final Potion[] chosenPotions = new Potion[3];
	private final Enemy[] chosenEnemies = new Enemy[3];

	private final Button[] chosenCardsBut = new Button[10];
	private final Button[] chosenPotionsBut = new Button[3];
	private final Button[] chosenEnemiesBut = new Button[3];
	
	// Barre de recherche
	private StringBuilder searchBar = new StringBuilder();
	private Button deleteCharBut;
	private Button deleteSearchBut;
	// Boutons de selection.
	private Button confirmBut;
	private final ArrayList<Button> cardsBut = new ArrayList<>();
	private final ArrayList<Button> potionsBut = new ArrayList<>();
	private final ArrayList<Button> enemiesBut = new ArrayList<>();

	private final ArrayList<String> cardsName = new ArrayList<>(); // permet de recuperer la bonne carte lors du clic
	private final ArrayList<String> potionsName = new ArrayList<>();
	private final ArrayList<String> enemiesName = new ArrayList<>();
	
	private StateDebug stateDebug = StateDebug.CHOOSING_CARDS;
	private int selection = -1;
	private BufferedImage selectionImg = null;
	private String selectionTxt = null;
	
	public DataDebugMod() {
		width = RunManager.getWidthScreen();
		height = RunManager.getHeightScreen();
		marginTop = height/4;
		heightResult = height/2;
		marginLeft = width/5;
		widthResult = width*4/5;
		
		confirmBut = new Button(width*7/8, height*3/4, width/8, height/4, "Confirmer", UI.BUTTON_COLOR, null);
		loadSearchBar();
		loadCardsButtons();
		loadChosenCardsButtons();
	}
	
	@Override
	public Scene resolvePlayerAction(Event event) {
		tryChangeSearch(event);
		trySelectObject(event);
		if (tryConfirm(event)) return Scene.BATTLE_ROOM;
		return null;
	}
	
	private void tryChangeSearch(Event event) {
		boolean changed = false;
		if (event.getAction() == Action.KEY_PRESSED) {
			KeyboardKey key = event.getKey();
			if (RunManager.isLetter(key)) { // On change la recherche
				searchBar.append(key.toString());
				changed = true;
			}
		}
		if (deleteCharBut.isClicked(event) && searchBar.length() > 0) {
			searchBar.deleteCharAt(searchBar.length()-1);
			changed = true;
		}
		if (deleteSearchBut.isClicked(event) && searchBar.length() > 0) {
			searchBar = new StringBuilder();
			changed = true;
		}
		
		if (changed) {
			switch (stateDebug) {
			case CHOOSING_CARDS: 
				loadCardsButtons();
				break;
			case CHOOSING_POTIONS: 
				loadPotionsButtons();
				break;
			case CHOOSING_ENEMIES: 
				loadEnemiesButtons();
				break;
			default:
				throw new IllegalStateException();
			}
		}
	}
	
	private void trySelectObject(Event event) {
		ArrayList<Button> objBut;
		
		switch (stateDebug) { // Définition de l'array de selection
		case CHOOSING_CARDS:
			objBut = cardsBut;
			break;
		case CHOOSING_POTIONS:
			objBut = potionsBut;
			break;
		case CHOOSING_ENEMIES:
			objBut = enemiesBut;
			break;
		default:
			throw new IllegalStateException(stateDebug + " n'est pas intégrée.");
		}
		
		for (int i=0; i<objBut.size(); i++) {
			if (objBut.get(i).isClicked(event)) {
				changeSelection(i);
				return; // On arrete comme on sait qu'on ne peut pas sélectionner plus d'un élément.
			}
		}
		
		if (selection >= 0) {
			switch (stateDebug) { // Ajout de la selection aux objets a utiliser
			case CHOOSING_CARDS:
				for (int i=0; i<chosenCardsBut.length; i++) {
					if (chosenCardsBut[i].isClicked(event)) {
						chosenCards[i] = Datas.getCard(cardsName.get(selection));
						chosenCardsBut[i].setImg(chosenCards[i].img());
					}
				}
				break;
			case CHOOSING_POTIONS:
				for (int i=0; i<chosenPotionsBut.length; i++) {
					if (chosenPotionsBut[i].isClicked(event)) {
						chosenPotions[i] = Datas.getPotion(potionsName.get(selection));
						chosenPotionsBut[i].setImg(chosenPotions[i].img());
					}
				}
				break;
			case CHOOSING_ENEMIES:
				for (int i=0; i<chosenEnemiesBut.length; i++) {
					if (chosenEnemiesBut[i].isClicked(event)) {
						chosenEnemies[i] = Datas.getEnemy(enemiesName.get(selection));
						chosenEnemiesBut[i].setImg(chosenEnemies[i].img());
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	/** Retourne true si le joueur essai de quitter la scene. */
	private boolean tryConfirm(Event event) {
		if (confirmBut.isClicked(event)) {
			switch (stateDebug) {
			case CHOOSING_CARDS:
				stateDebug = StateDebug.CHOOSING_POTIONS;
				searchBar = new StringBuilder();
				loadPotionsButtons();
				loadChosenPotionsButtons();
				selectionImg = null;
				selectionTxt = null;
				break;
			case CHOOSING_POTIONS:
				stateDebug = StateDebug.CHOOSING_ENEMIES;
				searchBar = new StringBuilder();
				loadEnemiesButtons();
				loadChosenEnemiesButtons();
				selectionImg = null;
				selectionTxt = null;
				break;
			case CHOOSING_ENEMIES:
				if (!getChosenEnemies().isEmpty()) return true;
			default:
				break;
			}
		}
		
		return false;
	}
	
	private void loadSearchBar() {
		int marginBut = 5;

		if (deleteCharBut == null) {
			deleteCharBut = new Button(marginBut + width/4, height/8, width/4 - marginBut*2, height/8, "Supprimer\nun caractère", UI.BUTTON_COLOR, null);
		}
		if (deleteSearchBut == null) {
			deleteSearchBut = new Button(marginBut + width*2/4, height/8, width/4 - marginBut*2, height/8, "Supprimer\nla recherche", UI.BUTTON_COLOR, null);
		}
	}
	
	/** Charge tous les emplacements des cartes choisis. */
	private void loadChosenCardsButtons() {
		int margin = 2;
		BufferedImage img;
		
		for (int i=0; i<chosenCards.length; i++) {
			img = chosenCards[i] == null ? null : chosenCards[i].img();

			chosenCardsBut[i] = new Button(
					margin + (width*7/8)*i/chosenCards.length, 
					margin + height*3/4, 
					(width*7/8)/chosenCards.length - margin*2, 
					height/4 - margin*2, 
					null, UI.BUTTON_COLOR, img);
		}
	}

	/** Charge tous les emplacements des cartes choisis. */
	private void loadChosenPotionsButtons() {
		int margin = 2;
		BufferedImage img;
		
		for (int i=0; i<chosenPotionsBut.length; i++) {
			img = chosenPotions[i] == null ? null : chosenPotions[i].img();

			chosenPotionsBut[i] = new Button(
					margin + (width*7/8)/3 + width*i/chosenPotions.length/3, 
					margin + height*3/4, 
					(width*7/8)/3/chosenPotions.length - margin*2, 
					height/4 - margin*2, 
					null, UI.BUTTON_COLOR, img);
		}
	}

	/** Charge tous les emplacements des cartes choisis. */
	private void loadChosenEnemiesButtons() {
		int margin = 2;
		BufferedImage img;
		
		for (int i=0; i<chosenEnemiesBut.length; i++) {
			img = chosenEnemies[i] == null ? null : chosenEnemies[i].img();

			chosenEnemiesBut[i] = new Button(
					margin + (width*7/8)/3 + width*i/chosenEnemies.length/3, 
					margin + height*3/4, 
					(width*7/8)/3/chosenEnemies.length - margin*2, 
					height/4 - margin*2, 
					null, UI.BUTTON_COLOR, img);
		}
	}
	
	private void loadCardsButtons() {
		cardsBut.clear();
		cardsName.clear();
		
		int i=0; // num de ligne
		int maxI=2;
		int j=0; // num de colonne
		int maxJ=10;

		for (Card card : Datas.getAllCards()) {
			if (card.name().toUpperCase().startsWith(searchBar.toString())) {
				// Création d'un nouveau bouton de selection
				cardsBut.add(new Button(
						marginLeft + widthResult*j/maxJ,
						marginTop + heightResult*i/maxI,
						widthResult/maxJ,
						heightResult/maxI,
						null, null, card.img()
						));
				
				cardsName.add(card.name());
				
				// Repositionnement pour le placement du bouton suivant
				j++;
				if (j==maxJ) {
					j=0;
					i++;
					if (i==maxI) {
						break; // On ne peut pas afficher plus de cartes sur l'écran. Donc on stop l'ajout de Button.
					}
				}
			}
		}
		
		// Suppression de la selection comme on ne sait plus ce qui était sélectionné avant.
		selection = -1;
	}
	
	private void loadPotionsButtons() {
		potionsBut.clear();
		potionsName.clear();
		
		int i=0; // num de ligne
		int maxI=3;
		int j=0; // num de colonne
		int maxJ=10;

		for (Potion potion : Datas.getAllPotions()) {
			if (potion.name().toUpperCase().startsWith(searchBar.toString())) {
				// Création d'un nouveau bouton de selection
				potionsBut.add(new Button(
						marginLeft + widthResult*j/maxJ,
						marginTop + heightResult*i/maxI,
						widthResult/maxJ,
						heightResult/maxI,
						null, null, potion.img()
						));
				
				potionsName.add(potion.name());
				
				// Repositionnement pour le placement du bouton suivant
				j++;
				if (j==maxJ) {
					j=0;
					i++;
					if (i==maxI) {
						break;
					}
				}
			}
		}
		
		// Suppression de la selection comme on ne sait plus ce qui était sélectionné avant.
		selection = -1;
	}
	
	private void loadEnemiesButtons() {
		enemiesBut.clear();
		enemiesName.clear();
		
		int i=0; // num de ligne
		int maxI=3;
		int j=0; // num de colonne
		int maxJ=10;

		for (Enemy enem : Datas.getAllEnemies()) {
			if (enem.name().toUpperCase().startsWith(searchBar.toString())) {
				// Création d'un nouveau bouton de selection
				enemiesBut.add(new Button(
						marginLeft + widthResult*j/maxJ,
						marginTop + heightResult*i/maxI,
						widthResult/maxJ,
						heightResult/maxI,
						null, null, enem.img()
						));
				
				enemiesName.add(enem.name());
				
				// Repositionnement pour le placement du bouton suivant
				j++;
				if (j==maxJ) {
					j=0;
					i++;
					if (i==maxI) {
						break;
					}
				}
			}
		}
		
		// Suppression de la selection comme on ne sait plus ce qui était sélectionné avant.
		selection = -1;
	}
	
	private void changeSelection(int newSelection) {
		if (newSelection < 0) { // Cas si on supprime la sélection
			selection = newSelection;
			selectionImg = null;
			selectionTxt = null;
			return;
		}
		// Sélection de l'objet
		switch (stateDebug) {
		case CHOOSING_CARDS:
			if (selection >= 0) cardsBut.get(selection).setBgColor(null);
			selection = newSelection;
			cardsBut.get(selection).setBgColor(UI.SELECT_COLOR);
			selectionImg = Datas.getCard(cardsName.get(selection)).img();
			selectionTxt = null;
			break;
		case CHOOSING_POTIONS:
			if (selection >= 0) potionsBut.get(selection).setBgColor(null);
			selection = newSelection;
			potionsBut.get(selection).setBgColor(UI.SELECT_COLOR);
			Potion po = Datas.getPotion(potionsName.get(selection));
			selectionImg = null;
			selectionTxt = po.name() + "\n" + po.description();
			break;
		case CHOOSING_ENEMIES:
			if (selection >= 0) enemiesBut.get(selection).setBgColor(null);
			selection = newSelection;
			enemiesBut.get(selection).setBgColor(UI.SELECT_COLOR);
			Enemy en = Datas.getEnemy(enemiesName.get(selection));
			selectionImg = en.img();
			selectionTxt = null;
			break;
		default:
			break;
		}
	}
	
	// getters
	public ArrayList<Potion> getChosenPotions() { 
		ArrayList<Potion> copy = new ArrayList<>();
		for (Potion item : chosenPotions) {
			if (item != null) {
				copy.add(item);
			}
		}
		return copy;
	}

	public ArrayList<Card> getChosenCards() { 
		ArrayList<Card> copy = new ArrayList<>();
		for (Card item : chosenCards) {
			if (item != null) {
				copy.add(item);
			}
		}
		return copy;
	}
	
	public ArrayList<Enemy> getChosenEnemies() { 
		ArrayList<Enemy> copy = new ArrayList<>();
		for (Enemy item : chosenEnemies) {
			if (item != null) {
				copy.add(item);
			}
		}
		return copy;
	}

	public Button[] getChosenCardsButtons() { return Arrays.copyOf(chosenCardsBut, chosenCards.length); }
	public Button[] getChosenPotionsButtons() { return Arrays.copyOf(chosenPotionsBut, chosenPotions.length); }
	public Button[] getChosenEnemiesButtons() { return Arrays.copyOf(chosenEnemiesBut, chosenEnemies.length); }
	public String getSearch() { return searchBar.toString(); }
	public Button getDeleteCharButton() { return deleteCharBut; }
	public Button getDeleteSearchButton() { return deleteSearchBut; }
	public ArrayList<Button> getCardsButtons() { return new ArrayList<>(cardsBut); }
	public ArrayList<Button> getPotionsButtons() { return new ArrayList<>(potionsBut); }
	public ArrayList<Button> getEnemiesButtons() { return new ArrayList<>(enemiesBut); }
	public StateDebug getStateDebug() { return stateDebug; }
	public Button getConfirmButton() { return confirmBut; }
	public BufferedImage getSelectionImg() { return selectionImg; }
	public String getSelectionText() { return selectionTxt; }
	public int marginTop() { return marginTop; }
	public int marginLeft() { return marginLeft; }
	public int heightResult() { return heightResult; }
	public int widthResult() { return widthResult; }
}

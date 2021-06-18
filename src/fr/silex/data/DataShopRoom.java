package fr.silex.data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import fr.silex.RunManager;
import fr.silex.action.Potion;
import fr.silex.room.CardItem;
import fr.silex.room.PotionItem;
import fr.silex.room.ShopRoom;
import fr.silex.ui.Button;
import fr.silex.ui.UI;
import fr.umlv.zen5.Event;

/**
 * Stocke tous les boutons pour l'affichage et resout l'action de l'utilisateur.
 */
public class DataShopRoom implements Data {
	private static enum Choice {
		CARD_CLASS, CARD_COLORLESS, POTION;
	}
	
	private final ShopRoom shop;
	private final int widthScreen;
	private final int heightScreen;
	private final int widthShop;
	private final int heightShop;
	private final int marginTop;
	private final int marginLeft;
	
	
	private final Button[] cardsClassBut;
	private final Button[] cardsColorlessBut;
	private final Button[] potionsBut;
	private Button confirmBut;
	private Button leaveShop;
	private String selectionText = null;
	private BufferedImage selectionImg = null;
	
	private int choiceNum = -1;
	private Choice choice = null;
	
	public DataShopRoom(ShopRoom shop) {
		this.shop = shop;
		this.widthScreen = RunManager.getWidthScreen();
		this.heightScreen = RunManager.getHeightScreen();
		widthShop = widthScreen/2;
		heightShop = heightScreen*3/4;
		marginLeft = (widthScreen - widthShop) / 2;
		marginTop = (heightScreen - heightShop) / 2;

		cardsClassBut = new Button[shop.seeCardsClass().length];
		cardsColorlessBut = new Button[shop.seeCardsColorless().length];
		potionsBut = new Button[shop.seePotions().length];
		
		loadCardsClassButtons();
		loadCardsColorlessButtons();
		loadPotionsButtons();
		loadConfirmButton();
		loadLeaveButton();
	}

	/** Charge les boutons des cartes de la classe du heros */
	private void loadCardsClassButtons() {
		CardItem[] cards = shop.seeCardsClass();
		Color curCardBg; // Couleur de fond de l'item
		boolean isSelected;
		
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != null) {
				isSelected = choice == Choice.CARD_CLASS && choiceNum == i;
				curCardBg = isSelected ? UI.SELECT_COLOR : null;
				cardsClassBut[i] = new Button(marginLeft + widthShop * i / cards.length, 
						marginTop,
						widthShop/cards.length,
						heightShop/3,
						null,
						curCardBg,
						cards[i].getCard().img());
			} else {
				cardsClassBut[i] = null;
			}
		}
	}
	
	private void loadCardsColorlessButtons() {
		CardItem[] cards = shop.seeCardsColorless();
		Color curCardBg; // Couleur de fond de l'item
		
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != null) {
				curCardBg = choice == Choice.CARD_COLORLESS && choiceNum == i ? UI.SELECT_COLOR : null;
				cardsColorlessBut[i] = new Button(marginLeft + (widthShop*2/5) * i / cards.length, 
						marginTop + heightShop/2,
						widthShop/5,
						heightShop/3,
						null,
						curCardBg,
						cards[i].getCard().img());
			} else {
				cardsColorlessBut[i] = null;
			}
		}
	}
	
	private void loadPotionsButtons() {
		PotionItem[] potions = shop.seePotions();
		Color curBg; // Couleur de fond de l'item
		int widthPotion = widthShop*2/(5*potions.length);
		
		for (int i = 0; i < potions.length; i++) {
			if (potions[i] != null) {
				curBg = choice == Choice.POTION && choiceNum == i ? UI.SELECT_COLOR : null;
				potionsBut[i] = new Button(marginLeft + (widthShop*2/5) + widthPotion * i,
						marginTop + heightShop/2,
						widthPotion,
						widthPotion,
						null,
						curBg,
						potions[i].getPotion().img());
			} else {
				potionsBut[i] = null;
			}
		}
	}
	
	/**
	 * Charge le contenu a afficher pour avoir plus d'informations sur l'item selectionné.
	 */
	private void loadMoreInformation() {
		if (choice != null && choiceNum != -1) {
			switch (choice) {
			case CARD_CLASS:
				selectionText = null;
				selectionImg = shop.seeCardsClass()[choiceNum].getCard().img();
				break;
			case CARD_COLORLESS:
				selectionText = null;
				selectionImg = shop.seeCardsColorless()[choiceNum].getCard().img();
				break;
			case POTION:
				Potion potion = shop.seePotions()[choiceNum].getPotion();
				selectionText = potion.name() + " :\n" + potion.description();
				selectionImg = null;
				break;
			default:
				break;
			}
		} 
		else {
			selectionText = null;
			selectionImg = null;
		}
	}
	
	private void loadConfirmButton() {
		int widthBut = widthScreen/4;
		
		confirmBut = new Button(widthScreen/2-widthBut/2, heightScreen-marginTop, widthBut, marginTop, 
				"Acheter", UI.BUTTON_COLOR, null);
	}
	
	private void loadLeaveButton() {
		leaveShop = new Button(widthShop+marginLeft, heightScreen-marginTop, marginLeft, marginTop,
				"Quitter le magasin", UI.BUTTON_COLOR, null);
	}
	
	/**
	 * Résout tous les effets des action de l'utilisateur.
	 * @return true si le joueur a demandé à sortir du magasin.
	 */
	@Override
	public Boolean resolvePlayerAction(Event event) {
		trySelectCardsClass(event);
		trySelectCardsColorless(event);
		trySelectPotion(event);
		tryBuyItem(event);
		return tryLeave(event);
	}

	/** Selectionne une carte en fonction de l'action du joueur. */
	private void trySelectCardsClass(Event event) {
		for (int i=0; i<cardsClassBut.length; i++) {
			if (cardsClassBut[i] != null && cardsClassBut[i].isClicked(event)) {
				choice = Choice.CARD_CLASS;
				choiceNum = i;
				loadCardsClassButtons();
				loadCardsColorlessButtons();
				loadPotionsButtons();
				loadMoreInformation();
			}
		}
	}

	/** Selectionne une carte en fonction de l'action du joueur. */
	private void trySelectCardsColorless(Event event) {
		for (int i=0; i<cardsColorlessBut.length; i++) {
			if (cardsColorlessBut[i] != null && cardsColorlessBut[i].isClicked(event)) {
				choice = Choice.CARD_COLORLESS;
				choiceNum = i;
				loadCardsClassButtons();
				loadCardsColorlessButtons();
				loadPotionsButtons();
				loadMoreInformation();
			}
		}
	}

	/** Selectionne une potion en fonction de l'action du joueur. */
	private void trySelectPotion(Event event) {
		for (int i=0; i<potionsBut.length; i++) {
			if (potionsBut[i] != null && potionsBut[i].isClicked(event)) {
				choice = Choice.POTION;
				choiceNum = i;
				loadCardsClassButtons();
				loadCardsColorlessButtons();
				loadPotionsButtons();
				loadMoreInformation();
			}
		}
	}
	
	private void tryBuyItem(Event event) {
		if (confirmBut.isClicked(event)) {
			if (choice == null) {
				System.out.println("Veuillez sélectionner un article à acheter.");
				return;
			}
			
			switch (choice) {
			case CARD_CLASS:
				if (shop.buyCardClass(choiceNum)) {
					choice = null;
					choiceNum = -1;
					loadCardsClassButtons();
				}
				break;
			case CARD_COLORLESS:
				if (shop.buyCardColorless(choiceNum)) {
					choice = null;
					choiceNum = -1;
					loadCardsColorlessButtons();
				}
				break;
			case POTION:
				if (shop.buyPotion(choiceNum)) {
					choice = null;
					choiceNum = -1;
					loadPotionsButtons();
				}
				break;

			default:
				throw new IllegalStateException("No.");
			}
			loadMoreInformation();
		}
	}

	private boolean tryLeave(Event event) {
		if (leaveShop.isClicked(event)) {
			return true;
		}
		return false;
	}
	
	public Button[] getCardsClassButtons() { return Arrays.copyOf(cardsClassBut, cardsClassBut.length); }
	public Button[] getCardsColorlessButtons() { return Arrays.copyOf(cardsColorlessBut, cardsColorlessBut.length); }
	public Button[] getPotionsButtons() { return Arrays.copyOf(potionsBut, potionsBut.length); }
	public Button getConfirmButton() { return confirmBut; }
	public Button getLeaveButton() { return leaveShop; }
	public ShopRoom getShop() { return shop; }
	public int widthShop() { return widthShop; }
	public int heightShop() { return heightShop; }
	public int marginTop() { return marginTop; }
	public int marginLeft() { return marginLeft; }
	/** Texte de l'item sélectionné */
	public String getSelectionText() { return selectionText; }
	/** Image de l'item sélectionné */
	public BufferedImage getSelectionImage() { return selectionImg; }
}

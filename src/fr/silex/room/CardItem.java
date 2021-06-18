package fr.silex.room;

import java.util.Random;

import fr.silex.action.Card;

public class CardItem {
	private static Random random = new Random();
	
	private Card card;
	private int price;
	private boolean isOnSale = false;
	
	public CardItem(Card card) {
		this(card, false);
	}
	
	public CardItem(Card card, boolean reduction) {
		this.card = card;
		definePrice(reduction);
	}
	
	/**
	 * Utilis� dans le constructeur, d�finit le prix de la Card.
	 */
	private void definePrice(boolean reduc) {
		int min = 0;
		int max = 0;
		
		switch (card.classCard()) {
		case IRONCLAD, SILENT:
			switch (card.rarity()) {
			case COMMON:
				min = 45;
				max = 55;
				break;
			case UNCOMMON:
				min = 68;
				max = 82;
				break;
			case RARE:
				min = 135;
				max = 165;
				break;
			default:
				break;
			}
			break;

		case COLORLESS:
			switch (card.rarity()) {
			case UNCOMMON:
				min = 81;
				max = 99;
				break;
			case RARE:
				min = 162;
				max = 198;
				break;
			default:
				break;
			}
			break;
			
		default:
			break;
		}
		
		price = min + random.nextInt(max+1 - min);
	}

	public void setOnSale() {
		if (!isOnSale) {
			price /= 2;
			isOnSale = true;
		}
	}
	
	public Card getCard() { return card; }
	public int getPrice() { return price; }
}

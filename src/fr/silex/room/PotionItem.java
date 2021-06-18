package fr.silex.room;

import java.util.Random;

import fr.silex.action.Potion;

public class PotionItem {
	private static Random random = new Random();
	
	private Potion potion;
	private int price;
	
	public PotionItem(Potion potion) {
		this.potion = potion;
		definePrice();
	}
	
	/**
	 * Utilisé dans le constructeur, définit le prix de la Card.
	 */
	private void definePrice() {
		int min=0, max=0;
		
		switch (potion.rarity()) {
		case COMMON:
			min = 48;
			max = 52;
			break;
		case UNCOMMON:
			min = 72;
			max = 78;
			break;
		case RARE:
			min = 95;
			max = 105;
			break;
		default:
			break;
		}
		
		price = min + random.nextInt(max+1 - min);
	}
	
	public Potion getPotion() { return potion; }
	public int getPrice() { return price; }
}

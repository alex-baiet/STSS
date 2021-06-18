package fr.silex.room;

import java.util.Arrays;
import java.util.Random;

import fr.silex.action.Card;
import fr.silex.action.Card.ClassCard;
import fr.silex.action.Potion;
import fr.silex.data.Datas;
import fr.silex.data.Datas.EnemyCategory;
import fr.silex.entity.Hero;
import fr.silex.entity.Hero.HeroClass;

public class Drop {
	private static final Random random = new Random();
	private static float potionDrop = 0.4f;
	
	private int gold;
	private Potion potion;
	private Card[] cards = new Card[3];
	private final ClassCard classCardLoot;
	
	public Drop(Hero hero, EnemyCategory category) {
		classCardLoot = hero.getHeroClass() == HeroClass.IRONCLAD ? ClassCard.IRONCLAD : ClassCard.SILENT;
		loadGoldLoot(category);
		loadPotionLoot();
		loadCardsLoot(category);
	}
	
	private void loadGoldLoot(EnemyCategory category) {
		int minGold;
		int maxGold;
		
		switch (category) {
		case NORMAL:
			minGold = 10;
			maxGold = 20;
			break;
		case ELITE:
			minGold = 25;
			maxGold = 35;
			break;
		case BOSS:
			minGold = 95;
			maxGold = 105;
			break;
		default:
			throw new RuntimeException("too bad.");
		}
		
		gold = minGold + random.nextInt(maxGold-minGold +1);
	}
	
	private void loadPotionLoot() {
		if (random.nextFloat() <= potionDrop) {
			potionDrop -= 0.1f;
			float result = random.nextFloat();
			if (result < 0.65f) {
				potion = Datas.getRandomPotion(Potion.Rarity.COMMON);
			} else if (result < 0.90f) {
				potion = Datas.getRandomPotion(Potion.Rarity.UNCOMMON);
			} else {
				potion = Datas.getRandomPotion(Potion.Rarity.RARE);
			}
			
		} else {
			potionDrop += 0.1f;
			potion = null;
		}
	}
	
	private void loadCardsLoot(EnemyCategory category) {
		float probUncommon;
		float probRare;
		
		switch (category) {
		case NORMAL:
			probUncommon = 0.37f;
			probRare = 0.03f;
			break;
		case ELITE:
			probUncommon = 0.40f;
			probRare = 0.10f;
			break;
		case BOSS:
			probUncommon = 0.0f;
			probRare = 1.0f;
			break;
		default:
			throw new RuntimeException("wut");
		}
		
		for (int i=0; i<cards.length; i++) {
			float result = random.nextFloat();
			if (result < probRare) {
				cards[i] = Datas.getRandomCard(classCardLoot, Card.Rarity.RARE);
			} else if (result < probRare+probUncommon) {
				cards[i] = Datas.getRandomCard(classCardLoot, Card.Rarity.UNCOMMON);
			} else {
				cards[i] = Datas.getRandomCard(classCardLoot, Card.Rarity.COMMON);
			}
		}
	}

	/** renvoie l'item du drop en question. Supprime le loot du drop. */
	public int lootGold() { 
		int result = gold;
		gold = 0;
		return result; 
	}
	/** renvoie l'item du drop en question. Supprime le loot du drop. */
	public Potion lootPotion() { 
		Potion result = potion;
		potion = null;
		return result; 
	}
	/** renvoie l'item du drop en question. Supprime le loot du drop. */
	public Card lootCard(int numCard) { 
		if (cards != null) {
			Card result = cards[numCard];
			cards = null;
			return result; 			
		}
		return null;
	}
	
	/** renvoie l'item du drop en question. A utiliser uniquement pour l'affichage. */
	public int getGold() { return gold; }
	/** renvoie l'item du drop en question. A utiliser uniquement pour l'affichage. */
	public Potion getPotion() { return potion; }
	/** renvoie l'item du drop en question. A utiliser uniquement pour l'affichage. */
	public Card[] getCards() { return cards!=null ? Arrays.copyOf(cards, cards.length) : null; }
	
}

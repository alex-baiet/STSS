package fr.silex.room;

import java.util.Arrays;
import java.util.Random;

import fr.silex.action.Card;
import fr.silex.action.Card.Category;
import fr.silex.action.Card.ClassCard;
import fr.silex.data.Datas;
import fr.silex.entity.Hero;

/**
 * Contient toutes les données pour le shop, et des fonctionnalités pour le gérer.
 */
public class ShopRoom { 
	private final static Random random = new Random();
	
	private final Hero hero;
	
	private final CardItem[] cardsClass = new CardItem[5]; // Liste des cartes spécifiques à la class du Hero a vendre
	private final CardItem[] cardsColorless = new CardItem[2]; // Liste des cartes colorless a vendre.
	private final PotionItem[] potions = new PotionItem[3];
	
//	private static int removalPrice = 75;
//	private boolean removalServiceAvailable = true;
	
	public ShopRoom(Hero hero) {
		this.hero = hero;
		loadCardsClass();
		loadCardsColorless();
		loadPotions();
	}
	
	/**
	 * Utilisé dans le constructeur, prépare une liste de carte de la classe du Hero à vendre
	 */
	private void loadCardsClass() {
		ClassCard classCard;
		
		switch (hero.getHeroClass()) {
		case IRONCLAD:
			classCard = ClassCard.IRONCLAD;
			break;
		case SILENT:
			classCard = ClassCard.SILENT;
			break;
		default:
			throw new IllegalStateException("No.");
		}

		cardsClass[0] = new CardItem(Datas.getRandomCard(classCard, Category.ATTACK));
		cardsClass[1] = new CardItem(Datas.getRandomCard(classCard, Category.ATTACK));
		cardsClass[2] = new CardItem(Datas.getRandomCard(classCard, Category.SKILL));
		cardsClass[3] = new CardItem(Datas.getRandomCard(classCard, Category.SKILL));
		cardsClass[4] = new CardItem(Datas.getRandomCard(classCard, Category.POWER));
		
		cardsClass[random.nextInt(cardsClass.length)].setOnSale();
	}
	
	/**
	 * Utilisé dans le constructeur, prépare une liste de carte colorless à vendre
	 */
	private void loadCardsColorless() {
		cardsColorless[0] = new CardItem(Datas.getRandomCard(ClassCard.COLORLESS, Card.Rarity.UNCOMMON));
		cardsColorless[1] = new CardItem(Datas.getRandomCard(ClassCard.COLORLESS, Card.Rarity.RARE));
	}
	
	/**
	 * Utilisé dans le constructeur, préprare une liste de potions à vendre
	 */
	private void loadPotions() {
		for (int i=0; i<potions.length; i++) {
			potions[i] = new PotionItem(Datas.getRandomPotion());
		}
	}


	/**
	 * Achète une carte, la donne au Hero, et la retire du magasin.
	 * @param num : numéro de l'article dans le magasin.
	 * @return False si le Hero n'a pas assez d'argent. Dans ce cas, rien ne se passe.
	 */
	public boolean buyCardClass(int num) {
		if (cardsClass[num] != null && hero.getGold() > cardsClass[num].getPrice()) {
			hero.spendGold(cardsClass[num].getPrice());
			hero.deck().addCard(cardsClass[num].getCard());
			System.out.println("Vous avez acheté : "+cardsClass[num].getCard().name()+".");
			System.out.println("Argent restant : "+hero.getGold());
			cardsClass[num] = null;
			
			return true;
		}
		return false;
	}

	/**
	 * Achète une carte, la donne au Hero, et la retre du magasin.
	 * @param num : numéro de l'article dans le magasin.
	 * @return False si le Hero n'a pas assez d'argent. Dans ce cas, rien ne se passe.
	 */
	public boolean buyCardColorless(int num) {
		if (cardsColorless[num] != null && hero.getGold() > cardsColorless[num].getPrice()) {
			hero.spendGold(cardsColorless[num].getPrice());
			hero.deck().addCard(cardsColorless[num].getCard());
			System.out.println("Vous avez acheté : "+cardsColorless[num].getCard().name()+".");
			System.out.println("Argent restant : "+hero.getGold());
			cardsColorless[num] = null;
			
			return true;
		}
		return false;
	}

	/**
	 * Achète une carte, la donne au Hero, et la retre du magasin.
	 * @param num : numéro de l'article dans le magasin.
	 * @return False si le Hero n'a pas assez d'argent. Dans ce cas, rien ne se passe.
	 */
	public boolean buyPotion(int num) {
		if (potions[num] != null 
				&& hero.getGold() > potions[num].getPrice()
				&& hero.addPotion(potions[num].getPotion())) 
		{
			hero.spendGold(potions[num].getPrice());
			System.out.println("Vous avez acheté : "+potions[num].getPotion().name()+".");
			System.out.println("Argent restant : "+hero.getGold());
			potions[num] = null;
			return true;
		}
		return false;
	
	}
	
	/**
	 * Retourne True si le Hero peut utiliser le Card removal service.
	 */
//	public boolean buyRemovalService() {
//		return false;
//	}
	
	/**
	 * Retourne la liste des cartes de la classe du Hero à vendre.
	 */
	public CardItem[] seeCardsClass() { 
		return Arrays.copyOf(cardsClass, cardsClass.length);
	}
	
	/**
	 * Retourne la liste des cartes colorless a vendre.
	 */
	public CardItem[] seeCardsColorless() { 
		return Arrays.copyOf(cardsColorless, cardsColorless.length);
	}
	
	/**
	 * Retourne la liste des potions à vendre.
	 */
	public PotionItem[] seePotions() { 
		return Arrays.copyOf(potions, potions.length);
	}
	
//	public boolean removalServiceAvailable() {
//		return !removalServiceAvailable;
//	}
	
	public Hero getHero() { return hero; }
}

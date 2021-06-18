package fr.silex;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.silex.action.Card;
import fr.silex.action.Card.Category;
import fr.silex.action.Card.ClassCard;
import fr.silex.action.HeroAction.ConditionBonus;
import fr.silex.action.HeroAction.Target;
import fr.silex.data.DataBattleRoom;
import fr.silex.entity.Enemy;
import fr.silex.entity.Hero;

/**
 * Utilisé pour stocker et gérer les cartes du joueur, 
 * pendant et hors des combats.
 */
public class Deck {
	public static enum DeckPile {
		DECK, HAND, DRAW, DISCARD, EXHAUST
	}
	
	private static Random random = new Random();
	private Hero user;
	
	// Toutes les piles de cartes
	private ArrayList<Card> cards;
	private ArrayList<Card> drawPile;
	private ArrayList<Card> discardPile;
	private ArrayList<Card> exhaustPile;
	private ArrayList<Card> hand;

	// Autres variables
	private int strikeCount = 0;
	private int cardsPlayedCount = 0;
	private int attacksPlayedCount = 0;
	
	public Deck(Hero user) {
		this(user, new ArrayList<>());
	}

	public Deck(Hero user, List<Card> cards) {
		this.user = user; 
		this.cards = new ArrayList<>(cards);
		drawPile = new ArrayList<>();
		discardPile = new ArrayList<>();
		exhaustPile = new ArrayList<>();
		hand = new ArrayList<>();
	}

	
	
	/**
	 * Prepare le deck pour un nouveau combat.
	 */
	public void prepareDeck() {
		drawPile.clear();
		hand.clear();
		discardPile.clear();
		exhaustPile.clear();
		
		for (Card card : cards) {
			card.updateBattle();
		}

		// Création d'un copie des cartes du deck
		ArrayList<Card> cardsCopy = new ArrayList<>(cards);

		// Ajout des cartes dans la pioche dans le désordre
		while (!cardsCopy.isEmpty()) {
			drawPile.add(cardsCopy.remove(random.nextInt(cardsCopy.size())));
		}
		
		// Positionnement carte innate sur le dessus de la pioche.
		for (int i = drawPile.size()-1; i >= 0; i--) {
			if (drawPile.get(i).isInnate()) {
				drawPile.add(drawPile.remove(i));
			}
		}
	}

	public void newTurn() {
		cardsPlayedCount = 0;
		attacksPlayedCount = 0;
	}
	
	/**
	 * Mélange la discardPile dans la drawPile.
	 */
	public void shuffleDeck() {
		while (!discardPile.isEmpty()) {
			drawPile.add(discardPile.remove(random.nextInt(discardPile.size())));
		}
	}

	/**
	 * Ajoute une carte au Deck.
	 * @param card
	 */
	public void addCard(Card card) {
		Objects.requireNonNull(card);
		DataBattleRoom.println("Ajout de la carte "+card.name()+" au deck.");
		cards.add(card);
		
		if (card.name().contains("Strike")) strikeCount ++;
	}
	
	public void addCard(Card card, int nbr) {
		for (int i=0; i < nbr; i++ ) {
			addCard(card);
		}
	}

	/** Ajoute une carte dans la pile indiquée. */
	public void addCardToPile(Card card, DeckPile pile, boolean onTop) {
		Objects.requireNonNull(card);
		Objects.requireNonNull(pile);
		
		switch (pile) {
		case DECK:
			addCard(card);		
			break;
		case HAND:
			hand.add(card);
			DataBattleRoom.println("Ajout de "+card.name()+" à la main.");
			break;
		case DRAW:
			if (onTop) {
				drawPile.add(card);
			} else {
				drawPile.add(random.nextInt(drawPileSize()+1), card);
			}
			DataBattleRoom.println("Ajout de "+card.name()+" à la pioche.");			
			break;
		case DISCARD:
			discardPile.add(card);
			DataBattleRoom.println("Ajout de "+card.name()+" à la défausse.");			
			break;
		case EXHAUST:
			exhaustPile.add(card);
			DataBattleRoom.println("Ajout de "+card.name()+" aux cartes défaussées.");			
			break;
		default:
			break;
		}
	}

	public void addCardToPile(Card card, DeckPile pile, boolean onTop, int nbr) {
		for (int i=0; i < nbr; i++ ) {
			addCardToPile(card, pile, onTop);
		}
	}
	
	private Card draw() {
		// Préparation é la pioche
		if (drawPile.isEmpty()) {
			shuffleDeck();
		}
		if (drawPile.isEmpty()) {
			DataBattleRoom.println("Vous ne pouvez plus piocher, la pioche est vide !");
			return null;
		}
		if (hand.size() >= 10) {
			DataBattleRoom.println("Vous ne pouvez plus piocher, votre main est déjà pleine !");
			return null;
		}
		
		// Pioche
		Card drew = drawPile.remove(drawPile.size()-1);
		hand.add(drew);
		drew.applyDrawEffects(user);
		return drew;
	}

	/**
	 * Ajoute des cartes é la main, 
	 * si c'est possible.
	 * @param nbr : number of card to draw.
	 */
	private ArrayList<Card> draw(int nbr) {
		
		if (nbr>0) {
			if (!user.hasNoDraw()) {
				ArrayList<Card> drewList = new ArrayList<>();
				DataBattleRoom.println("Vous piochez "+nbr+" cartes.");
				
				for (int i = 0; i < nbr; i++) {
					Card drew = draw();
					if (drew == null) break;
					drewList.add(drew);
					if (drew.classCard() == ClassCard.STATUS) { 
						drewList.addAll(draw(user.getEvolve()));
					}
				}
				return drewList;
			} else {
				DataBattleRoom.println("pioche impossible : noDraw est actif !");
			}
		}
		return new ArrayList<>();
	}

	public ArrayList<Card> draw(int nbr, List<Enemy> enemies) {
		ArrayList<Card> drewList = draw(nbr);
		
		for (Card card : drewList) {
			if (card.classCard() == ClassCard.STATUS || card.classCard() == ClassCard.CURSE) {
				int damage;
				// Application de FireBreathing
				if (user.getFireBreathing() != 0) {
					for (Enemy enemy : enemies) {
						if (enemy.getLife() > 0) {
							damage = user.getFireBreathing();
							damage = user.attack(damage);
							damage = enemy.damage(damage);
							DataBattleRoom.println(user.name() + " a infligé "+damage+" a "+enemy.name()+".");
						}
					}
				}
			}
		}
		
		return drewList;
	}
	/**
	 *  Défausse les cartes dans la main.
	 *  Certains effets de cartes sont activés a ce moment.
	 */
	public void endTurn() {
		Card card;
		while (hand.size() != 0) {
			card = hand.get(0);
			card.applyEndTurnEffects(user);
			card.updateTurn();
			if (card.ethereal()) {
				exhaustPile.add(hand.remove(0));
			} else {
				discardPile.add(hand.remove(0));
			}
		}
	}
	
	public ArrayList<Card> hand() {
		return new ArrayList<>(hand);
	}
	
	/** 
	 * Joue la carte indiquée. 
	 * @return True si la carte a bien été jouée.
	 */
	private boolean playCard(Card card, List<Enemy> enemies, int targetEnemy) {
		if (!card.isUsable(user)) {
			DataBattleRoom.println("Vous ne pouvez pas jouer cette carte.");
		}
		if (user.isEntangled() && card.category() == Card.Category.ATTACK) {
			DataBattleRoom.println("Vous ne peut pas utiliser d'attaque en étant entangled."); // juste pour le debuggage
			return false;
		}
		
		// A partir d'ici, la carte est jouée
		if (user.getAThousandCuts() > 0) {
			for (Enemy enemy : enemies) {
				enemy.damage(user.getAThousandCuts());
			}
		}
		user.addBlock(user.getAfterImage());
		// Application de l'effet de la carte
		if (card.target() == Target.ALL) {
			card.applyEffects(user, enemies);
		}
		else {
			if (card.target() == Target.RANDOM) {
				targetEnemy = random.nextInt(enemies.size());
			}
			card.applyEffects(user, enemies, targetEnemy);
		}
		if (card.category() == Category.ATTACK) attacksPlayedCount++;
		if (user.getPanache()!=0 && ++cardsPlayedCount >= 5) {
			for (Enemy enemy : enemies) {
				int d = enemy.damage(user.getPanache());
				if (d != 0) DataBattleRoom.println("L'effet panache inflige "+d+" dégâts à "+enemy.name()+".");
				cardsPlayedCount = 0;
			}
		}

		for (Enemy enemy : enemies) {
			enemy.buffFromHeroAction(card.category());
		}
		card.updateTurn();
		return true;
	}
	
	/**
	 * Utilise la carte choisie de la main.
	 */
	public void playCard(List<Enemy> enemies, int numCardPlayed, int targetEnemy) {
		Card card = hand.remove(numCardPlayed);
		if (playCard(card, enemies, targetEnemy)) {
			
			// Défausse de la carte
			if (card.exhaust() || (user.hasCorruption() && card.category() == Category.SKILL)) {
				exhaustCard(card);
			} else if (card.category() != Category.POWER) {
				discardPile.add(card);
			}
		} else {
			hand.add(numCardPlayed, card);
		}
	}
	
	/** Joue la carte du dessus de la pioche. */
	public void playTopDrawPileCard(List<Enemy> enemies, int targetEnemy, boolean exhaust) {
		if (drawPile.isEmpty()) {
			shuffleDeck();
		}
		if (drawPile.isEmpty()) {
			DataBattleRoom.println("Vous ne pouvez pas jouer la carte du dessus du pioche : elle est vide.");
			return;
		}
		Card drew = drawPile.remove(drawPile.size()-1);
		DataBattleRoom.println("Carte jouée par effet : "+drew.name());
		user.useEnergy(-drew.getEnergyCost(user));
		playCard(drew, enemies, targetEnemy);

		if (exhaust || drew.exhaust() || (user.hasCorruption() && drew.category() == Category.SKILL)) {
			exhaustCard(drew);
		} else {
			discardPile.add(drew);
		}
	}
	
	/** Exhaust la carte de la main. */
	public void exhaustCardFromHand(int numCard) {
		if (numCard >= hand.size() || numCard < 0) throw new IllegalArgumentException(numCard + " ne permet pas de sélectionner une carte de la main.");
		Card card = hand.remove(numCard);
		exhaustCard(card);
		DataBattleRoom.println(card.name() + " a été exhaust depuis la main.");
	}
	
	/** Supprime toutes les cartes du Deck. (hors combat) */
	public void clear() {
		cards.clear();
		strikeCount = 0;
	}

	public int drawPileSize() { return drawPile.size(); }
	public int discardPileSize() { return discardPile.size(); }
	public int exhaustPileSize() { return exhaustPile.size(); }
	
	public void printPileSize() {
		StringBuilder builder = new StringBuilder();
		builder.append("total cards=").append(cards.size());
		builder.append(", hand=").append(hand.size());
		builder.append(", draw pile=").append(drawPile.size());
		builder.append(", discard pile=").append(discardPile.size());
		builder.append(", exhaust pile=").append(exhaustPile.size());
		System.out.println(builder.toString());
	}
	
	private void exhaustCard(Card card) {
		exhaustPile.add(card);
		draw(user.getDarkEmbrace());
		user.addBlock(user.getFeelNoPain());
		if (card.getConditionBonus() == ConditionBonus.EXHAUST_SELF) {
			card.applyBonusEffectsHero(user, user.getEnemies().get(0), user.getEnemies());
		}
	}
	
	public int getStrikeCount() { return strikeCount; }
	public int getCardsPlayedCount() { return cardsPlayedCount; }
	public int getAttacksPlayedCount() { return attacksPlayedCount; }
}

package fr.silex.action;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import fr.silex.Deck.DeckPile;
import fr.silex.FileManager;
import fr.silex.entity.Enemy;
import fr.silex.entity.Hero;
import fr.silex.exception.BadFormatException;

public class Card extends HeroAction {
	public static enum Category {
		ATTACK, SKILL, POWER, STATUS, CURSE;
	}

	public static enum ClassCard {
		NONE, PLAYER, COLORLESS, IRONCLAD, SILENT, STATUS, CURSE;
	}
	
	public static enum KeepCost {
		TURN, BATTLE;
	}
	
	private final String data;
	private int actualEnergyCost;
	private KeepCost keepCost = KeepCost.BATTLE;
	private int damageBattle = 0;
	
	private final String imagePath;
	private BufferedImage img = null;
	private final ClassCard classCard;
	private final Category category;
	private final int energyCost; // Cout en energy de la carte.
	private final boolean exhaust; // La carte est bannie après être joué.
	private final boolean ethereal; // La carte est bannie si elle n'est pas joué.
	private final int damageCurse; // Dégâts subis à la fin du tour.
	private final boolean unplayable; // La carte ne peut pas être joué.
	private final int energyOnDraw;
	private final int costBonusForEachWound;
	private final boolean innate;
	private final int addCopyOnDraw;
	private final int damageUpBattle;
	
	// Effet en fonction du cout de la carte
	private final boolean attackNbrX;
	private final boolean addRandomCardX;
	private final boolean drawNextTurnX;
	private final boolean energyGainNextTurnX;
	private final boolean damageStrengthX;
	private final boolean weakX;
	

	public Card(String data) throws BadFormatException {
		super(data);
		this.data = data;
		HashMap<String, String> map = FileManager.getFileValue(data);

		this.imagePath = map.get("imagePath");
		this.classCard = map.containsKey("class") ? ClassCard.valueOf(map.get("class").toUpperCase()) : ClassCard.COLORLESS;
		this.category = map.containsKey("category") ? Category.valueOf(map.get("category").toUpperCase()) : Category.STATUS;
		this.energyCost = map.containsKey("energyCost") ? Integer.parseInt(map.get("energyCost")) : 0;
		actualEnergyCost = energyCost;
		this.exhaust = map.containsKey("exhaust") ? map.get("exhaust").equals("1") : false;
		this.ethereal = map.containsKey("ethereal") ? map.get("ethereal").equals("1") : false;
		this.damageCurse = map.containsKey("damageCurse") ? Integer.parseInt(map.get("damageCurse")) : 0;
		this.unplayable = map.containsKey("unplayable") ? map.get("unplayable").equals("1") : false;
		this.energyOnDraw = map.containsKey("energyOnDraw") ? Integer.parseInt(map.get("energyOnDraw")) : 0;	
		this.costBonusForEachWound = map.containsKey("costBonusForEachWound") ? Integer.parseInt(map.get("costBonusForEachWound")) : 0;		
		this.innate = map.containsKey("innate") ? map.get("innate").equals("1") : false;
		this.addCopyOnDraw = map.containsKey("addCopyOnDraw") ? Integer.parseInt(map.get("addCopyOnDraw")) : 0;
		this.damageUpBattle = map.containsKey("damageUpBattle") ? Integer.parseInt(map.get("damageUpBattle")) : 0;	
		this.attackNbrX = map.containsKey("attackNbrX") ? map.get("attackNbrX").equals("1") : false;
		this.addRandomCardX = map.containsKey("addRandomCardX") ? map.get("addRandomCardX").equals("1") : false;
		this.drawNextTurnX = map.containsKey("drawNextTurnX") ? map.get("drawNextTurnX").equals("1") : false;
		this.energyGainNextTurnX = map.containsKey("energyGainNextTurnX") ? map.get("energyGainNextTurnX").equals("1") : false;
		this.damageStrengthX = map.containsKey("damageStrengthX") ? map.get("damageStrengthX").equals("1") : false;
		this.weakX = map.containsKey("weakX") ? map.get("weakX").equals("1") : false;

		checkErrors();
	}

	/** Utilisé uniquement dans le constructeur pour vérifier que toutes les variables sont bonnes. */
	private void checkErrors() {
		if (energyCost < -1) {
			throw new IllegalArgumentException("The value 'energyCost' must be -1 or more.");
		}
	}
	
	/**
	 * Applique les effets au premier ennemies de la liste, ou a tout les ennemies si target = Target.ALL.
	 */
	public void applyEffects(Hero user, List<Enemy> enemies) {
		applyEffects(user, enemies, 0);
	}
	
	@Override
	public void applyEffects(Hero user, List<Enemy> enemies, int targetIndex) {
		if (attackNbrX) setAttackNbr(user.getEnergy());
		if (weakX) setWeak(user.getEnergy());
		if (damageStrengthX) setDamageStrength(user.getEnergy());
		if (drawNextTurnX) setDrawNextTurn(user.getEnergy());
		if (energyGainNextTurnX) setEnergyGainNextTurn(user.getEnergy());
		
		super.applyEffects(user, enemies, targetIndex);
		if (category == Category.ATTACK && user.useDoubleTap()
				|| category == Category.SKILL && user.useBurst()
		) {
			actualEnergyCost = 0;
			keepCost = KeepCost.TURN;
			super.applyEffects(user, enemies, targetIndex);
		}
	}

	@Override
	public void applyEffectsHero(Hero user, List<Enemy> enemies) {
		super.applyEffectsHero(user, enemies);
		if (!(user.hasCorruption() && category == Category.SKILL)) {
			user.useEnergy(getEnergyCost(user));
		}
		if (category == Category.ATTACK && user.getRage() > 0) {
			user.addBlock(user.getRage());
		}
		
		damageBattle += damageUpBattle;
	}

	/**
	 * Applique les effets au Hero lors de la fin de son tour, 
	 * A utiliser si le hero a cette carte en main.
	 */
	public void applyEndTurnEffects(Hero user) {
		user.damage(damageCurse);
	}

	/**
	 * Applique les effets de la carte lors de sa pioche.
	 */
	public void applyDrawEffects(Hero user) {
		if (user.getEnergy() < -energyOnDraw) {
			user.useEnergy(user.getEnergy());
		} else {
			user.useEnergy(-energyOnDraw);
		}
		for (int i=0; i<addCopyOnDraw; i++) {
			user.deck().addCardToPile(copy(), DeckPile.HAND, true); // Rajoute une copie de cette carte.
		}
	}
	
	public void updateTurn() {
		if (keepCost == KeepCost.TURN) {
			actualEnergyCost = energyCost;
		}
	}
	
	public void updateBattle() {
		if (keepCost == KeepCost.BATTLE) {
			actualEnergyCost = energyCost;
		}
		damageBattle = 0;
	}

	/**
	 * True si le Hero peut jouer la carte.
	 */
	@Override
	public boolean isUsable(Hero hero) {
		return super.isUsable(hero) 
				&& (hero.getEnergy() >= getEnergyCost(hero) || (hero.hasCorruption() && category == Category.SKILL))
				&& !unplayable;
	}

	@Override
	public String toString() {
		return "Card "+super.toString()+", category="+category;
	}

	/**
	 * Change le cout de la carte
	 * @param keepCost : temps que va durer le changement de coût.
	 */
	public void setEnergyCost(int cost, KeepCost keepCost) {
		actualEnergyCost = cost;
		this.keepCost = keepCost;
	}

	/**
	 * Change le cout de la carte si la valeur indiqué est inférieur au coût actuel.
	 * @param keepCost : temps que va durer le changement de coût.
	 */
	public void setEnergyCostBonus(int cost, KeepCost keepCost) {
		if (cost < actualEnergyCost) setEnergyCost(cost, keepCost);
	}

	// Tous les getters
	public BufferedImage img() {
		if (img == null) img = FileManager.loadImage("pictures/cards/" + imagePath);
		return img;
	}
	
	@Override
	public boolean needTarget() {
		return (super.needTarget()
				|| weakX
				|| damageStrengthX)
				&& target() == Target.SINGLE;
	}
	
	public boolean exhaust() { return exhaust; }
	public boolean ethereal() { return ethereal; }
	public ClassCard classCard() { return classCard; }
	public Category category() { return category; }
	public int getEnergyCost(Hero user) {
		if (energyCost == -1) return user.getEnergy();
		int finalCost = actualEnergyCost + user.getDamagedCount()*costBonusForEachWound;
		finalCost = finalCost < 0 ? 0 : finalCost;
		return finalCost; 
	}
	public boolean isInnate() { return innate; }
	@Override
	public int getDamage() { return super.getDamage() + damageBattle; }
	@Override
	public int getAddRandomCard(Hero user) {
		return addRandomCardX ? getEnergyCost(user) : super.getAddRandomCard(user);
	}
	
	public Card copy() {
		return new Card(data);
	}
}

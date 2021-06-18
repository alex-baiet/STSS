package fr.silex.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import fr.silex.FileManager;
import fr.silex.action.Card.Category;
import fr.silex.action.Card.ClassCard;
import fr.silex.action.Card.KeepCost;
import fr.silex.data.DataBattleRoom;
import fr.silex.data.Datas;
import fr.silex.entity.Enemy;
import fr.silex.entity.Hero;
import fr.silex.exception.BadFormatException;

/**
 * Classe réunissant les propriétés communes aux classes Card et Potion.
 */
public abstract class HeroAction extends Action {
	public static enum Target { // A INTEGRER
		SINGLE, RANDOM, ALL, SELF;
	}
	
	public static enum ConditionBonus { // Liste des conditions possibles a respecter pour qu'un autre effet de carte soit déclenché.
		VULNERABLE_ON_ENEMY, 
		EXHAUST_SELF, 
		TARGET_ATTACKING, 
		KILL, 
		NO_ATTACK_IN_HAND,
		TARGET_POISONED, 
		DISCARDED_CARD, 
		DRAW_IS_SKILL,
		DRAWPILE_EMPTY, 
		WEAK_ON_ENEMY;
	}
	
	public static enum Rarity {
		NULL, STARTER, COMMON, UNCOMMON, RARE;
	}
	
	private final static Random random = new Random();
	private final Rarity rarity;
	private final Target target; // single, all, random
	private final int draw; // Nbr de carte piocher apres avoir utiliser la carte
	private final int energyGain;
	private final Category requireHandFullOfCategory; // L'item n'est jouable que si le Hero a une main pleine de carte d'une certaine Category
	private final int bonusDamageFromStrike; // Bonus de dégéts par carte Strike dans le deck
	private final int mindBlast; // Dégâts équivalent au nombre de carte.
	private final float healRatio; // % de vie recupéré.
	private final int fillEmptyPotion; // Remplie toutes les emplacements de potions.
	private final int gainMaxLife;
	private final int poisonMultiplier;
	private final int fillHand;
	private final int finisher;
	private final int flechettes;

	// Power
	private final int noDraw; // Nombre de tour ou le joueur ne pourra plus piocher.
	private final int combust; // inflige des dégéts é tous les ennemies é la fin du tour.
	private final int darkEmbrace; // Pioche une carte pour chaque carte exhaust.
	private final int noBlock; // interdit le gain de block par les cartes.
	private final int evolve; // Pioche une carte en piochant un Status.
	private final int feelNoPain; // +X block quand une carte est exhaust.
	private final int fireBreathing; // X damages é tous les ennemies en piochant un Status/Curse.
	private final int rupture; //+X strength a chaque des dégéts provenant des cartes.
	private final boolean barricade; // block reste é la fin du tour
	private final int berserk; // +X energy aux début des tours
	private final int brutality; // -Xpv, +Xdraw au début du tour
	private final boolean corruption; // Les Skills coutent 0 mais sont exhaust.
	private final int demonForm; // +X strength au début de chaque tour.
	private final int juggernaut; // X damage é un ennemies au pif é chaque gain de block
	private final int flameBarrier; // X dégéts é l'attaquant ce tour.
	private final int rage; // +X block en jouant une Attack ce tour
	private final int doubleTap; // Nombre de fois que la prochaine attaque va étre joué.
	private final int burst; // Le prochain skill sera joué X fois en plus.
	private final int magnetism; // Ajoute un carte colorless au début de chaque tour.
	private final int mayhem; // Joue la carte du dessus de la pioche a chaque tour.
	private final int panache; // X damage aux ennemies toutes les 5 cartes jouées..
	private final int bomb; // Explose aprés 3 tours, infligeant des dégéts é tous les ennmies.
	private final int accuracy; // +4 degats des shiv.
	private final int keepBlock; // garde le block ce tour.
	private final int choke; // X degats a chaque carte jouées.
	private final int infiniteBlades; // +1 shiv a la fin du tour.
	private final int noxiousFumes; // +X poison aux ennemies au debut du tour.
	private final int aThousandCuts; // X degats aux ennemies a chaque jouée.
	private final int afterImage; // +X block a chaque carte jouée.
	private final boolean explodeOnDeath; // L'ennemie inflige ses pv max en degats a tous les ennemies.
	private final int envenom; // +X poison a l'ennemies a chaque degats.

	// Power effets prochain tour uniquement
	private final int blockNextTurn;
	private int drawNextTurn;
	private int energyGainNextTurn;
	private final int phantasmalKiller;
	
	// Gestion de cartes
	private final int playTopDrawPileCard; // Joue le nombre de carte du dessus de la pioche indiqué.
	private final boolean playedCardExhaust; // Exhaust la carte jouée (carte qui n'est pas celle ci).
	private final int exhaustRandomCardFromHand;
	private final Category exhaustHandCategory;
	private final boolean reverseSelection; // Renverse la selection, fait un "if (!condition)"
	private final int addRandomCard; // Ajoute des cartes au hasard.
	private final ClassCard addRandomCardClass; // Class de la carte au hasard ajoutée.
	private final Category addRandomCardCategory; // Category de la carte au hasard ajoutée.
	private final int addRandomCardCost; // prix de la carte ajoutée.
	private final boolean shuffleDeck;
	private final int changeRandomCardCost;
	private final KeepCost keepEnergyCost;
	private final int changeHandCost;
	
	// Tous les effets fonctionnant uniquement que certaines conditions sont remplies
	private final ConditionBonus conditionBonus;
	private final int drawBonus;
	private final int energyGainBonus;
	private final int strengthBonus;
	private final int maxLifeBonus;
	private final int goldBonus;
	private final int damageBonus;
	
	
	public HeroAction(String data) throws BadFormatException {
		super(data);
		HashMap<String, String> map = FileManager.getFileValue(data);
		
		this.rarity = Rarity.valueOf(map.get("rarity").toUpperCase());
		this.target = map.containsKey("target") ? Target.valueOf(map.get("target").toUpperCase()) : Target.SINGLE;
		this.draw = map.containsKey("draw") ? Integer.parseInt(map.get("draw")) : 0;
		this.noDraw = map.containsKey("noDraw") ? Integer.parseInt(map.get("noDraw")) : 0;
		this.energyGain = map.containsKey("energyGain") ? Integer.parseInt(map.get("energyGain")) : 0;
		this.requireHandFullOfCategory = map.containsKey("requireHandFullOfCategory") ? Category.valueOf(map.get("requireHandFullOfCategory").toUpperCase()) : null;
		this.playTopDrawPileCard = map.containsKey("playTopDrawPileCard") ? Integer.parseInt(map.get("playTopDrawPileCard")) : 0;
		this.playedCardExhaust = map.containsKey("playedCardExhaust") ? map.get("playedCardExhaust").equals("1") : false;
		this.bonusDamageFromStrike = map.containsKey("bonusDamageFromStrike") ? Integer.parseInt(map.get("bonusDamageFromStrike")) : 0;
		this.exhaustRandomCardFromHand = map.containsKey("exhaustRandomCardFromHand") ? Integer.parseInt(map.get("exhaustRandomCardFromHand")) : 0;
		this.exhaustHandCategory = map.containsKey("exhaustHandCategory") ? Category.valueOf(map.get("exhaustHandCategory").toUpperCase()) : null;
		this.reverseSelection = map.containsKey("reverseSelection") ? map.get("reverseSelection").equals("1") : false;
		this.combust = map.containsKey("combust") ? Integer.parseInt(map.get("combust")) : 0;
		this.darkEmbrace = map.containsKey("darkEmbrace") ? Integer.parseInt(map.get("darkEmbrace")) : 0;
		this.drawBonus = map.containsKey("drawBonus") ? Integer.parseInt(map.get("drawBonus")) : 0;
		this.energyGainBonus = map.containsKey("energyGainBonus") ? Integer.parseInt(map.get("energyGainBonus")) : 0;
		this.strengthBonus = map.containsKey("strengthBonus") ? Integer.parseInt(map.get("strengthBonus")) : 0;
		this.maxLifeBonus = map.containsKey("maxLifeBonus") ? Integer.parseInt(map.get("maxLifeBonus")) : 0;
		this.goldBonus = map.containsKey("goldBonus") ? Integer.parseInt(map.get("goldBonus")) : 0;
		this.conditionBonus = map.containsKey("conditionBonus") ? ConditionBonus.valueOf(map.get("conditionBonus").toUpperCase()) : null;
		this.noBlock = map.containsKey("noBlock") ? Integer.parseInt(map.get("noBlock")) : 0;
		this.evolve = map.containsKey("evolve") ? Integer.parseInt(map.get("evolve")) : 0;
		this.feelNoPain = map.containsKey("feelNoPain") ? Integer.parseInt(map.get("feelNoPain")) : 0;
		this.fireBreathing = map.containsKey("fireBreathing") ? Integer.parseInt(map.get("fireBreathing")) : 0;
		this.rupture = map.containsKey("rupture") ? Integer.parseInt(map.get("rupture")) : 0;
		this.barricade = map.containsKey("barricade") ? map.get("barricade").equals("1") : false;
		this.berserk = map.containsKey("berserk") ? Integer.parseInt(map.get("berserk")) : 0;
		this.brutality = map.containsKey("brutality") ? Integer.parseInt(map.get("brutality")) : 0;
		this.corruption = map.containsKey("corruption") ? map.get("corruption").equals("1") : false;
		this.demonForm = map.containsKey("demonForm") ? Integer.parseInt(map.get("demonForm")) : 0;
		this.juggernaut = map.containsKey("juggernaut") ? Integer.parseInt(map.get("juggernaut")) : 0;
		this.flameBarrier = map.containsKey("flameBarrier") ? Integer.parseInt(map.get("flameBarrier")) : 0;
		this.rage = map.containsKey("rage") ? Integer.parseInt(map.get("rage")) : 0;
		this.doubleTap = map.containsKey("doubleTap") ? Integer.parseInt(map.get("doubleTap")) : 0;
		this.magnetism = map.containsKey("magnetism") ? Integer.parseInt(map.get("magnetism")) : 0;
		this.mayhem = map.containsKey("mayhem") ? Integer.parseInt(map.get("mayhem")) : 0;
		this.panache = map.containsKey("panache") ? Integer.parseInt(map.get("panache")) : 0;
		this.bomb = map.containsKey("bomb") ? Integer.parseInt(map.get("bomb")) : 0;
		this.addRandomCard = map.containsKey("addRandomCard") ? Integer.parseInt(map.get("addRandomCard")) : 0;
		this.addRandomCardClass = map.containsKey("addRandomCardClass") ? ClassCard.valueOf(map.get("addRandomCardClass").toUpperCase()) : null;
		this.addRandomCardCategory = map.containsKey("addRandomCardCategory") ? Category.valueOf(map.get("addRandomCardCategory").toUpperCase()) : null;
		this.addRandomCardCost = map.containsKey("addRandomCardCost") ? Integer.parseInt(map.get("addRandomCardCost")) : -2;
		this.shuffleDeck = map.containsKey("shuffleDeck") ? map.get("shuffleDeck").equals("1") : false;
		this.changeRandomCardCost = map.containsKey("changeRandomCardCost") ? Integer.parseInt(map.get("changeRandomCardCost")) : -2;
		this.keepEnergyCost = map.containsKey("keepEnergyCost") ? KeepCost.valueOf(map.get("keepEnergyCost").toUpperCase()) : KeepCost.BATTLE;
		this.changeHandCost = map.containsKey("changeHandCost") ? Integer.parseInt(map.get("changeHandCost")) : -2;
		this.mindBlast = map.containsKey("mindBlast") ? Integer.parseInt(map.get("mindBlast")) : 0;
		this.healRatio = map.containsKey("healRatio") ? Float.parseFloat(map.get("healRatio")) : 0f;
		this.fillEmptyPotion = map.containsKey("fillEmptyPotion") ? Integer.parseInt(map.get("fillEmptyPotion")) : 0;
		this.gainMaxLife = map.containsKey("gainMaxLife") ? Integer.parseInt(map.get("gainMaxLife")) : 0;
		this.damageBonus = map.containsKey("damageBonus") ? Integer.parseInt(map.get("damageBonus")) : 0;
		this.burst = map.containsKey("burst") ? Integer.parseInt(map.get("burst")) : 0;
		this.accuracy = map.containsKey("accuracy") ? Integer.parseInt(map.get("accuracy")) : 0;
		this.keepBlock = map.containsKey("keepBlock") ? Integer.parseInt(map.get("keepBlock")) : 0;
		this.choke = map.containsKey("choke") ? Integer.parseInt(map.get("choke")) : 0;
		this.infiniteBlades = map.containsKey("infiniteBlades") ? Integer.parseInt(map.get("infiniteBlades")) : 0;
		this.noxiousFumes = map.containsKey("noxiousFumes") ? Integer.parseInt(map.get("noxiousFumes")) : 0;
		this.aThousandCuts = map.containsKey("aThousandCuts") ? Integer.parseInt(map.get("aThousandCuts")) : 0;
		this.afterImage = map.containsKey("afterImage") ? Integer.parseInt(map.get("afterImage")) : 0;
		this.explodeOnDeath = map.containsKey("explodeOnDeath") ? map.get("explodeOnDeath").equals("1") : false;
		this.envenom = map.containsKey("envenom") ? Integer.parseInt(map.get("envenom")) : 0;
		this.phantasmalKiller = map.containsKey("phantasmalKiller") ? Integer.parseInt(map.get("phantasmalKiller")) : 0;
		this.blockNextTurn = map.containsKey("blockNextTurn") ? Integer.parseInt(map.get("blockNextTurn")) : 0;
		this.drawNextTurn = map.containsKey("drawNextTurn") ? Integer.parseInt(map.get("drawNextTurn")) : 0;
		this.energyGainNextTurn = map.containsKey("energyGainNextTurn") ? Integer.parseInt(map.get("energyGainNextTurn")) : 0;
		this.poisonMultiplier = map.containsKey("poisonMultiplier") ? Integer.parseInt(map.get("poisonMultiplier")) : 1;
		this.fillHand = map.containsKey("fillHand") ? Integer.parseInt(map.get("fillHand")) : 0;
		this.finisher = map.containsKey("finisher") ? Integer.parseInt(map.get("finisher")) : 0;
		this.flechettes = map.containsKey("flechettes") ? Integer.parseInt(map.get("flechettes")) : 0;

		checkErrors();
	}
	
	/** Utilisé dans le constructeur, vérifie que toutes les variables sont bonnes. */
	private void checkErrors() {
		if (draw < -1) throw new IllegalArgumentException("The value 'draw' must be -1 or more.");
		if (noDraw < 0) throw new IllegalArgumentException("The value 'noDraw' must be 0 or more.");
		if (energyGain < 0) throw new IllegalArgumentException("The value 'energyGain' must be 0 or more.");
		if (drawBonus < 0) throw new IllegalArgumentException("The value 'drawBonus' must be 0 or more.");
		if (energyGainBonus < 0) throw new IllegalArgumentException("The value 'energyGainBonus' must be 0 or more.");
	}

	/**
	 * Applique tous les effets de la carte.
	 * 
	 * @param targetIndex (index de l'enemie ciblé dans la liste)
	 */
	public void applyEffects(Hero user, List<Enemy> enemies, int targetIndex) {
		DataBattleRoom.println(user.name()+" utilise "+name()+".");
		System.out.println(toString());
		
		// Autre effets
		for (int i=0; i<playTopDrawPileCard; i++) {
			user.deck().playTopDrawPileCard(enemies, targetIndex, playedCardExhaust);
		}
		// Attaque / debuff
		for (int i=0; i<getAttackNbr(); i++) {
			switch (target) {
			case SINGLE, SELF:
				applyEffectsEnemy(user, enemies.get(targetIndex), enemies);
				break;

			case ALL:
				for (Enemy enemy : enemies) {
					applyEffectsEnemy(user, enemy, enemies);
				}
				break;
				
			case RANDOM:
				Enemy enemy = enemies.get(random.nextInt(enemies.size()));
				applyEffectsEnemy(user, enemy, enemies);
				break;
			default:
				throw new IllegalStateException("target est surement null.");
			}
		}
		// Application buff
		applyEffectsHero(user, enemies);
	}
	
	/** Applique les degats et les effets dependant des degats infligés. */
	private void damageEnemy(int damage, Hero user, Enemy enemy) {
		damage = enemy.damage(user.attack(damage));
		if (damage != 0) {
			DataBattleRoom.println(user.name()+" inflige "+damage+" a "+enemy.name() + " AHAH " + user.getEnvenom());
		}
		user.attackSideEffect(damage, enemy);
	}
	
	public void applyEffectsEnemy(Hero user, Enemy enemy, List<Enemy> enemies) {
		super.applyEffectsOpponent(user, enemy);
		
		if (flechettes > 0) {
			for (Card card : user.deck().hand()) {
				if (card.category() == Category.SKILL) {
					damageEnemy(flechettes, user, enemy);
				}
			}
		}
		
		damageEnemy(
				user.deck().drawPileSize()*mindBlast
				+ user.deck().getAttacksPlayedCount()*finisher
				, user, enemy);
		
		enemy.addChoke(choke);
		enemy.setExplodeOnDeath(explodeOnDeath);
		enemy.addPoison(enemy.getPoison()*(poisonMultiplier-1));
		
		if (conditionBonus == ConditionBonus.VULNERABLE_ON_ENEMY && enemy.getVulnerable() > 0
				|| conditionBonus == ConditionBonus.KILL && enemy.getLife()<=0
				|| conditionBonus == ConditionBonus.TARGET_ATTACKING && enemy.nextAction().getIntents().contains(TypeIntent.ATTACK)
				|| conditionBonus == ConditionBonus.TARGET_POISONED && enemy.getPoison() > 0
				|| conditionBonus == ConditionBonus.DRAWPILE_EMPTY && user.deck().drawPileSize() == 0
				|| conditionBonus == ConditionBonus.WEAK_ON_ENEMY && enemy.getWeak() > 0
		) {
			applyBonusEffectsHero(user, enemy, enemies);
		}
		if (conditionBonus == ConditionBonus.NO_ATTACK_IN_HAND) {
			boolean res = true;
			for (Card card : user.deck().hand()) {
				if (card.category() == Category.ATTACK) {
					res = false;
					break;
				}
			}
			if (res) applyBonusEffectsHero(user, enemy, enemies);
		}
	}
	
	public void applyBonusEffectsHero(Hero user, Enemy enemy, List<Enemy> enemies) {
		int d = enemy.damage(user.attack(damageBonus));
		if (d != 0) { DataBattleRoom.println(user.name()+" inflige "+d+" a "+enemy.name()); }
		
		user.deck().draw(drawBonus, enemies);
		user.useEnergy(-energyGainBonus);
		user.addStrength(strengthBonus);
		user.addMaxLife(maxLifeBonus);
		user.addGold(goldBonus);
	}
	
	/**
	 * Applique les effets ciblant uniquement le Hero (qui est ici l'utilisateur).
	 */
	public void applyEffectsHero(Hero user, List<Enemy> enemies) {
		super.applyEffectsUser(user);
		if (shuffleDeck) user.deck().shuffleDeck();		
		user.deck().draw(draw, enemies);
		user.addNoDraw(noDraw);
		user.useEnergy(-energyGain);
		user.addCombust(combust);
		user.addDarkEmbrace(darkEmbrace);
		user.addEvolve(evolve);
		user.addFeelNoPain(feelNoPain);
		user.addFireBreathing(fireBreathing);
		user.addRupture(rupture);
		user.setBarricade(barricade);
		user.addBerserk(berserk);
		user.addBrutality(brutality);
		user.setCorruption(corruption);
		user.addDemonForm(demonForm);
		user.addJuggernaut(juggernaut);
		user.addFlameBarrier(flameBarrier);
		user.addRage(rage);
		user.addDoubleTap(doubleTap);
		user.addMagnetism(magnetism);
		user.addMayhem(mayhem);
		user.addPanache(panache);
		user.addBomb(bomb);
		user.addMaxLife(gainMaxLife);
		user.heal((int)(user.getMaxLife()*healRatio));
		user.addAccuracy(accuracy);
		user.setKeepBlock(keepBlock);
		user.addInfiniteBlades(infiniteBlades);
		user.addNoxiousFumes(noxiousFumes);
		user.addAThousandCuts(aThousandCuts);
		user.addAfterImage(afterImage);
		user.addBurst(burst);
		user.addEnvenom(envenom);
		user.addDrawNextTurn(drawNextTurn);
		user.addBlockNextTurn(blockNextTurn);
		user.addEnergyGainNextTurn(energyGainNextTurn);
		user.addPhantasmalKiller(phantasmalKiller);
		
		if (getAddCardToPile() != null) {
			user.deck().addCardToPile(Datas.getCard(getAddCardToPile()), getTargetPile(), getAddCardToTopPile(), getAddCardToPileNbr());
		}
		for (int i=0; i<exhaustRandomCardFromHand; i++) {
			if (user.deck().hand().size() > 0) {
				user.deck().exhaustCardFromHand(random.nextInt(user.deck().hand().size()));
			}
		}
		if (exhaustHandCategory != null) {
			ArrayList<Card> hand = user.deck().hand();
			for (int i=hand.size()-1; i>=0; i--) {
				if (hand.get(i).category() == exhaustHandCategory ^ reverseSelection) {
					user.deck().exhaustCardFromHand(i);
				}
			}
		}
		
		if (getAddRandomCard(user) > 0) {
			Card card = null;
			for (int i=0; i<getAddRandomCard(user); i++) {
				if (addRandomCardClass != null) {
					if (addRandomCardCategory != null) {
						card = Datas.getRandomCard(addRandomCardClass, addRandomCardCategory);
					} else {
						card = Datas.getRandomCard(addRandomCardClass);
					}
				}
				if (card == null) throw new IllegalStateException();
				
				card.setEnergyCostBonus(addRandomCardCost, keepEnergyCost);
				user.deck().addCardToPile(card, getTargetPile(), getAddCardToTopPile());
				
			}
		}
		
		for (int i=0; i<fillEmptyPotion; i++) {
			if (!user.addPotion(Datas.getRandomPotion())) break;
		}
		
		if (changeRandomCardCost >= -1) {
			ArrayList<Card> hand = user.deck().hand();
			hand.get(random.nextInt(hand.size())).setEnergyCostBonus(changeRandomCardCost, keepEnergyCost);
		}
		
		if (changeHandCost >= -1) {
			for (Card card : user.deck().hand()) {
				card.setEnergyCostBonus(changeHandCost, keepEnergyCost);
			}
		}
		
		user.deck().draw(fillHand - user.deck().hand().size(), enemies);
		
		user.addNoBlock(noBlock);
	}
	
	public boolean isUsable(Hero hero) {
		if (requireHandFullOfCategory != null) {
			for (Card card : hero.deck().hand()) {
				if (card.category() != requireHandFullOfCategory) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean needTarget() {
		System.out.println(target);
		return (super.needTarget() 
				|| playTopDrawPileCard > 0
				|| bonusDamageFromStrike > 0
				|| conditionBonus == ConditionBonus.TARGET_ATTACKING
				|| mindBlast != 0
				|| poisonMultiplier != 1
				|| choke != 0
				|| explodeOnDeath
				|| finisher > 0
				|| flechettes > 0
				) && target == Target.SINGLE
				;
	}
	
	public void setEnergyGainNextTurn(int amount) { energyGainNextTurn = amount; }
	public void setDrawNextTurn(int amount) { drawNextTurn = amount; }

	public Target target() { return target; }
	public int getBonusDamageFromStrike() { return bonusDamageFromStrike; }
	public Rarity rarity() { return rarity; }
	public ConditionBonus getConditionBonus() { return conditionBonus; }
	public int getEnergyGainBonus() { return energyGainBonus; }
	public int getAddRandomCard(Hero user) { return addRandomCard; }
}
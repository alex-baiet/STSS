package fr.silex.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import fr.silex.Deck.DeckPile;
import fr.silex.FileManager;
import fr.silex.data.DataBattleRoom;
import fr.silex.entity.Enemy;
import fr.silex.entity.Entity;
import fr.silex.exception.BadFormatException;

public class Action implements Comparable<Action> {
	/**
	 * Enum listant les différentes intention possible ennemie.
	 */
	public static enum TypeIntent {
		ATTACK, BLOCK, BUFF, DEBUFF, UNKNOWN;
		
		/**
		 * Renvoie le nom modifié comme ceci : "ATTACK" devient "Attack".
		 */
		@Override
		public String toString() {
			String firstChar = ""+super.toString().charAt(0);
			return super.toString().toLowerCase().replaceFirst(firstChar, firstChar.toUpperCase());
		}
	}
	
	private final ArrayList<TypeIntent> intents = new ArrayList<>();
	
	private final String name;
	private final String description;
	
	private int damage; // valeur nécessaire pour avancer dans le jeu :)
	private final int block; // défense bloquant un dégâts par point.
	private final int vulnerable; // +50% de dégâts subis
	private int weak; // -25% de dégâts infligés
	private final int strength; // +1 dégâts par points
	private final int strengthDown; // -1 strength par point à la fin du tour.
	private final int strengthAffect; // multiplicateur de dégâts ajoutés par la strength
	private int attackNbr; // Nombre d'attaque en une action.
	private final int heal; // Nombre de points de vie récupérer
	private int damageStrength; // Strength retiré à l'ennemie
	private final int strengthDownTarget; // Strength retiré à l'ennemie a la fin de son tour
	private final int blockMultiplier; // Multiplie le block de l'utilisateur
	private final int selfVulnerable; // Ajoute du vulnerable à l'utilisateur.
	private final boolean vampirism; // Récupère de la vie proportionnelement au dégâts infligés
	private final int ritual; // Ajoute 1 strength par ritual par tour.
	private final int frail; // Réduit le block gagné par les cartes de 25%.
	private final boolean damageIsBlock; // Inflige des dégâts équivalent à la défense.
	private final int strengthMultiplier; // Multiplie la strength actuel de l'entité.
	private final int regenerate; // Soigne l'entité à la fin de chaque tour.
	private final int regeneration; // Soigne l'entité à la fin de chaque tour. Diminue à chaque tour.
	private final int artifact; // Annule un debuff de l'ennemie.
	private final int metallicize; // +X block à la fin du tour.
	private final int platedArmor; // +X block à la fin du tour. -1 platedArmor a chaque attaque non bloqué
	private final int dexterity; // +X block par gain de block.
	private final int dexterityDown; // -X dexterity à la fin du tour.
	private final int intangible; // Dégâts réduit à 1.
	private final int thorns; // +X dégâts à l'attaquant.
	private final int poison; // +X dégâts et -1 poison en début de tour.	

	// Gestion de carte
	private final String addCardToPile; // Nom de la carte ajoutée dans la pile de deck.
	private final int addCardToPileNbr; // Nombre de carte à ajouter dans une pile du deck.
	private final DeckPile targetPile; // Pile où est ajoutée la carte.
	private final boolean addCardToTopPile;

	public Action(String data) throws BadFormatException {
		HashMap<String, String> map = FileManager.getFileValue(data);
		name = Objects.requireNonNull(map.get("name"));
		description = map.containsKey("description") ? map.get("description") : "no description";
		damage = map.containsKey("damage") ? Integer.parseInt(map.get("damage")): 0;
		block = map.containsKey("block") ? Integer.parseInt(map.get("block")): 0;
		vulnerable = map.containsKey("vulnerable") ? Integer.parseInt(map.get("vulnerable")): 0;
		weak = map.containsKey("weak") ? Integer.parseInt(map.get("weak")): 0;
		strength = map.containsKey("strength") ? Integer.parseInt(map.get("strength")): 0;
		strengthDown = map.containsKey("strengthDown") ? Integer.parseInt(map.get("strengthDown")): 0;
		strengthAffect = map.containsKey("strengthAffect") ? Integer.parseInt(map.get("strengthAffect")): 1;
		attackNbr = map.containsKey("attackNbr") ? Integer.parseInt(map.get("attackNbr")): 1;
		heal = map.containsKey("heal") ? Integer.parseInt(map.get("heal")): 0;
		damageStrength = map.containsKey("damageStrength") ? Integer.parseInt(map.get("damageStrength")): 0;
		strengthDownTarget = map.containsKey("strengthDownTarget") ? Integer.parseInt(map.get("strengthDownTarget")): 0;
		blockMultiplier = map.containsKey("blockMultiplier") ? Integer.parseInt(map.get("blockMultiplier")): 1;
		selfVulnerable = map.containsKey("selfVulnerable") ? Integer.parseInt(map.get("selfVulnerable")): 0;
		vampirism = map.containsKey("vampirism") ? map.get("vampirism").equals("1") : false;
		ritual = map.containsKey("ritual") ? Integer.parseInt(map.get("ritual")): 0;
		frail = map.containsKey("frail") ? Integer.parseInt(map.get("frail")): 0;
		addCardToPile = map.containsKey("addCardToPile") ? map.get("addCardToPile") : null;
		addCardToPileNbr = map.containsKey("addCardToPileNbr") ? Integer.parseInt(map.get("addCardToPileNbr")) : 1;
		targetPile = map.containsKey("targetPile") ? DeckPile.valueOf(map.get("targetPile").toUpperCase()) : DeckPile.DISCARD;
		addCardToTopPile = map.containsKey("addCardToTopPile") ? map.get("addCardToTopPile").equals("1") : true;
		damageIsBlock = map.containsKey("damageIsBlock") ? map.get("damageIsBlock").equals("1") : false;
		strengthMultiplier = map.containsKey("strengthMultiplier") ? Integer.parseInt(map.get("strengthMultiplier")) : 1;
		regenerate = map.containsKey("regenerate") ? Integer.parseInt(map.get("regenerate")) : 0;
		regeneration = map.containsKey("regeneration") ? Integer.parseInt(map.get("regeneration")) : 0;
		artifact = map.containsKey("artifact") ? Integer.parseInt(map.get("artifact")) : 0;
		metallicize = map.containsKey("metallicize") ? Integer.parseInt(map.get("metallicize")) : 0;
		platedArmor = map.containsKey("platedArmor") ? Integer.parseInt(map.get("platedArmor")) : 0;
		dexterity = map.containsKey("dexterity") ? Integer.parseInt(map.get("dexterity")) : 0;
		dexterityDown = map.containsKey("dexterityDown") ? Integer.parseInt(map.get("dexterityDown")) : 0;
		intangible = map.containsKey("intangible") ? Integer.parseInt(map.get("intangible")) : 0;
		thorns = map.containsKey("thorns") ? Integer.parseInt(map.get("thorns")) : 0;
		poison = map.containsKey("poison") ? Integer.parseInt(map.get("poison")) : 0;
		
		checkErrors();
	}

	private void checkErrors() {
		if (damage < 0) {
			throw new IllegalArgumentException("The value 'damage' must be 0 or more.");
		}
		if (block < 0) {
			throw new IllegalArgumentException("The value 'block' must be 0 or more.");
		}
		if (vulnerable < 0) {
			throw new IllegalArgumentException("The value 'vulnerable' must be 0 or more.");
		}
		if (weak < 0) {
			throw new IllegalArgumentException("The value 'weak' must be 0 or more.");
		}
		if (attackNbr < -1) {
			throw new IllegalArgumentException("The value 'attackNbr' must be -1 or more.");
		}
		if (selfVulnerable < 0) {
			throw new IllegalArgumentException("The value 'selfVulnerable' must be 0 or more.");
		}
	}

	/**
	 * Applique tous les effets de l'action.
	 */
	public void applyEffects(Entity user, Entity enemy) {
		applyEffectsOpponent(user, enemy);
		applyEffectsUser(user);
	}
	
	/**
	 * Applique les effets ciblant l'ennemie (attaque, debuff, ...)
	 */
	public void applyEffectsOpponent(Entity user, Entity enemy) {

		int finalDamage;
		if (damageIsBlock) {
			finalDamage = user.getBlock();
		} else {
			finalDamage = user.attack(this);				
		}
		int ultimateFinalDamage = enemy.damage(finalDamage);
		user.attackSideEffect(ultimateFinalDamage, enemy);
		
		if (vampirism) {
			DataBattleRoom.println(user + " a récupéré " + ultimateFinalDamage + " pv.");
			user.heal(ultimateFinalDamage);
		}
		if (damage > 0 || damageIsBlock) {
			DataBattleRoom.println(user.name() + " inflige " + ultimateFinalDamage + " dégâts à " + enemy.name());
		}
		if (vulnerable > 0 && !enemy.useArtifact()) {
			enemy.addVulnerable(vulnerable);
			DataBattleRoom.println(user.name() + " inflige " + vulnerable + " vulnerable à " + enemy.name());
		}
		if (weak > 0 && !enemy.useArtifact()) {
			enemy.addWeak(weak);
			DataBattleRoom.println(user.name() + " inflige " + weak + " weak à " + enemy.name());
		}
		if (damageStrength > 0 && !enemy.useArtifact()) {
			enemy.addStrength(-damageStrength);								
			if (strengthDownTarget != 0) { 
				enemy.addStrengthDown(strengthDownTarget);
			}
		}
		if (frail > 0 && !enemy.useArtifact()) {
			enemy.addFrail(frail);
			DataBattleRoom.println(user.name() + " inflige " + frail + " frail à " + enemy.name());					
		}
		if (poison > 0 && !enemy.useArtifact()) {
			enemy.addPoison(poison);
			DataBattleRoom.println(user.name() + " inflige " + poison + " poison à " + enemy.name());				
		}
	}

	/**
	 * Applique les effets ciblant l'utilisateur. (buff, block, ...)
	 */
	public void applyEffectsUser(Entity user) {
		user.addBlock(block);
		user.addStrength(strength);
		user.addStrength(user.getStrength()*strengthMultiplier - user.getStrength());
		user.addStrengthDown(strengthDown);
		user.heal(heal);
		user.multiplyBlock(blockMultiplier);
		user.addVulnerable(selfVulnerable);
		user.addRitual(ritual);
		user.addRegeneration(regeneration);
		user.addRegenerate(regenerate);
		user.addArtifact(artifact);
		user.addMetallicize(metallicize);
		user.addPlatedArmor(platedArmor);
		user.addDexterity(dexterity);
		user.addDexterityDown(dexterityDown);
		user.addIntangible(intangible);
		user.addThorns(thorns);
	}

	/**
	 * Renvoie les intentions cette action (ce que ca va faire)
	 */
	public ArrayList<TypeIntent> getIntents() {
		if (intents == null || intents.isEmpty()) {			
			if (damage > 0) {
				intents.add(TypeIntent.ATTACK);
			}
			if (block > 0) {
				intents.add(TypeIntent.BLOCK);
			}
			if (vulnerable>0 || weak>0 || strengthDown>0 || frail>0) {
				intents.add(TypeIntent.DEBUFF);
			}
			if (strength>0 || heal>0 || vampirism || ritual>0) {
				intents.add(TypeIntent.BUFF);
			}
			if (intents.size() == 0) {
				intents.add(TypeIntent.UNKNOWN);
			}
		}
				
		return new ArrayList<TypeIntent>(intents);
		
	}
	
	/**
	 * Renvoie les intentions de l'action sous une forme agréable pour le joueur.
	 */
	public String getIntentsString() {
		if (intents == null) { getIntents(); }
		StringBuilder builder = new StringBuilder("Prochaine action :");
		for (TypeIntent intent : intents) {
			if (intent == TypeIntent.ATTACK) {
				builder.append("\n    - " + intent + " (" + damage + (attackNbr>1 ? "x"+attackNbr : "") + ")");
			}
			else {
				builder.append("\n    - " + intent);
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * Retourne true si l'application de l'un des champs de la carte requiert une la sélection d'une cible.
	 */
	public boolean needTarget() {
		return (damage != 0 
				|| vulnerable != 0 
				|| weak != 0 
				|| damageStrength != 0
				|| frail != 0
				|| damageIsBlock
				|| poison != 0
				);
	}
	
	@Override
	public String toString() {
		return name+" ("+description+")";
	}

	/** Renvoie les intentions de l'action sous une forme agréable pour le joueur. */
	public String getIntentsString(Enemy enemy) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDamage(int amount) { damage = amount; }
	public void setWeak(int amount) { weak = amount; }
	public void setDamageStrength(int amount) { damageStrength = amount; }
	
	public String name() { return name; }
	public int getDamage() { return damage; }
	public int getStrengthAffect() { return strengthAffect; }
	public int getAttackNbr() { return attackNbr; }
	public void setAttackNbr(int amount) { attackNbr = amount; }
	public String description() { return description; }
	public int getWeak() { return weak; }
	public String getAddCardToPile() { return addCardToPile; }
	public int getAddCardToPileNbr() { return addCardToPileNbr; }
	public boolean getAddCardToTopPile() { return addCardToTopPile; }
	public DeckPile getTargetPile() { return targetPile; }

	@Override
	public int compareTo(Action o) {
		return name.compareTo(o.name);
	}
	
}

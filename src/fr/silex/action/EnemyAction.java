package fr.silex.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import fr.silex.FileManager;
import fr.silex.data.DataBattleRoom;
import fr.silex.data.Datas;
import fr.silex.entity.Enemy;
import fr.silex.entity.Hero;
import fr.silex.exception.BadFormatException;

/**
 * Action pour les Enemies.
 */
public class EnemyAction extends Action {
	private static Random random = new Random();
	
	private ArrayList<TypeIntent> intents = null;
	private String data;
	private int minDamage;
	private int maxDamage;
	private String invoke;
	private int nbrInvoke;
	private boolean disappear;
	private int protect;
	private boolean entangled; // Interdit l'utilisation de carte attack pour le Hero pour un tour.
	private int enrage;
	
	private boolean divider; // ? peut-etre un effet du boss Hexagon jsp
	
	public EnemyAction(String data) throws BadFormatException {
		super(data);
		this.data = data;
		HashMap<String, String> map = FileManager.getFileValue(data);

		minDamage = map.containsKey("minDamage") ? Integer.parseInt(map.get("minDamage")) : getDamage();
		maxDamage = map.containsKey("maxDamage") ? Integer.parseInt(map.get("maxDamage")) : getDamage();
		setDamage(random.nextInt(maxDamage+1-minDamage) + minDamage);
		invoke = map.containsKey("invoke") ? map.get("invoke") : null;
		nbrInvoke = map.containsKey("nbrInvoke") ? Integer.parseInt(map.get("nbrInvoke")) : 1;
		disappear = map.containsKey("disappear") ? map.get("disappear").equals("1") : false;
		protect = map.containsKey("protect") ? Integer.parseInt(map.get("protect")) : 0;
		entangled = map.containsKey("entangled") ? map.get("entangled").equals("1") : false;;
		enrage = map.containsKey("enrage") ? Integer.parseInt(map.get("enrage")) : 0;

		divider = map.containsKey("divider") ? map.get("divider").equals("1") : false;
	}
	
	/**
	 * Apply all effects of an EnemyAction.
	 * @param user
	 * @param hero
	 * @param allies
	 */
	public void applyEffects(Enemy user, Hero hero, ArrayList<Enemy> team) {
		for (int i=0; i<getAttackNbr(); i++) {
			applyEffectHero(user, hero);
		}
		applyDebuffHero(user, hero);
		applyEffectUser(user);
		
		if (invoke != null) { // ajout sbires
			for (int i=0; i<nbrInvoke; i++) {
				team.add(Datas.getEnemy(invoke));
			}
		}
		
		if (disappear) {
			team.remove(user);
		}
		
		ArrayList<Enemy> allies = new ArrayList<>(team);
		allies.remove(user);
		if (allies.size() > 0) {
			Enemy allyToBuff = allies.get(random.nextInt(allies.size()));
			allyToBuff.addBlock(protect);
		}
	}
	
	/**
	 * Appliques les effets de l'Action au monstre 
	 * (qui est l'utilisateur).
	 */
	public void applyEffectUser(Enemy user) {
		super.applyEffectsUser(user);
		user.addEnrage(enrage);
	}
	
	/**
	 * Applique les effets à l'ennemie
	 * (qui est ici le Hero).
	 */
	public void applyEffectHero(Enemy user, Hero hero) {
		if (divider) {
			int damage = (hero.getLife() / 12 + 1) * 6;
			int finalDamage = user.attack(this, damage);
			int ultimateFinalDamage = hero.damage(finalDamage);
			DataBattleRoom.println(user.name() + " inflige " + ultimateFinalDamage + " dégâts à " + hero.name());
		} else {
			super.applyEffectsOpponent(user, hero);
		}
		if (divider || getDamage() > 0) {
			int damage = user.damage(hero.getFlameBarrier());
			if (damage > 0) DataBattleRoom.println("L'effet flame barrier inflige " + damage + " dégâts à " + user.name());
		}
	}
	
	/** Applique les debuff au Hero qui ne sont sensé être appliqués qu'une fois. */
	public void applyDebuffHero(Enemy user, Hero hero) {
		hero.setEntangled(entangled);
		if (getAddCardToPile() != null) {
			hero.deck().addCardToPile(Datas.getCard(getAddCardToPile()), getTargetPile(), getAddCardToTopPile(), getAddCardToPileNbr());
		}
	}
	
	/**
	 * Renvoie une copie de lui-même.
	 */
	public EnemyAction copy() {
		return new EnemyAction(data);
	}
	
	@Override
	public String toString() {
		return "EnemyAction "+super.toString();
	}

	/**
	 * Renvoie les intentions de l'entité (A UTILISER SEULEMENT POUR VOIR LES INTENTION D'UN MONSTRE)
	 */
	@Override
	public ArrayList<TypeIntent> getIntents() {
		if (intents == null || intents.isEmpty()) {
			intents = super.getIntents();
			if (intents.contains(TypeIntent.UNKNOWN)) {
				intents = new ArrayList<>();
			}
			if (divider) {
				intents.add(TypeIntent.ATTACK);
			}
			if (protect > 0) {
				intents.add(TypeIntent.BLOCK);
			}
			if ((getAddCardToPile()!=null && getAddCardToPileNbr()>0) || entangled) {
				intents.add(TypeIntent.DEBUFF);
			}
			if (enrage > 0) {
				intents.add(TypeIntent.BUFF);
			}
			if (intents.size() == 0) {
				intents.add(TypeIntent.UNKNOWN);
			}
		}
				
		return intents;
	}

	/**
	 * Renvoie les intentions de l'action sous une forme agréable pour le joueur.
	 */
	@Override
	public String getIntentsString(Enemy enemy) {
		if (intents == null) { getIntents(); }
		StringBuilder builder = new StringBuilder("Prochaine action :");
		for (TypeIntent intent : intents) {
			if (intent == TypeIntent.ATTACK) {
				if (divider) {
					builder.append("\n    - " + intent + " (your life/2 + 6)");
				} else {
					builder.append("\n    - " + intent + " (" + (enemy.attack(this)) + (getAttackNbr()>1 ? "x"+getAttackNbr() : "") + ")");
				}
			}
			else {
				builder.append("\n    - " + intent);
			}
		}
		
		return builder.toString();
	}
	
	
}

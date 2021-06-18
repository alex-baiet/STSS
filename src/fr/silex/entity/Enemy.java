package fr.silex.entity;

import java.util.ArrayList;
import java.util.List;

import fr.silex.action.Card;
import fr.silex.action.EnemyAction;
import fr.silex.data.DataBattleRoom;
import fr.silex.strategy.Strategy;

public class Enemy extends AbstractEntity {
	private Strategy strategy;
	private EnemyAction nextAct;
	
	private int enrage = 0; // +X strength a chaque Skill jouée.
	private int choke = 0; // X degats a chaque carte jouées.
	private boolean explodeOnDeath = false; // X degats a chaque carte jouées.
	private int angry = 0;
	
	public Enemy(String name, String imgPath, int maxLife, Strategy strategy) {
		super(name, imgPath, maxLife);
		this.strategy = strategy.copy();
	}
	
	/**
	 * Must be used at the start of each turn.
	 */
	public void prepareAct() {
		nextAct = strategy.getAction();
		System.out.println("Prochaine action de "+name()+" : "+nextAct.name());
	}
	
	public void act(Hero hero, ArrayList<Enemy> allies) {
		nextAct.applyEffects(this, hero, allies);
	}
	
	public EnemyAction nextAction() {
		return nextAct;
	}
	
	public void checkChangeStrategy(List<Enemy> allies) {
		nextAct = strategy.checkAction(this, allies);
	}
	
	/**
	 * Ajoute un buff en fonction des effets sur l'Enemy et de la carte du joueur jouée.
	 * @param cardCategory: Category of the played card.
	 */
	public void buffFromHeroAction(Card.Category cardCategory) {
		damage(choke);
		
		switch (cardCategory) {
		case SKILL:
			addStrength(enrage);
			break;
		default:
			break;
		}
	}

	public void addEnrage(int amount) { 
		if (amount != 0) {
			DataBattleRoom.println(name()+" a gagné "+amount+" enrage.");
			enrage += amount;
		}
	}

	public void addChoke(int amount) { 
		if (amount != 0) {
			DataBattleRoom.println(name()+" a gagné "+amount+" choke.");
			choke += amount;
		}
	}

	public void setExplodeOnDeath(boolean value) { 
		if (value) {
			DataBattleRoom.println(name()+" explosera à sa mort.");
			explodeOnDeath = value;
		}
	}

	public void addAngry(int amount) { 
		if (amount != 0) {
			DataBattleRoom.println(name()+" a gagné "+amount+" angry.");
			angry += amount;
		}
	}

	@Override
	public void newTurn() {
		removeBlock();
		super.newTurn();
	}
	
	@Override
	public void endTurn() {
		super.endTurn();
		choke = 0;
	}
	
	@Override
	public int damage(int amount) {
		int d = super.damage(amount);
		if (d > 0) {
			addStrength(angry);
		}
		return d;
	}
	
	@Override
	public ArrayList<String> getInformations() {
		ArrayList<String> infos = super.getInformations();
		if (enrage != 0) infos.add("enrage="+enrage);
		if (choke != 0) infos.add("choke="+choke);
		if (explodeOnDeath) infos.add("explode on death");
		if (nextAct != null) infos.add(nextAct.getIntentsString(this));
		
		return infos;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Enemy ").append(name()).append(" : ");
		ArrayList<String> infos = getInformations();
		for (int i=0; i<infos.size(); i++) {
			builder.append(infos.get(i));
			if (i!=infos.size()-1) builder.append(", ");
		}
		
		return builder.toString();
	}
	
	public boolean explodeOnDeath() { return explodeOnDeath; }
}

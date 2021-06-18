package fr.silex.strategy;

import fr.silex.action.EnemyAction;

/**
 * Used for PatternStrategy.
 */
public class ElementPattern {
	private EnemyAction action;
	private int nextAction;
	private float weight;
	
	public ElementPattern(EnemyAction action, int nextAction, float weight) {
		this.action = action;
		this.nextAction = nextAction;
		this.weight = weight;
	}

	private ElementPattern(ElementPattern elem) {
		action = elem.action.copy();
		nextAction = elem.nextAction;
		weight = elem.weight;
	}
	
	public ElementPattern copy() {
		return new ElementPattern(this);
	}
	
	public EnemyAction action() { return action; }
	public int nextAction() { return nextAction; }
	public float weight() { return weight; }

	
	@Override
	public String toString() {
		return action.toString()+", nextAction="+nextAction+", weight="+weight;
	}
}
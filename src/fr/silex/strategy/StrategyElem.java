package fr.silex.strategy;

import java.util.Objects;

import fr.silex.action.EnemyAction;

/**
 * Used for SimpleStrategy.
 */
public class StrategyElem {
	private EnemyAction action;
	private float weight;
	private int maxCombo = -1;
	
	/**
	 * @param action : EnemyAction.
	 * @param weight : The probability to play this action.
	 * @param maxCombo : The maximum consecutive uses.
	 * -1 mean infinite maximum.
	 */
	public StrategyElem(EnemyAction action, float weight, int maxCombo) {
		this.action = Objects.requireNonNull(action);
		this.weight = weight;
		this.maxCombo = maxCombo;
	}
	
	private StrategyElem(StrategyElem elem) {
		action = elem.action.copy();
		weight = elem.weight;
		maxCombo = elem.maxCombo;
	}

	public StrategyElem copy() {
		return new StrategyElem(this);
	}
	
	public EnemyAction action() { return action; }
	public float weight() { return weight; }
	public int maxCombo() { return maxCombo; }
}

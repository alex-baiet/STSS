package fr.silex.strategy;

import java.util.List;

import fr.silex.action.EnemyAction;
import fr.silex.entity.Enemy;

/**
 * This Strategy use the first Strategy if other allies are here. 
 * Else it will use the second Strategy.
 * May do wrong things if the Enemy using it is alone from the start ofthe battle.
 */
public class SupportStrategy implements Strategy {

	private Strategy[] strategies = { null, null };
	private int actualStrategy = 0;
	
	public SupportStrategy(Strategy strat1, Strategy strat2) {
		strategies[0] = strat1;
		strategies[1] = strat2;
	}
	
	private SupportStrategy(SupportStrategy strat) {
		strategies[0] = strat.strategies[0].copy();
		strategies[1] = strat.strategies[1].copy();
	}
	
	@Override
	public EnemyAction getAction() {
		return strategies[actualStrategy].getAction();
	}

	@Override
	public Strategy copy() {
		return new SupportStrategy(this);
	}

	@Override
	public EnemyAction checkAction(Enemy user, List<Enemy> allies) {
		int newStrategy = allies.size() == 1 ? 1 : 0;
		if (newStrategy != actualStrategy) {
			actualStrategy = newStrategy;
			return strategies[actualStrategy].getAction();
		}
		return strategies[actualStrategy].checkAction(user, allies);
	}
}

package fr.silex.strategy;

import java.util.List;

import fr.silex.action.EnemyAction;
import fr.silex.entity.Enemy;

/**
 * NON TESTE
 * 
 * Use a given Strategy, and when the life of the monster
 * drop under a limit, use the second given Strategy.
 */
public class LifeStrategy implements Strategy {

	private Strategy[] strategies = { null, null };
	// Valeur indiqué à quel moment il faut changer de stratégie.
	private float ratioChange;
	
	private int actualStrategy = 0;
	
	public LifeStrategy(Strategy strat1, Strategy strat2, float ratioChange) {
		strategies[0] = strat1;
		strategies[1] = strat2;
		this.ratioChange = ratioChange;
	}
	
	private LifeStrategy(LifeStrategy strat) {
		strategies[0] = strat.strategies[0].copy();
		strategies[1] = strat.strategies[1].copy();
		ratioChange = strat.ratioChange;
	}
	
	@Override
	public EnemyAction getAction() {
		return strategies[actualStrategy].getAction();
	}

	@Override
	public Strategy copy() {
		return new LifeStrategy(this);
	}

	@Override
	public EnemyAction checkAction(Enemy user, List<Enemy> allies) {
		int newStrategy = (float)user.getLife()/(float)user.maxLife() > ratioChange ? 0 : 1;
		if (newStrategy != actualStrategy) {
			actualStrategy = newStrategy;
			return strategies[actualStrategy].getAction();
		}
		return strategies[actualStrategy].checkAction(user, allies);
	}
}

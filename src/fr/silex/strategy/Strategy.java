package fr.silex.strategy;

import java.util.List;

import fr.silex.action.EnemyAction;
import fr.silex.entity.Enemy;

/**
 * Interface for an Enemy's strategy.
 */
public interface Strategy {
	
	/**
	 * @return the action that will play the monster.
	 */
	public EnemyAction getAction();

	/**
	 * @param user of the Strategy.
	 * @return return the same value has getAction, or a new action if the Strategy has changed.
	 */
	public EnemyAction checkAction(Enemy user, List<Enemy> allies);

	public Strategy copy();
	
}

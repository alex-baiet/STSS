package fr.silex.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.silex.action.EnemyAction;
import fr.silex.entity.Enemy;

/**
 * The simplest and most common Strategy.
 * Use a specified first action, 
 * then use a random action with a given probability
 * and a given max consecutive allowed use of an action.
 */
public class SimpleStrategy implements Strategy {

	private EnemyAction startAction = null;
	private ArrayList<StrategyElem> actions = new ArrayList<>();
	
	private static Random random = new Random();
	private int combo = 0;
	private EnemyAction lastAction = null;

	public SimpleStrategy() { }
	
	private SimpleStrategy(SimpleStrategy strat) {
		startAction = strat.startAction==null ? null : strat.startAction.copy();
		for (StrategyElem act : strat.actions) {
			actions.add(act.copy());
		}
	}
	
	public void addAction(EnemyAction action, float weight, int maxCombo) {
		addAction(new StrategyElem(action, weight, maxCombo));
	}
	
	public void addAction(StrategyElem stratElem) {
		Objects.requireNonNull(stratElem);
		actions.add(stratElem);
	}
	
	public void setStartAction(EnemyAction action) { startAction = action; }

	private boolean isPlayable(StrategyElem elem) {
		return !(elem.action() == lastAction && combo >= elem.maxCombo()) || elem.maxCombo() == -1;
	}
	
	@Override
	public EnemyAction getAction() {
		// Vérification que la liste d'action n'est pas vide.
		if (actions.size() == 0) {
			throw new IllegalStateException("The list of action of this SimpleStrategy is empty. You can't get any action.");
		}
		
		// Utilisation première action
		if (lastAction == null && startAction != null) {
			combo++;
			lastAction = startAction;
			return startAction;
		}
		
		// Sinon utilisation d'une action dans la liste
		// Cacul probabilité total
		float maxWeight = 0;
		for (StrategyElem elem : actions) {
			if (isPlayable(elem)) {
				maxWeight += elem.weight();
			}
		}
		
		// Selection de l'action
		float selection = random.nextFloat() * maxWeight;
		float totalWeight = 0;
		for (StrategyElem elem : actions) {
			if (isPlayable(elem)) {
				totalWeight += elem.weight();
				if (totalWeight > selection) {
					// On retourne l'action
					if (elem.action() == lastAction) {
						combo++;
					} else {
						lastAction = elem.action();
						combo=1;
					}
					return elem.action();
				}
			}
		}
		throw new IllegalStateException("An error occured while trying to get an action.\n"
				+ "Maybe no action was available (limited combo with only one action in list)");
	}

	@Override
	public EnemyAction checkAction(Enemy user, List<Enemy> allies) {
		return lastAction;
	}
	
	@Override
	public Strategy copy() {
		return new SimpleStrategy(this);
	}
}

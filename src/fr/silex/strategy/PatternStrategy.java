package fr.silex.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.silex.action.EnemyAction;
import fr.silex.entity.Enemy;

/**
 * Work like an automate.
 * 
 * Each element contains a list of possible action, 
 * and each action point toward another element.
 */
public class PatternStrategy implements Strategy {
	private static Random random = new Random();
	
	private ArrayList<ArrayList<ElementPattern>> pattern;
	private int curSelect=0;
	private EnemyAction lastAction = null;

	public PatternStrategy(List<ElementPattern> pattern) {
		pattern = new ArrayList<>(pattern);
	}
	
	public PatternStrategy() { 
		pattern = new ArrayList<>();
	}

	private PatternStrategy(PatternStrategy strat) {
		pattern = new ArrayList<>();
		for (ArrayList<ElementPattern> array : strat.pattern) {
			if (array == null) {
				pattern.add(array);
			}
			else {
				ArrayList<ElementPattern> underArray = new ArrayList<>();
				for (ElementPattern elem : array) {
					underArray.add(elem.copy());
				}
				pattern.add(underArray);
			}
		}
	}
	
	public void add(EnemyAction action, int nextAction, float weight, int position) {
		add(new ElementPattern(action, nextAction, weight), position);
	}
	
	public void add(ElementPattern elem, int position) {
		Objects.requireNonNull(elem);
		if (pattern.size() <= position) {
			while (pattern.size() < position) {
				pattern.add(new ArrayList<>());
			}
			pattern.add(new ArrayList<ElementPattern>(List.of(elem)));
		} else {
			pattern.get(position).add(elem);
		}
	}
	
	@Override
	public EnemyAction getAction() {
		if (pattern.get(curSelect) == null) {
			throw new IllegalStateException("The element at position "+curSelect+" does not exist.");
		}
		if (pattern.get(curSelect).size() == 1) {
			ElementPattern elem = pattern.get(curSelect).get(0);
			curSelect = elem.nextAction();
			lastAction = elem.action();
			return elem.action();
		}
		
		float totalWeight = 0f;
		float currentWeight = 0f;
		float targetWeight;
		
		for (ElementPattern elem : pattern.get(curSelect)) {
			totalWeight += elem.weight();
		}
		
		targetWeight = random.nextFloat() * totalWeight;
		
		for (ElementPattern elem : pattern.get(curSelect)) {
			currentWeight += elem.weight();
			if (currentWeight >= targetWeight) {
				curSelect = elem.nextAction();
				lastAction = elem.action();
				return elem.action();
			}
		}

		throw new RuntimeException("N'est pas sensé s'afficher.");
	}
	
	/**
	 * Raise an IllegalStateException if something is badly defined.
	 */
	public void checkIsUsable() {
		System.out.println("PATTERN:");
		for (ArrayList<ElementPattern> arrayList : pattern) {
			System.out.println(arrayList);
		}
		if (pattern.get(0) == null) {
			throw new IllegalStateException("The pattern has no default EnemyAction (= action at position 0).");
		}
		for (int i=0; i < pattern.size(); i++) {
			for (ElementPattern elem : pattern.get(i)) {
				if (elem.nextAction() >= pattern.size() || pattern.get(elem.nextAction()) == null) {
					throw new IllegalArgumentException("An ElementPattern has a nextAction an unexisting element.\n  position: "+i+"\n  EnemyAction: "+elem.action().toString());
				}
			}
		}
	}

	@Override
	public EnemyAction checkAction(Enemy user, List<Enemy> allies) {
		return lastAction;
	}
	
	@Override
	public Strategy copy() {
		return new PatternStrategy(this);
	}
	
}

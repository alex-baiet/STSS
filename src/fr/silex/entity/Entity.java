package fr.silex.entity;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import fr.silex.action.Action;

public interface Entity {
	
	public String name();

	/**
	 * Return the damage after taking in count all stats of the action's user.
	 * @param used action
	 * @return final damage.
	 */
	
	public String imgPath();
	
	public BufferedImage img();
	
	public int maxLife();
	
	public int attack(Action action);

	public int attack(float damage);

	/** Applique les effets en fonction des degats infligés. */
	public void attackSideEffect(int damage, Entity entity);

	/**
	 * damage entity depending of the initial damage entered.
	 * @param amount
	 * @return damage finally removed after calculation.
	 */
	public int damage(int amount);
	
	public int heal(int amount);
	
	public void removeBlock();

	public void multiplyBlock(int coef);

	public void addVulnerable(int amount);
	public int addBlock(int amount);
	public void addWeak(int amount);
	public void addStrength(int amount);
	public void addStrengthDown(int amount);
	public void addRitual(int amount);
	public void addFrail(int amount);
	public void addArtifact(int amount);
	public void addRegeneration(int amount);
	public void addRegenerate(int amount);
	public void addMaxLife(int amount);
	public void addMetallicize(int amount);
	public void addPlatedArmor(int amount);
	public void addDexterity(int amount);
	public void addDexterityDown(int amount);
	public void addIntangible(int amount);
	public void addThorns(int amount);
	public void addPoison(int amount);

	public boolean hasArtifact();
	
	public boolean useArtifact();

	/**
	 * L'entité commence un nouveau tour :
	 * Certains buff/debuf sont résolut.
	 */
	public void newTurn();
	
	/**
	 * L'entité termine son tour : 
	 * Certains buff/debuf sont résolut.
	 */
	public void endTurn();
	

	public int getLife();
	public int getMaxLife();
	public int getBlock();
	public int getStrength();
	public int getVulnerable();
	public int getWeak();
	public int getDexterity();
	public int getThorns();
	public int getPoison();

	public ArrayList<String> getInformations();
}

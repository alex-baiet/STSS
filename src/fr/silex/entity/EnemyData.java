package fr.silex.entity;

import java.util.Random;

import fr.silex.strategy.Strategy;

/**
 * This is not an Enemy that can fight : 
 * his purpose his only to be stored in Datas and to 
 * generate Enemy that can fight with some random values.
 */
public class EnemyData {
	private static final Random random = new Random();

	private final String name;
	private final String imgPath;
	private final int minStartLife;
	private final int maxStartLife;
	private final Strategy strategy;

	private int artifact=0;
	private int angry=0;
	
	public EnemyData(String name, String imgPath, int minStartLife, int maxStartLife, Strategy strategy) {
		this.name = name;
		this.imgPath = imgPath;
		this.minStartLife = minStartLife;
		this.maxStartLife = maxStartLife;
		this.strategy = strategy;
	}
	
	public Enemy createEnemy() {
		int maxLife = minStartLife + random.nextInt(maxStartLife+1 - minStartLife);
		Enemy enemy = new Enemy(name, imgPath, maxLife, strategy);
		enemy.addArtifact(artifact);
		enemy.addAngry(angry);
		
		return enemy;
	}
	
	public String name() { return name; }
	
	public void addArtifact(int amount) { artifact += amount; }
	public void addAngry(int amount) { angry += amount; }
	
}

package fr.silex.entity;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

import fr.silex.FileManager;
import fr.silex.action.Action;
import fr.silex.data.DataBattleRoom;

public abstract class AbstractEntity implements Entity {
	private final String name;
	private int maxLife;
	private int life;
	private final String imgPath;
	private BufferedImage img = null;
	private int block = 0;
	private int vulnerable = 0; // L'entité prend +50% de dégâts.
	private int weak = 0; // L'entité inflige -25% de dégâts.
	private int strength = 0; // L'entité inflige 1 dégât supplémentaire par point.
	private int strengthDown = 0; // L'entité perd de la strength équivalent à strengthDown à la fin du tour.
	private int ritual = 0; // L'entité perd de la strength équivalent à strengthDown à la fin du tour.
	private int frail = 0; // L'entité gagne 25% de block en moins.
	private int artifact = 0; // Chaque debuff n'est pas appliqué et retire un artifact à la place.
	private int regeneration = 0; // +X pv et -1 de regen à la fin du tour.
	private int regenerate = 0; // +X pv a la fin du tour.
	private int metallicize = 0; // +X block à la fin du tour.
	private int platedArmor = 0; // +X block à la fin du tour. -1 platedArmor a chaque attaque non bloqué
	private int dexterity = 0; // +X block par gain de block.
	private int dexterityDown = 0; // -X dexterity à la fin du tour.
	private int intangible = 0; // Dégâts réduit à 1.
	private int thorns = 0; // +X dégâts à l'attaquant.
	private int poison = 0; // +X dégâts et -1 poison en début de tour.	
	
	public AbstractEntity(String name, String imgPath, int maxLife) {
		if (maxLife < 1) {
			throw new IllegalArgumentException("'life' doit être supérieur ou égal à 1.");
		}
		this.name = Objects.requireNonNull(name);
		this.imgPath= imgPath; 
		this.maxLife = maxLife;
		life = maxLife;
	}

	public void resetStatus() {
		block = 0;
		vulnerable = 0;
		weak = 0; 
		strength = 0;
		strengthDown = 0;
		ritual = 0;
		frail = 0;
		artifact = 0;
		regeneration = 0;
		regenerate = 0;
		metallicize = 0;
		platedArmor = 0;
		dexterity = 0;
		dexterityDown = 0;
		intangible = 0;
		thorns = 0;
		poison = 0;
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public String imgPath() {
		return imgPath;
	}
	
	@Override
	public int maxLife() {
		return maxLife;
	}
	
	@Override
	public int damage(int amount) {
		if (amount > 0) {
			if (intangible > 0) { // Reduction des degats a 1
				amount = 1;
			}
			else if (vulnerable > 0) { // Sinon pas d'intangible, on applique les differents bonus de degats
				amount *= 1.5;
			}
			if (block > 0) {
				if (block >= amount) {
					block -= amount;
					DataBattleRoom.println(name + " a perdu " + amount + " block.");
					return 0;
				} else {
					DataBattleRoom.println(name + " a perdu " + block + " block.");
					amount -= block;
					block = 0;
				}
			}
	
			life -= amount;
	
			if (life <= 0) {
				amount += life;
				life = 0;
			}
		}
		if (amount > 0 && platedArmor > 0) addPlatedArmor(-1);
		return amount;
	}
	
	@Override
	public int heal(int amount) {
		if (amount > 0) DataBattleRoom.println(name + " a récupéré "+amount+ " pv.");
		if (amount < 0) DataBattleRoom.println(name + " a perdu "+(-amount)+ " pv.");
		life += amount;
		if (life > maxLife) {
			amount -= life - maxLife;
			life = maxLife;
		}
		if (life < 0) {
			amount += life;
			life = 0;
		}
		return amount;
	}

	@Override
	public int attack(Action action) {
		return attack(action, action.getDamage());
	}

	public int attack(Action action, float damage) {
		if (damage != 0) {
			damage += strength * action.getStrengthAffect();
			return attack(damage);
		}
		return 0;
	}
	
	@Override
	public int attack(float damage) {
		damage *= weak > 0 ? 0.75 : 1;
		int finalDamage = (int)(damage+0.5f); // Valeur arrondie
		return finalDamage>0 ? finalDamage : 0;
	}
	
	@Override
	public void attackSideEffect(int damage, Entity enemy) {
		if (enemy.getThorns() > 0) {
			int d = damage(enemy.getThorns());
			DataBattleRoom.println("L'effet thorns inflige " + d + " dégâts à " + name());
		}
	}

	@Override
	public int addBlock(int amount) {
		if (amount > 0) {
			int added = (int)((float)amount * (frail>0?0.75f:1.0f) + 0.5f) + dexterity;
			DataBattleRoom.println(name+" a gagné "+added+" block.");
			block += added;
			return added;
		}
		return 0;
	}
	
	@Override
	public void addMaxLife(int amount) {
		if (amount != 0) {
			maxLife += amount;
			if (life > maxLife) life = maxLife;
			if (maxLife > 0) DataBattleRoom.println(name+" a gagné "+amount+" pv max.");
			if (maxLife < 0) DataBattleRoom.println(name+" a perdu "+amount+" pv max.");
		}
	}
	
	@Override
	public void removeBlock() {
		block = 0;
	}
	
	@Override
	public void multiplyBlock(int coef) {
		if (coef != 1) {
			int added = block*(coef-1);
			DataBattleRoom.println(name+" a gagné "+added+" block.");
			block *= coef;
		}
	}

	@Override
	public void addVulnerable(int amount) {
		vulnerable += amount;
	}

	@Override
	public void addWeak(int amount) {
		weak += amount;
	}

	@Override
	public void addStrength(int amount) {
		strength += amount;
		if (amount > 0) DataBattleRoom.println(name+" a gagné "+amount+" strength.");
		if (amount < 0) DataBattleRoom.println(name+" a perdu "+(-amount)+" strength.");
	}

	@Override
	public void addStrengthDown(int amount) {
		strengthDown += amount;
		if (amount > 0) DataBattleRoom.println(name+" a gagné "+amount+" strength down.");
		if (amount < 0) DataBattleRoom.println(name+" a gagné "+(-amount)+" strength up.");
	}
	
	@Override
	public void addRitual(int amount) {
		if (amount != 0) {
			ritual += amount;
			DataBattleRoom.println(name+" a gagné "+amount+" ritual.");
		}
	}
	
	@Override
	public void addFrail(int amount) {
		frail += amount;
	}
	
	@Override
	public void addArtifact(int amount) {
		if (amount != 0) {
			artifact += amount;
			DataBattleRoom.println(name+" a gagné "+amount+" ritual.");
		}
	}
	
	@Override
	public void addRegeneration(int amount) {
		if (amount != 0) {
			regeneration += amount;
			DataBattleRoom.println(name+" a gagné "+amount+" regeneration.");
		}
	}
	
	@Override
	public void addRegenerate(int amount) {
		if (amount > 0)	DataBattleRoom.println(name+" a gagné "+amount+" regenerate.");
		if (amount < 0) DataBattleRoom.println(name+" a perdu "+amount+" regenerate.");
		regenerate += amount;
	}

	@Override
	public void addMetallicize(int amount) {
		if (amount > 0) {
			metallicize += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" metallicize.");
		}
	}

	@Override
	public void addPlatedArmor(int amount) {
		if (amount != 0) {
			platedArmor += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" plated armor.");
		}
	}

	@Override
	public void addDexterity(int amount) {
		if (amount != 0) {
			dexterity += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" dexterity.");
		}
	}

	@Override
	public void addDexterityDown(int amount) {
		dexterityDown += amount;
		if (amount > 0) DataBattleRoom.println(name+" a gagné "+amount+" dexterity down.");
		if (amount < 0) DataBattleRoom.println(name+" a gagné "+amount+" dexterity up.");
	}

	@Override
	public void addIntangible(int amount) {
		if (amount != 0) {
			intangible += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" intangible.");
		}
	}

	@Override
	public void addThorns(int amount) {
		if (amount != 0) {
			thorns += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" thorns.");
		}
	}

	@Override
	public void addPoison(int amount) {
		if (amount != 0) {
			poison += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"subi":"perdu")+" "+amount+" poison.");
		}
	}

	@Override
	public boolean hasArtifact() {
		return artifact > 0;
	}
	
	@Override
	public boolean useArtifact() {
		if (artifact > 0) {
			artifact--;
			DataBattleRoom.println(name + " a utilisé un artifact.");
			return true;
		}
		return false;
	}

	@Override
	public void newTurn() {
		vulnerable -= vulnerable > 0 ? 1 : 0;
		strength += ritual;
		intangible -= intangible > 0 ? 1 : 0;
		if (poison > 0) DataBattleRoom.println(name()+" a subi "+damage(poison--)+" dégâts du poison.");
	}
	
	@Override
	public void endTurn() {
		weak -= weak > 0 ? 1 : 0;
		strength -= strengthDown;
		strengthDown = 0;
		frail -= frail > 0 ? 1 : 0;
		dexterity -= dexterityDown;
		dexterityDown = 0;
		if (regeneration > 0) heal(regeneration--);
		if (regenerate != 0) heal(regenerate);
		if (metallicize > 0) addBlock(metallicize);
		if (platedArmor > 0) addBlock(platedArmor);
	}
	
	@Override
	public BufferedImage img() {
		if (img == null) {
			img = FileManager.loadImage("pictures/entities/" + imgPath);
		}
		return img;
	}
	
	@Override
	public ArrayList<String> getInformations() {
		ArrayList<String> infos = new ArrayList<>();
		infos.add("life="+life+"/"+maxLife);
		if (block!=0) infos.add("block="+block);
		if (vulnerable!=0) infos.add("vulnerable="+vulnerable);
		if (weak!=0) infos.add("weak="+weak);
		if (strength!=0) infos.add("strength="+strength);
		if (strengthDown>0) infos.add("strength down="+strengthDown);
		if (strengthDown<0) infos.add("strength up="+(-strengthDown));
		if (ritual!=0) infos.add("ritual="+ritual);
		if (frail!=0) infos.add("frail="+frail);
		if (artifact!=0) infos.add("artifact="+artifact);
		if (regeneration!=0) infos.add("regeneration="+regeneration);
		if (regenerate!=0) infos.add("regenerate="+regenerate);
		if (metallicize != 0) infos.add("metallicize="+metallicize);
		if (platedArmor != 0) infos.add("plated armor="+platedArmor);
		if (dexterity != 0) infos.add("dexterity="+dexterity);
		if (dexterityDown>0) infos.add("dexterity down="+dexterityDown);
		if (dexterityDown<0) infos.add("strength up="+(-dexterityDown));
		if (intangible != 0) infos.add("intangible="+intangible);
		if (thorns != 0) infos.add("thorns="+thorns);
		if (poison != 0) infos.add("poison="+poison);
		return infos;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append(" : ");
		ArrayList<String> infos = getInformations();
		for (int i=0; i<infos.size(); i++) {
			builder.append(infos.get(i));
			if (i!=infos.size()-1) builder.append(", ");
		}
		return builder.toString();
	}
	

	@Override
	public int getLife() { return life; }
	@Override
	public int getMaxLife() { return maxLife; }
	@Override
	public int getBlock() { return block; }
	@Override
	public int getStrength() { return strength; }
	@Override
	public int getVulnerable() { return vulnerable; }
	@Override
	public int getWeak() { return weak; }
	@Override
	public int getDexterity() { return dexterity; }
	@Override
	public int getThorns() { return thorns; }
	@Override
	public int getPoison() { return poison; }
}

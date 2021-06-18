package fr.silex.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import fr.silex.Deck;
import fr.silex.Deck.DeckPile;
import fr.silex.action.Action;
import fr.silex.action.Card.ClassCard;
import fr.silex.action.HeroAction;
import fr.silex.action.Potion;
import fr.silex.data.DataBattleRoom;
import fr.silex.data.Datas;

/**
 * Represent the character of the player.
 */
public class Hero extends AbstractEntity {
	public enum HeroClass {
		IRONCLAD, SILENT
	}
	
	private static final Random random = new Random();
	
	public final HeroClass heroClass;
	private int gold;
	private int maxEnergy;
	private int energy; // Nombre de points d'actions par tour
	private Deck deck = new Deck(this);
	private String description;
	private Potion[] potions = new Potion[3];
	private ArrayList<Enemy> enemies = new ArrayList<>();

	private int noDraw = 0;
	private boolean entangled = false;
	private int combust = 0; // dégâts infligés à tous les ennemies a la fin du tour
	private int darkEmbrace = 0;
	private int noBlock = 0; // interdit le gain de block par les cartes.
	private int evolve = 0; // Pioche une carte en piochant un Status.
	private int feelNoPain = 0; // +X block quand une carte est exhaust.
	private int fireBreathing = 0; // X damages à tous les ennemies en piochant un Status/Curse.
	private int rupture = 0; //+X strength a chaque des dégâts provenant des cartes.
	private boolean barricade = false; // block reste à la fin du tour
	private int berserk = 0; // +X energy aux début des tours
	private int brutality = 0; // -Xpv, +Xdraw au début du tour
	private boolean corruption = false; // Les Skills coutent 0 mais sont exhaust.
	private int demonForm = 0; // +X strength au début de chaque tour.
	private int juggernaut = 0; // X damage à un ennemies au pif à chaque gain de block
	private int flameBarrier = 0; // X dégâts à l'attaquant ce tour.
	private int rage = 0; // +X block en jouant une Attack ce tour
	private int doubleTap = 0; // nombre de prochaine attaque qui va être jouer en double.
	private int magnetism = 0; // Ajoute un carte colorless au début de chaque tour.
	private int mayhem = 0; // Joue la carte du dessus de la pioche a chaque tour.
	private int panache = 0; // X damage aux ennemies toutes les 5 cartes jouées.
	private int[] bomb = new int[3]; // Explose après 3 tours, infligeant des dégâts à tous les ennmies.
	private int accuracy = 0; // +4 degats des shiv.
	private int keepBlock = 0; // garde le block ce tour.
	private int infiniteBlades = 0; // +1 shiv a la fin du tour.
	private int noxiousFumes = 0; // +X poison aux ennemies au debut du tour.
	private int aThousandCuts = 0; // X degats aux ennemies a chaque jouée.
	private int afterImage = 0; // +X block a chaque carte jouée.
	private int burst = 0; // Le prochain Skill sera jouée X fois en plus.
	private int envenom = 0; // +X poison a l'ennemies a chaque degats.
	private int phantasmalKiller = 0;
	private int multiplyDamage = 1;
	
	private int blockNextTurn = 0;
	private int drawNextTurn = 0;
	private int energyGainNextTurn = 0;

	private int damagedCount; // Nombre de fois que l'entité a été blessé

	public Hero(String name, String imgPath, int maxLife, int gold, int maxEnergy) {
		this(name, imgPath, maxLife, gold, maxEnergy, HeroClass.IRONCLAD, "");
	}
	
	/**
	 * Constructeur complet du Hero.
	 */
	public Hero(String name, String imgPath, int maxLife, int gold, int maxEnergy, HeroClass heroClass, String description) {
		super(name, imgPath, maxLife);
		if (gold < 0) {
			throw new IllegalArgumentException("La valeur pour 'gold' entrée doit être supérieur ou égal à 0.");
		}
		if (maxEnergy <= 0) {
			throw new IllegalArgumentException("La valeur pour 'actPoints' entrée doit être supérieur ou égal à 1.");
		}

		this.heroClass = heroClass;
		this.gold = gold;
		this.maxEnergy = maxEnergy;
		energy = maxEnergy;
		this.description = Objects.requireNonNull(description);
	}
	
	public Deck deck() { return deck; }
	
	/**
	 * Prepare le Hero avant de commencé un combat.
	 */
	public void prepareBattle(ArrayList<Enemy> enemies) {
		resetStatus();
		damagedCount = 0;
		deck.prepareDeck();
		this.enemies = enemies;
	}

	/** Remet tous les effets à 0. */
	@Override
	public void resetStatus() {
		super.resetStatus();
		noDraw = 0;
		entangled = false;
		combust = 0;
		darkEmbrace = 0;
		noBlock = 0;
		evolve = 0;
		feelNoPain = 0;
		fireBreathing = 0;
		rupture = 0;
		barricade = false;
		berserk = 0;
		brutality = 0;
		corruption = false;
		demonForm = 0;
		juggernaut = 0;
		flameBarrier = 0;
		rage = 0;
		doubleTap = 0;
		magnetism = 0;
		mayhem = 0;
		panache = 0;
		Arrays.fill(bomb, 0);
		accuracy = 0;
		keepBlock = 0;
		infiniteBlades = 0;
		noxiousFumes = 0;
		aThousandCuts = 0;
		afterImage = 0;
		burst = 0;
		envenom = 0;
		phantasmalKiller = 0;
		multiplyDamage = 1;
		blockNextTurn = 0;
		drawNextTurn = 0;
		energyGainNextTurn = 0;
	}
	
	public void useEnergy(int usedEnergy) {
		energy -= usedEnergy;
		if (usedEnergy < 0) DataBattleRoom.println(name() + " a gagné " + (-usedEnergy) + " energy.");
		if (energy < 0) {
			throw new IllegalStateException("Something has used more player's energy that the amount available.");
		}
	}

	@Override
	public int addBlock(int amount) {
		if (noBlock > 0 && amount != 0) {
			DataBattleRoom.println("Vous ne pouvez pas gagner de block.");
			return 0;
		} else {
			int added = super.addBlock(amount);
			if (added > 0) {
				int damage = attack(juggernaut);
				Enemy target = enemies.get(random.nextInt(enemies.size()));
				damage = target.damage(damage);
				if (damage != 0) DataBattleRoom.println(name() + " a infligé " + damage + " a " + target.name());
			}
			return added;
		}
	}

	public void addNoDraw(int amount) {
		if (amount > 0) {
			noDraw = noDraw < amount ? amount : noDraw;
			DataBattleRoom.println("Vous ne pouvez plus piocher pendant "+noDraw+" tours.");
		}
	}
	
	public void addCombust(int amount) { 
		if (amount > 0) {
			combust += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" combust.");
		}
	}
	
	public void addDarkEmbrace(int amount) {
		if (amount > 0) {
			darkEmbrace += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" dark embrace.");
		}
	}
	
	public void addNoBlock(int amount) {
		if (amount > 0) {
			noBlock += amount;
			DataBattleRoom.println("Vous ne pouvez plus bloquer pendant "+noBlock+" tours.");
		}
	}

	public void setEntangled(boolean value) { 
		if (value && !entangled) {
			entangled = value; 
		}
	}
	
	public void addEvolve(int amount) {
		if (amount > 0) {
			evolve += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" evolve.");
		}
	}

	public void addFeelNoPain(int amount) {
		if (amount > 0) {
			feelNoPain += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" feel no pain.");
		}
	}

	public void addFireBreathing(int amount) {
		if (amount > 0) {
			fireBreathing += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" fire breathing.");
		}
	}

	public void addRupture(int amount) {
		if (amount > 0) {
			rupture += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" rupture.");
		}
	}

	public void setBarricade(boolean value) {
		if (value && !barricade) {
			barricade = value;
			DataBattleRoom.println(name()+" a maintenant l'effet barricade.");
		}
	}

	public void addBerserk(int amount) {
		if (amount > 0) {
			berserk += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" berserk.");
		}
	}

	public void addBrutality(int amount) {
		if (amount > 0) {
			brutality += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" brutality.");
		}
	}

	public void setCorruption(boolean value) {
		if (value && !corruption) {
			corruption = value;
			DataBattleRoom.println(name()+" a maintenant l'effet corruption.");
		}
	}

	public void addDemonForm(int amount) {
		if (amount > 0) {
			demonForm += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" demon Form.");
		}
	}

	public void addJuggernaut(int amount) {
		if (amount > 0) {
			juggernaut += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" juggernaut.");
		}
	}

	public void addFlameBarrier(int amount) {
		if (amount > 0) {
			flameBarrier += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" flame barrier.");
		}
	}

	public void addRage(int amount) {
		if (amount > 0) {
			rage += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" rage.");
		}
	}
	
	public void addDoubleTap(int amount) {
		if (amount > 0) {
			doubleTap += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" doubleTap.");
		}
	}

	public void addMagnetism(int amount) {
		if (amount > 0) {
			magnetism += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" magnetism.");
		}
	}

	public void addMayhem(int amount) {
		if (amount > 0) {
			mayhem += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" mayhem.");
		}
	}

	public void addPanache(int amount) {
		if (amount > 0) {
			panache += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" panache.");
		}
	}

	public void addBomb(int amount) {
		if (amount > 0) {
			bomb[2] += amount;
			DataBattleRoom.println(name()+" a gagné "+amount+" bomb.");
		}
	}
	
	public void addAccuracy(int amount) {
		if (amount != 0) {
			accuracy += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" accuracy.");
		}
	}

	public void setKeepBlock(int amount) {
		if (amount != 0 && keepBlock <= amount) {
			keepBlock = amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" keep block.");
		}
	}

	public void addInfiniteBlades(int amount) {
		if (amount != 0) {
			infiniteBlades += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" infinite blade.");
		}
	}

	public void addNoxiousFumes(int amount) {
		if (amount != 0) {
			noxiousFumes += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" noxious fumes.");
		}
	}

	public void addAThousandCuts(int amount) {
		if (amount != 0) {
			aThousandCuts += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" a thousand cuts.");
		}
	}

	public void addAfterImage(int amount) {
		if (amount != 0) {
			afterImage += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" after image.");
		}
	}

	public void addBurst(int amount) {
		if (amount != 0) {
			burst += amount;
			DataBattleRoom.println("Le prochain Skill sera jouée "+(burst+1)+" fois.");
		}
	}

	public void addEnvenom(int amount) {
		if (amount != 0) {
			envenom += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" envenom.");
		}
	}

	public void addPhantasmalKiller(int amount) {
		if (amount != 0) {
			phantasmalKiller += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" phantasmal killer.");
		}
	}

	public void addMultiplyDamage(int amount) {
		if (amount != 0) {
			multiplyDamage += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" multiply damage.");
		}
	}

	public void addDrawNextTurn(int amount) {
		if (amount != 0) {
			drawNextTurn += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" draw next turn.");
		}
	}

	public void addEnergyGainNextTurn(int amount) {
		if (amount != 0) {
			energyGainNextTurn += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" energy gain next turn.");
		}
	}

	public void addBlockNextTurn(int amount) {
		if (amount != 0) {
			blockNextTurn += amount;
			DataBattleRoom.println(name()+" a "+(amount>0?"gagné":"perdu")+" "+amount+" block next turn.");
		}
	}

	/**
	 * Ajoute une potion utilisable plus tard en combat.
	 * @param potion : La potion a rajouter.
	 * @return True si la potion a bien été ajoutée, et donc si le Hero a assez de place sur lui.
	 */
	public boolean addPotion(Potion potion) {
		for (int i=0; i<3; i++) {
			if (potions[i] == null) {
				potions[i] = potion;
				DataBattleRoom.println("Vous avez récupéré "+potion.name()+".");
				return true;
			}
		}
		return false;
	}
	
	/** Ajoute la quantité d'or indiqué ajoute joueur. */
	public void addGold(int amount) {
		if (amount < 0) throw new IllegalArgumentException("La valeur pour addGold ("+amount+") n'est pas positif");
		if (amount > 0) {
			DataBattleRoom.println("Vous avez récupéré "+amount+" or.");
			gold+=amount;
		}
	}

	public boolean useDoubleTap() { 
		if (doubleTap > 0) {
			doubleTap--;
			return true;
		}
		return false;
	}

	public boolean useBurst() { 
		if (burst > 0) {
			burst--;
			return true;
		}
		return false;
	}
	
	/** Applique les effets de la potion indiqué et la consomme. */
	public boolean usePotion(int numPotion, int targetIndex) {
		if (potions[numPotion] != null) {
			Potion potion = potions[numPotion];
			potions[numPotion] = null;
			potion.applyEffects(this, enemies, targetIndex);
			return true;
		}
		return false;
	}
	
	public void spendGold(int amount) {
		gold -= amount;
		
		if (gold < 0) {
			throw new IllegalStateException("L'argent depensé est supérieur à l'argent disponible.");
		}
	}
	
	@Override
	public void newTurn() {
		if (!(barricade || keepBlock > 0)) removeBlock();
		keepBlock-= keepBlock > 0 ? 1 : 0;
		super.newTurn();
		addBlock(blockNextTurn);
		blockNextTurn = 0;
		
		energy = maxEnergy + berserk + energyGainNextTurn;
		energyGainNextTurn = 0;
		
		deck.addCardToPile(Datas.getCard("Shiv"), DeckPile.HAND, true, infiniteBlades);
		deck.draw(5 + brutality + drawNextTurn, enemies);
		drawNextTurn = 0;
		heal(-brutality);
		
		multiplyDamage = phantasmalKiller+1;
		phantasmalKiller = 0;
		
		addStrength(demonForm);
		flameBarrier = 0;
		deck.newTurn();
		for (int i=0; i<mayhem; i++) {
			deck.playTopDrawPileCard(enemies, random.nextInt(enemies.size()), false);
		}
		for (int i=0; i<magnetism; i++) {
			deck.addCardToPile(Datas.getRandomCard(ClassCard.COLORLESS), DeckPile.HAND, true);
		}
		if (noxiousFumes > 0) {
			for (Enemy enemy : enemies) {
				enemy.addPoison(noxiousFumes);
			}
		}
	}
	
	public void endTurn() {
		super.endTurn();
		multiplyDamage = 1;
		noDraw -= noDraw > 0 ? 1 : 0;
		entangled = false;
		noBlock -= noBlock > 0 ? 1 : 0;
		rage = 0;
		doubleTap = 0;
		burst = 0;
		
		for (Enemy enemy : enemies) {
			int d = enemy.damage(combust);
			if (d != 0) DataBattleRoom.println("L'effet combust inflige "+d+" dégâts à "+enemy.name()+".");
		}
		
		if (bomb[0] != 0) {
			for (Enemy enemy : enemies) {
				int d = enemy.damage(bomb[0]);
				if (d != 0) DataBattleRoom.println("La bomb inflige "+d+" dégâts à "+enemy.name()+".");
			}
		}
		for (int i=1; i<bomb.length; i++) {
			bomb[i-1] = bomb[i];
			bomb[i] = 0;
		}
		
		deck.endTurn();
	}
	
	@Override
	public int heal(int amount) {
		amount = super.heal(amount);
		if (amount < 0) {
			addStrength(rupture);
		}
		return amount;
	}
	
	@Override
	public int attack(Action action, float damage) {
		HeroAction heroAction = (HeroAction)action; 
		damage += deck.getStrikeCount()*heroAction.getBonusDamageFromStrike();
		if (action.name().equals("Shiv")) damage += accuracy;
		return super.attack(action, damage) * multiplyDamage;
	}
	
	@Override
	public void attackSideEffect(int damage, Entity enemy) {
		super.attackSideEffect(damage, enemy);
		if (damage > 0) {
			enemy.addPoison(envenom);
		}
	}
	
	@Override
	public int damage(int amount) {
		int finalDamage = super.damage(amount);
		if (finalDamage > 0) damagedCount++;
		
		if (getLife() <= 0) {
			for (int i=0; i<potions.length; i++) {
				if (potions[i] != null && potions[i].getResurrect() > 0f) {
					DataBattleRoom.println("Vous êtes mort,\nmais "+potions[i].name()+" vous a ressuscité !");
					heal((int)(getMaxLife() * potions[i].getResurrect()));
					potions[i] = null;
					break;
				}
			}
		}
		
		return finalDamage;
	}
	
	@Override
	public ArrayList<String> getInformations() {
		ArrayList<String> infos = new ArrayList<>();
		infos.add("energy="+energy+"/"+maxEnergy);
		infos.add("gold="+gold);
		infos.addAll(super.getInformations());
		if (noDraw > 0) infos.add("no draw="+noDraw);
		if (entangled) infos.add("entangled");
		if (combust != 0) infos.add("combust="+combust);
		if (darkEmbrace != 0) infos.add("dark embrace="+darkEmbrace);
		if (noBlock != 0) infos.add("no block="+noBlock);
		if (evolve != 0) infos.add("evolve="+evolve);
		if (feelNoPain != 0) infos.add("feel no pain="+feelNoPain);
		if (fireBreathing != 0) infos.add("fire breathing="+fireBreathing);
		if (rupture != 0) infos.add("rupture="+rupture);
		if (barricade) infos.add("barricade");
		if (berserk != 0) infos.add("berserk="+berserk);
		if (brutality != 0) infos.add("brutality="+brutality);
		if (corruption) infos.add("corruption");
		if (demonForm != 0) infos.add("demon form="+demonForm);
		if (juggernaut != 0) infos.add("juggernaut="+juggernaut);
		if (flameBarrier != 0) infos.add("flame barrier="+flameBarrier);
		if (rage != 0) infos.add("rage="+rage);
		if (doubleTap != 0) infos.add("double tap="+doubleTap);
		if (magnetism != 0) infos.add("magnetism="+magnetism);
		if (mayhem != 0) infos.add("mayhem="+mayhem);
		if (panache != 0) infos.add("panache ("+(5-deck.getCardsPlayedCount())+")="+panache);
		for (int i=0; i<bomb.length; i++) {
			if (bomb[i] != 0) infos.add("bomb (" +(i+1)+" tour)="+bomb[i]);
		}
		if (accuracy != 0) infos.add("accuracy="+accuracy);
		if (keepBlock != 0) infos.add("keepBlock="+keepBlock);
		if (infiniteBlades != 0) infos.add("infinite blades="+infiniteBlades);
		if (noxiousFumes != 0) infos.add("noxious fumes="+noxiousFumes);
		if (aThousandCuts != 0) infos.add("a thousand cuts="+aThousandCuts);
		if (afterImage != 0) infos.add("after image="+afterImage);
		if (burst != 0) infos.add("burst="+burst);
		if (envenom != 0) infos.add("envenom="+envenom);
		if (multiplyDamage != 1) infos.add("multiply damage="+multiplyDamage);
		if (phantasmalKiller != 0) infos.add("phantasmal killer="+phantasmalKiller);
		if (blockNextTurn != 0) infos.add("block next turn="+blockNextTurn);
		if (drawNextTurn != 0) infos.add("draw next turn="+drawNextTurn);
		if (energyGainNextTurn != 0) infos.add("energy gain next turn="+energyGainNextTurn);
		return infos;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Hero ").append(name()).append(" : ");
		ArrayList<String> infos = getInformations();
		for (int i=0; i<infos.size(); i++) {
			builder.append(infos.get(i));
			if (i!=infos.size()-1) builder.append(", ");
		}
		return builder.toString();
	}

	// getters
	public boolean isEntangled() { return entangled; }
	public boolean hasNoDraw() { return noDraw > 0; }
	public boolean hasCorruption() { return corruption; }
	public int getEnergy() { return energy; }
	public int getMaxEnergy() { return maxEnergy; }
	public int getGold() { return gold; }
	public String getDescription() { return description; }
	public Potion[] getPotions() { return Arrays.copyOf(potions, potions.length); }
	public HeroClass getHeroClass() { return heroClass; }
	public int getDamagedCount() { return damagedCount; }
	public int getDarkEmbrace() { return darkEmbrace; }
	public int getEvolve() { return evolve; }
	public int getFeelNoPain() { return feelNoPain; }
	public int getFireBreathing() { return fireBreathing; }
	public int getFlameBarrier() { return flameBarrier; }
	public int getRage() { return rage; }
	public ArrayList<Enemy> getEnemies() { return enemies; }
	public int getPanache() { return panache; }
	public int getEnvenom() { return envenom; }
	public int getAThousandCuts() { return aThousandCuts; }
	public int getAfterImage() { return afterImage; }
	
}

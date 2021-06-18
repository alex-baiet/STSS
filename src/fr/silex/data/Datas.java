package fr.silex.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

import fr.silex.FileManager;
import fr.silex.RunManager;
import fr.silex.action.Card;
import fr.silex.action.Card.Category;
import fr.silex.action.Card.ClassCard;
import fr.silex.action.EnemyAction;
import fr.silex.action.HeroAction.Rarity;
import fr.silex.action.Potion;
import fr.silex.entity.Enemy;
import fr.silex.entity.EnemyData;
import fr.silex.entity.Hero;
import fr.silex.strategy.ElementPattern;
import fr.silex.strategy.PatternStrategy;
import fr.silex.strategy.SimpleStrategy;
import fr.silex.strategy.SupportStrategy;

/**
 * Contient toutes les données des entités/actions.
 */
public class Datas {
	public enum EnemyCategory {
		NORMAL, ELITE, BOSS
	}
	
	private static final String SEPARATOR = "#";
	private static final Random random = new Random();

	private static boolean loadedCards = false;
	private static ClassCard loadedCardsClass = null;
	private static final TreeMap<String, Card> cards = new TreeMap<>(); // Toutes les cartes triés par nom.
	private static final HashMap<ClassCard, ArrayList<Card>> cardsByClass = new HashMap<>();  // Les cartes triés par classes, puis par nom.

	private static final HashMap<ClassCard, HashMap<Category, ArrayList<Card>>> cardsClassCateg = new HashMap<>();

	private static final HashMap<ClassCard, HashMap<Card.Rarity, ArrayList<Card>>> cardsClassRarity = new HashMap<>();
	
	private static final ArrayList<String> allEnemiesName = new ArrayList<>();
	private static final HashMap<String, EnemyData> allEnemies = new HashMap<>(); // Tous les ennemies triés par noms.

	private static boolean loadedEnemies = false;
	private static final ArrayList<String> enemiesName = new ArrayList<>();
	private static final HashMap<String, EnemyData> enemies = new HashMap<>(); // Les ennemies communs triés par noms.

	private static boolean loadedElites = false;
	private static final ArrayList<String> elitesName = new ArrayList<>();
	private static final HashMap<String, EnemyData> elites = new HashMap<>(); // Les élites communs triés par noms.

	private static boolean loadedBoss = false;
	private static final ArrayList<String> bossName = new ArrayList<>();
	private static final HashMap<String, EnemyData> boss = new HashMap<>(); // Les boss communs triés par noms.

	private static boolean loadedHeroes = false;
	private static final HashMap<String, Hero> heroes = new HashMap<>(); // Les héros triés par noms.

	private static boolean loadedPotions = false;
	private static final TreeMap<String, Potion> potions = new TreeMap<>(); // Les potions triés par noms.
	private static final ArrayList<String> potionsName = new ArrayList<>();
	
	private static final HashMap<Potion.Rarity, ArrayList<Potion>> potionsRarity = new HashMap<>(); // les potions triés par rareté.

	private Datas() { } // Constructeur rendu privée
	
	//------------------------------------------------------
	//              RECUPERATION DES DONNEES
	
	/**
	 * Renvoie la Card stockée du nom indiqué.
	 */
	public static Card getCard(String name) {
		if (!loadedCards) loadAllCards();
		
		if (!cards.containsKey(name)) {
			throw new IndexOutOfBoundsException("La carte " + name + " n'existe pas.");
		}

		return cards.get(name).copy();
	}

	/**
	 * Renvoie toutes les cartes disponibles.
	 * A N'UTILISER QUE POUR VOIR LA LISTE DES CARTES
	 */
	public static ArrayList<Card> getAllCards() {
		if (!loadedCards) loadAllCards();
		
		ArrayList<Card> cardsCopy = new ArrayList<>(cards.values());
		
		return cardsCopy;
	}
	
	/** Renvoie une card au hasard vérifiant les paramètres. */
	public static Card getRandomCard(ClassCard classCard, Category category) {
		return getRandomCard(cardsClassCateg.get(classCard).get(category));
	}

	/** Renvoie une card au hasard vérifiant les paramètres. */
	public static Card getRandomCard(ClassCard classCard, Card.Rarity rarity) {
		return getRandomCard(cardsClassRarity.get(classCard).get(rarity));
	}

	/** Renvoie une card au hasard vérifiant les paramètres. */
	public static Card getRandomCard(ClassCard classCard) {
		return getRandomCard(cardsByClass.get(classCard));
	}
	
	private static Card getRandomCard(ArrayList<Card> result) {
		if (!loadedCards) loadAllCards();
		if (RunManager.getHero() != null && loadedCardsClass != ClassCard.valueOf(RunManager.getHero().heroClass.toString())) loadAllPlayerCard();
		
		if (result.size() == 0) {
			throw new IndexOutOfBoundsException("Aucune carte correspond au option de recherche n'existe.");
		}

		return result.get(random.nextInt(result.size())).copy();
	}
	
	/**
	 * Renvoie l'Enemy du nom indiqué.
	 */
	public static Enemy getEnemy(String name) {
		if (!loadedEnemies) loadAllEnemies();
		if (!loadedElites) loadAllElites();
		if (!loadedBoss) loadAllBoss();
		
		if (!allEnemies.containsKey(name)) {
			throw new IndexOutOfBoundsException("The EnemyData " + name + " does not exist.");
		}
		return allEnemies.get(name).createEnemy();
	}
	
	public static ArrayList<Enemy> getAllEnemies() {
		if (!loadedEnemies) loadAllEnemies();
		if (!loadedElites) loadAllElites();
		if (!loadedBoss) loadAllBoss();
		
		ArrayList<Enemy> copy = new ArrayList<>();
		for (EnemyData enemyData : allEnemies.values()) {
			copy.add(enemyData.createEnemy());
		}
		
		return copy;
	}

	/**
	 * Renvoie un Enemy au hasard stocké.
	 */
	public static ArrayList<Enemy> getRandomEnemies(int nbr) {
		if (!loadedEnemies) loadAllEnemies();
		if (!loadedElites) loadAllElites();
		if (!loadedBoss) loadAllBoss();
		
		ArrayList<Enemy> result = new ArrayList<>();
		
		for (int i=0; i < nbr; i++) {
			int r = random.nextInt(allEnemies.size());
			result.add(enemies.get(allEnemiesName.get(r)).createEnemy());
		}
		return result;
	}
	
	/**
	 * Renvoie un Enemy au hasard de la categorie indiquée.
	 */
	public static ArrayList<Enemy> getRandomEnemies(int nbr, EnemyCategory cat) {
		if (!loadedEnemies) loadAllEnemies();
		if (!loadedElites) loadAllElites();
		if (!loadedBoss) loadAllBoss();
		
		HashMap<String, EnemyData> target;
		ArrayList<String> targetName;
		switch (cat) {
		case NORMAL:
			if (!loadedEnemies) loadAllEnemies();
			target = enemies;
			targetName = enemiesName;
			break;
		case ELITE:
			if (!loadedElites) loadAllElites();
			target = elites;
			targetName = elitesName;
			break;
		case BOSS:
			if (!loadedBoss) loadAllBoss();
			target = boss;
			targetName = bossName;
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + cat);
		}

		ArrayList<Enemy> result = new ArrayList<>();
		
		for (int i=0; i < nbr; i++) {
			int r = random.nextInt(target.size());
			result.add(target.get(targetName.get(r)).createEnemy());
		}
		return result;
		
	}

	/**
	 * Renvoie le Hero avec le nom indiqué.
	 */
	public static Hero getHero(String name) {
		if (!loadedHeroes) {
			loadAllHeroes();
		}
		if (!heroes.containsKey(name)) {
			throw new IndexOutOfBoundsException("The Hero " + name + " does not exist.");
		}
		return heroes.get(name);
	}

	/**
	 * Renvoie la potion du nom indiqué.
	 */
	public static Potion getPotion(String name) {
		if (!loadedPotions) loadAllPotions(); // Charge les potions si ce n'est pas déjà fait.
		
		if (!potions.containsKey(name)) {
			throw new IndexOutOfBoundsException("The Hero " + name + " does not exist.");
		}
		return potions.get(name);
	}

	/** Renvoie toutes les potions chargées. */
	public static ArrayList<Potion> getAllPotions() {
		if (!loadedPotions) loadAllPotions();
		
		ArrayList<Potion> potionsCopy = new ArrayList<>(potions.values());
		return potionsCopy;
	}
	
	/**
	 * Renvoie une potion stockée au hasard.
	 */
	public static Potion getRandomPotion() {
		if (!loadedPotions) loadAllPotions(); // Charge les potions si ce n'est pas déjà fait.

		return potions.get(potionsName.get(random.nextInt(potionsName.size())));
	}
	
	/**
	 * Renvoie une potion au hasard de la rareté indiqué.
	 */
	public static Potion getRandomPotion(Potion.Rarity rarity) {
		if (!loadedPotions) loadAllPotions(); // Charge les potions si ce n'est pas déjà fait.
		
		ArrayList<Potion> target = potionsRarity.get(rarity);
		return target.get(random.nextInt(target.size()));
	}

	//------------------------------------------------------
	//             AJOUT DES DONNEES PAR UNITE
	
	/**
	 * Ajoute la Card dans Datas.
	 */
	public static void addCard(Card card) {
		cards.put(card.name(), card);
		
		if (!cardsByClass.containsKey(card.classCard())) {
			cardsByClass.put(card.classCard(), new ArrayList<>());
		}
		cardsByClass.get(card.classCard()).add(card);

		if (card.rarity() != Rarity.STARTER && card.rarity() != Rarity.NULL) {
			cardsClassCateg.get(card.classCard()).get(card.category()).add(card);
		}
		
		cardsClassRarity.get(card.classCard()).get(card.rarity()).add(card);
	}

	/**
	 * Socke les données d'un Enemy.
	 * @param enemy
	 */
	public static void addEnemy(EnemyData enemy) {
		addEnemy(enemy, EnemyCategory.NORMAL);
	}
	
	public static void addEnemy(EnemyData enemy, EnemyCategory cat) {
		switch (cat) {
		case NORMAL:
			enemies.put(enemy.name(), enemy);
			enemiesName.add(enemy.name());
			break;
		case ELITE:
			elites.put(enemy.name(), enemy);
			elitesName.add(enemy.name());
			break;
		case BOSS:
			boss.put(enemy.name(), enemy);
			bossName.add(enemy.name());
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + cat);
		}
		allEnemies.put(enemy.name(), enemy);
		allEnemiesName.add(enemy.name());
	}

	/**
	 * Ajoute une potion dans Datas.
	 */
	public static void addPotion(Potion potion) {
		potions.put(potion.name(), potion);
		potionsName.add(potion.name());
		potionsRarity.get(potion.rarity()).add(potion);
	}

	//------------------------------------------------------
	//               CHARGEMENT DES DONNEES
	
	/**
	 * Charge toutes les cartes.
	 */
	public static void loadAllCards() {
		// Initialisation des dictionnaires de cartes
		cardsClassCateg.clear();
		for (ClassCard classCard : ClassCard.values()) {
			cardsClassCateg.put(classCard, new HashMap<>());
			for (Category categ : Category.values()) {
				cardsClassCateg.get(classCard).put(categ, new ArrayList<>());
			}
		}

		cardsClassRarity.clear();
		for (ClassCard classCard : ClassCard.values()) {
			cardsClassRarity.put(classCard, new HashMap<>());
			for (Card.Rarity rarity : Card.Rarity.values()) {
				cardsClassRarity.get(classCard).put(rarity, new ArrayList<>());
			}
		}
		
		// Chargement de chaque cartes
		String cardsFolderPath = "data/cards";
		File file = new File(cardsFolderPath);
		for (String fileName : file.list()) {

			String fileContent = FileManager.loadFile(cardsFolderPath + "/" + fileName);
			String[] cardsContent = fileContent.split(SEPARATOR);

			for (String cardContent : cardsContent) {
				if (cardContent.isBlank()) {
					continue;
				}
				Card card;
				try {
					card = new Card(cardContent);
					addCard(card);
				} catch (Exception e) {
					System.out.println("Error while loading " + cardsFolderPath + "/" + fileName
							+ " : " + cardContent);
					e.printStackTrace();
				}
			}
		}

		loadedCards = true;
	}
	
	private static void loadAllPlayerCard() {
		ClassCard classCard = ClassCard.valueOf(RunManager.getHero().heroClass.toString());
		
		cardsByClass.put(ClassCard.PLAYER, new ArrayList<>());
		cardsClassCateg.put(ClassCard.PLAYER, new HashMap<>());
		for (Category categ : Category.values()) cardsClassCateg.get(ClassCard.PLAYER).put(categ, new ArrayList<>());
		cardsClassRarity.put(ClassCard.PLAYER, new HashMap<>());
		for (Rarity rarity : Rarity.values()) cardsClassRarity.get(ClassCard.PLAYER).put(rarity, new ArrayList<>());
		
		for (Card card : cards.values()) {
			if (card.classCard() == classCard || card.classCard() == ClassCard.COLORLESS) {
				cardsByClass.get(ClassCard.PLAYER).add(card);
				cardsClassCateg.get(ClassCard.PLAYER).get(card.category()).add(card);
				cardsClassRarity.get(ClassCard.PLAYER).get(card.rarity()).add(card);
			}
		}
	}
	
	/**
	 * Charge tous les heros du jeu.
	 */
	public static void loadAllHeroes() {
		Hero hero = new Hero("Ironclad", "Ironclad.png", 80, 99, 3, Hero.HeroClass.IRONCLAD, 
				"Le soldat survivant des cuirassés.\nA vendu son âme pour maîtriser l'énergie démoniaque.");
		hero.deck().addCard(getCard("Ironclad Strike"), 5);
		hero.deck().addCard(getCard("Ironclad Defend"), 4);
		hero.deck().addCard(getCard("Bash"));
		heroes.put(hero.name(), hero);

		hero = new Hero("Silent", "Silent.png", 70, 99, 3, Hero.HeroClass.SILENT, 
				"Une chasseuse mortel des terres brumeuses.\nAbat ses ennemies à l'aide de dagues et de poison.");
		hero.deck().addCard(getCard("Silent Strike"), 5);
		hero.deck().addCard(getCard("Silent Defend"), 5);
		//hero.deck().addCard(getCard("Survivor"));
		hero.deck().addCard(getCard("Neutralize"));
		heroes.put(hero.name(), hero);
		
		loadedHeroes = true;
	}

	/**
	 * Charge tous les ennemies du jeu.
	 */
	public static void loadAllEnemies() {
		try {
			// Cultist
			PatternStrategy pattern = new PatternStrategy();
			pattern.add(new ElementPattern(new EnemyAction("name=Incantation\nritual=3"), 1, 1), 0);
			pattern.add(new ElementPattern(new EnemyAction("name=Dark Strike\ndamage=6"), 1, 1), 1);
			EnemyData enemy = new EnemyData("Cultist", "Cultist.png", 48, 54, pattern);
			addEnemy(enemy);

			// Jaw Worm
			SimpleStrategy simpleStrat = new SimpleStrategy();
			EnemyAction chomp = new EnemyAction("name=Chomp\ndamage=11");
			EnemyAction thrash = new EnemyAction("name=Thrash\ndamage=7\nblock=5");
			EnemyAction bellow = new EnemyAction("name=Bellow\nstrength=3\nblock=6");
			simpleStrat.setStartAction(chomp);
			simpleStrat.addAction(chomp, 0.25f, 1);
			simpleStrat.addAction(bellow, 0.45f, 1);
			simpleStrat.addAction(thrash, 0.30f, 2);
			enemy = new EnemyData("Jaw Worm", "JawWorm.png", 40, 44, simpleStrat);
			addEnemy(enemy);

			// Red Louse
			simpleStrat = new SimpleStrategy();
			EnemyAction bite = new EnemyAction("name=Bite\nminDamage=5\nmaxDamage=7");
			EnemyAction grow = new EnemyAction("name=Grow\nstrength=3\n");
			simpleStrat.addAction(bite, 0.75f, 2);
			simpleStrat.addAction(grow, 0.25f, 2);
			enemy = new EnemyData("Red Louse", "LouseRed.png", 10, 15, simpleStrat);
			addEnemy(enemy);

			// Green Louse
			simpleStrat = new SimpleStrategy();
			EnemyAction spitWeb = new EnemyAction("name=Spit Web\nweak=2\n");
			simpleStrat.addAction(bite, 0.75f, 2);
			simpleStrat.addAction(spitWeb, 0.25f, 2);
			enemy = new EnemyData("Green Louse", "LouseGreen.png", 11, 17, simpleStrat);
			addEnemy(enemy);

			// Acid Slime (S)
			simpleStrat = new SimpleStrategy();
			EnemyAction lick = new EnemyAction("name=Lick\nweak=1\n");
			EnemyAction tackle = new EnemyAction("name=Tackle\ndamage=3\n");
			simpleStrat.addAction(lick, 0.5f, 1);
			simpleStrat.addAction(tackle, 0.5f, 1);
			enemy = new EnemyData("Acid Slime (S)", "AcidSlimeS.png", 8, 12, simpleStrat);
			addEnemy(enemy);

			// Acid Slime (M)
			simpleStrat = new SimpleStrategy();
			EnemyAction corrosiveSpit = new EnemyAction("name=Corrosive Spit\ndamage=7\nslimed=1\n");
			lick = new EnemyAction("name=Lick\nweak=1\n");
			tackle = new EnemyAction("name=Tackle\ndamage=10\n");
			simpleStrat.addAction(corrosiveSpit, 0.3f, 2);
			simpleStrat.addAction(lick, 0.3f, 2);
			simpleStrat.addAction(tackle, 0.4f, 1);
			enemy = new EnemyData("Acid Slime (M)", "AcidSlimeM.png", 28, 32, simpleStrat);
			addEnemy(enemy);

			// Acid Slime (L) NON FONCTIONNEL
//			simpleStrat = new SimpleStrategy();
//			corrosiveSpit = new EnemyAction("name=Corrosive Spit\ndamage=11\nslimed=2\n");
//			lick = new EnemyAction("name=Lick\nweak=2\n");
//			tackle = new EnemyAction("name=Tackle\ndamage=16\n");
//			simpleStrat.addAction(corrosiveSpit, 0.3f, 2);
//			simpleStrat.addAction(lick, 0.3f, 2);
//			simpleStrat.addAction(tackle, 0.4f, 1);
//			SimpleStrategy simpleStrat2 = new SimpleStrategy();
//			EnemyAction split = new EnemyAction("name=Split\ninvoke=Acid Slime (M)\nnbrInvoke=2\ndisappear=1");
//			simpleStrat2.addAction(split, 1, -1);
//			LifeStrategy lifeStrat = new LifeStrategy(simpleStrat, simpleStrat2, 0.5f);
//			enemy = new EnemyData("Acid Slime (L)", "AcidSlimeL.png", 65, 69, lifeStrat);
//			addEnemy(enemy);

			// Spike Slime (S)
			simpleStrat = new SimpleStrategy();
			tackle = new EnemyAction("name=Tackle\ndamage=5\n");
			simpleStrat.addAction(tackle, 1f, -1);
			enemy = new EnemyData("Spike Slime (S)", "SpikeSlimeS.png", 10, 14, simpleStrat);
			addEnemy(enemy);

			// Spike Slime (M)
			simpleStrat = new SimpleStrategy();
			EnemyAction flameTackle = new EnemyAction("name=Flame Tackle\ndamage=8\nslimed=1");
			lick = new EnemyAction("name=Lick\nfrail=1");
			simpleStrat.addAction(lick, 0.7f, 2);
			simpleStrat.addAction(flameTackle, 0.3f, 2);
			enemy = new EnemyData("Spike Slime (M)", "SpikeSlimeM.png", 28, 32, simpleStrat);
			addEnemy(enemy);

			// Spike Slime (L) NON FONCTIONNEL
//			simpleStrat = new SimpleStrategy();
//			flameTackle = new EnemyAction("name=Flame Tackle\ndamage=16\nslimed=2");
//			lick = new EnemyAction("name=Lick\nfrail=2");
//			simpleStrat.addAction(lick, 0.7f, 3);
//			simpleStrat.addAction(flameTackle, 0.3f, 3);
//			SimpleStrategy simpleStrat2 = new SimpleStrategy();
//			EnemyAction split = new EnemyAction("name=Split\ninvoke=Spike Slime (M)\nnbrInvoke=2\ndisappear=1");
//			simpleStrat2.addAction(split, 1, -1);
//			LifeStrategy lifeStrat = new LifeStrategy(simpleStrat, simpleStrat2, 0.5f);
//			enemy = new EnemyData("Spike Slime (L)", "SpikeSlimeL.png", 64, 70, lifeStrat);
//			addEnemy(enemy);

			// Fat Gremlin
			simpleStrat = new SimpleStrategy();
			EnemyAction smash = new EnemyAction("name=Smash\ndamage=4\nweak=1");
			simpleStrat.addAction(smash, 1f, -1);
			enemy = new EnemyData("Fat Gremlin", "FatGremlin.png", 13, 17, simpleStrat);
			addEnemy(enemy);

			// Mad Gremlin
			simpleStrat = new SimpleStrategy();
			EnemyAction scratch = new EnemyAction("name=Scratch\ndamage=4");
			simpleStrat.addAction(scratch, 1f, -1);
			enemy = new EnemyData("Mad Gremlin", "MadGremlin.png", 20, 24, simpleStrat);
			enemy.addAngry(1);
			addEnemy(enemy);

			// Shield Gremlin
			simpleStrat = new SimpleStrategy();
			EnemyAction protect = new EnemyAction("name=Protect\nprotect=7");
			simpleStrat.addAction(protect, 1f, -1);
			SimpleStrategy simpleStrat2 = new SimpleStrategy();
			EnemyAction shieldBash = new EnemyAction("name=Shield Bash\ndamage=6");
			simpleStrat2.addAction(shieldBash, 1f, -1);
			SupportStrategy support = new SupportStrategy(simpleStrat, simpleStrat2);
			enemy = new EnemyData("Shield Gremlin", "ShieldGremlin.png", 12, 15, support);
			addEnemy(enemy);

			// Sneaky Gremlin
			simpleStrat = new SimpleStrategy();
			EnemyAction puncture = new EnemyAction("name=puncture\ndamage=9");
			simpleStrat.addAction(puncture, 1f, -1);
			enemy = new EnemyData("Sneaky Gremlin", "SneakyGremlin.png", 10, 14, simpleStrat);
			addEnemy(enemy);

			// Gremlin Wizard
			pattern = new PatternStrategy();
			EnemyAction charging = new EnemyAction("name=Charging\ndescription=Do nothing.");
			EnemyAction blast = new EnemyAction("name=Ultimate Blast\ndamage=25");
			pattern.add(charging, 1, 1f, 0);
			pattern.add(charging, 2, 1f, 1);
			pattern.add(blast, 3, 1f, 2);
			pattern.add(charging, 0, 1f, 3);
			enemy = new EnemyData("Gremlin Wizard", "WizardGremlin.png", 23, 25, pattern);
			addEnemy(enemy);

			// Blue Slaver
			simpleStrat = new SimpleStrategy();
			EnemyAction stab = new EnemyAction("name=Stab\ndamage=12");
			EnemyAction rake = new EnemyAction("name=Rake\ndamage=7\nweak=1");
			simpleStrat.addAction(rake, 0.4f, 2);
			simpleStrat.addAction(stab, 0.6f, 2);
			enemy = new EnemyData("Blue Slaver", "SlaverBlue.png", 46, 50, simpleStrat);
			addEnemy(enemy);

			// Red Slaver
			pattern = new PatternStrategy();
			stab = new EnemyAction("name=Stab\ndamage=13");
			EnemyAction scrap = new EnemyAction("name=Scrap\ndamage=8\nvulnerable=1");
			EnemyAction entangle = new EnemyAction("name=Entangle\nentangled=1");
			pattern.add(stab, 1, 1f, 0);
			pattern.add(scrap, 2, 0.75f, 1);
			pattern.add(entangle, 4, 0.25f, 1);
			pattern.add(scrap, 3, 0.75f, 2);
			pattern.add(entangle, 4, 0.25f, 2);
			pattern.add(stab, 1, 0.75f, 3);
			pattern.add(entangle, 4, 0.25f, 3);
			pattern.add(scrap, 5, 0.55f, 4);
			pattern.add(stab, 7, 0.45f, 4);
			pattern.add(scrap, 6, 0.55f, 5);
			pattern.add(stab, 6, 0.45f, 5);
			pattern.add(stab, 7, 1f, 6);
			pattern.add(stab, 8, 0.45f, 7);
			pattern.add(scrap, 5, 0.55f, 7);
			pattern.add(scrap, 5, 1f, 8);
			enemy = new EnemyData("Red Slaver", "SlaverRed.png", 46, 50, pattern);
			addEnemy(enemy);

			// Fungi Beast
			simpleStrat = new SimpleStrategy();
			bite = new EnemyAction("name=Bite\ndamage=6");
			grow = new EnemyAction("name=Grow\nstrength=3");
			simpleStrat.addAction(bite, 0.6f, 2);
			simpleStrat.addAction(grow, 0.4f, 1);
			enemy = new EnemyData("Fungi Beast", "FungiBeast.png", 22, 28, simpleStrat);
			addEnemy(enemy);

			// Looter NON FONCTIONNEL
//			pattern = new PatternStrategy();
//			EnemyAction mug = new EnemyAction("name=Mug\ndamage=10\n");
//			EnemyAction lunge = new EnemyAction("name=Lunge\ndamage=12");
//			EnemyAction smokBomb = new EnemyAction("name=Smok Bomb\nblock=6");
//			EnemyAction escape = new EnemyAction("name=Escape\ndisappear=1");
//			pattern.add(mug, 1, 1f, 0);
//			pattern.add(mug, 2, 1f, 1);
//			pattern.add(lunge, 3, 1f, 2);
//			pattern.add(smokBomb, 4, 1f, 2);
//			pattern.add(smokBomb, 4, 1f, 3);
//			pattern.add(escape, 4, 1f, 4);
//			enemy = new EnemyData("Looter", "Looter.png", 44, 48, pattern);
//			addEnemy(enemy);

			// Mugger NON FONCTIONNEL
//			pattern = new PatternStrategy();
//			lunge = new EnemyAction("name=Lunge\ndamage=16");
//			smokBomb = new EnemyAction("name=Smok Bomb\nblock=11");
//			pattern.add(mug, 1, 1f, 0);
//			pattern.add(mug, 2, 1f, 1);
//			pattern.add(lunge, 3, 1f, 2);
//			pattern.add(smokBomb, 4, 1f, 2);
//			pattern.add(smokBomb, 4, 1f, 3);
//			pattern.add(escape, 4, 1f, 4);
//			enemy = new EnemyData("Mugger", "Mugger.png", 48, 52, pattern);
//			addEnemy(enemy);

			loadedEnemies = true;
		} catch(Exception e) { // A changer
			e.printStackTrace();
		}
	}

	/**
	 * Charge tous les elites du jeu.
	 */
	public static void loadAllElites() {
		try {
			// Gremlin Nob
			SimpleStrategy simpleStrat = new SimpleStrategy();
			EnemyAction bellow = new EnemyAction("name=Bellow\nenrage=2");
			EnemyAction rush = new EnemyAction("name=Rush\ndamage=14");
			EnemyAction skullBash = new EnemyAction("name=skullBash\ndamage=6\nvulnerable=2");
			simpleStrat.setStartAction(bellow);
			simpleStrat.addAction(rush, 0.67f, 2);
			simpleStrat.addAction(skullBash, 0.33f, -1);
			EnemyData enemy = new EnemyData("Gremlin Nob", "GremlinNob.png", 82, 86, simpleStrat);
			addEnemy(enemy, EnemyCategory.ELITE);

			// Lagavulin (COMPLIQUE A INTEGREE)
			
			// Sentry
			simpleStrat = new SimpleStrategy();
			EnemyAction beam = new EnemyAction("name=Beam\ndamage=9");
			EnemyAction bolt = new EnemyAction("name=Bolt\naddCardToPile=Dazed\naddCardToPileNbr=2\ntargetPile=discard");
			simpleStrat.addAction(beam, 0.5f, 1);
			simpleStrat.addAction(bolt, 0.5f, 1);
			enemy = new EnemyData("Sentry", "Sentry.png", 38, 42, simpleStrat);
			enemy.addArtifact(1);
			addEnemy(enemy, EnemyCategory.ELITE);
			
			loadedElites = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Charge tous les boss du jeu.
	 */
	public static void loadAllBoss() {
		try {
			// Hexaghost
			PatternStrategy pattern = new PatternStrategy();
			EnemyAction activate = new EnemyAction("name=Activate\ndescription=Do nothing.");
			EnemyAction divider = new EnemyAction("name=Divider\ndivider=1");
			EnemyAction inferno = new EnemyAction("name=Inferno\ndamage=2\nattackNbr=6\n"
					+ "addCardToPile=Burn\naddCardToPileNbr=3");
			EnemyAction sear = new EnemyAction("name=Sear\ndamage=6\naddCardToPile=Burn");
			EnemyAction tackle = new EnemyAction("name=Tackle\ndamage=5\nattackNbr=2");
			EnemyAction inflame = new EnemyAction("name=Inflame\nstrength=2\nblock=12");
			pattern.add(activate, 1, 1f, 0);
			pattern.add(divider, 2, 1f, 1);
			pattern.add(sear, 3, 1f, 2);
			pattern.add(tackle, 4, 1f, 3);
			pattern.add(sear, 5, 1f, 4);
			pattern.add(inflame, 6, 1f, 5);
			pattern.add(tackle, 7, 1f, 6);
			pattern.add(sear, 8, 1f, 7);
			pattern.add(inferno, 2, 1f, 8);
			EnemyData enemy = new EnemyData("Hexaghost", "Hexaghost.png", 250, 250, pattern); // VIE INITIAL : 250
			addEnemy(enemy, EnemyCategory.BOSS);
			
			loadedBoss = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Charge toutes les potions du jeu.
	 */
	public static void loadAllPotions() {
		String fileName = "data/potions.txt";
		// initialisation list des potions par rareté
		for (Potion.Rarity rarity : Potion.Rarity.values()) {
			potionsRarity.put(rarity, new ArrayList<>());
		}
		
		// Chargement/ajouts des potions depuis le fichier texte.
		String fileContent = FileManager.loadFile(fileName);
			String[] contents = fileContent.split(SEPARATOR);

			for (String content : contents) {
				if (content.isBlank()) {
					continue;
				}
				Potion potion;
				try {
					potion = new Potion(content);
					addPotion(potion);
				} catch (Exception e) {
					System.out.println("Error while loading " + fileName
							+ " : " + content);
					e.printStackTrace();
				}
			}

		loadedPotions = true;
	}

}

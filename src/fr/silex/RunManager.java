package fr.silex;

import java.awt.Color;
import java.util.ArrayList;

import fr.silex.action.Card;
import fr.silex.action.Potion;
import fr.silex.data.Data;
import fr.silex.data.DataBattleRoom;
import fr.silex.data.DataDebugMod;
import fr.silex.data.DataMainMenu;
import fr.silex.data.DataMap;
import fr.silex.data.DataPlayerChoice;
import fr.silex.data.DataShopRoom;
import fr.silex.data.Datas;
import fr.silex.data.Datas.EnemyCategory;
import fr.silex.entity.Enemy;
import fr.silex.entity.Hero;
import fr.silex.room.ShopRoom;
import fr.silex.ui.UI;
import fr.silex.ui.UIBattleRoom;
import fr.silex.ui.UIDebugMod;
import fr.silex.ui.UIMainMenu;
import fr.silex.ui.UIMap;
import fr.silex.ui.UIPlayerChoice;
import fr.silex.ui.UIShopRoom;
import fr.umlv.zen5.Application;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;
import fr.umlv.zen5.KeyboardKey;

/**
 * Classe principal du jeu.
 */
public class RunManager {
	public static enum Scene {
		MAIN_MENU, PLAYER_CHOICE, MAP, BATTLE_ROOM, SHOP_ROOM, EVENT_ROOM, EXIT_GAME, DEBUG_MOD
	}
	
	private static Scene actualScene;
	private static ApplicationContext context;
	private static UI ui; // UI actuel.
	private static boolean debugMod = false;
	
	private static Data data;
	private static Hero hero;
	
	private static int widthScreen;
	private static int heightScreen;
	private static ArrayList<Enemy> defaultEnemies = null;
	
	public static void main(String[] args) {
		Application.run(Color.BLACK, RunManager::game);
	}
	
	/**
	 * Contient tout le code qui sera exécuté au lancement du programme.
	 */
	private static void game(ApplicationContext context) {
		RunManager.context = context;
		widthScreen = (int)context.getScreenInfo().getWidth();
		heightScreen = (int)context.getScreenInfo().getHeight();
		// hero = Datas.getHero("Ironclad"); // Définition d'un hero par défaut pour les tests
		
		loadScene(Scene.MAIN_MENU);
		
		ui.draw();
		while (true) {
			// Récupération de l'action du joueur
			Event event = context.pollOrWaitEvent(100);
			if (event == null || event.getAction() == Action.POINTER_MOVE) continue; // Faire bouger le pointeur fais lag le jeu donc on l'interdis. -(;_;)-
			
			tryExitGame(event);
			
			// Effectue les actions en fonction de la scene actuelle
			switch (actualScene) {
			case MAIN_MENU:
				actionMainMenu(event);
				break;
			case PLAYER_CHOICE:
				actionPlayerChoice(event);
				break;
			case BATTLE_ROOM:
				actionBattleRoom(event);
				break;
			case SHOP_ROOM:
				actionShopRoom(event);
				break;
			case MAP:
				actionMap(event);
				break;
			case DEBUG_MOD:
				actionDebug(event);
				break;
			default:
				break;
			}
			
			// Affichage.
			ui.draw();
		}
	}
	
	/**
	 * Change la scene actuelle avec la nouvelle.
	 */
	private static void loadScene(Scene newScene) {
		loadScene(newScene, null);
	}
	
	/**
	 * Change la scene actuelle avec la nouvelle.
	 */
	private static void loadScene(Scene newScene, EnemyCategory category) {
		if (newScene == null) return;
		
		switch (newScene) {
		case MAIN_MENU:
			System.out.println("\n##### MENU PRINCIPAL #####\n");
			actualScene = newScene;
			data = new DataMainMenu();
			ui = new UIMainMenu(context, (DataMainMenu)data);
			debugMod = false;
			Datas.loadAllHeroes();
			break;
		case PLAYER_CHOICE:
			System.out.println("\n##### CHOIX DU PERSONNAGE #####\n");
			actualScene = newScene;
			data = new DataPlayerChoice();
			ui = new UIPlayerChoice(context, (DataPlayerChoice)data);
			break;
		case BATTLE_ROOM:
			System.out.println("\n##### SALLE DE COMBAT #####\n");
			actualScene = newScene;
			if (category == null) {
				data = new DataBattleRoom(hero, new ArrayList<>(defaultEnemies));
			} else {
				data = new DataBattleRoom(hero, category);
			}
			ui = new UIBattleRoom(context, (DataBattleRoom)data);
			break;
		case SHOP_ROOM:
			System.out.println("\n##### MAGASIN #####\n");
			actualScene = newScene;
			data = new DataShopRoom(new ShopRoom(hero));
			ui = new UIShopRoom(context, (DataShopRoom)data);
			break;
		case MAP:
			System.out.println("\n##### CARTE #####\n");
			actualScene = newScene;
			data = new DataMap();
			ui = new UIMap(context, (DataMap)data);
			break;
		case DEBUG_MOD:
			System.out.println("\n##### DEBUG MOD #####\n");
			hero = Datas.getHero("Ironclad");
			actualScene = newScene;
			data = new DataDebugMod();
			ui = new UIDebugMod(context, (DataDebugMod)data);
			debugMod = true;
			break;
		case EXIT_GAME:
			exitGame();
			break;
		default:
			System.out.println(newScene + " n'a pas pu être chargée car elle n'est pas integrée.");
			break;
		}
	}
	
	/**
	 * Code utilisé quand le joueur est sur le menu principal.
	 */
	private static void actionMainMenu(Event event) {
		DataMainMenu dataLoc = (DataMainMenu)data;
		loadScene(dataLoc.resolvePlayerAction(event));
	}

	/**
	 * Code utilisé quand le joueur est sur le choix du joueur.
	 */
	private static void actionPlayerChoice(Event event) {
		DataPlayerChoice dataLoc = (DataPlayerChoice)data;
		Scene nextScene = dataLoc.resolvePlayerAction(event);
		
		if (nextScene == Scene.MAP) {
			Map.resetInstance();
			Datas.loadAllHeroes(); // On recharge les heros
			hero = dataLoc.getChosenHero();
		}
		
		loadScene(nextScene);
	}
	
	/**
	 * Code utilisé quand le joueur est dans une salle de combat.
	 */
	private static void actionBattleRoom(Event event) {
		DataBattleRoom dataLoc = (DataBattleRoom)data;
		Scene nextScene = dataLoc.resolvePlayerAction(event);
		loadScene(nextScene);
	}

	/**
	 * Code utilisé quand le joueur est dans un magasin.
	 */
	private static void actionShopRoom(Event event) {
		DataShopRoom dataLoc = (DataShopRoom)data;
		if (dataLoc.resolvePlayerAction(event)) { // Si le joueur demande a sortir du magasin
			loadScene(Scene.MAP);
		}
	}

	/**
	 * Code utilisé quand le joueur est dans un magasin.
	 */
	private static void actionMap(Event event) {
		DataMap dataLoc = (DataMap)data;
		RoomType room = dataLoc.resolvePlayerAction(event);
		
		if (room != null) { 
			switch (room) {
			case REST:
				hero.heal((int)(hero.maxLife()*0.3));
				System.out.println(hero.name() + " a recupéré "+ (int)(hero.maxLife()*0.3) + ("pv."));
				break;
			case SHOP:
				loadScene(Scene.SHOP_ROOM);
				break;
			case BATTLE:
				loadScene(Scene.BATTLE_ROOM, EnemyCategory.NORMAL);
				break;
			case ELITE:
				loadScene(Scene.BATTLE_ROOM, EnemyCategory.ELITE);
				break;
			case BOSS:
				loadScene(Scene.BATTLE_ROOM, EnemyCategory.BOSS);
				break;
			default:
				break;
			}
		}
	}
	
	private static void actionDebug(Event event) {
		DataDebugMod dataLoc = (DataDebugMod)data;
		Scene room = dataLoc.resolvePlayerAction(event);
		
		if (room != null) {
			defaultEnemies = dataLoc.getChosenEnemies();
			for (Potion potion : dataLoc.getChosenPotions()) {
				hero.addPotion(potion);
			}
			hero.deck().clear();
			for (Card card : dataLoc.getChosenCards()) {
				hero.deck().addCard(card);
			}
			loadScene(room);
		}
	}
	
	/**
	 * Test si le joueur veut sortir du jeu,
	 * et si oui, arrête le programme.
	 */
	private static void tryExitGame(Event event) {
		if (event.getAction() == Action.KEY_PRESSED && event.getKey() == KeyboardKey.SPACE) {
			exitGame();
		}
	}
	
	private static void exitGame() {
		System.out.println("Vous avez quitté le jeu.");
		context.exit(0);
	}
	
	/**
	 * Renvoie true si la touche entrée est un caractère.
	 */
	public static boolean isLetter(KeyboardKey key) {
		return key != KeyboardKey.ALT
				&& key != KeyboardKey.ALT_GR
				&& key != KeyboardKey.CTRL
				&& key != KeyboardKey.DOWN
				&& key != KeyboardKey.LEFT
				&& key != KeyboardKey.META
				&& key != KeyboardKey.RIGHT
				&& key != KeyboardKey.SHIFT
				&& key != KeyboardKey.SPACE
				&& key != KeyboardKey.UNDEFINED
				&& key != KeyboardKey.UP;
	}

	public static int getWidthScreen() { return widthScreen; }
	public static int getHeightScreen() { return heightScreen; }
	public static Hero getHero() { return hero; }
	public static boolean inDebugMod() { return debugMod; }
}

package fr.silex;

import static fr.silex.RoomType.BATTLE;
import static fr.silex.RoomType.BOSS;
import static fr.silex.RoomType.ELITE;
import static fr.silex.RoomType.REST;
import static fr.silex.RoomType.SHOP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class Map {
	// 3 salles combat noobs, 1 elite, 2 repos, 1 shop, 1 boss (minimum)
	private static Map map = null;
	private static Random random = new Random();
	
	private int playerPosition; // Position du joueur dans la map
	private final HashMap<Integer, ArrayList<Integer>> nodeTransitions = new HashMap<Integer, ArrayList<Integer>>(); // Liste des chemin possible a partir d'une node
	private final Integer[][] nodePositions = new Integer[3][11]; // Tableau qui va servir pour l'affichage
	private final HashMap<Integer, RoomType> nodeType = new HashMap<>(); // Contient le type de chaque node (salle)
	
	private Map() {
		initMap();
	}
	
	private void addNodeTransition(int originNode, int destNode, RoomType roomType) {
		if (!nodeTransitions.containsKey(originNode)) {
			System.out.println("WARNING : Tentative de créer un chemin vers la node "+destNode+" qui n'est pas accessible, "
					+ "car la node d'origine " + originNode + " n'existe pas. La nouvelle node est donc inaccessible.");
		}
		nodeTransitions.putIfAbsent(destNode, new ArrayList<>());
		nodeTransitions.get(destNode).add(originNode);
		nodeType.put(destNode, roomType);
	}
	
	// Retourne l'unique instance de Map.
	public static Map getInstance() {
		if (map == null) {
			map = new Map();
		}
		return map;
	}
	
	/**
	 * Réinitialise et recrée la map sans créer de nouvelle instance.
	 * /!\ N'a pas l'air de fonctionner pour le moment 
	 */
	public static void resetInstance() {
		if (map != null) {
			map.initMap();
		}
	}
	
	private void initMap() {
		playerPosition=0;
		// Nouveau constructeur aléatoire
		ArrayList<RoomType> possibleRoom = new ArrayList<>(List.of(BATTLE, BATTLE, BATTLE, ELITE, REST, SHOP)); // Toutes les rooms a affecter
		// Definition de la node de depart
		nodeTransitions.put(0, new ArrayList<>());
		nodeType.put(0, REST);
		nodePositions[1][0] = 0;
		
		// Definition de 3 chemin aleatoires
		RoomType nextRoom;
		int numRoom = 2; // numéro de la room qui va etre ajoutée.
		                 // On commence a 2 car la room 0 est celle de depart et la 1 est la room du Boss.
		int[] endPaths = new int[3];
		boolean startPath; // Verifie si on commence un nouveau chemin ou pas.
		
		for (int i=0; i<3; i++) { // Création de 3 chemin aléatoire
			startPath = true;
			ArrayList<RoomType> possibility = new ArrayList<>(possibleRoom);
			while (!possibility.isEmpty()) {
				nextRoom = possibility.remove(random.nextInt(possibility.size()));
				if (startPath) {
					addNodeTransition(0, numRoom, nextRoom);
					startPath = false;
				} else {
					addNodeTransition(numRoom-1, numRoom, nextRoom);
				}
				nodePositions[i][possibleRoom.size()-possibility.size()] = numRoom;
				numRoom++;
			}
			// Ajout REST avant le boss
			addNodeTransition(numRoom-1, numRoom, REST);
			nodePositions[i][possibleRoom.size()+1] = numRoom;
			
			endPaths[i] = numRoom; // On stocke la node de fin de chemin pour pouvoir le relier au Boss
			numRoom++;
		}
		
		// Ajout transitions tous les chemins vers le Boss
		for (int i=0; i<endPaths.length; i++) {
			addNodeTransition(endPaths[i], 1, BOSS);
		}
		nodePositions[1][nodePositions[1].length-2] = 1;
	}
	
	/**
	 * Essaie de bouger le joueur vers la node indiqué.
	 * @return False si la destination n'est pas atteignable à partir de la position de joueur. Dans ce cas, il ne se passe rien.
	 */
	public boolean movePlayer(int destination) {
		if (nodeTransitions.get(Integer.valueOf(destination)).contains(Integer.valueOf(playerPosition))) {
			playerPosition = destination;
			return true;
		}
		return false;
	}
	
	/**
	 * Retourne le type de la node du joueur.
	 */
	public RoomType type() {
		return nodeType.get(playerPosition);
	}
	
	/**
	 * Retourne le type de la node indiqué
	 */
	public RoomType type(int node) {
		return nodeType.get(node);
	}
	
	public Integer[][] getNodePositions() { 
		Integer[][] copy = new Integer[nodePositions.length][];
		for (int i=0; i<copy.length; i++) {
			copy[i] = Arrays.copyOf(nodePositions[i], nodePositions[i].length);
		}
		return copy;
	}
	
	public HashMap<Integer, ArrayList<Integer>> getNodeTransitions() {
		HashMap<Integer, ArrayList<Integer>> copy = new HashMap<>(nodeTransitions);
		for (Entry<Integer, ArrayList<Integer>> entry : copy.entrySet()) {
			copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		
		return copy;
	}
	
	public int getPlayerPosition() { return playerPosition; }
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("Map\nPlayer Position: "+playerPosition+"\n");
		string.append("nodeTransitions: "+nodeTransitions.toString()+"\n");
		string.append("nodeType: "+nodeType.toString()+"\n");
		return string.substring(0);
	}
	
}
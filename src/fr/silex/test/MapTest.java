package fr.silex.test;

import fr.silex.Map;
import fr.silex.exception.BadFormatException;

/**
 * Classe de test de la Map.
 */
public class MapTest {
	public static void main(String[] args) throws BadFormatException {
		Map map = Map.getInstance();
		System.out.println(map);
		
		System.out.println(map.movePlayer(1));
		System.out.println(map);
		System.out.println(map.movePlayer(0));
		System.out.println(map);
	}
}

package fr.silex.data;

import fr.umlv.zen5.Event;

public interface Data {
	
	/**
	 * Retourne des informations sur la scene cible en fonction de l'action du joueur.
	 */
	public Object resolvePlayerAction(Event event);
}

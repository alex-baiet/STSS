package fr.silex.data;

import java.util.Arrays;

import fr.silex.RunManager.Scene;
import fr.silex.ui.Button;
import fr.silex.ui.UI;
import fr.umlv.zen5.Event;

public class DataMainMenu implements Data {
	private Button[] buttons;
	
	public DataMainMenu() {
		String[] butNames = { "Nouvelle partie", "Quitter", "Mode de debuggage" };
		this.buttons = new Button[butNames.length];
		
		for (int i=0; i<butNames.length; i++) {
			buttons[i] = new Button(50, 50 + i * 40, 200, UI.BUTTON_HEIGHT, butNames[i], UI.BUTTON_COLOR, null);
		}
	}
	
	
	@Override
	public Scene resolvePlayerAction(Event event) {
		switch (getClickedButton(event)) {
		case 0: // Clic bouton nouvelle partie
			return Scene.PLAYER_CHOICE;
		case 1: // Clic bouton nouvelle partie
			return Scene.EXIT_GAME;
		case 2: // Clic bouton Quitter
			return Scene.DEBUG_MOD;
		default: // Clic dans le vide
			break;
		}
		return null;
	}
	
	/**
	 * Retourne le numéro du bouton cliqué.
	 */
	private int getClickedButton(Event event) {
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].isClicked(event)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Renvoie la liste des boutons du menu principal.
	 * @return
	 */
	public Button[] getButtons() {
		return Arrays.copyOf(buttons, buttons.length);
	}
}

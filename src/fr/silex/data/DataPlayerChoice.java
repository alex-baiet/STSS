package fr.silex.data;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import fr.silex.FileManager;
import fr.silex.RunManager;
import fr.silex.RunManager.Scene;
import fr.silex.entity.Hero;
import fr.silex.ui.Button;
import fr.silex.ui.UI;
import fr.umlv.zen5.Event;

public class DataPlayerChoice implements Data {
	private final Hero[] heroes;
	private final BufferedImage[] bgs;
	private int selectedHero = 0;
	private final Button[] buttons;
	
	public DataPlayerChoice() {
		int widthScreen = RunManager.getWidthScreen();
		int heightScreen = RunManager.getHeightScreen();
		heroes = new Hero[] {
				Datas.getHero("Ironclad"),
				Datas.getHero("Silent")
		};
	
		bgs = new BufferedImage[] {
				FileManager.loadImage("pictures/bg/ironclad_bg.jpg"),
				FileManager.loadImage("pictures/bg/silent_bg.jpg")
		};
		
		buttons = new Button[4];
		int butSize = 120;
		int margin = 200;
		for (int i=0; i<2; i++) {
			String imgName = "pictures/buttons/" + (i==0 ? "ironclad_but.png" : "silent_but.png");
			buttons[i] = new Button(
					widthScreen/2 - butSize/2 - margin/2 + margin*i, 
					heightScreen - 160, butSize, butSize, 
					null, null, FileManager.loadImage(imgName));
		}
		butSize = 200;
		buttons[2] = new Button(40, heightScreen*2/3, butSize, UI.BUTTON_HEIGHT, "Retour au menu", UI.BUTTON_COLOR, null);
		buttons[3] = new Button(widthScreen - butSize - 40, heightScreen*2/3, butSize, UI.BUTTON_HEIGHT, "Commencer la partie", UI.BUTTON_COLOR, null);
		
	}
	
	@Override
	public Scene resolvePlayerAction(Event event) {
		int clickedBut = getClickedButton(event);
		switch (clickedBut) {
		case 0, 1:
			selectedHero = clickedBut;
			System.out.println("Hero s�l�ctionn� : "+heroes[selectedHero].name());
			break;
		case 2:
			return Scene.MAIN_MENU;
		case 3:
			return Scene.MAP;
		default:
			break;
		}
		return null;
	}
	
	/**
	 * Retourne le bouton press�.
	 * 
	 *   0 : Ironclad
	 *   1 : Silent
	 *   2 : Retour au menu
	 *   3 : Commencer la partie
	 */
	private int getClickedButton(Event event) {
		for (int i=0; i<buttons.length; i++) {
			if (buttons[i].isClicked(event)) {
				return i;
			}
		}
		return -1;
	}

	public Hero getChosenHero() {
		return heroes[selectedHero];
	}
	
	public BufferedImage getBgHero() {
		return bgs[selectedHero];
	}
	
	public Button[] getButtons() {
		return Arrays.copyOf(buttons, buttons.length);
	}

//	public void setSelection(int newSelectedHero) {
//		if (newSelectedHero > 1 || newSelectedHero < 0) throw new IllegalArgumentException("le param�tre newSelection doit �tre entre 0 et 1.");
//		selectedHero = newSelectedHero;
//	}
}

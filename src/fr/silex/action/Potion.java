package fr.silex.action;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import fr.silex.FileManager;
import fr.silex.exception.BadFormatException;

public class Potion extends HeroAction {
	
	private final String imagePath;
	private BufferedImage img = null;
	
	private final float resurrect; // Dégâts équivalent au nombre de carte.
	
	public Potion(String data) throws BadFormatException {
		super(data);
		HashMap<String, String> map = FileManager.getFileValue(data);
		
		this.imagePath = map.get("imagePath");
		this.resurrect = map.containsKey("resurrect") ? Float.parseFloat(map.get("resurrect")) : 0f;
	}
	
	public BufferedImage img() {
		if (img == null) img = FileManager.loadImage("pictures/potions/" + imagePath);
		return img;
	}
	
	public float getResurrect() { return resurrect; }
}

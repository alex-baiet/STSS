package fr.silex;

import java.awt.image.BufferedImage;

public enum RoomType {
	BOSS("boss"), // La salle du boss n'a que une icone de map.
	BATTLE("enemy"),
	ELITE("elite"),
	REST("rest"),
	SHOP("shop");
	//UNKNOWN, 
	//TREASURE
	
	String imgBaseName;
	BufferedImage img = null;
	BufferedImage icon = null;
	
	private RoomType(String imgBaseName) {
		this.imgBaseName = imgBaseName;
	}
	
	public BufferedImage getImage() {
		if (img == null) {
			String fileName = "pictures/map/"+imgBaseName+".png";
			img = FileManager.loadImage(fileName);
		}
		return img;		
	}
	
	public BufferedImage getIcon() {
		if (icon == null) {
			String fileName = "pictures/map/"+imgBaseName+"_ico.png";
			icon = FileManager.loadImage(fileName);
		}
		return icon;
	}
}

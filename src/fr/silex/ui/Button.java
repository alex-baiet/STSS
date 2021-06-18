package fr.silex.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.silex.GraphicsHelper;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;

/**
 * Bouton pour faciliter la création de menu.
 */
public class Button {
	private int posX;
	private int posY;
	private int width;
	private int height;
	private String text;
	private Color bgColor;
	private BufferedImage img;
	
	public Button(int posX, int posY, int width, int height, String text, Color bgColor, BufferedImage img) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.text = text;
		this.bgColor = bgColor;
		this.img = img;
	}
	
	public boolean isClicked(Event event) {
		if (event.getAction() == Action.POINTER_DOWN) {
			return isHover(event);
		}
		return false;
	}
	
	public boolean isHover(Event event) {
		if (event.getLocation() != null) {
			int x = (int)(event.getLocation().getX());
			int y = (int)(event.getLocation().getY());
			
			if (x >= posX && x < posX + width &&
					y >= posY && y < posY + height) {
				return true;
			}
		}
		return false;
	}
	
	public void display(Graphics2D graphics) {
		Color colorSave = graphics.getColor();	
		
		if (bgColor != null) {
			graphics.setColor(bgColor);
			graphics.fillRect(posX, posY, width, height);

			// Affichage contour du bouton
			graphics.setColor(UI.BUTTON_CONTOUR);
			graphics.drawLine(posX, posY, posX+width, posY);
			graphics.drawLine(posX, posY, posX, posY+height);
			graphics.drawLine(posX+width, posY, posX+width, posY+height);
			graphics.drawLine(posX, posY+height, posX+width, posY+height);
		}
		
		if (text != null) {
			GraphicsHelper.drawText(graphics, text, UI.LINE_SPACE, posX + UI.MARGIN_TEXT, posY);
		}
		
		if (img != null) {
			GraphicsHelper.drawImageContain(graphics, img, posX, posY, posX + width, posY + height);
		}
		
		graphics.setColor(colorSave);		
	}
	
	public void setBgColor(Color color) { bgColor = color; }
	public void setImg(BufferedImage img) { this.img = img; }

	public int getX() { return posX; }
	public int getY() { return posX; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
}

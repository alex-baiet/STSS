package fr.silex.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import fr.silex.GraphicsHelper;
import fr.silex.data.DataPlayerChoice;
import fr.silex.entity.Hero;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.ScreenInfo;

public class UIPlayerChoice implements UI {

	private ApplicationContext context;
	private DataPlayerChoice data;
	private int width;
	private int height;
	
	public UIPlayerChoice(ApplicationContext context, DataPlayerChoice data) {
		this.context = context;
		this.data = data;
		ScreenInfo screen = context.getScreenInfo();
		width = (int)screen.getWidth();
		height = (int)screen.getHeight();
		
	}
	
	@Override
	public void draw() {
		draw(context);
	}
	
	private void draw(ApplicationContext context) {
		context.renderFrame(graphics -> draw(graphics));
	}
	
	private void draw(Graphics2D graphics) {
		GraphicsHelper.setFontDefault(graphics);
		GraphicsHelper.fillRect(graphics, 0, 0, width, height, UI.DEFAULT_BG);
		// Affichage fond
		GraphicsHelper.drawImageFill(graphics, data.getBgHero(), 0, 0, width, height);
		
		// Affichage info Hero
		displayHero(graphics);
		
		//Affichage boutons
		for (Button but : data.getButtons()) {
			but.display(graphics);
		}
		
		for (Button but : data.getButtons()) {
			but.display(graphics);
		}
	}
	
	private void displayHero(Graphics2D graphics) {
		float nameSizeCoef = 3;
		float descSizeCoef = 1.5f;
		
		
		Hero hero = data.getChosenHero();	
		String txt = 
				"Vie : " + hero.maxLife() + "\n" 
				+ "Argent : "+ hero.getGold() + "\n\n" 
				+ hero.getDescription();

		GraphicsHelper.setFontSize(graphics, (int)(FONT_SIZE * nameSizeCoef));
		GraphicsHelper.drawText(graphics, hero.name(), (int)(LINE_SPACE * nameSizeCoef), width/4, height/3, Color.WHITE);
		GraphicsHelper.setFontSize(graphics, (int)(FONT_SIZE*descSizeCoef));
		GraphicsHelper.drawText(graphics, txt, (int)(LINE_SPACE * descSizeCoef), width/4, height/3 + (int)(UI.FONT_SIZE*nameSizeCoef*1.5), Color.WHITE);
		
		GraphicsHelper.setFontDefault(graphics);
	}
	
}

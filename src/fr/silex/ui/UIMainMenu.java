package fr.silex.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.silex.FileManager;
import fr.silex.GraphicsHelper;
import fr.silex.data.DataMainMenu;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.ScreenInfo;

public class UIMainMenu implements UI {

	private ApplicationContext context;
	private DataMainMenu data;
	private int width;
	private int height;
	private BufferedImage bg;
	
	public UIMainMenu(ApplicationContext context, DataMainMenu data) {
		this.context = context;
		this.data = data;
		ScreenInfo screen = context.getScreenInfo();
		width = (int)screen.getWidth();
		height = (int)screen.getHeight();
		
		bg = FileManager.loadImage("pictures/bg/menu_bg.jpg");
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
		GraphicsHelper.drawImageFill(graphics, bg, 0, 0, width, height);
		
		for (Button but : data.getButtons()) {
			but.display(graphics);
		}
	}
	
}

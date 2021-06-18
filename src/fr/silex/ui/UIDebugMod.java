package fr.silex.ui;

import java.awt.Graphics2D;
import java.util.ArrayList;

import fr.silex.GraphicsHelper;
import fr.silex.data.DataDebugMod;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.ScreenInfo;

public class UIDebugMod implements UI {

	private final DataDebugMod data;
	private final ApplicationContext context;
	private final int width;
	private final int height;
	private final int marginTop;
	private final int marginLeft;
	private final int heightResult;

	public UIDebugMod(ApplicationContext context, DataDebugMod data) {
		this.data = data;
		this.context = context;
		ScreenInfo screen = context.getScreenInfo();
		width = (int)screen.getWidth();
		height = (int)screen.getHeight();
		
		marginTop = data.marginTop();
		marginLeft = data.marginLeft();
		heightResult = data.heightResult();
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
		
		drawSearchBar(graphics);
		drawCatalog(graphics);
		drawChoice(graphics);
		drawSelection(graphics);
		data.getConfirmButton().display(graphics);
	}

	private void drawSearchBar(Graphics2D graphics) {
		GraphicsHelper.drawText(graphics, "Tapez du texte pour chercher", UI.LINE_SPACE*2, width/4, 0, null, UI.FONT_SIZE*2);
		GraphicsHelper.drawText(graphics, data.getSearch(), UI.LINE_SPACE*2, width/4, height/16, null, UI.FONT_SIZE*2);
		data.getDeleteCharButton().display(graphics);
		data.getDeleteSearchButton().display(graphics);
	}
	
	private void drawCatalog(Graphics2D graphics) {
		ArrayList<Button> toDisplay;
		
		// Recupération de la bonne liste à afficher.
		switch (data.getStateDebug()) {
		case CHOOSING_CARDS:
			toDisplay = data.getCardsButtons();
			break;
		case CHOOSING_POTIONS:
			toDisplay = data.getPotionsButtons();
			break;
		case CHOOSING_ENEMIES:
			toDisplay = data.getEnemiesButtons();
			break;
		default:
			toDisplay = new ArrayList<>();
			break;
		}
		
		// Affichage de la liste.
		for (Button but : toDisplay) {
			but.display(graphics);
		}
	}
	
	private void drawChoice(Graphics2D graphics) {
		Button[] toDisplay;
		
		// Recupération de la bonne liste à afficher.
		switch (data.getStateDebug()) {
		case CHOOSING_CARDS:
			toDisplay = data.getChosenCardsButtons();
			break;
		case CHOOSING_POTIONS:
			toDisplay = data.getChosenPotionsButtons();
			break;
		case CHOOSING_ENEMIES:
			toDisplay = data.getChosenEnemiesButtons();
			break;
		default:
			toDisplay = new Button[0];
			break;
		}

		// Affichage de la liste.
		for (Button but : toDisplay) {
			but.display(graphics);
		}
	}
	
	private void drawSelection(Graphics2D graphics) {
		if (data.getSelectionText() != null) { 
			GraphicsHelper.drawText(graphics, data.getSelectionText(), UI.LINE_SPACE, UI.MARGIN_TEXT, marginTop/2 + UI.MARGIN_TEXT);
		}
		if (data.getSelectionImg() != null) {
			GraphicsHelper.drawImageContain(graphics, data.getSelectionImg(), 0, marginTop, marginLeft, marginTop+heightResult);
		}
	}
}

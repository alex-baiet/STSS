package fr.silex.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import fr.silex.FileManager;
import fr.silex.GraphicsHelper;
import fr.silex.data.DataShopRoom;
import fr.silex.room.CardItem;
import fr.silex.room.PotionItem;
import fr.umlv.zen5.ApplicationContext;

public class UIShopRoom implements UI {
	private final ApplicationContext context;
	private final DataShopRoom data;
	
	private final int width;
	private final int height;
	private final int widthShop;
	private final int heightShop;
	private final int marginTop;
	private final int marginLeft;
	
	private Color bgColor = Color.DARK_GRAY;
	private BufferedImage merchant;
	
	public UIShopRoom(ApplicationContext context, DataShopRoom data) {
		this.context = context;
		this.data = data;

		width = (int)context.getScreenInfo().getWidth();
		height = (int)context.getScreenInfo().getHeight();
		widthShop = width/2;
		heightShop = height*3/4;
		marginLeft = (width - widthShop) / 2;
		marginTop = (height - heightShop) / 2;
		
		merchant = FileManager.loadImage("pictures/others/Merchant.png");
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
		
		drawBackground(graphics);
		GraphicsHelper.drawImageContain(graphics, merchant, width-marginLeft, 0, width, height); // Affichage marchand
		drawCardsClassButtons(graphics);
		drawCardsColorlessButtons(graphics);
		drawPotionsButtons(graphics);
		data.getConfirmButton().display(graphics); // Affichage du bouton de confirmation d'achat
		data.getLeaveButton().display(graphics); // Affichage du bouton pour quitter le magasin
		drawCurrentMoney(graphics);
		drawSelectionInfos(graphics);
	}
	
	private void drawBackground(Graphics2D graphics) {
		Color save = graphics.getColor();
		graphics.setColor(bgColor);
		
		graphics.fillRect(marginLeft, marginTop, widthShop, heightShop);
		
		graphics.setColor(save);
	}

	private void drawCardsClassButtons(Graphics2D graphics) {
		GraphicsHelper.drawButtonArray(graphics, data.getCardsClassButtons());
		CardItem[] items = data.getShop().seeCardsClass();
		for (int i=0; i<items.length; i++) {
			if (items[i] != null) {
				GraphicsHelper.drawText(graphics, items[i].getPrice()+" or", 
						UI.LINE_SPACE, 
						marginLeft + UI.MARGIN_TEXT + widthShop*i/items.length, 
						marginTop + heightShop/3);
			} else {
				GraphicsHelper.drawText(graphics, "Vendu", 
						UI.LINE_SPACE, 
						marginLeft + UI.MARGIN_TEXT + widthShop*i/items.length, 
						marginTop + heightShop/3);
			}
		}
	}

	private void drawCardsColorlessButtons(Graphics2D graphics) {
		GraphicsHelper.drawButtonArray(graphics, data.getCardsColorlessButtons());
		CardItem[] items = data.getShop().seeCardsColorless();
		for (int i=0; i<items.length; i++) {
			if (items[i] != null) {
				GraphicsHelper.drawText(graphics, items[i].getPrice()+" or", 
						UI.LINE_SPACE, 
						marginLeft + UI.MARGIN_TEXT + (widthShop*2/5)*i/items.length, 
						marginTop + heightShop*5/6);
			} else {
				GraphicsHelper.drawText(graphics, "Vendu", 
						UI.LINE_SPACE, 
						marginLeft + UI.MARGIN_TEXT + (widthShop*2/5)*i/items.length, 
						marginTop + heightShop*5/6);
			}
		}
	}

	private void drawPotionsButtons(Graphics2D graphics) {
		GraphicsHelper.drawButtonArray(graphics, data.getPotionsButtons());
		PotionItem[] items = data.getShop().seePotions();
		int widthPotion = widthShop*2/(5*items.length);
		for (int i=0; i<items.length; i++) {
			if (items[i] != null) {
				GraphicsHelper.drawText(graphics, items[i].getPrice()+" or", 
						UI.LINE_SPACE, 
						marginLeft + (widthShop*2/5) + widthPotion * i + UI.MARGIN_TEXT, 
						marginTop + heightShop/2 + widthPotion);
			} else {
				GraphicsHelper.drawText(graphics, "Vendu", 
						UI.LINE_SPACE, 
						marginLeft + (widthShop*2/5) + widthPotion * i + UI.MARGIN_TEXT, 
						marginTop + heightShop/2 + widthPotion);
			}
		}
	}
	
	private void drawCurrentMoney(Graphics2D graphics) {
		int gold = data.getShop().getHero().getGold();
		
		GraphicsHelper.setFontSize(graphics, UI.FONT_SIZE*2);
		GraphicsHelper.setFontStyle(graphics, Font.BOLD);
		GraphicsHelper.drawText(graphics, "Argent : "+gold, UI.LINE_SPACE*2, marginLeft + widthShop + UI.MARGIN_TEXT, UI.MARGIN_TEXT);
		GraphicsHelper.setFontDefault(graphics);
	}
	
	private void drawSelectionInfos(Graphics2D graphics) {
		if (data.getSelectionText() != null) {
			GraphicsHelper.drawText(graphics, data.getSelectionText(), UI.LINE_SPACE*2, UI.MARGIN_TEXT, UI.MARGIN_TEXT, null, UI.FONT_SIZE*2);
		}
		if (data.getSelectionImage() != null) {
			GraphicsHelper.drawImageContain(graphics, data.getSelectionImage(), 0, marginTop, marginLeft, marginTop+heightShop);
		}
	}
}

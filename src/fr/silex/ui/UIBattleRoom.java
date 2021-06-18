package fr.silex.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import fr.silex.GraphicsHelper;
import fr.silex.data.DataBattleRoom;
import fr.silex.data.Datas.EnemyCategory;
import fr.silex.entity.Enemy;
import fr.silex.entity.Hero;
import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.ScreenInfo;

public class UIBattleRoom implements UI {

	private DataBattleRoom data;
	private ApplicationContext context;
	private int width;
	private int height;

	public UIBattleRoom(ApplicationContext context, DataBattleRoom data) {
		this.data = data;
		this.context = context;
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
		
		debug(graphics); // A supprimer ? ca rend plutot bien
		drawHero(graphics);
		drawPotions(graphics);
		drawLog(graphics);
		drawEndTurnButton(graphics);			
		drawHeroInfo(graphics);
		
		switch (data.getBattleState()) {
		case FIGHTING:
			drawEnemies(graphics);
			drawEnemiesInfo(graphics);
			drawDeckPiles(graphics);
			drawHand(graphics);
			break;
		case DEFEAT:
			// Rien.
			break;
		case LOOTING:
			if (data.getEnemyCategory() == EnemyCategory.BOSS) { // Affichage de victoire
				GraphicsHelper.drawText(graphics, "Vous avez terminé le jeu !\nBravo !", UI.LINE_SPACE*2, width/2 + UI.MARGIN_TEXT, height/4+UI.MARGIN_TEXT, null, UI.FONT_SIZE*2);
			} else {
				drawLoots(graphics);
				if (data.isChoosingCard()) drawCardsChoice(graphics);
			}
			break;
		default:
			break;
		}
		
	}

	/**
	 * Affichage de l'image du Hero.
	 */
	private void drawHero(Graphics2D graphics) {
		BufferedImage img = data.getHero().img();
		int posX = width*3/8 - img.getWidth()/2;
		int posY = height/2 - img.getHeight();
		
		GraphicsHelper.drawImage(graphics, img, posX, posY);
	}
	
	/**
	 * Affichage des images de tous les ennemies
	 */
	private void drawEnemies(Graphics2D graphics) {
		Enemy enemy;
		BufferedImage img;
		int originX;
		int nextOriginX;
		ArrayList<Enemy> enemies = data.getEnemies();
		for (int i=0; i < enemies.size(); i++) {
			enemy = enemies.get(i);
			img = enemy.img();
			originX = width/2 + (width/2)*i/enemies.size();
			nextOriginX = width/2 + (width/2)*(i+1)/enemies.size();
			
			GraphicsHelper.drawImageContainBottom(graphics, img, originX, 0, nextOriginX, height/2);
		}
	}

	/**
	 * Affichage des informations du Hero.
	 */
	private void drawHeroInfo(Graphics2D graphics) {
		Hero hero = data.getHero();
		ArrayList<String> infos = hero.getInformations();
		StringBuilder builder = new StringBuilder(hero.name());
		for (String info : infos) {
			builder.append("\n").append(info);
		}
		GraphicsHelper.drawText(graphics, builder.toString(), LINE_SPACE, width/4 + MARGIN_TEXT, height/2 + MARGIN_TEXT);
	}

	/**
	 * Affichage des informations de chaque ennemie.
	 */
	private void drawEnemiesInfo(Graphics2D graphics) {
		ArrayList<Enemy> enemies = data.getEnemies();
		for (int i=0; i<enemies.size(); i++) {
			Enemy enemy = enemies.get(i);
			ArrayList<String> infos = enemy.getInformations();
			StringBuilder builder = new StringBuilder(enemy.name());
			for (String info : infos) {
				builder.append("\n").append(info);
			}
			GraphicsHelper.drawText(graphics, builder.toString(), LINE_SPACE, width/2 + (width/2)*i/enemies.size() + MARGIN_TEXT, height/2 + MARGIN_TEXT);
		}
	}

	/**
	 * Affichage des cartes de la main du joueur.
	 */
	private void drawHand(Graphics2D graphics) {
		for (Button but : data.getHandButtons()) {
			but.display(graphics);
		}
	}

	/**
	 * Affichage de tout ce qui s'est passé en combat.
	 * NON IMPLEMENTE
	 */
	private void drawLog(Graphics2D graphics) {
		int size = 24; // Nombre de ligne a afficher.
		
		ArrayList<String> log = DataBattleRoom.getLog();
		StringBuilder builder = new StringBuilder();
		int start = 0;
		int end = log.size();
		if (log.size() > size) {
			start = log.size()-size;
		}
		
		for (int i=start; i<end; i++) {
			builder.append(log.get(i)).append("\n");
		}
		
		GraphicsHelper.drawText(graphics, builder.toString(), LINE_SPACE, MARGIN_TEXT, MARGIN_TEXT);
	}

	/**
	 * Affichage de la taille des différentes piles de cartes en combat.
	 * (pioche, défausse, cartes bannies)
	 */
	private void drawDeckPiles(Graphics2D graphics) {
		Hero hero = data.getHero();
		graphics.drawString("Draw pile : "+hero.deck().drawPileSize(), MARGIN_TEXT, height*9/12 + FONT_SIZE + MARGIN_TEXT);
		graphics.drawString("Discard pile : "+hero.deck().discardPileSize(), MARGIN_TEXT, height*10/12 + FONT_SIZE + MARGIN_TEXT);
		graphics.drawString("Exhaust pile : "+hero.deck().exhaustPileSize(), MARGIN_TEXT, height*11/12 + FONT_SIZE + MARGIN_TEXT);
	}

	/**
	 * Affiche le Button de fin de tour.
	 */
	private void drawEndTurnButton(Graphics2D graphics) {
		data.getEndButton().display(graphics);
		//GraphicsHelper.drawImageContain(graphics, endTurnButton, width*7/8, height*3/4, width, height);
	}

	/**
	 * Affichage des cartes de la main du joueur.
	 */
	private void drawPotions(Graphics2D graphics) {
		for (Button but : data.getPotionsButtons()) {
			if (but != null) {
				but.display(graphics);
			}
		}
		data.getUsePotionButton().display(graphics);
	}
	
	/**
	 * Affiche des différents loots.
	 */
	private void drawLoots(Graphics2D graphics) {
		Button gold = data.getGoldLootButton();
		Button potion = data.getPotionLootButton();
		Button cards = data.getCardLootButton();
		
		GraphicsHelper.drawText(graphics, "Butin des ennemies", UI.FONT_SIZE*2, width*5/8, 30, null, UI.FONT_SIZE*2);
		
		if (gold != null) gold.display(graphics);
		if (potion != null) potion.display(graphics);
		if (cards != null) cards.display(graphics);
	}
	
	/**
	 * Affiche le choix de carte lors de la phase de loot.
	 */
	private void drawCardsChoice(Graphics2D graphics) {
		for (Button card : data.getCardChoiceButton()) {
			card.display(graphics);
		}
		data.getSkipCardButton().display(graphics);
	}

	/**
	 * Affiche des lignes séparant les différentes zones d'affichages.
	 */
	private void debug(Graphics2D graphics) {
		graphics.setColor(Color.WHITE);
		graphics.drawLine(width/4, height/2, width, height/2); // ligne h milieu
		graphics.drawLine(0, height*3/4, width, height*3/4); // ligne h bas
		graphics.drawLine(width/2, 0, width/2, height*3/4); // ligne v milieu
		graphics.drawLine(width*7/8, height*3/4, width*7/8, height); // ligne h bas gauche pour les piles de cartes
		graphics.drawLine(width/8, height*3/4, width/8, height); // ligne h bas gauche pour les piles de cartes
		graphics.drawLine(0, height*10/12, width/8, height*10/12); // ligne pile 1
		graphics.drawLine(0, height*11/12, width/8, height*11/12); // ligne pile 1
		graphics.drawLine(width/4, 0, width/4, height*3/4); // ligne v player
		graphics.drawLine(width/4, height/8, width*2/4, height/8); // ligne potions
		int nbrEnemy = data.getEnemies().size();
		for (int i=1; i<nbrEnemy; i++) {
			graphics.drawLine(width/2 + (width/2)*i/nbrEnemy, 0, width/2 + (width/2)*i/nbrEnemy, height*3/4);
		}
	}
}

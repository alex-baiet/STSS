package fr.silex;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Objects;

import fr.silex.ui.Button;
import fr.silex.ui.UI;

/**
 * Contient des fonctions pour aider a dessiner diverses choses.
 */
public class GraphicsHelper {
	/**
	 * Affiche l'image avecsa taille par défaut.
	 */
	public static boolean drawImage(Graphics2D graphics, BufferedImage img, int x, int y) {
		return drawImage(graphics, img, x, y, 1, 1);
	}

	/**
	 * Affiche l'image.
	 */
	public static boolean drawImage(Graphics2D graphics, BufferedImage img, int x, int y, float sizeX, float sizeY) {
		try {
			AffineTransformOp transform = new AffineTransformOp(AffineTransform.getScaleInstance(sizeX, sizeY),
					AffineTransformOp.TYPE_BILINEAR);
			graphics.drawImage(img, transform, x, y);
		} catch (Exception e) {
			System.out.println("L'image n'a pas pu être affiché.");
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * Affiche l'image directement à partir des fichiers.
	 * A utiliser uniquement pour des tests rapide.
	 */
	public static boolean drawImage(Graphics2D graphics, String imgPath, int x, int y, float sizeX, float sizeY) {
		return drawImage(graphics, FileManager.loadImage(imgPath), x, y, sizeX, sizeY);
	}
	
	/**
	 * Affiche l'image dans la zone indiqué.
	 * L'image est contenu et est centré dans la zone indiquée.
	 * 
	 * @param graphics
	 * @param img : Image à afficher.
	 * @param ax : Côté gauche.
	 * @param ay : Côté droit.
	 * @param bx : Côté haut.
	 * @param by : Côté bas.
	 */
	public static void drawImageContain(Graphics2D graphics, BufferedImage img, int ax, int ay, int bx, int by) {
		int sizeX = bx - ax;
		int sizeY = by - ay;
		float sizeRatioX;
		float sizeRatioY;
		float ratio = (float)sizeX/(float)sizeY;
		if (img.getWidth() > img.getHeight()*ratio) {
			sizeRatioX = (float)sizeX/(float)img.getWidth();
			sizeRatioY = sizeRatioX;
			ay = (int)(ay + sizeY/2 - img.getHeight()*sizeRatioY/2);
		} else {
			sizeRatioY = (float)sizeY/(float)img.getHeight();
			sizeRatioX = sizeRatioY;
			ax = (int)(ax + sizeX/2 - img.getWidth()*sizeRatioX/2);
		}
		drawImage(graphics, img, ax, ay, sizeRatioX, sizeRatioY);
	}
	
	/**
	 * Affiche l'image dans la zone indiqué.
	 * L'image est contenu et est placé en bas dans la zone indiquée.
	 */
	public static void drawImageContainBottom(Graphics2D graphics, BufferedImage img, int ax, int ay, int bx, int by) {
		int sizeX = bx - ax;
		int sizeY = by - ay;
		float sizeRatioX;
		float sizeRatioY;
		float ratio = (float)sizeX/(float)sizeY;
		if (img.getWidth() > img.getHeight()*ratio) {
			sizeRatioX = (float)sizeX/(float)img.getWidth();
			sizeRatioY = sizeRatioX;
			ay = (int)(ay + sizeY - img.getHeight()*sizeRatioY);
		} else {
			sizeRatioY = (float)sizeY/(float)img.getHeight();
			sizeRatioX = sizeRatioY;
			ax = (int)(ax + sizeX/2 - img.getWidth()*sizeRatioX/2);
		}
		drawImage(graphics, img, ax, ay, sizeRatioX, sizeRatioY);
	}

	/**
	 * Affiche l'image dans la zone indiqué.
	 * la taille de l'image est redéfini pour remplir entièrement la zone, 
	 * et peut en déborder.
	 */
	public static void drawImageFill(Graphics2D graphics, BufferedImage img, int ax, int ay, int bx, int by) {
		int sizeX = bx - ax;
		int sizeY = by - ay;
		float sizeRatio;
		float ratio = (float)sizeX/(float)sizeY;
		
		if (img.getWidth() > img.getHeight()*ratio) {
			sizeRatio = (float)sizeY/(float)img.getHeight();
			ax = (int)(ax + sizeX/2 - (img.getWidth()*sizeRatio)/2);
		} else {
			sizeRatio = (float)sizeX/(float)img.getWidth();
			ay = (int)(ay + sizeY/2 - (img.getHeight()*sizeRatio)/2);
		}
		drawImage(graphics, img, ax, ay, sizeRatio, sizeRatio);
	}

	/**
	 * Affiche un texte composé de plusieurs ligne
	 */
	public static void drawText(Graphics2D graphics, String text, int lineSpace, int x, int y) {
		drawText(graphics, text, lineSpace, x, y, null, null);
	}

	public static void drawText(Graphics2D graphics, String text, int lineSpace, int x, int y, Color color) {
		drawText(graphics, text, lineSpace, x, y, color, null);
	}
	/**
	 * Affiche un texte composé de plusieurs ligne,
	 * avec la couleur indiquée.
	 */
	public static void drawText(Graphics2D graphics, String text, int lineSpace, int x, int y, Color color, Integer fontSize) {
		// Changement de la couleur du texte.
		Color saveColor = graphics.getColor();
		if (color != null) {
			graphics.setColor(color);
		} else {
			graphics.setColor(UI.TEXT_COLOR);
		}
		
		// Changement de taille de la police
		Font saveFont = graphics.getFont();
		if (fontSize != null) setFontSize(graphics, fontSize);
		
		// Affichage du texte.
		String[] lines = text.split("\n");
		for (int i=0; i<lines.length; i++) {
			graphics.drawString(lines[i], x, y+lineSpace*(i+1));
		}
		
		// Changement de la police par celle initial.
		graphics.setFont(saveFont);
		// Changement de la couleur par celle initial.
		graphics.setColor(saveColor);
	}

	/**
	 * Change la police du graphics par celle par défaut.
	 */
	public static void setFontDefault(Graphics2D graphics) {
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setFont(UI.FONT);
	}

	/**
	 * Prépare la police par défaut avec la taille indiquée.
	 */
	public static void setFontSize(Graphics2D graphics, int fontSize) {
		setFont(graphics, null, null, fontSize);
	}

	/**
	 * Prépare la police par défaut avec la taille indiquée.
	 */
	public static void setFontStyle(Graphics2D graphics, int fontStyle) {
		setFont(graphics, null, fontStyle, null);
	}

	/**
	 * Prépare la police par défaut avec la taille indiquée.
	 */
	public static void setFontName(Graphics2D graphics, String fontName) {
		setFont(graphics, fontName, null, null);
	}
	
	/**
	 * Prépare la police du système indiquée avec la taille indiquée.
	 * Remplacez toutes les valeurs par null pour pour mettre la police par défaut.
	 * (sauf pour graphics c'est obligatoire)
	 *
	 * @param fontName : Nom de la famille du font dans les dossier systeme.
	 * @param fontSyle : Style du font. voir les constantes dans Font pour les valeurs possibles.
	 * @param fontSize : Taille de la police.
	 */
	public static void setFont(Graphics2D graphics, String fontName, Integer fontStyle, Integer fontSize) {
		Objects.requireNonNull(graphics);
		Font actualFont = graphics.getFont();
		if (fontName == null) { fontName = actualFont.getFamily(); }
		if (fontStyle == null) { fontStyle = actualFont.getStyle(); }
		if (fontSize == null) { fontSize = actualFont.getSize(); }
		
		Font font = new Font(fontName, fontStyle, fontSize);
		graphics.setFont(font);
	}

	/**
	 * Affiche tous les boutons de l'array.
	 */
	public static void drawButtonArray(Graphics2D graphics, Button[] buttons) {
		for (Button button : buttons) {
			if (button != null) {
				button.display(graphics);
			}
		}
	}
	
	/**
	 * Affiche un carre de la couleur indiqué sans interferer avec la couleur des autres éléments affichés par la suite.
	 */
	public static void fillRect(Graphics2D graphics, int x, int y, int width, int height, Color color) {
		Color save = graphics.getColor();
		if (color != null) graphics.setColor(color);
		
		graphics.fillRect(x, y, width, height);
		
		graphics.setColor(save);
	}
	
}

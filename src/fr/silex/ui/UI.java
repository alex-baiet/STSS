package fr.silex.ui;

import java.awt.Color;
import java.awt.Font;

public interface UI {

	public static final int FONT_SIZE = 16; // Taille par défaut de la police.
	public static final int LINE_SPACE = FONT_SIZE+2; // Espace entre les lignes, utilisé avec GraphicsHelper2D.drawText
	public static final int MARGIN_TEXT = 8; // Marge du texte, a utilisé avec GraphicsHelper2D.drawText
	public static final Font FONT = new Font("Comic Sans MS", Font.PLAIN, FONT_SIZE); // Police par défaut du projet
	public static final Color TEXT_COLOR = Color.WHITE;
	
	public static final Color DEFAULT_BG = Color.BLACK; // Couleur par défaut du fond
	
	public static final int BUTTON_HEIGHT = 32; // Taille par défaut des Buttons
	public static final Color BUTTON_COLOR = Color.DARK_GRAY; // Couleur par défaut des boutons
	public static final Color BUTTON_CONTOUR = Color.WHITE; // Couleur par défaut des boutons
	public static final Color SELECT_COLOR = Color.LIGHT_GRAY; // Couleur des boutons sélectionnés
	
	/**
	 * Utilisé pour afficher chaque frame.
	 * @param graphics : graphique pour l'affichage
	 */
	public void draw();
	
}

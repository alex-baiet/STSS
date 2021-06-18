package fr.silex.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import fr.silex.GraphicsHelper;
import fr.silex.Map;
import fr.silex.RunManager;
import fr.silex.data.DataMap;
import fr.umlv.zen5.ApplicationContext;

public class UIMap implements UI {
	private final ApplicationContext context;
	private final Map map;
	private final DataMap data;
	private final Button[][] mapButs;
	private final Integer[][] nodePositions;
	private final HashMap<Integer, ArrayList<Integer>> nodeTransitions;

	private final int width;
	private final int height;
	private final int widthMap;
	private final int heightMap;
	private final int marginTop;
	private final int marginLeft;
	
	private Color bgColor = new Color(0xa7bcc5);
	
	public UIMap(ApplicationContext context, DataMap data) {
		this.context = context;
		map = Map.getInstance();
		this.data = data;
		mapButs = this.data.getMapButtons();
		nodePositions = map.getNodePositions();
		nodeTransitions = map.getNodeTransitions();
		
		width = RunManager.getWidthScreen();
		height = RunManager.getHeightScreen();
		widthMap = width*3/4;
		heightMap = height/3;
		marginLeft = (width-widthMap)/2;
		marginTop = (height-heightMap)/2;
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
		
		GraphicsHelper.fillRect(graphics, marginLeft, marginTop, widthMap, heightMap, bgColor);
		drawPlayerPosition(graphics);
		//drawTransitions(graphics);
		drawPaths(graphics);
		drawNodes(graphics);
	}

	/** Affiche la position du joueur sur la map. */
	private void drawPlayerPosition(Graphics2D graphics) {
		int playerNode = map.getPlayerPosition();
		for (int i = 0; i < nodePositions.length; i++) {
			for (int j = 0; j < nodePositions[i].length; j++) {
				if (nodePositions[i][j] != null && playerNode == nodePositions[i][j]) {
					GraphicsHelper.fillRect(graphics, 
							marginLeft + widthMap*j/nodePositions[i].length, 
							marginTop + heightMap*i/nodePositions.length, 
							widthMap/nodePositions[i].length, 
							heightMap/nodePositions.length, 
							Color.WHITE);
					return;
				}
			}
		}
	}
	
	private void drawNodes(Graphics2D graphics) {
		for (int i = 0; i < mapButs.length; i++) {
			for (int j = 0; j < mapButs[i].length; j++) {
				if (mapButs[i][j] != null) {
					mapButs[i][j].display(graphics);
				}
			}
		}
	}
	
	private void drawPaths(Graphics2D graphics) {
		for (int i = 0; i < nodePositions.length; i++) { // Pour chaque node existant
			for (int j = 1; j < nodePositions[i].length; j++) {
				if (nodePositions[i][j] != null) {
					for (int k=-1; k<=1; k++) { // Pour chaque node situé a gauche sur la carte
						if (i+k >= 0 && i+k < nodePositions.length
								&& nodeTransitions.get(nodePositions[i][j]).contains(nodePositions[i+k][j-1])) {
							graphics.drawLine(
									marginLeft + widthMap*(j-1)/nodePositions[i].length + widthMap/nodePositions[i].length/2,
									marginTop + heightMap*(i+k)/nodePositions.length + heightMap/nodePositions.length/2,
									marginLeft + widthMap*(j)/nodePositions[i].length + widthMap/nodePositions[i].length/2,
									marginTop + heightMap*(i)/nodePositions.length + heightMap/nodePositions.length/2);
						}
					}
				}
			}
		}
	}
}

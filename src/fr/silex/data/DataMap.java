package fr.silex.data;

import java.util.Arrays;

import fr.silex.Map;
import fr.silex.RoomType;
import fr.silex.RunManager;
import fr.silex.ui.Button;
import fr.umlv.zen5.Event;

public class DataMap implements Data {
	
	private final Button[][] mapButs;
	private final Map map;
	private final Integer[][] nodePositions;
	
	private final int width;
	private final int height;
	private final int widthMap;
	private final int heightMap;
	private final int marginTop;
	private final int marginLeft;
	
	public DataMap() {
		map = Map.getInstance();
		nodePositions = map.getNodePositions();
		mapButs = new Button[nodePositions.length][nodePositions[0].length];
		width = RunManager.getWidthScreen();
		height = RunManager.getHeightScreen();
		widthMap = width*3/4;
		heightMap = height/3;
		marginLeft = (width-widthMap)/2;
		marginTop = (height-heightMap)/2;
		
		loadMapButtons();
	}
	
	private void loadMapButtons() {
		int widthBut = widthMap/mapButs[0].length/2;
		int heightBut = heightMap/mapButs.length/2;
		
		for (int i=0; i<mapButs.length; i++) {
			for (int j=0; j<mapButs[i].length; j++) {
				if (nodePositions[i][j] != null) {
					if (map.type(nodePositions[i][j]) == RoomType.BOSS) {						
						mapButs[i][j] = new Button(
								marginLeft + widthMap*j/mapButs[i].length + widthMap/mapButs[i].length/2 - widthBut*3/2, 
								marginTop + heightMap*i/mapButs.length + heightMap/mapButs.length/2 - heightBut*3/2, 
								widthBut*3, 
								heightBut*3, 
								null, 
								null,
								map.type(nodePositions[i][j]).getIcon());
					} else {
						mapButs[i][j] = new Button(
								marginLeft + widthMap*j/mapButs[i].length + widthMap/mapButs[i].length/2 - widthBut/2, 
								marginTop + heightMap*i/mapButs.length + heightMap/mapButs.length/2 - heightBut/2, 
								widthBut, 
								heightBut, 
								null, 
								null,
								map.type(nodePositions[i][j]).getIcon());
					}
				}
			}
		}
	}
	
	/**
	 * Retourne où est-ce que le joueur veut aller
	 */
	@Override
	public RoomType resolvePlayerAction(Event event) {
		for (int i=0; i<mapButs.length; i++) {
			for (int j=0; j<mapButs[i].length; j++) {
				if (mapButs[i][j] != null
						&& mapButs[i][j].isClicked(event) 
						&& map.movePlayer(nodePositions[i][j])) {
					System.out.println("Salle choisie : " + map.type(nodePositions[i][j]));
					return map.type(nodePositions[i][j]);
				}
			}
		}
		
		return null;
	}
	
	public Button[][] getMapButtons() { 
		Button[][] copy = new Button[mapButs.length][];
		for (int i=0; i<copy.length; i++) {
			copy[i] = Arrays.copyOf(mapButs[i], mapButs[i].length);
		}
		return copy;
	}
}

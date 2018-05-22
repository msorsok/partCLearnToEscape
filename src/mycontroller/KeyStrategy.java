package mycontroller;

import java.util.ArrayList;

import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;

public class KeyStrategy implements PathStrategy{
	public ArrayList<Coordinate> findPath(GameState gameState){
		Coordinate dest = findDest(gameState);
		if (dest == null){
			return null;
		}
		// search
		ArrayList<Coordinate> path;
		
		// include logic for changing costs depending on current key and other squares etc.
		float lavaCost = 150 - gameState.carState.health;
		float healthCost = 5 - gameState.carState.health/20;
		float grassCost = 7;
		path = AStarSearch.findPath(gameState.carState.position, dest, lavaCost, healthCost, grassCost, gameState);

		if (willSurvive(path, gameState)){
			return path;
		}
		return null;
	}
	
	public Coordinate findDest(GameState gameState){
		Coordinate dest = null;
		for (Coordinate c: gameState.exploredMap.keySet()){
			if(!c.equals(gameState.carState.position)){
				MapTile t = gameState.exploredMap.get(c);
					if (gameState.currKey == 1){
						if (t.getType().equals(MapTile.Type.FINISH)){
							return c;
						}
					}
					else{
						if (t instanceof LavaTrap){
							if (((LavaTrap) t).getKey() == (gameState.currKey - 1)){
								return c;
							}
						}
					}
				}
			}
		return dest;
	}
	
	private Boolean willSurvive(ArrayList<Coordinate> path, GameState gameState){
		return gameState.carState.health - 2*lavaPathCost(path, gameState) > 0 ? true: false;
	}
	private float lavaPathCost(ArrayList<Coordinate> path, GameState gameState) {
		int lavaCrossed = 0;
		for(Coordinate c: path) {
			if(gameState.combinedMap.get(c) instanceof LavaTrap) {
				lavaCrossed ++;
			}
		}
		return lavaCrossed/gameState.maxSpeed * LavaTrap.HealthDelta;
	}
}

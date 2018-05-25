//group 118 Ben Burgess, Lucas Nash, and Mina Sorsok
package mycontroller;

import java.util.ArrayList;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class KeyStrategy implements PathStrategy{
	
	private final int FIXED_LAVA_COST = 3000;
	private final int FIXED_GRASS_COST = 120;
	private final int LOW_HEALTH = 60;
	private final int HIGH_HEALTH = 98;

	/**
	 * returns a path when we know where the next key is
	 */
	public ArrayList<Coordinate> findPath(GameState gameState){
		Coordinate dest = findDest(gameState);
		ArrayList<Coordinate> path;
		if (dest == null || dest.equals(gameState.carState.position)){
			return null;
		}
		
		// include logic for changing costs depending on current key and other squares etc.
		float lavaCost = FIXED_LAVA_COST - gameState.carState.health;
		float healthCost = gameState.carState.health;
		path = Search.findPath(gameState.carState.position, dest, lavaCost, healthCost, FIXED_GRASS_COST, gameState);
		// if we should go for key return path to key
		if (goForKey(path, gameState)){
			return path;
		}
		//if we shouldn't go for key check that the path to health has less lava on it 
		else {
			Coordinate nearestHealth = getNearestHealth(gameState);
			ArrayList<Coordinate> alternatePath = null;
			if(nearestHealth == null) {
				return path;
			}
			//dest is current position
			if (nearestHealth.equals(gameState.carState.position)){
				alternatePath = new ArrayList<>();
				alternatePath.add(nearestHealth);
				return alternatePath;	
			}
			
			alternatePath = Search.findPath(gameState.carState.position, nearestHealth, lavaCost, healthCost, FIXED_GRASS_COST, gameState);
			if(lavaCrossed(alternatePath, gameState) > lavaCrossed(path, gameState)) {
				return path;
			}
			
			return alternatePath;
		}
	}
	
	/**
	 * Returns the coordinate of the next key and null if we haven't found key yet
	 */
	public Coordinate findDest(GameState gameState){
		Coordinate dest = null;
		// Searches through explored map
		for (Coordinate c: gameState.exploredMap.keySet()){
			MapTile t = gameState.exploredMap.get(c);
			//if found all keys look for finish	
			if (gameState.currKey == 1){
				if (t.getType().equals(MapTile.Type.FINISH)){
					return c;
				}
			}
			// otherwise looking in lava traps for keys
			else{
				if (t instanceof LavaTrap){
					if (((LavaTrap) t).getKey() == (gameState.currKey - 1)){
						return c;
					}
				}
			}
		}
		return dest;
	}
	
	/**
	 * Uses heuristics to determine when to go for key, second part of statement tells you to stay on health if already there
	 */
	private Boolean goForKey(ArrayList<Coordinate> path, GameState gameState){
		return (gameState.carState.health > LOW_HEALTH && (gameState.carState.health > HIGH_HEALTH || !(gameState.combinedMap.get(gameState.carState.position) instanceof HealthTrap)));
	}
	
	/**
	 * checks through the map to see how many tiles in the path are lava and returns that number
	 */
	private float lavaCrossed(ArrayList<Coordinate> path, GameState gameState) {
		int lavaCrossed = 0;
		for(Coordinate c: path){
			if(gameState.combinedMap.get(c) instanceof LavaTrap) {
				lavaCrossed ++;
			}
		}
		return lavaCrossed;
	}
	
	/** 
	 * uses a variation of a breadth first search to return the nearest health tile
	 */
	private Coordinate getNearestHealth(GameState gameState) {
		ArrayList <Coordinate> reachable = new ArrayList<>();
		ArrayList <Coordinate> q = new ArrayList<>();
		Coordinate curr;
		q.add(gameState.carState.position);
		while(!q.isEmpty()){
			curr = q.remove(0);
			if (!reachable.contains(curr)){
				reachable.add(curr);
				for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
					Coordinate newCoordinate = null;
					switch(d) {
						case EAST:
							newCoordinate = new Coordinate(curr.x + 1, curr.y);
							break;
						case WEST:
							newCoordinate = new Coordinate(curr.x - 1, curr.y);
							break;
						case NORTH:
							newCoordinate = new Coordinate(curr.x, curr.y + 1);
							break;
						case SOUTH:
							newCoordinate = new Coordinate(curr.x, curr.y - 1);
							break;
						default:
					}
					
					MapTile newMapTile = gameState.combinedMap.get(newCoordinate);
					//if the map tile isn't a wall then add it to arraylist to explore later
					if(newMapTile != null && newMapTile.getType()!= MapTile.Type.WALL ){
						q.add(newCoordinate);
						// if its a health tile then return immediately
						if(newMapTile != null && newMapTile instanceof HealthTrap){
							return newCoordinate;
						}
					}	
				}			
			}
		}
		return null; //haven't found any health
	}
}

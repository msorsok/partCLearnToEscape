//group 118 Ben Burgess, Lucas Nash, and Mina Sorsok
package mycontroller;

import java.util.ArrayList;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class ExploreStrategy implements PathStrategy{
	private final int UNSEEN_WEIGHT = 1;
	private final int DISTANCE_WEIGHT = -1;
	private final int FIXED_LAVA_COST = 3000;
	private final int FIXED_GRASS_COST = 120;
	private final int LOW_UNSEEN_AMOUNT = 9;
	private final int LAVA_UTILITY = 1000;
	private final int NEAR_LAVA_UTILITY = 100;
	private final int ALL_SEEN_UTILITY = -2000;
	private final int HEALTH_UTILITY_BONUS = 10000;
	private final int HEALTH_DIST_UTILITY_BONUS = 1000;
	private final int HEALTH_UTILITY_FUNC_A = 400;
	private final int HEALTH_UTILITY_FUNC_B = 40;
	private final int HEALTH_UTILITY_FUNC_C = 1;
	private final int LOW_HEALTH = 50;
	private final int HIGH_HEALTH = 95;
	
	/**
	 * finds and returns the path that we should be exploring along
	 */
	public ArrayList<Coordinate> findPath(GameState gameState){
		ArrayList<Coordinate> path;
		Coordinate dest = null;
		dest = findDest(gameState);
		//dest is current position so no need to calculate path
		if (dest.equals(gameState.carState.position)){
			path = new ArrayList<>();
			path.add(dest);
			return path;
		}
		float lavaCost = FIXED_LAVA_COST - gameState.carState.health;
		float healthCost = gameState.carState.health;
		path = Search.findPath(gameState.carState.position, dest, lavaCost, healthCost, FIXED_GRASS_COST, gameState);
		return path;
	}
	
	/**
	 * finds the best destination and returns its coordinates by searching through all reachable tiles
	 * and calculating utility
	 */
	private Coordinate findDest(GameState gameState){
		Coordinate bestDest = null;
		float highestUtility = -Float.MAX_VALUE;
		float thisUtility;
		// find the tile in reachable without crossing lava the highest utility
		for(Coordinate dest: getReachable(gameState, false)) {
			thisUtility = calculateUtility(dest, gameState);
				if(thisUtility > highestUtility){
					bestDest = dest;
					highestUtility = thisUtility;
				}
		}
		// if there are very few tiles unseen at this dest then we might need to cross lava to explore more
		if (getUnseen(bestDest, gameState) < LOW_UNSEEN_AMOUNT){
			highestUtility = -Float.MAX_VALUE;
			for(Coordinate dest: getReachable(gameState, true)) { //includes things across lava in reachable
				thisUtility = calculateUtility(dest, gameState);
					if(thisUtility > highestUtility){
						bestDest = dest;
						highestUtility = thisUtility;
					}
			}
		}
		return bestDest;
	}
		
	/**
	 * returns the total utility assigned to each square
	 */
	private float calculateUtility(Coordinate dest, GameState gameState) {
		float totalUtility = 0;
		MapTile thisTile = gameState.combinedMap.get(dest);
		int unseen = getUnseen(dest, gameState);
		if(thisTile instanceof LavaTrap) {
			totalUtility -= LAVA_UTILITY;
		}
		// reward tiles near lava as we will see keys from them
		else if(getLava(dest, gameState) > 1 && unseen > 0) {
			unseen+=NEAR_LAVA_UTILITY;
		}
		totalUtility += UNSEEN_WEIGHT * unseen;
		float distance = getManhattanDistance(gameState.carState.position, dest);
		totalUtility += DISTANCE_WEIGHT * distance;
		//avoid setting dest as tile with unseen == 0
		if(unseen == 0) {
			totalUtility += ALL_SEEN_UTILITY;
		}
		if(thisTile instanceof HealthTrap){
			totalUtility += HEALTH_UTILITY_FUNC_A * Math.sin((-gameState.carState.health)/HEALTH_UTILITY_FUNC_B + HEALTH_UTILITY_FUNC_C) + HEALTH_UTILITY_FUNC_A;			
			if(gameState.carState.health < LOW_HEALTH){
				totalUtility+=HEALTH_UTILITY_BONUS;
				totalUtility += HEALTH_DIST_UTILITY_BONUS*(DISTANCE_WEIGHT * distance); // Close health tile better than one thats got unseens
			}
			else if(gameState.combinedMap.get(gameState.carState.position) instanceof HealthTrap && gameState.carState.health < HIGH_HEALTH) {
				//currently on health tile may as well stay
				totalUtility+=HEALTH_UTILITY_BONUS;
			}
		}
		return totalUtility;
	}
	
	/**
	 * returns the number of tiles that we have not yet seen that are visible from tile c
	 */
	private int getUnseen(Coordinate c, GameState gameState) {
		int unseen = 0;
		for(int x=-4;x<5;x++) {
			for(int y=-4;y<5;y++) {
				Coordinate newCoordinate = new Coordinate(c.x + x, c.y + y);
				if(!gameState.exploredMap.containsKey(newCoordinate)&&(gameState.combinedMap.containsKey(newCoordinate))&&(gameState.combinedMap.get(newCoordinate)).getType()!=MapTile.Type.EMPTY) {
	 				unseen ++;
				}
			}
		}
		return unseen;
	}
	
	/**
	 * calculates Manhattan Distance\
	 */
	private int getManhattanDistance(Coordinate src, Coordinate dest) {
		return Math.abs(src.x - dest.x) + Math.abs(src.y - dest.y);
	}
	
	/**
	 * returns the amount of lava surrounding a square
	 */
	private int getLava(Coordinate c, GameState gameState) {
		int lava = 0;
		for(int x=-1;x<2;x++) {
			for(int y=-1;y<2;y++) {
				Coordinate newCoordinate = new Coordinate(c.x + x, c.y + y);
				if(gameState.combinedMap.containsKey(newCoordinate)&&(gameState.combinedMap.get(newCoordinate)) instanceof LavaTrap) {
	 				lava ++;
				}
			}
		}
		return lava;
	}
	
	/**
	 * Returns a list of coordinates that are reachable found using a depth first search, 
	 * reachable will include things across lava if includeLava is true
	 */
	private ArrayList<Coordinate> getReachable(GameState gameState, Boolean includeLava){
		ArrayList <Coordinate> reachable = new ArrayList<>();
		ArrayList <Coordinate> q = new ArrayList<>();
		Coordinate curr;
		q.add(gameState.carState.position);
		// while q is no empty keep searching
		while(!q.isEmpty()){
			curr = q.remove(0);
			if (!reachable.contains(curr)){
				reachable.add(curr);
				//add the tile in each of the cardinal directions
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
							System.out.println("not a direction");	
							}
					
					MapTile newMapTile = gameState.combinedMap.get(newCoordinate);
					// if tile is in map, not a wall and its either not an instance of lava or we're including lava
					if(newMapTile != null && newMapTile.getType()!= MapTile.Type.WALL && (includeLava || !(newMapTile instanceof LavaTrap))){
						q.add(0, newCoordinate);
					}
				}			
			}
		}
		return reachable;
	}	
}


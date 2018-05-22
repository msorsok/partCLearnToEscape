package mycontroller;

import java.util.ArrayList;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

public class ExploreStrategy implements PathStrategy{
	private final int unseenWeight = 9;
	private final int distanceWeight = -1;
	
	
	public ArrayList<Coordinate> findPath(GameState gameState){
		ArrayList<Coordinate> path;
		Coordinate dest = null;
		dest = findDest(gameState);
		System.out.print("exploring dest is: ");
		System.out.println(dest);
		float lavaCost = 150 - gameState.carState.health;
		float healthCost = 5 - gameState.carState.health/20;
		float grassCost = 7;
		path = AStarSearch.findPath(gameState.carState.position, dest, lavaCost, healthCost, grassCost, gameState);
		System.out.print("exploring path is: ");
		System.out.println(path);
		return path;
	}
	
	private Coordinate findDest(GameState gameState){
		Coordinate bestDest = null;
		float highestUtility = -Float.MAX_VALUE;
		float thisUtility;
		for(Coordinate dest: getReachable(gameState)) {
			thisUtility = calculateUtility(dest, gameState);
				if(thisUtility > highestUtility){
					bestDest = dest;
					highestUtility = thisUtility;
				}
		}
		return bestDest;
	}
		
	
	private float calculateUtility(Coordinate dest, GameState gameState) {
		float totalUtility = 0;
		MapTile thisTile = gameState.combinedMap.get(dest);
		int unseen = getUnseen(dest, gameState);
		if(thisTile instanceof LavaTrap) {
			// only go to lava if everywhere else explored
			totalUtility -= 1000 ;
		}
		else if(getLava(dest, gameState) > 1 && unseen > 0) {
			// tiles near lava are worth 5 extra unseens
			unseen+=5;
		}
		totalUtility += unseenWeight * unseen;
		float distance = getManhattanDistance(gameState.carState.position, dest);
		totalUtility += distanceWeight * distance;
		
		if(unseen == 0) {
			//never set dest as tile with unseen == 0
			totalUtility -= 2000;
		}
		if(thisTile instanceof HealthTrap){
			totalUtility += 10000000/Math.pow(gameState.carState.health, 3);
		}
		return totalUtility;
	}
	
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
	
	private int getManhattanDistance(Coordinate src, Coordinate dest) {
		return Math.abs(src.x - dest.x) + Math.abs(src.y - dest.y);
	}
	
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
	
	private ArrayList<Coordinate> getReachable(GameState gameState){
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
							System.out.println("not a direction");	
							}
					
					MapTile newMapTile = gameState.combinedMap.get(newCoordinate);
					if(newMapTile != null && newMapTile.getType()!= MapTile.Type.WALL){
						q.add(0, newCoordinate);
					}
				}			
			}
		}
		reachable.remove(0);
		System.out.print("reachable.size()-----------");
		System.out.println(reachable.size());
		if (reachable.contains(null)){
			System.out.println("how?????");
			System.exit(0);
		}
		return reachable;
	}
}


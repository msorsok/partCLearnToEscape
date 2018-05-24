package mycontroller;

import java.util.ArrayList;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class KeyStrategy implements PathStrategy{
	public ArrayList<Coordinate> findPath(GameState gameState){
		Coordinate dest = findDest(gameState);
		if (dest == null || dest.equals(gameState.carState.position)){
			return null;
		}
		// search
		ArrayList<Coordinate> path;
		
		// include logic for changing costs depending on current key and other squares etc.
		float lavaCost = 3000 - gameState.carState.health;
		float healthCost = gameState.carState.health;
		float grassCost = 120;
		path = Search.findPath(gameState.carState.position, dest, lavaCost, healthCost, grassCost, gameState);

		if (willSurvive(path, gameState)){
			return path;
		}
		//go to closest health
		else {
			Coordinate nearestHealth = getNearestHealth(gameState);
			ArrayList<Coordinate> alternatePath = null;
			if(nearestHealth == null) {
				return path;
			}
			if (nearestHealth.equals(gameState.carState.position)){
				//dest is current position
				alternatePath = new ArrayList<>();
				alternatePath.add(nearestHealth);
				return alternatePath;
				
			}
			
			alternatePath = Search.findPath(gameState.carState.position, nearestHealth, lavaCost, healthCost, grassCost, gameState);
			if(lavaPathCost(alternatePath, gameState) > lavaPathCost(path, gameState)) {
				return path;
			}
			
			return alternatePath;
		}
	}
	
	public Coordinate findDest(GameState gameState){
		Coordinate dest = null;
		for (Coordinate c: gameState.exploredMap.keySet()){
			MapTile t = gameState.exploredMap.get(c);
				if (gameState.currKey == 1){
					if (t.getType().equals(MapTile.Type.FINISH)){
						return c;
					}
				}
				else{
					if (t instanceof LavaTrap){
						if (((LavaTrap) t).getKey() > 0){
							System.out.print("in exploredMap key is: ");
							System.out.println(((LavaTrap) t).getKey());
						}
						if (((LavaTrap) t).getKey() == (gameState.currKey - 1)){
							return c;
						}
					}
				}
			}
		return dest;
	}
	
	private Boolean willSurvive(ArrayList<Coordinate> path, GameState gameState){
		return (gameState.carState.health > 60 && (gameState.carState.health > 99 || !(gameState.combinedMap.get(gameState.carState.position) instanceof HealthTrap)));
	}
	private float lavaPathCost(ArrayList<Coordinate> path, GameState gameState) {
		int lavaCrossed = 0;
		for(Coordinate c: path){
			if(gameState.combinedMap.get(c) instanceof LavaTrap) {
				lavaCrossed ++;
			}
		}
		return lavaCrossed;
	}
	
	private Coordinate getNearestHealth(GameState gameState) {
		Coordinate bestHealth = null;
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
					if(newMapTile != null && newMapTile.getType()!= MapTile.Type.WALL ){
						q.add(0, newCoordinate);
						if(newMapTile != null && newMapTile instanceof HealthTrap){
							return newCoordinate;
						}
					}
					
				}			
			}
		}
		
		return bestHealth;
	}
}

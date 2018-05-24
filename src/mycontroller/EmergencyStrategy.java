package mycontroller;

import java.util.ArrayList;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class EmergencyStrategy implements PathStrategy{
	private final int DELTA_COUNT = 10;
	private int emergencyDeltaCount = 0;
	
	/**
	 * determines whether we are in an emergency situation and returns a path to resolve emergency
	 */
	public ArrayList<Coordinate> findPath(GameState gameState){
		ArrayList<Coordinate> path = null;
		
		//not moving, not currently in an emergency cycle, probably stuck, initiate emergency
		if (isZero(gameState.carState.speed) && isZero(gameState.carState.previousSpeed) && emergencyDeltaCount==0
				&& !gameState.lastPath.get(gameState.lastPath.size()-1).equals(gameState.carState.position)){
			gameState.isEmergency = true;
		}
		//cycle complete end emergency
		if (emergencyDeltaCount>DELTA_COUNT){
			gameState.isEmergency = false;
			emergencyDeltaCount = 0;
		}
		//continue emergency escape procedure 
		if(gameState.isEmergency){	
			path = new ArrayList<>();
			Coordinate curr = gameState.carState.position;
			int newX = curr.x;
			int newY = curr.y;
			Coordinate lastDest = gameState.lastPath.get(0);
			// check each direction to see which is the wall we're crashing into
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
				// Calculates where the "opposite" square to the one we're stuck on is 
				if (gameState.combinedMap.get(newCoordinate).getType().equals(MapTile.Type.WALL)){
					newX = 2*newX - newCoordinate.x;
					newY = 2*newY - newCoordinate.y;
					break;
				}
			}
			//no walls were found in cardinal directions need to check corners too
			if (newX==curr.x && newY==curr.y){
				for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
					Coordinate newCoordinate = null;
					switch(d) {
						case EAST:
							//actually northeast
							newCoordinate = new Coordinate(curr.x + 1, curr.y+1);
							break;
						case WEST:
							//actually southwest
							newCoordinate = new Coordinate(curr.x - 1, curr.y-1);
							break;
						case NORTH:
							//actually northwest
							newCoordinate = new Coordinate(curr.x-1, curr.y + 1);
							break;
						case SOUTH:
							//actually southeast
							newCoordinate = new Coordinate(curr.x+1, curr.y - 1);
							break;
						default:
							System.out.println("not a direction");	
							}
					// Calculates where the "opposite" square to the one we're stuck on is 
					if (gameState.combinedMap.get(newCoordinate).getType().equals(MapTile.Type.WALL)){
						newX = 2*newX - newCoordinate.x;
						newY = 2*newY - newCoordinate.y;
						break;
					}
				}
			}

			newX = 2*curr.x - lastDest.x;
			newY = 2*curr.y - lastDest.y;
			path.add(new Coordinate(newX, newY)); // Sets this "opposite" square as destination
			emergencyDeltaCount++;
			return path;
		}
		return path;
	}
	
	/**
	 * tests whether the speed is currently zero, accounting for floating point inaccuracies 
	 */
	private boolean isZero(float speed){
	    return speed <= 0.01;
	}
}

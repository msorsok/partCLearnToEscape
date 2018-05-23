package mycontroller;

import java.util.ArrayList;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class EmergencyStrategy implements PathStrategy{
	private final int DELTA_COUNT = 10;
	private int emergencyDeltaCount = 0;
	private boolean isEmergency = false;
	public ArrayList<Coordinate> findPath(GameState gameState){
		ArrayList<Coordinate> path = null;
		if (isZero(gameState.carState.speed) && isZero(gameState.carState.previousSpeed) && emergencyDeltaCount==0
				&& !gameState.lastPath.get(gameState.lastPath.size()-1).equals(gameState.carState.position)){
			//not moving, not currently in an emergency cycle, probably stuck, initiate emergency
			isEmergency = true;
		}
		if (emergencyDeltaCount>DELTA_COUNT){
			//cycle complete end emergency
			isEmergency = false;
			emergencyDeltaCount = 0;
		}
		if(isEmergency){
			
			path = new ArrayList<>();
			Coordinate curr = gameState.carState.position;
			int newX = curr.x;
			int newY = curr.y;
			Coordinate lastDest = gameState.lastPath.get(0);
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
				if (gameState.combinedMap.get(newCoordinate).getType().equals(MapTile.Type.WALL)){
					newX = 2*newX - newCoordinate.x;
					newY = 2*newY - newCoordinate.y;
					break;
				}
			}
			if (newX==curr.x && newY==curr.y){
				//no walls were found in cardinal directions need to check corners too
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
					if (gameState.combinedMap.get(newCoordinate).getType().equals(MapTile.Type.WALL)){
						newX = 2*newX - newCoordinate.x;
						newY = 2*newY - newCoordinate.y;
						break;
					}
				}
			}

			newX = 2*curr.x - lastDest.x;
			newY = 2*curr.y - lastDest.y;
			path.add(new Coordinate(newX, newY));
			emergencyDeltaCount++;
			return path;
		}
		return path;
	}
	
	private boolean isZero(double speed){
	    return speed >= -0.01 && speed <= 0.01;
	}
}

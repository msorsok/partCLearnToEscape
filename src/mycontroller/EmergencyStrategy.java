package mycontroller;

import java.util.ArrayList;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class EmergencyStrategy implements PathStrategy{
	public ArrayList<Coordinate> findPath(GameState gameState){
		ArrayList<Coordinate> path = null;
		if (isZero(gameState.carState.previousSpeeds.get(gameState.carState.previousSpeeds.size()-1)) && isZero(gameState.carState.previousSpeeds.get(gameState.carState.previousSpeeds.size()-2))){
			path = new ArrayList<>();
			Coordinate curr = gameState.carState.position;
			int newX;
			int newY;
			Coordinate lastDest = gameState.lastPath.get(0);
			newX = 2*curr.x - lastDest.x;
			newY = 2*curr.y - lastDest.y;
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
					newX = 2*gameState.carState.position.x - newCoordinate.x;
					newY = 2*gameState.carState.position.y - newCoordinate.y;
					break;
				}
			}
			path.add(new Coordinate(newX, newY));
			return path;
		}
		return path;
	}
	
	private boolean isZero(double speed){
	    return speed >= -0.01 && speed <= 0.01;
	}
}

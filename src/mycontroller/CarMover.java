package mycontroller;

import java.util.ArrayList;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class CarMover {
	private final float WALL_MARGIN = 0.2f;
	
	/**
	 * Returns a list of booleans instructing the car which way to turn and accelerate
	 */
	public ArrayList<Boolean> getInstructions(ArrayList<Coordinate> path, GameState gameState){
		ArrayList<Boolean> instructions = new ArrayList<>();
		Coordinate firstDest = path.get(0);
		// if our destination is where we currently are we don't need to do anything
		if (path.get(0).equals(gameState.carState.position)){
			instructions.add(null);
			instructions.add(null);
			return instructions;
		}
		// adjust our destination away from the walls to avoid getting stuck
		ArrayList<Float> adjustedDest  = adjustAwayFromWall(firstDest, gameState);
		float maxSpeed = PhysicsCalculations.findMaxSpeed(gameState.carState.position.x, gameState.carState.position.y,
				adjustedDest.get(0), adjustedDest.get(1), gameState.carState.angle);  // maximum speed we want to be traveling in for this situation
		instructions.add(PhysicsCalculations.acceleratingForward(gameState.isEmergency)); // add acceleration instruction
		instructions.add(PhysicsCalculations.getTurningRight(gameState.carState.position.x, gameState.carState.position.y,
				adjustedDest.get(0), adjustedDest.get(1), gameState.carState.angle, instructions.get(0))); //add turning instruction

		boolean closeToHealthGoal = false;
		if(gameState.combinedMap.get(path.get(path.size()-1)) instanceof HealthTrap && path.size() < 3 && gameState.carState.speed > 0.5) {
			closeToHealthGoal = true;
		}
		// if we are not close to health goal and we're under speed limit we're free to accelerate, if on lava no speed limit
		if (((!closeToHealthGoal && gameState.carState.speed < maxSpeed) || gameState.combinedMap.get(gameState.carState.position) instanceof LavaTrap)){
			
		}
		// if above conditions aren't true then we want to slow down
		else {
			ArrayList<Boolean> newInstructions = new ArrayList<>();
			newInstructions.add(false);
			newInstructions.add(instructions.get(1));
			return newInstructions;
		}
		return instructions;
	}
	
	/**
	 * returns two floats representing x and y coordinates of the destination once we adjust it away from nearby walls
	 */
	private ArrayList<Float> adjustAwayFromWall(Coordinate c, GameState gameState){
		ArrayList<Float> output = new ArrayList<>();
		float newX = c.x;
		float newY = c.y;
		// check each of the directions and if its a wall then add the margin to stay away from them
		for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
			Coordinate newCoordinate;
			switch(d) {
				case EAST:
					newCoordinate = new Coordinate(c.x + 1, c.y);
					if(gameState.combinedMap.get(newCoordinate) != null && gameState.combinedMap.get(newCoordinate).getType() == MapTile.Type.WALL){
						newX-=WALL_MARGIN;
					}
					break;
				case WEST:
					newCoordinate = new Coordinate(c.x - 1, c.y);
					if(gameState.combinedMap.get(newCoordinate) != null && gameState.combinedMap.get(newCoordinate).getType() == MapTile.Type.WALL){
						newX+=WALL_MARGIN;
					}
					break;
				case NORTH:
					newCoordinate = new Coordinate(c.x, c.y + 1);
					if(gameState.combinedMap.get(newCoordinate) != null && gameState.combinedMap.get(newCoordinate).getType() == MapTile.Type.WALL){
						newY-=WALL_MARGIN;
					}
					break;
				case SOUTH:
					newCoordinate = new Coordinate(c.x, c.y - 1);
					if(gameState.combinedMap.get(newCoordinate) != null && gameState.combinedMap.get(newCoordinate).getType() == MapTile.Type.WALL){
						newY+=WALL_MARGIN;
					}
					break;
				default:
			}
		}
		output.add(newX);
		output.add(newY);
		return output;	
	}

}

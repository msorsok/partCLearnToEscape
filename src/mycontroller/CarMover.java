package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class CarMover {
	
	public static ArrayList<Boolean> getInstructions(ArrayList<Coordinate> path, GameState gameState){
		ArrayList<Boolean> instructions = new ArrayList<>();
		Coordinate firstDest = path.get(0);
		instructions.add(PhysicsCalculations.acceleratingForward(gameState.carState.position.x, gameState.carState.position.y,
				firstDest.x, firstDest.y, gameState.carState.angle));
		instructions.add(PhysicsCalculations.getTurningRight(gameState.carState.position.x, gameState.carState.position.y,
				firstDest.x, firstDest.y, gameState.carState.angle, gameState.carState.previousSpeeds.get(gameState.carState.previousSpeeds.size() - 1)));
		return instructions;
	}

}

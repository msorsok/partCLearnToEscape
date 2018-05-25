//group 118 Ben Burgess, Lucas Nash, and Mina Sorsok
package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController{
	
	private GameState gameState; // stores the state of the game
	private PathFinder pathFinder; // creates paths for the car to follow
	private CarMover carMover; // creates commands to move the car
	
	/**
	 * Constructor instialises the MyAIController object
	 */
	public MyAIController(Car car) {
		super(car);
		this.pathFinder = new PathFinder();
		this.gameState = new GameState(getPosition(), getAngle(), getSpeed(), getHealth(), getKey());
		this.carMover = new CarMover();
	}

	@Override
	/**
	 * makes updates to the cars movement after each time step of the simulation
	 */
	public void update(float delta) {
		HashMap<Coordinate, MapTile> currentView = getView();
		gameState.updateGameState(currentView, getPosition(), getAngle(), getSpeed(), getHealth(), getKey());
		ArrayList<Coordinate> path = pathFinder.findPath(gameState);
		ArrayList<Boolean> instructions = carMover.getInstructions(path, gameState);
		// Checks whether our instructions have an acceleration command to implement
		if (instructions.get(1) == null){
		}
		else if (instructions.get(1)){
			turnRight(delta);
		}
		else{
			turnLeft(delta);
		}
		
		// Checks whether our instructions have a turning command to implement
		if (instructions.get(0) == null){
		}
		else if(instructions.get(0)){
				applyForwardAcceleration();
		}
		else{
				applyReverseAcceleration();
		}
	}
}
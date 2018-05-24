package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;


public class MyAIController extends CarController{
	
	// Car Speed to move at
	private GameState gameState;
	private PathFinder pathFinder;
	private CarMover carMover;
	
	
	public MyAIController(Car car) {
		super(car);
		this.pathFinder = new PathFinder();
		this.gameState = new GameState(getPosition(), getAngle(), getSpeed(), getHealth(), getKey());
		this.carMover = new CarMover();
	}

	@Override
	public void update(float delta) {
		HashMap<Coordinate, MapTile> currentView = getView();
		gameState.updateGameState(currentView, getPosition(), getAngle(), getSpeed(), getHealth(), getKey());
		ArrayList<Coordinate> path = pathFinder.findPath(gameState);
		System.out.println(gameState.combinedMap.get(path.get(0)).getType());
		System.out.print("path: ");
		System.out.println(path);
		ArrayList<Boolean> instructions = carMover.getInstructions(path, gameState);
		//System.out.println(instructions);

		if (instructions.get(1) == null){
		}
		else if (instructions.get(1)){
			turnRight(delta);
		}
		else{
			turnLeft(delta);
		}
		
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
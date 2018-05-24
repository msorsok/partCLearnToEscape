package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class PathFinder implements PathStrategy {
	
	PathStrategy keyStrategy;
	PathStrategy emergencyStrategy;
	PathStrategy exploreStrategy;
	
	/**
	 * Constructor instialises the path finder object
	 */
	public PathFinder(){
		this.keyStrategy = new KeyStrategy();
		this.emergencyStrategy = new EmergencyStrategy();
		this.exploreStrategy = new ExploreStrategy();
	}
	
	/**
	 * chooses the appropriate strategy and returns the path we want to take as an array list of coordinates
	 */
	public ArrayList<Coordinate> findPath(GameState gameState){
		ArrayList<Coordinate> path = emergencyStrategy.findPath(gameState);
		// Checks if an emergency path was found
		if (path != null){
			return path;
		}
		
		path = keyStrategy.findPath(gameState);
		// Checks if a path to a key can be found
		if (path != null){
			gameState.updateLastPath(path);
			return path;
		}
		
		// If no other paths are found then generate and explore path
		path = exploreStrategy.findPath(gameState);
		// Stores the last path
		gameState.updateLastPath(path);
		return path;
	}
}

package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class PathFinder implements PathStrategy {
	
	PathStrategy keyStrategy;
	PathStrategy emergencyStrategy;
	PathStrategy exploreStrategy;
	
	public PathFinder(){
		this.keyStrategy = new KeyStrategy();
		this.emergencyStrategy = new EmergencyStrategy();
		this.exploreStrategy = new ExploreStrategy();
	}
	
	public ArrayList<Coordinate> findPath(GameState gameState){
		ArrayList<Coordinate> path = emergencyStrategy.findPath(gameState);
		if (path != null){
			System.out.print("Emergency--");
			return path;
		}
		path = keyStrategy.findPath(gameState);
		if (path != null){
			System.out.print("Key--");
			gameState.updateLastPath(path);
			return path;
		}
		System.out.print("Explore--");
		path = exploreStrategy.findPath(gameState);
		gameState.updateLastPath(path);
		return path;
	}
}

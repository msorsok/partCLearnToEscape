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
			System.out.println("doing emergency");
			return path;
		}
		path = keyStrategy.findPath(gameState);
		if (path != null){
			System.out.println("doing key");
			return path;
		}
		System.out.println("doing explore");
		return exploreStrategy.findPath(gameState);
	}
}

package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class Search {
	public static ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest, float lavaCost, float healthCost, float grassCost, GameState gameState){
		return AStarSearch.findPath(src, dest, lavaCost, healthCost, grassCost, gameState);
	}
}

//group 118 Ben Burgess, Lucas Nash, and Mina Sorsok
package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public class Search {
	/**
	 * Finds shortest path between two points for given weightings of different tiles
	 */
	public static ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest, float lavaCost, float healthCost, float grassCost, GameState gameState){
		return AStarSearch.findPath(src, dest, lavaCost, healthCost, grassCost, gameState);
	}
}

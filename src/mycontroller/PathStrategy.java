//group 118 Ben Burgess, Lucas Nash, and Mina Sorsok
package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public interface PathStrategy {
	
	/**
	 * returns the path we want to take as an array list of coordinates
	 */
	public abstract ArrayList<Coordinate>  findPath(GameState gameState);

}

package mycontroller;

import java.util.ArrayList;

import utilities.Coordinate;

public interface PathStrategy {
	
	public abstract ArrayList<Coordinate>  findPath(GameState gameState);

}

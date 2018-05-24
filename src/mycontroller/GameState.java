package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;

public class GameState {
	CarState carState;
	HashMap<Coordinate, MapTile> exploredMap; // map of what we've seen
	HashMap<Coordinate, MapTile> combinedMap; // composite of map we were originally provided and what we've discovered 
	int currKey; //key we're currently looking for
	ArrayList<Coordinate> lastPath; //the path we were traveling on in the last time step
	boolean isEmergency; //if its currently an emergency
	
	/**
	 * Constructor instialises the game state
	 */
	public GameState(String position, float angle, float speed, float health, int currKey){
		this.carState = new CarState(position, angle, health, speed);
		this.currKey = currKey;
		isEmergency = false;
		this.exploredMap = new HashMap<>();
		this.combinedMap = new HashMap<>();
		// Go through the world map and add all the walls to both maps
		for (Coordinate c: World.getMap().keySet()){
			if(World.getMap().get(c).getType().equals(MapTile.Type.WALL)){
				this.exploredMap.put(c, World.getMap().get(c));
				this.combinedMap.put(c, World.getMap().get(c));
			}
			// Also add all the roads to the combined map
			if(World.getMap().get(c).getType().equals(MapTile.Type.ROAD)) {
				this.combinedMap.put(c, World.getMap().get(c));
			}
		}
		this.lastPath = new ArrayList<>();
		lastPath.add(carState.position);
	}
	
	/**
	 * updates the game state with any changes that have occurred over the last time step
	 */
	public void updateGameState(HashMap<Coordinate, MapTile> view, String position, float angle, float speed, float health, int currKey){
		this.carState.updateCarState(position, angle, speed, health);
		this.currKey = currKey;
		
		//updating both maps with new view
		for (Coordinate c: view.keySet()){
			if (!(this.exploredMap.containsKey(c)) && !(view.get(c).getType().equals(MapTile.Type.EMPTY))){
				this.exploredMap.put(c, view.get(c));
			}
			if(!(view.get(c).getType().equals(MapTile.Type.EMPTY))){
				this.combinedMap.remove(c);
				this.combinedMap.put(c, view.get(c));
			}
		}	
	}
	
	/**
	 * updates the last path separately to everything else as a path must be generated after the new path is found
	 */
	public void updateLastPath(ArrayList<Coordinate> lastPath){
		this.lastPath = lastPath;
	}
	
	
}

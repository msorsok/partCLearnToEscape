package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;

public class GameState {
	CarState carState;
	HashMap<Coordinate, MapTile> exploredMap;
	HashMap<Coordinate, MapTile> combinedMap;
	int currKey;
	float maxSpeed;
	ArrayList<Coordinate> lastPath;
	
	public GameState(String position, float angle, float speed, float health, int currKey, float maxSpeed){
		this.carState = new CarState(position, angle, health, speed);
		this.currKey = currKey;
		this.maxSpeed = maxSpeed;
		this.exploredMap = new HashMap<>();
		this.combinedMap = new HashMap<>();
		for (Coordinate c: World.getMap().keySet()){
			if(World.getMap().get(c).getType().equals(MapTile.Type.WALL)){
				this.exploredMap.put(c, World.getMap().get(c));
				this.combinedMap.put(c, World.getMap().get(c));
			}
			if(World.getMap().get(c).getType().equals(MapTile.Type.ROAD)) {
				this.combinedMap.put(c, World.getMap().get(c));
			}
		}
		this.lastPath = new ArrayList<>();
		lastPath.add(carState.position);
	}
	
	public void updateGameState(HashMap<Coordinate, MapTile> view, String position, float angle, float speed, float health, int currKey){
		//update car
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
	
	public void updateLastPath(ArrayList<Coordinate> lastPath){
		this.lastPath = lastPath;
	}
	
	
}

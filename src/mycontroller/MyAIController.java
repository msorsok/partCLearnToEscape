package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import controller.CarController;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;



public class MyAIController extends CarController{
	
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 2;
	private int currKey = 4;
	public HashMap<Coordinate, MapTile> map; 
	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	
	// Car Speed to move at
	private final float CAR_SPEED = 3f;
	
	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;
	public MyAIController(Car car) {
		super(car);
		initialiseMap();
	}
	Coordinate initialGuess;
	boolean notSouth = true;

	@Override
	public void update(float delta) {
		System.out.println(delta);
		HashMap<Coordinate, MapTile> currentView = getView();
		updateMap(currentView);
		Coordinate src = new Coordinate(getPosition());
		Coordinate dest;
		if (getDestKey() != null){
			dest = getDestKey();
		}
		else{
			dest = getDestination(getReachable(src), src);
		}
		
		if(getSpeed() < CAR_SPEED){
			applyForwardAcceleration();
		}
		
		ArrayList<Coordinate> path = findPath(src, dest);		
		ArrayList<Float> destCoordinates  = adjustAwayFromWall(path.get(0));
		System.out.println(path.get(0));
		System.out.println(destCoordinates);
		System.out.println(getX());
		System.out.println(getY());
		WorldSpatial.RelativeDirection direction = PhysicsCalculations.getTurningDirection(getX(), getY(),  destCoordinates.get(0), destCoordinates.get(1), getAngle());
		if (direction == null){
		}
		else if((direction).equals(WorldSpatial.RelativeDirection.LEFT)){
			turnLeft(delta);
		}
		else if ((direction).equals(WorldSpatial.RelativeDirection.RIGHT)){
			turnRight(delta);
		}
	}
	
	private ArrayList<Coordinate> getReachable(Coordinate src){
		ArrayList <Coordinate> reachable = new ArrayList<>();
		ArrayList <Coordinate> q = new ArrayList<>();
		Coordinate curr;
		q.add(src);
		while(!q.isEmpty()){
			curr = q.remove(0);
			if (!reachable.contains(curr)){
				reachable.add(curr);
				for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
					Coordinate newCoordinate = null;
					switch(d) {
						case EAST:
							newCoordinate = new Coordinate(curr.x + 1, curr.y);
							break;
						case WEST:
							newCoordinate = new Coordinate(curr.x - 1, curr.y);
							break;
						case NORTH:
							newCoordinate = new Coordinate(curr.x, curr.y + 1);
							break;
						case SOUTH:
							newCoordinate = new Coordinate(curr.x, curr.y - 1);
							break;
						default:
							System.out.println("not a direction");	
							}
					
					MapTile newMapTile = this.map.get(newCoordinate);
					if(newMapTile != null && newMapTile.getType() != MapTile.Type.WALL){
						q.add(newCoordinate);
					}
				}			
			}
		}
		return reachable;
	}
	
	private ArrayList<Float> adjustAwayFromWall(Coordinate c){
		ArrayList<Float> output = new ArrayList<>();
		float newX = c.x;
		float newY = c.y;
		for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
			Coordinate newCoordinate;
			switch(d) {
				case EAST:
					newCoordinate = new Coordinate(c.x + 1, c.y);
					if(map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newX-=0.5;
					}
					break;
				case WEST:
					newCoordinate = new Coordinate(c.x - 1, c.y);
					if(map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newX+=0.5;
					}
					break;
				case NORTH:
					newCoordinate = new Coordinate(c.x, c.y + 1);
					if(map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newY-=0.5;
					}
					break;
				case SOUTH:
					newCoordinate = new Coordinate(c.x, c.y - 1);
					if(map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newY+=0.5;
					}
					break;
				default:
					System.out.println("not a direction");	
			}
		}
		output.add(newX);
		output.add(newY);
		return output;	
	}
		
	
	/** 
	 * Searches the map to determine which is the most useful coordinate to visit next
	 * @param src the coordinate we're travelling from
	 * @return The coordinate we want to travel to
	 */
	private Coordinate getDestination(ArrayList<Coordinate> reachable, Coordinate src) {
		Coordinate bestDest = src;
		float highestUtility = -1;
		for(Coordinate c: reachable) {
				int unseen = getUnseen(c);
				float distance = getEuclideanDistance(src, c);
				MapTile thisTile = map.get(c);
				float thisUtility = calculateUtility(unseen, distance, thisTile);
				if(thisUtility > highestUtility) {
					bestDest = c;
					highestUtility = thisUtility;
				}
		}
		return bestDest;
	}
	
	private float calculateUtility(int unseen, float distance, MapTile tile) {
		float totalUtility = 0;
		int unseenWeight = 10;
		int distanceWeight = 2;
		
		totalUtility += unseenWeight * unseen;
		totalUtility += distanceWeight * distance;
		if(tile.getType().equals(MapTile.Type.ROAD)) {
			totalUtility += 10;
		}
		else if(tile.getType().equals(MapTile.Type.TRAP)) {
			if( ((TrapTile) tile).getTrap().equals("health")){
				totalUtility += 11;
			}
		}
		
		return totalUtility;
	}
	
	private float getEuclideanDistance(Coordinate src, Coordinate dest) {
		return (float) Math.sqrt(Math.pow(Math.abs(src.x - dest.x), 2) + Math.pow(Math.abs(src.y - dest.y), 2));
	}
	
	private int getUnseen(Coordinate c) {
		int unseen = 0;
		for(int x=-4;x<5;x++) {
			for(int y=-4;y<5;y++) {
				Coordinate newCoordinate = new Coordinate(c.x + x, c.y + y);
				if(!map.containsKey(newCoordinate)&&(World.getMap().containsKey(newCoordinate))&&(World.getMap().get(newCoordinate)).getType()!=MapTile.Type.EMPTY) {
	 				unseen ++;
				}
				newCoordinate = null;
			}
		}
		return unseen;
	}
	
	private void initialiseMap(){
		map = new HashMap<>();
		for (Coordinate c: World.getMap().keySet()){
			if(World.getMap().get(c).equals(MapTile.Type.WALL)){
				map.put(c, World.getMap().get(c));
			}
		}
	}
	
	private void updateMap(HashMap<Coordinate, MapTile> view){
		for (Coordinate c: view.keySet()){
			if (!(map.containsKey(c)) && !(view.get(c).getType().equals(MapTile.Type.EMPTY))){
				map.put(c, view.get(c));
			}
		}
	}
	
	private Coordinate getDestKey(){
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate dest = null;
		for (Coordinate c: currentView.keySet()){
			MapTile t = currentView.get(c);
			if (t instanceof LavaTrap){
				if (((LavaTrap) t).getKey() == currKey){
					return c;
				}
			}
		}
		System.out.println(dest);
		return dest;
	}
	
	private ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest){
		ArrayList<AStarNode> path = new ArrayList<>();
		ArrayList<AStarNode> open = new ArrayList<>();
		HashMap<Coordinate, AStarNode> openHashMap = new HashMap<>();
		HashMap<Coordinate, AStarNode> closedHashMap = new HashMap<>();
		
		AStarNode root = new AStarNode(map, src, null, 0, dest);
		AStarNode curr;
		open.add(root);
		openHashMap.put(src, root);
		
		while(!open.isEmpty()){
			open.sort(AStarNode.NodeComparator);
			curr = open.remove(0);
			openHashMap.remove(curr.coordinate);
			closedHashMap.put(curr.coordinate, curr);
			if (curr.coordinate.equals(dest)){
				// found dest
				return curr.tracePath();
			}
			
			for(AStarNode succ: curr.getSuccessors()){
				if (closedHashMap.containsKey(succ.coordinate)){
					continue;
				}
				if(!openHashMap.containsKey(succ.coordinate)){
					open.add(succ);
					openHashMap.put(succ.coordinate, succ);
				}					
				if (succ.getCostFromStart() > openHashMap.get(succ.coordinate).getCostFromStart()){
					continue;
				}
				if (closedHashMap.containsKey(succ.coordinate) && succ.getCostFromStart() > closedHashMap.get(succ.coordinate).getCostFromStart()){
					continue;
				}
				open.remove(openHashMap.get(succ.coordinate));
				openHashMap.remove(curr.coordinate);
				open.add(succ);
				openHashMap.put(succ.coordinate,succ);
			}
		}
		return null;
	}
}

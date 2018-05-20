package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import controller.CarController;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;


public class MyAIController extends CarController{
	
	public HashMap<Coordinate, MapTile> map; 

	// Car Speed to move at
	private float carSpeed = 8f;
	
	public MyAIController(Car car) {
		super(car);
		initialiseMap();
	}
	Coordinate initialGuess;
	boolean notSouth = true;

	@Override
	public void update(float delta) {
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
		
		boolean reversing = getSpeed() < 0 ? true : false;
		ArrayList<Coordinate> path = findPath(src, dest);
		if (path.get(0).equals(src)){
			path.remove(0);
		}
		ArrayList<Float> destCoordinates  = adjustAwayFromWall(path.get(0));
		
		if (PhysicsCalculations.acceleratingForward(getX(), getY(),  destCoordinates.get(0), destCoordinates.get(1), getAngle())){
			if(getSpeed() < carSpeed){
				applyForwardAcceleration();
			}
		}
		else{
			if(getSpeed() > -carSpeed){
				applyReverseAcceleration();
			}
		}
		WorldSpatial.RelativeDirection direction = PhysicsCalculations.getTurningDirection(getX(), getY(),  destCoordinates.get(0), destCoordinates.get(1), getAngle(), reversing);
		if (direction == null){
		}
		else if((direction).equals(WorldSpatial.RelativeDirection.LEFT)){
			turnLeft(delta);
		}
		else if ((direction).equals(WorldSpatial.RelativeDirection.RIGHT)){
			turnRight(delta);
		}
	}
	
	private void applyBrakes(Coordinate src){
		ArrayList<Coordinate> check = new ArrayList<>();
		if (0 <= getAngle() && getAngle() < 90){
			check.add(new Coordinate(src.x+1, src.y));
			check.add(new Coordinate(src.x+1, src.y+1));
			check.add(new Coordinate(src.x, src.y+1));
		}
		else if (90 <= getAngle() && getAngle() < 180){
			check.add(new Coordinate(src.x, src.y+1));
			check.add(new Coordinate(src.x-1, src.y+1));
			check.add(new Coordinate(src.x-1, src.y));
		}
		else if (180 <= getAngle() && getAngle() < 270){
			check.add(new Coordinate(src.x-1, src.y));
			check.add(new Coordinate(src.x-1, src.y-1));
			check.add(new Coordinate(src.x, src.y-1));
		}
		else {
			check.add(new Coordinate(src.x, src.y-1));
			check.add(new Coordinate(src.x+1, src.y-1));
			check.add(new Coordinate(src.x, src.y+1));
		}
		
		for(Coordinate c: check){
			if (map.get(c).getType()==MapTile.Type.WALL){
				carSpeed = 1f;
				//applyReverseAcceleration();
			}
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
		reachable.remove(0);
		if (reachable.contains(null)){
			System.out.println("how?????");
			System.exit(0);
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
		Coordinate bestDest = null;
		float highestUtility = -Float.MAX_VALUE;
		
		for(Coordinate c: reachable) {
				int unseen = getUnseen(c);
				if (unseen == 0){
					continue;
				}
				float distance = getEuclideanDistance(src, c);
				MapTile thisTile = map.get(c);
				float thisUtility = calculateUtility(unseen, distance, thisTile);
				if(thisUtility > highestUtility){
					bestDest = c;
					highestUtility = thisUtility;
					System.out.println(bestDest);
				}
		}
		return bestDest;
	}
	
	private float calculateUtility(int unseen, float distance, MapTile tile) {
		float totalUtility = 0;
		int unseenWeight = 1;
		int distanceWeight = -2;
		
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
			if(!c.equals(new Coordinate(getPosition()))){
				MapTile t = currentView.get(c);
				if (t instanceof LavaTrap){
					if (((LavaTrap) t).getKey() == (getKey() - 1)){
						return c;
					}
				}
			}
		}
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
			//System.out.println(open);
			//System.out.println(closedHashMap);
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

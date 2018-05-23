package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;


public class MyAIController extends CarController{
	
	// Car Speed to move at
	private float CAR_SPEED = 3f;
	private final float RESET_DELTAS = 5;
	
	private boolean previousTurningRight = true;
	private float previousHealth;
	private int resetDeltaCount;
	private boolean isResetting = false;
	
	private GameState gameState;
	private PathFinder pathFinder;
	private CarMover carMover;
	
	
	public MyAIController(Car car) {
		super(car);
		this.pathFinder = new PathFinder();
		this.gameState = new GameState(getPosition(), getAngle(), getSpeed(), getHealth(), getKey(), CAR_SPEED);
		this.carMover = new CarMover();
	}

	@Override
	public void update(float delta) {
		HashMap<Coordinate, MapTile> currentView = getView();
		gameState.updateGameState(currentView, getPosition(), getAngle(), getSpeed(), getHealth(), getKey());
		ArrayList<Coordinate> path = pathFinder.findPath(gameState);
		System.out.println(gameState.combinedMap.get(path.get(0)).getType());
		System.out.print("path: ");
		System.out.println(path);
		ArrayList<Boolean> instructions = carMover.getInstructions(path, gameState);
		//System.out.println(instructions);
		boolean goingForHealth = false;
		if(gameState.combinedMap.get(path.get(path.size()-1)) instanceof HealthTrap && path.size() < 2 && getSpeed() > 0.1) {
			goingForHealth = true;
		}
		if (instructions.get(1) == null){
		}
		else if (instructions.get(1)){
			turnRight(delta);
		}
		else{
			turnLeft(delta);
		}
		
		if (instructions.get(0) == null){
		}
		else if(instructions.get(0)){
			if ((( !goingForHealth && getSpeed()<CAR_SPEED)|| gameState.combinedMap.get(gameState.carState.position) instanceof LavaTrap)){
				applyForwardAcceleration();
			}
		}
		else{
			if (getSpeed()<CAR_SPEED || gameState.combinedMap.get(gameState.carState.position) instanceof LavaTrap){
				applyReverseAcceleration();
			}
			
		}
	}
	
	private ArrayList<Coordinate> findStraightPathOut(Coordinate startingCoodinate){
		int xIncrement = 0;
		int yIncrement = 0;
		ArrayList<Coordinate> newPath = new ArrayList<>();
		switch(getOrientation()) {
		case EAST:
			xIncrement = 1;
			break;
		case WEST:
			xIncrement = -1;
			break;
		case NORTH:
			yIncrement = 1;
			break;
		case SOUTH:
			yIncrement = -1;
			break;
		default:
			System.out.println("not a direction");	
			}
		System.out.println("xincrement" + xIncrement);
		System.out.println("yincrement" + yIncrement);

		if(xIncrement != 0 || yIncrement != 0) {
			Coordinate coordToAdd = new Coordinate( startingCoodinate.x + xIncrement, startingCoodinate.y + yIncrement);
			System.out.println("First new coordinate to test is " + coordToAdd);	

			boolean reachesRoad = false;
			while(map.containsKey(coordToAdd) && reachesRoad == false && !map.get(coordToAdd).getType().equals(MapTile.Type.WALL)) {
				newPath.add(coordToAdd);
				if(map.get(coordToAdd).getType().equals(MapTile.Type.ROAD)){
					reachesRoad = true;
				}
				coordToAdd = new Coordinate(coordToAdd.x+xIncrement, coordToAdd.y+yIncrement);
						
			}
			if(reachesRoad) {
				return newPath;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	private boolean lavaBeforeKeyLava(ArrayList<Coordinate> path) {
		boolean lavaFlag = false;
		for(Coordinate c: path) {
			if(map.containsKey(c) && map.get(c) instanceof LavaTrap) {
				lavaFlag = true;
			}
			if(lavaFlag) {
				if(map.containsKey(c) && !(map.get(c) instanceof LavaTrap)) {
					return true;
				}
			}
		}
		return false;
	}
	private int checkPathForLava(ArrayList<Coordinate> path, HashMap<Coordinate, MapTile> mapSearching) {
		int lavaCrossed = 0;
		for(Coordinate c: path) {
			if(mapSearching.get(c) instanceof LavaTrap) {
				lavaCrossed ++;
			}
		}
		return lavaCrossed;
	}
	
	private float getNextTurnDistance(ArrayList<Coordinate> turningPath) {
		Coordinate first = turningPath.get(0);
		Coordinate turnPoint = first;
		for(int i = 1; i < turningPath.size(); i++ ) {
			if(Math.abs(first.x - turningPath.get(i).x) + Math.abs(first.y - turningPath.get(i).y) > 1) {
				turnPoint = turningPath.get(i);
				break;
			}
		}
		return getEuclideanDistance(first, turnPoint);
	}
	
	private boolean isStationary(double speed){
	    return speed >= -0.1 && speed <= 0.1;
	}
	
	private void initialiseReset(){
		System.out.println("doing a reset");
		isResetting = true;
		resetDeltaCount = 0;
	}
	private void reset(float delta){
		double avgSpeed =  previousSpeeds.stream().mapToDouble(val -> val).average().getAsDouble();
		if (resetDeltaCount < RESET_DELTAS){
			resetDeltaCount++;
			if (previousTurningRight){
				turnLeft(delta);
			}
			else{
				turnRight(delta);
			}
			if (avgSpeed > 0){
				applyReverseAcceleration();
			}
			else{
				applyForwardAcceleration();				
			}

		}
		else{
			isResetting = false;
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
					if(newMapTile != null && newMapTile.getType()!= MapTile.Type.WALL){
						q.add(0, newCoordinate);
					}
				}			
			}
		}
		reachable.remove(0);
		System.out.print("reachable.size()-----------");
		System.out.println(reachable.size());
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
					if(map.get(newCoordinate) != null && map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newX-=WALL_MARGIN;
					}
					break;
				case WEST:
					newCoordinate = new Coordinate(c.x - 1, c.y);
					if(map.get(newCoordinate) != null && map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newX+=WALL_MARGIN;
					}
					break;
				case NORTH:
					newCoordinate = new Coordinate(c.x, c.y + 1);
					if(map.get(newCoordinate) != null && map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newY-=WALL_MARGIN;
					}
					break;
				case SOUTH:
					newCoordinate = new Coordinate(c.x, c.y - 1);
					if(map.get(newCoordinate) != null && map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newY+=WALL_MARGIN;
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
				float distance = getEuclideanDistance(src, c);
				MapTile thisTile = map.get(c);
				float thisUtility = calculateUtility(unseen, distance, thisTile, c);

				if(thisUtility > highestUtility){
					bestDest = c;
					highestUtility = thisUtility;
				}
		}
		return bestDest;
	}
	
	private float calculateUtility(int unseen, float distance, MapTile tile, Coordinate c) {
		float totalUtility = 0;
		int unseenWeight = 1;
		int distanceWeight = -1;
		
		totalUtility += unseenWeight * unseen;
		if(unseen == 0) {
			totalUtility -= 2000;
		}
		totalUtility += distanceWeight * distance;
		if(tile instanceof LavaTrap) {
			totalUtility -= 1000 ;
		}
		else if(tile instanceof HealthTrap){
			totalUtility += Math.pow(100 - getHealth(), 2);
			//System.out.println("health tile being considered");
			//System.out.print("the utility of this tile is: ");
			//System.out.println(totalUtility);
		}
		else {
			if(getLava(c) > 1 && unseen > 0) {
				totalUtility += 1000;
			}
		}
		return totalUtility;
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
	private int getLava(Coordinate c) {
		int lava = 0;
		for(int x=-1;x<2;x++) {
			for(int y=-1;y<2;y++) {
				Coordinate newCoordinate = new Coordinate(c.x + x, c.y + y);
				if(map.containsKey(newCoordinate)&&(map.get(newCoordinate)) instanceof LavaTrap) {
	 				lava ++;
				}
			}
		}
		return lava;
	}
	private void initialiseMap(){
		map = new HashMap<>();
		alternateMap = new HashMap<>();
		for (Coordinate c: World.getMap().keySet()){
			if(World.getMap().get(c).getType().equals(MapTile.Type.WALL)){
				map.put(c, World.getMap().get(c));
				alternateMap.put(c, World.getMap().get(c));
			}
			if(World.getMap().get(c).getType().equals(MapTile.Type.ROAD)) {
				alternateMap.put(c, World.getMap().get(c));
			}
		}
	}
	
	private void updateMap(HashMap<Coordinate, MapTile> view){
		for (Coordinate c: view.keySet()){
			if (!(map.containsKey(c)) && !(view.get(c).getType().equals(MapTile.Type.EMPTY))){
				map.put(c, view.get(c));
			}
			
			if(!(view.get(c).getType().equals(MapTile.Type.EMPTY)) && !(view.get(c).getType().equals(MapTile.Type.ROAD) && (view.get(c).getType().equals(MapTile.Type.WALL))) ) {
				alternateMap.remove(c);
				alternateMap.put(c, view.get(c));
			}
			
		}

	}
	
	private Coordinate getDestKey(){
		Coordinate dest = null;
		for (Coordinate c: map.keySet()){
			if(!c.equals(new Coordinate(getPosition()))){
				MapTile t = map.get(c);
				if (getKey() == 1){
					if (t.getType().equals(MapTile.Type.FINISH)){
						return c;
					}
				}
				else{
					if (t instanceof LavaTrap){
						if (((LavaTrap) t).getKey() == (getKey() - 1)){
							return c;
						}
					}
				}
			}
		}
		return dest;
	}
	
	private ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest, HashMap<Coordinate, MapTile> mapSearching){
		ArrayList<AStarNode> path = new ArrayList<>();
		ArrayList<AStarNode> open = new ArrayList<>();
		HashMap<Coordinate, AStarNode> openHashMap = new HashMap<>();
		HashMap<Coordinate, AStarNode> closedHashMap = new HashMap<>();
		double health = getHealth()/20;
		
		AStarNode root = new AStarNode(mapSearching, src, null, 0, dest, health);
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
				//System.out.print("--------------------");
				//System.out.print(curr.getCost());
				//System.out.print("--");
				//System.out.print(curr.tracePath().size());
				//System.out.print("--");
				//System.out.println("--------------------");
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

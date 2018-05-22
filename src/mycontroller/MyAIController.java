package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import controller.CarController;
import tiles.HealthTrap;
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
	private float CAR_SPEED = 3f;
	private final float WALL_MARGIN = 0.3f;
	private final float RESET_DELTAS = 10;
	
	private ArrayList<Double> previousSpeeds;
	private boolean previousTurningRight = true;
	private float previousHealth;
	private int resetDeltaCount;
	private boolean isResetting = false;
	
	
	public MyAIController(Car car) {
		super(car);
		initialiseMap();
		previousSpeeds = new ArrayList<>();
		previousSpeeds.add((double)-1);
	}
	Coordinate initialGuess;
	boolean notSouth = true;

	@Override
	public void update(float delta) {
		HashMap<Coordinate, MapTile> currentView = getView();
		updateMap(currentView);
		Coordinate currCoordinates = new Coordinate(getPosition());
		MapTile currTile = map.get(currCoordinates);
		//System.out.print("getkey: ");
		//System.out.println(getKey());
		if (isResetting){
			reset(delta);
		}		
		else{
			if (currTile instanceof HealthTrap && getHealth() < 95){
				if (isStationary(getSpeed())){
					
				}
				else if (getSpeed()<0){
					applyForwardAcceleration();
				}
				else {
					applyReverseAcceleration();
				}
			}
			else{
				Coordinate dest;
				if ((dest = getDestKey()) != null && getHealth() > 50){
					if (dest!=null){
						System.out.println("going to get a key now");
					}
				}
				else{
					dest = getDestination(getReachable(currCoordinates), currCoordinates);
				}
				if (dest == null){
					System.out.println("no where to go!!!!!!");
					return;
				}
				boolean reversing = getSpeed() < 0 ? true : false;
				System.out.print("src: ");
				System.out.println(currCoordinates);
				System.out.print("dest: ");
				System.out.println(dest);
				if (currCoordinates == null || dest == null){
					System.out.println("a null src or dest ");
				}
				if (map.get(dest) instanceof LavaTrap){
					System.out.println("dest is lava");
				}
				ArrayList<Coordinate> path = findPath(currCoordinates, dest);
				System.out.println(path);
				if (path.get(0).equals(currCoordinates)){
					path.remove(0);
				}
				ArrayList<Float> destCoordinates  = adjustAwayFromWall(path.get(0));
				Boolean isAcceleratingForward = PhysicsCalculations.acceleratingForward(getX(), getY(),  destCoordinates.get(0), destCoordinates.get(1), getAngle());
				Boolean isTurningRight = PhysicsCalculations.getTurningRight(getX(), getY(),  destCoordinates.get(0), destCoordinates.get(1), getAngle(), reversing);
				System.out.print("getSpeed(): ");
				System.out.println(getSpeed());
				System.out.print("previousSpeed: ");
				System.out.println(previousSpeeds.get(0));
				if (getSpeed() == 0 && isStationary(previousSpeeds.get(0))){
					System.out.println("starting a new reset");
					initialiseReset();
					reset(delta);
				}
				else if(map.get(path.get(0)) instanceof HealthTrap && getHealth() < 60 && Math.abs(getSpeed()) > 1 ){
					System.out.println("**************close to  health");
					if (isTurningRight != null){
						if(isTurningRight){
							previousTurningRight = true;
							turnRight(delta);
						}
						else{
							previousTurningRight = false;
							turnLeft(delta);
						}	
					}
				}
				else{
					if (isAcceleratingForward){
						if(getSpeed() < CAR_SPEED){
							applyForwardAcceleration();
						}
					}
					else{
						if(getSpeed() > -CAR_SPEED){
							applyReverseAcceleration();
						}
					}
					if (isTurningRight != null){
						if(isTurningRight){
							previousTurningRight = true;
							turnRight(delta);
						}
						else{
							previousTurningRight = false;
							turnLeft(delta);
						}	
					}
				}
			}
			previousSpeeds.add((double)getSpeed());
			if (previousSpeeds.size()>5){
				previousSpeeds.remove(0);
			}
		}
	}
	/*
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
	*/
	
	private boolean isStationary(double speed){
	    return speed >= -0.1 && speed <= 0.1;
	}
	
	private void initialiseReset(){
		System.out.println("doing a reset");
		isResetting = true;
		resetDeltaCount = 0;
		double avgSpeed =  previousSpeeds.stream().mapToDouble(val -> val).average().getAsDouble();
		if (avgSpeed == 0){
			previousSpeeds.add(Math.random() - 0.5);
		}
	}
	private void reset(float delta){
		double avgSpeed =  previousSpeeds.stream().mapToDouble(val -> val).average().getAsDouble();
		System.out.println(avgSpeed);
		System.out.println(previousSpeeds);
		if (resetDeltaCount < RESET_DELTAS){
			resetDeltaCount++;
			if (avgSpeed > 0){
				System.out.println("trying to backup");
				applyReverseAcceleration();
			}
			else if (avgSpeed < 0){
				System.out.println("trying to move forward");
				applyForwardAcceleration();				
			}
			if (previousTurningRight){
				turnLeft(delta);
			}
			else{
				turnRight(delta);
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
		//System.out.print("reachable.size()-----------");
		//System.out.println(reachable.size());
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
						newX-=WALL_MARGIN;
					}
					break;
				case WEST:
					newCoordinate = new Coordinate(c.x - 1, c.y);
					if(map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newX+=WALL_MARGIN;
					}
					break;
				case NORTH:
					newCoordinate = new Coordinate(c.x, c.y + 1);
					if(map.get(newCoordinate).getType() == MapTile.Type.WALL){
						newY-=WALL_MARGIN;
					}
					break;
				case SOUTH:
					newCoordinate = new Coordinate(c.x, c.y - 1);
					if(map.get(newCoordinate).getType() == MapTile.Type.WALL){
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
				float thisUtility = calculateUtility(unseen, distance, thisTile);
				if(thisUtility > highestUtility){
					bestDest = c;
					highestUtility = thisUtility;
				}
		}
		return bestDest;
	}
	
	private float calculateUtility(int unseen, float distance, MapTile tile) {
		float totalUtility = 0;
		int unseenWeight = 1;
		int distanceWeight = -5;
		
		totalUtility += unseenWeight * unseen;
		totalUtility += distanceWeight * distance;
		if(unseen==0){
			totalUtility-=20;
		}
		if(tile instanceof LavaTrap) {
			totalUtility -= 10000 ;
		}
		if(tile instanceof HealthTrap){
			totalUtility += 100 - getHealth() ;
			//System.out.println("health tile being considered");
			//System.out.print("the utility of this tile is: ");
			//System.out.println(totalUtility);
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
	
	private ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest){
		ArrayList<AStarNode> path = new ArrayList<>();
		ArrayList<AStarNode> open = new ArrayList<>();
		HashMap<Coordinate, AStarNode> openHashMap = new HashMap<>();
		HashMap<Coordinate, AStarNode> closedHashMap = new HashMap<>();
		double health = getHealth()/20;
		AStarNode root = new AStarNode(map, src, null, 0, dest, health);
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
				System.out.print("--------------------");
				System.out.print(curr.getCost());
				System.out.print("--");
				System.out.print(curr.tracePath().size());
				System.out.print("--");
				System.out.println("--------------------");
				return curr.tracePath();
			}
			ArrayList<AStarNode> successors;
			if (curr.coordinate.equals(src)){
				successors = curr.getRootSuccessors(getAngle());
			}
			else{
				 successors = curr.getSuccessors();
			}
			
			for(AStarNode succ: successors){
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
				open.remove(openHashMap.get(succ.coordinate));
				openHashMap.remove(curr.coordinate);
				open.add(succ);
				openHashMap.put(succ.coordinate,succ);
			}
		}
		return null;
	}
}

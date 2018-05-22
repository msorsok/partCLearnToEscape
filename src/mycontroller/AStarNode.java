package mycontroller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class AStarNode extends Node{
	private AStarNode pathParent;
	private double costFromStart;
	private double estimatedCostToGoal;
	private HashMap<Coordinate, MapTile> map;
	private Coordinate dest;
	private double health;
	
	public AStarNode(HashMap<Coordinate, MapTile> map, Coordinate coordinate, AStarNode parent, double costFromStart, Coordinate dest, double health){
		super(coordinate, map.get(coordinate));
		this.map = map;
		this.pathParent = parent;
		this.costFromStart = costFromStart;
		this.estimatedCostToGoal = heuristic(coordinate, dest);
		this.dest = dest;
		this.health = health;
	}
	
	public static Comparator<AStarNode> NodeComparator = new Comparator<AStarNode>() {
		public int compare(AStarNode n1, AStarNode n2){
			return (int)Math.round(n1.getCost() - n2.getCost());
	}};
	
	public double getCostFromStart(){
		return costFromStart;
	}
	
	public double getCost() {
		return costFromStart + estimatedCostToGoal;
	}

	public int compareTo(Object other) {
		double thisValue = this.getCost();
		double otherValue = ((AStarNode)other).getCost();
		double v = thisValue - otherValue;
		return (v>0)?1:(v<0)?-1:0; // sign function
	}

	public double getEstimatedCost(AStarNode node){
		return this.estimatedCostToGoal;
	}

	public double heuristic(Coordinate coordinate, Coordinate dest){
		// straight line distance scaled up by 5
		return 5 * Math.pow(Math.pow((coordinate.x - dest.x),2) + Math.pow((coordinate.y - dest.y),2), 0.5);
	}
	
	public ArrayList<AStarNode> getRootSuccessors(double angle){
		ArrayList<AStarNode> successors = new ArrayList<>();
		double eastCost = 0;
		double westCost = 0;
		double northCost = 0;
		double southCost = 0;
		//System.out.print("the angle: ");
		//System.out.println(angle);
		if (angle < 45 && angle > 315){
			westCost+=1;
		}
		else if (angle>45 && angle<135){
			southCost+=1;
		}
		else if(angle>135 && angle <225){
			eastCost+=1;
		}
		else{
			northCost+=1;
		}		
		for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
			Coordinate newCoordinate = null;
			double rootCost = this.costFromStart;
			switch(d) {
				case EAST:
					newCoordinate = new Coordinate(this.coordinate.x + 1, this.coordinate.y);
					rootCost += eastCost;
					break;
				case WEST:
					newCoordinate = new Coordinate(this.coordinate.x - 1, this.coordinate.y);
					rootCost += westCost;
					break;
				case NORTH:
					newCoordinate = new Coordinate(this.coordinate.x, this.coordinate.y + 1);
					rootCost += northCost;
					break;
				case SOUTH:
					newCoordinate = new Coordinate(this.coordinate.x, this.coordinate.y - 1);
					rootCost += southCost;
					break;
				default:
					System.out.println("not a direction");	
			}
			//System.out.print("the root cost: ");
			//System.out.println(rootCost);
			MapTile newMapTile = this.map.get(newCoordinate);
			if(newMapTile != null){
				switch(newMapTile.getType()){
					case WALL:
						break;
					case TRAP:
						switch(((TrapTile) this.map.get(newCoordinate)).getTrap()){
							case "lava":
								successors.add(new AStarNode(this.map, newCoordinate, this, rootCost + 10000 + Math.pow(5 - this.health, 2), this.dest, this.health));
								break;
							case "health":
								successors.add(new AStarNode(this.map, newCoordinate, this, rootCost + this.health, this.dest, this.health));
								break;
							case "grass":
								successors.add(new AStarNode(this.map, newCoordinate, this, rootCost + 4, this.dest, this.health));
								break;
							}
						break;
					default:
						successors.add(new AStarNode(this.map, newCoordinate, this, rootCost + 5, this.dest, this.health));
				}
			}
		}
		return successors;	
	}
	
	
	public ArrayList<AStarNode> getSuccessors(){
		ArrayList<AStarNode> successors = new ArrayList<>();
		for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
			Coordinate newCoordinate = null;
			switch(d) {
				case EAST:
					newCoordinate = new Coordinate(this.coordinate.x + 1, this.coordinate.y);
					break;
				case WEST:
					newCoordinate = new Coordinate(this.coordinate.x - 1, this.coordinate.y);
					break;
				case NORTH:
					newCoordinate = new Coordinate(this.coordinate.x, this.coordinate.y + 1);
					break;
				case SOUTH:
					newCoordinate = new Coordinate(this.coordinate.x, this.coordinate.y - 1);
					break;
				default:
					System.out.println("not a direction");	
			}
			
			MapTile newMapTile = this.map.get(newCoordinate);
			if(newMapTile != null){
				switch(newMapTile.getType()){
					case WALL:
						break;
					case TRAP:
						switch(((TrapTile) this.map.get(newCoordinate)).getTrap()){
							case "lava":
								System.out.print("---------------lava cost from start: ");
								System.out.println(this.costFromStart + 10000 + Math.pow(5 - this.health, 2));
								successors.add(new AStarNode(this.map, newCoordinate, this, this.costFromStart + 10000 + Math.pow(5 - this.health, 2), this.dest, this.health));
								break;
							case "health":
								successors.add(new AStarNode(this.map, newCoordinate, this, this.costFromStart + this.health, this.dest, this.health));
								break;
							case "grass":
								successors.add(new AStarNode(this.map, newCoordinate, this, this.costFromStart + 4, this.dest, this.health));
								break;
							}
						break;
					default:
						successors.add(new AStarNode(this.map, newCoordinate, this, this.costFromStart + 5, this.dest, this.health));
				}
			}
		}
		for (AStarNode succ: successors){
			//System.out.println(succ.coordinate);
		}
		return successors;	
	}
	
	public ArrayList<Coordinate> tracePath(){
		AStarNode curr = this;
		ArrayList<Coordinate> path = new ArrayList<>();
		while(curr != null){
			if (map.get(curr.coordinate) instanceof LavaTrap){
				//System.out.print("lava part of the path, cost: ");
				//System.out.println(curr.costFromStart);
			}
			else{
				//System.out.print("other tile cost: ");
				//System.out.println(curr.costFromStart);
			}
			path.add(0, curr.coordinate);
			curr = curr.pathParent;
		}		
		return path;
	}
}


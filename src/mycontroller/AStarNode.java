package mycontroller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class AStarNode extends Node{
	AStarNode pathParent;
	double costFromStart;
	double estimatedCostToGoal;
	HashMap<Coordinate, MapTile> map;
	Coordinate dest;
	
	public AStarNode(HashMap<Coordinate, MapTile> map, Coordinate coordinate, AStarNode parent, double costFromStart, Coordinate dest){
		super(coordinate, map.get(coordinate));
		this.map = map;
		this.pathParent = parent;
		this.costFromStart = costFromStart;
		this.estimatedCostToGoal = heuristic(coordinate, dest);
		this.dest = dest;
	}
	
	public static Comparator<AStarNode> NodeComparator = new Comparator<AStarNode>() {
		public int compare(AStarNode n1, AStarNode n2){
			return (int)Math.round(n2.getCost() - n1.getCost());
	}};
	
	
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
		return Math.pow(Math.pow((coordinate.x - dest.x),2) + Math.pow((coordinate.x - dest.x),2), 0.5);
	}
	
	public ArrayList<AStarNode> getSuccessors(){
		ArrayList<AStarNode> successors = new ArrayList<>();
		for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
			switch(d) {
				Coordinate newCoordinate;
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
				
							
							
					}
			switch(this.map.get(newCoordinate).getType()){
			case WALL:
				break;
			case TRAP:
				switch(((TrapTile) this.map.get(newCoordinate)).getTrap()){
					case "lava":
						successors.add(new AStarNode(this.map, newCoordinate, this, this.costFromStart + 2, this.dest));
						break;
					case "health":
						successors.add(new AStarNode(this.map, newCoordinate, this, this.costFromStart + 0.7, this.dest));
						break;
					case "grass":
						successors.add(new AStarNode(this.map, newCoordinate, this, this.costFromStart + 1.1, this.dest));
						break;
				}
			
					successors.add(new AStarNode(this.map, this, this.costFromStart + 1, ));
			}
				
		}
		AStarNode succ;
		
	}
}


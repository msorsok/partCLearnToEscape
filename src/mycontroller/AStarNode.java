package mycontroller;

import java.util.ArrayList;
import java.util.Comparator;

import utilities.Coordinate;

public class AStarNode{
	private AStarNode pathParent;
	private float costFromStart;
	private float estimatedCostToGoal;
	private Coordinate coordinate;
	private Coordinate dest;
	
	
	public AStarNode(Coordinate coordinate, AStarNode parent, float costFromStart, Coordinate dest){
		this.coordinate = coordinate;
		this.pathParent = parent;
		this.costFromStart = costFromStart;
		this.estimatedCostToGoal = heuristic(dest);
		this.dest = dest;
	}
	
	public static Comparator<AStarNode> NodeComparator = new Comparator<AStarNode>() {
		public int compare(AStarNode n1, AStarNode n2){
			return (int)Math.round(n1.getCost() - n2.getCost());
	}};
	
	public float getCostFromStart(){
		return costFromStart;
	}
	
	public float getCost() {
		return costFromStart + estimatedCostToGoal;
	}

	public int compareTo(Object other) {
		double thisValue = this.getCost();
		double otherValue = ((AStarNode)other).getCost();
		double v = thisValue - otherValue;
		return (v>0)?1:(v<0)?-1:0; // sign function
	}

	public float getEstimatedCost(AStarNode node){
		return this.estimatedCostToGoal;
	}

	private float heuristic(Coordinate dest){
		// manhattan distance scaled up by 5
		return 5 * Math.abs(coordinate.x - dest.x) + Math.abs(coordinate.y - dest.y);
	}
	
	public Coordinate getCoordinate(){
		return this.coordinate;
	}
	
	public Coordinate getDest(){
		return this.dest;
	}

	public ArrayList<Coordinate> tracePath(){
		AStarNode curr = this;
		ArrayList<Coordinate> path = new ArrayList<>();
		while(curr != null){
			path.add(0, curr.coordinate);
			curr = curr.pathParent;
		}		
		return path;
	}
}


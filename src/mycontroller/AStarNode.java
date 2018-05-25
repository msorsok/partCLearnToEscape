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
	
	// used for ordering lists in ascending order of cost
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

	public float getEstimatedCost(AStarNode node){
		return this.estimatedCostToGoal;
	}

	private float heuristic(Coordinate dest){
		// manhattan distance scaled up by 5
		return 100 * Math.abs(coordinate.x - dest.x) + Math.abs(coordinate.y - dest.y);
	}
	
	public Coordinate getCoordinate(){
		return this.coordinate;
	}
	
	public Coordinate getDest(){
		return this.dest;
	}
	
	//find path from dest back to source, going from src->path
	public ArrayList<Coordinate> tracePath(){
		AStarNode curr = this;
		ArrayList<Coordinate> path = new ArrayList<>();
		while(curr != null){
			path.add(0, curr.coordinate);
			curr = curr.pathParent;
		}		
		path.remove(0);
		return path;
	}
}


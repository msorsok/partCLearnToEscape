package mycontroller;

import java.util.Comparator;

import utilities.Coordinate;

public class AStarNode{
	AStarNode pathParent;
	double costFromStart;
	double estimatedCostToGoal;
	Coordinate coordinate;
	
	public AStarNode(Coordinate coordinate, AStarNode parent, double costFromStart, Coordinate dest){
		this.coordinate = coordinate;
		this.pathParent = parent;
		this.costFromStart = costFromStart;
		this.estimatedCostToGoal = heuristic(coordinate, dest);
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
		
	}
}


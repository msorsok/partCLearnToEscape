package mycontroller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import automail.MailItem;
import automail.PriorityMailItem;
import utilities.Coordinate;

public class AStarsearch{
	public class AStarNode implements{
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
				return n1.getCost() - n2.getCost();
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
	
		public double heuristic(Coordinate )
		
		private ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest){
			ArrayList<Coordinate> path = new ArrayList<>();
			HashMap<Coordinate, Int> open = new HashMap<>();
			HashMap<Coordinate, Int> closed = new HashMap<>();
			open.put(src, 0);
			while(!open.isEmpty()){
				
			}
		}
	}
}

}

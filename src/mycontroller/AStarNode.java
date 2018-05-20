package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import utilities.Coordinate;

public class AStarsearch{
	public class AStarNode implements Comparable {
		AStarNode pathParent;
		double costFromStart;
		double estimatedCostToGoal;
	
	
		public float getCost() {
			return costFromStart + estimatedCostToGoal;
		}
	
		public int compareTo(Object other) {
			float thisValue = this.getCost();
			float otherValue = ((AStarNode)other).getCost();
			float v = thisValue - otherValue;
			return (v>0)?1:(v<0)?-1:0; // sign function
		}
	
	  /**
	    Gets the cost between this node and the specified
	    adjacent (AKA "neighbor" or "child") node.
	  */
		public float getCost(AStarNode node){
			
		}
		
	  /**
	    Gets the estimated cost between this node and the
	    specified node. The estimated cost should never exceed
	    the true cost. The better the estimate, the more
	    effecient the search.
	  */
		public abstract float getEstimatedCost(AStarNode node);
	
	  /**
	    Gets the children (AKA "neighbors" or "adjacent nodes")
	    of this node.
	  */
		public abstract List getNeighbors();
		
		
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

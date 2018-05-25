package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class AStarSearch {
	// using a star search this method returns a cheap path from src to dest using the edge costs supplied
	public static ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest, float lavaCost, float healthCost, float grassCost, GameState gameState){
		ArrayList<AStarNode> open = new ArrayList<>();					//list of nodes to be searched
		HashMap<Coordinate, AStarNode> openHashMap = new HashMap<>();	//hash for quick lookup of nodes in open list
		HashMap<Coordinate, AStarNode> closedHashMap = new HashMap<>();	//hash for nodes where shortest path was already found
		
		// create the root node
		AStarNode root = new AStarNode(src, null, 0, dest);
		
		// add root to open list to begin search
		open.add(root);
		openHashMap.put(src, root);
		
		//variable for current node being considered
		AStarNode curr;
		while(!open.isEmpty()){
			//pop current lowest cost node from open list
			open.sort(AStarNode.NodeComparator);
			curr = open.remove(0);
			openHashMap.remove(curr.getCoordinate());
			
			// add this node to the closed list
			closedHashMap.put(curr.getCoordinate(), curr);
			
			if (curr.getCoordinate().equals(dest)){
				// found dest return path
				return curr.tracePath();
			}
			//generate children
			for(AStarNode succ: getSuccessors(curr, lavaCost, healthCost, grassCost, gameState)){
				if (closedHashMap.containsKey(succ.getCoordinate())){
					//node already in closed list skip this
					continue;
				}
				if(!openHashMap.containsKey(succ.getCoordinate())){
					//node not in open list add it to open list
					open.add(succ);
					openHashMap.put(succ.getCoordinate(), succ);
				}					
				if (succ.getCostFromStart() > openHashMap.get(succ.getCoordinate()).getCostFromStart()){
					//this is not a better path
					continue;
				}
				if (closedHashMap.containsKey(succ.getCoordinate()) && succ.getCostFromStart() > closedHashMap.get(succ.getCoordinate()).getCostFromStart()){
					//this is not a better path
					continue;
				}
				// This path is the best until now, replace existing node with this one
				open.remove(openHashMap.get(succ.getCoordinate()));
				openHashMap.remove(curr.getCoordinate());
				open.add(succ);
				openHashMap.put(succ.getCoordinate(),succ);
			}
		}
		return null;
	}
	//generates all children nodes for a given node
	public static ArrayList<AStarNode> getSuccessors(AStarNode curr, float lavaCost, float healthCost, float grassCost, GameState gameState){
		ArrayList<AStarNode> successors = new ArrayList<>();
		// looping through coordinates in all 4 cardinal directions
		for (WorldSpatial.Direction d: WorldSpatial.Direction.values()){
			Coordinate newCoordinate = null;
			switch(d) {
				case EAST:
					newCoordinate = new Coordinate(curr.getCoordinate().x + 1, curr.getCoordinate().y);
					break;
				case WEST:
					newCoordinate = new Coordinate(curr.getCoordinate().x - 1, curr.getCoordinate().y);
					break;
				case NORTH:
					newCoordinate = new Coordinate(curr.getCoordinate().x, curr.getCoordinate().y + 1);
					break;
				case SOUTH:
					newCoordinate = new Coordinate(curr.getCoordinate().x, curr.getCoordinate().y - 1);
					break;
				default:
					System.out.println("not a direction");	
					}
			MapTile newMapTile = gameState.combinedMap.get(newCoordinate);
			// add new node to successors if in map using the correct path cost
			if(newMapTile != null){
				switch(newMapTile.getType()){
					case WALL:
						break;
					case TRAP:
						switch(((TrapTile) newMapTile).getTrap()){
							case "lava":
								successors.add(new AStarNode(newCoordinate, curr, curr.getCostFromStart() + lavaCost, curr.getDest()));
								break;
							case "health":
								successors.add(new AStarNode(newCoordinate, curr, curr.getCostFromStart() + healthCost, curr.getDest()));
								break;
							case "grass":
								successors.add(new AStarNode(newCoordinate, curr, curr.getCostFromStart() + grassCost, curr.getDest()));
								break;
							}
						break;
					default:
						successors.add(new AStarNode(newCoordinate, curr, curr.getCostFromStart() + 100, curr.getDest()));
				}
			}
		}
		return successors;	
	}

}

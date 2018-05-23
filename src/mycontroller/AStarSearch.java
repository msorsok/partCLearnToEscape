package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class AStarSearch {
	public static ArrayList<Coordinate> findPath(Coordinate src, Coordinate dest, float lavaCost, float healthCost, float grassCost, GameState gameState){
		ArrayList<AStarNode> open = new ArrayList<>();
		HashMap<Coordinate, AStarNode> openHashMap = new HashMap<>();
		HashMap<Coordinate, AStarNode> closedHashMap = new HashMap<>();
		
		AStarNode root = new AStarNode(src, null, 0, dest);
		AStarNode curr;
		open.add(root);
		openHashMap.put(src, root);
		
		while(!open.isEmpty()){
			open.sort(AStarNode.NodeComparator);
			curr = open.remove(0);
			openHashMap.remove(curr.getCoordinate());
			closedHashMap.put(curr.getCoordinate(), curr);
			if (curr.getCoordinate().equals(dest)){
				// found dest
				System.out.print("found dest: ");
				System.out.print(curr.getCoordinate());
				System.out.print("--");
				System.out.println(curr.getCost());
				return curr.tracePath();
			}
			
			for(AStarNode succ: getSuccessors(curr, lavaCost, healthCost, grassCost, gameState)){
				if (closedHashMap.containsKey(succ.getCoordinate())){
					continue;
				}
				if(!openHashMap.containsKey(succ.getCoordinate())){
					open.add(succ);
					openHashMap.put(succ.getCoordinate(), succ);
				}					
				if (succ.getCostFromStart() > openHashMap.get(succ.getCoordinate()).getCostFromStart()){
					continue;
				}
				if (closedHashMap.containsKey(succ.getCoordinate()) && succ.getCostFromStart() > closedHashMap.get(succ.getCoordinate()).getCostFromStart()){
					continue;
				}
				open.remove(openHashMap.get(succ.getCoordinate()));
				openHashMap.remove(curr.getCoordinate());
				open.add(succ);
				openHashMap.put(succ.getCoordinate(),succ);
			}
		}
		return null;
	}
	public static ArrayList<AStarNode> getSuccessors(AStarNode curr, float lavaCost, float healthCost, float grassCost, GameState gameState){
		ArrayList<AStarNode> successors = new ArrayList<>();
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

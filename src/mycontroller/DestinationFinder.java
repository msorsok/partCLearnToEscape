package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;


public class DestinationFinder {
	public ArrayList<Coordinate> path = new ArrayList<>();
	public ArrayList<WorldSpatial.RelativeDirection> turnList = new ArrayList<>();
	public Coordinate startedLine;
	private ArrayList<Node> nodes = new ArrayList<Node>();
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private HashMap<Coordinate, MapTile> givenMap;
	
	public DestinationFinder(HashMap<Coordinate, MapTile> givenMap) {
		this.givenMap = givenMap;
	}
	
	public void updateVertexAndEdge(HashMap<Coordinate, MapTile> currentView) {
		Iterator<Coordinate> keyIterator = currentView.keySet().iterator();
		
		// Iterate through all of the visible vertexes
	    while (keyIterator.hasNext()) {
	    	  Coordinate coordinateToAdd = keyIterator.next();
	    	  Node vertexToAdd = new Node(coordinateToAdd, currentView.get(coordinateToAdd));
	    	  if(!nodes.contains(vertexToAdd) && !currentView.get(coordinateToAdd).getType().equals(MapTile.Type.EMPTY) && !currentView.get(coordinateToAdd).getType().equals(MapTile.Type.WALL)) {
	    		  nodes.add(vertexToAdd);
	    	  }
	    }
	    	// Go through all the other nodes and try and build an edge
	    
	    for(Node v1 : nodes) {
	    	  for(Node v2 : nodes) {
	    		  if(v2 != v1 && Math.abs(v1.getCoordinate().x - v2.getCoordinate().x) + Math.abs(v1.getCoordinate().y - v2.getCoordinate().y) == 1) {
	    			  Edge edgeToAdd = new Edge(v2, v1, v1.getPenalty());
	    			  if(!edges.contains(edgeToAdd)) {
	    				  edges.add(edgeToAdd);
	    				  v1.increaseEdgesIn();
	    			  	}
	    			  }
	    		  }
	    	  }
	    }
	

	
	public void findNewPath(Coordinate src, MapTile.Type type) {
		path.clear();
		Graph map = new Graph(nodes, edges);
		DijkstraAlgorithm dA = new DijkstraAlgorithm(map);
		dA.execute(new Node(src, new MapTile(type)));
		Node dest = getBestDestination(dA, src);
		System.out.println(dest.getCoordinate());
		LinkedList<Node> longPath = dA.getPath(dest);
		for(Node v : longPath) {
			System.out.println(v.getCoordinate());
		}
		Coordinate lastSaved = null;
		Coordinate last = null;
		
		//Simplify path
		for(Node v : longPath) {
			if(lastSaved == null) {
				startedLine = v.getCoordinate();
				lastSaved = v.getCoordinate();
			}
			
			else if(lastSaved.x != v.getCoordinate().x && lastSaved.y != v.getCoordinate().y) {
				path.add(last);
				lastSaved = last;			
			}
			
			last = v.getCoordinate();
		}
		if(last != null && last != lastSaved) {
			path.add(last);
		}
	}
	
	private Node getBestDestination(DijkstraAlgorithm dA, Coordinate src) {
		Node bestVertexSoFar = null;
		int lowestEdgesIn = 5; //most edges into any square is 4
		float length = Float.MAX_VALUE;
		
		for(Node v : nodes) {
			
			if(v.getEdgesIn() <= lowestEdgesIn && dA.getPath(v) != null) {
				int wallAdjusted = v.getEdgesIn() + checkSurrounding(v.getCoordinate());
				
				if(wallAdjusted < lowestEdgesIn) {
					bestVertexSoFar = v;
					lowestEdgesIn = wallAdjusted;
					length = getEuclideanDistance(src, v.getCoordinate());
				}
				else if(wallAdjusted == lowestEdgesIn && getEuclideanDistance(src, v.getCoordinate()) < length){
					bestVertexSoFar = v;
					lowestEdgesIn = wallAdjusted;
					length = getEuclideanDistance(src, v.getCoordinate());
				}
			}
		}
		
		
		return bestVertexSoFar;
	}

	private int checkSurrounding(Coordinate c) {
		int surroundingWalls = 0;
		if(givenMap.get(new Coordinate(c.x + 1, c.y)).isType(MapTile.Type.WALL)){
			surroundingWalls ++;
		}
		if(givenMap.get(new Coordinate(c.x - 1, c.y)).isType(MapTile.Type.WALL)){
			surroundingWalls ++;
		}
		if(givenMap.get(new Coordinate(c.x, c.y + 1)).isType(MapTile.Type.WALL)){
			surroundingWalls ++;
		}
		if(givenMap.get(new Coordinate(c.x, c.y - 1)).isType(MapTile.Type.WALL)){
			surroundingWalls ++;
		}
		return surroundingWalls;
	}
	
	private float getEuclideanDistance(Coordinate src, Coordinate dest) {
		return (float) Math.sqrt(Math.pow(Math.abs(src.x - dest.x), 2) + Math.pow(Math.abs(src.y - dest.y), 2));
	}
}

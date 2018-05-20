package mycontroller;

import java.util.List;

public class Graph {
	private final List<Node> vertexes;
	private final List<Edge> edges;

	public Graph(List<Node> vertexes, List<Edge> edges) {
	    this.vertexes = vertexes;
	    this.edges = edges;
	}

	public List<Node> getVertexes() {
	    return vertexes;
	}

	public List<Edge> getEdges() {
	    return edges;
	}



}

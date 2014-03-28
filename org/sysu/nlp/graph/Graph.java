package org.sysu.nlp.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Graph {
	protected HashMap<Integer, Node> nodesIndex;
	protected HashMap<Integer, HashSet<Edge>> toEdgeIndex;
	protected int edgeNumber;

	public Graph(){
		nodesIndex = new HashMap<Integer, Node>();
		toEdgeIndex = new HashMap<Integer, HashSet<Edge>>();
		edgeNumber = 0;
	}
	
	public boolean insertToIndex(Node aNode) {
		if (aNode == null) {
			System.err.println("null node.");
			return false;
		}
		if (this.getNode(aNode.index) != null) {
			System.err.println("node already existed.");
			return false;
		}
		this.nodesIndex.put(aNode.index, aNode);
		for (Edge edge : aNode.edges) {
			this.insertEdgeIndex(edge);
			edgeNumber++;
		}
		
		
		return true;
	}
	
	public boolean insert(int fromIndex, int toIndex, double edgeWeight) {
		if (fromIndex == toIndex) {
			System.err.println("invaild index.");
			return false;
		}
		Node fromNode = this.getNode(fromIndex);
		if (fromNode == null) {
			fromNode = new Node();
			fromNode.index = fromIndex;
			this.insertToIndex(fromNode);
		}
		
		Node toNode = this.getNode(toIndex);
		if (toNode == null) {
			toNode = new Node();
			toNode.index = toIndex;
			this.insertToIndex(toNode);
		}
		
		for (Edge edge : fromNode.edges) {
			if (edge.toNode.index == toNode.index) {
				edge.weight = edgeWeight;
				return true;
			}
		}
		
		Edge newEdge = new Edge(edgeWeight);
		newEdge.toNode = toNode;
		fromNode.edges.add(newEdge);
		this.insertEdgeIndex(newEdge);
		edgeNumber++;
		
		return true;
	}
	
	public boolean deleteNodeFromIndex(Node aNode) {
		if (aNode == null) {
			System.err.println("null node.");
			return false;
		}
		this.nodesIndex.remove(aNode.index);
		return true;
	}
	
	public boolean delete(int deleteIndex) {
		Node deleteNode = this.getNode(deleteIndex);
		if (deleteNode == null) {
			System.err.println(deleteIndex + " index not exist.");
			return false;
		}
		
		this.deleteNodeFromIndex(deleteNode);
		for (Node node : nodesIndex.values()) {
			ArrayList<Edge> deleteEdges = new ArrayList<Edge>();
			for (Edge edge : node.edges) {
				if (edge.toNode.index == deleteIndex) {
					deleteEdges.add(edge);
				}
			}
			if (deleteEdges.size() != 0) {
				node.edges.removeAll(deleteEdges);
				
				edgeNumber -= deleteEdges.size();
			}
		}
		return true;
	}
	
	public Node getNode(int index) {
		return this.nodesIndex.get(index);
	}
	
	public int getNodeNumber() {
		return this.nodesIndex.size();
	}
	
	public int getEdgeNumber() {
		return this.edgeNumber;
	}
	
	public void insertEdgeIndex(Edge edge) {
		HashSet<Edge> edges = this.toEdgeIndex.get(edge.toNode.index);
		if (edges == null) {
			edges = new HashSet<Edge>();
			this.toEdgeIndex.put(edge.toNode.index, edges);
		}
		edges.add(edge);
		
		edgeNumber++;
	}
	
	public void clear() {
		this.nodesIndex.clear();
		this.toEdgeIndex.clear();
	}
}

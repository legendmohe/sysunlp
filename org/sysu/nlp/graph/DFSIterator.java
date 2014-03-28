package org.sysu.nlp.graph;

import java.util.HashSet;
import java.util.Stack;

public class DFSIterator {
	Graph graph;
	HashSet<Integer> visitedSet;
	Stack<Node> nodeStack;
	
	public DFSIterator(Graph graph, int startIndex) {
		this.graph = graph;
		visitedSet = new HashSet<Integer>();
		nodeStack = new Stack<Node>();
		nodeStack.push(graph.getNode(startIndex));
		visitedSet.add(startIndex);
	}
	
	public Node next(){
		Node currentNode = nodeStack.pop();
		for (Edge edge : currentNode.edges) {
			if (!visitedSet.contains(edge.toNode.index)) {
				visitedSet.add(edge.toNode.index);
				nodeStack.push(edge.toNode);
			}
		}
		
		return currentNode;
	}
}

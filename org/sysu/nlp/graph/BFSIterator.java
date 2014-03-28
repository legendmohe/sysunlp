package org.sysu.nlp.graph;

import java.util.ArrayList;
import java.util.HashSet;

public class BFSIterator {
	Graph graph;
	HashSet<Integer> visitedSet;
	ArrayList<Node> nodeArrayList;
	int currentIndex;
	
	public BFSIterator(Graph graph, int startIndex) {
		this.graph = graph;
		visitedSet = new HashSet<Integer>();
		nodeArrayList = new ArrayList<Node>();
		nodeArrayList.add(graph.getNode(startIndex));
		visitedSet.add(startIndex);
		currentIndex = 0;
	}
	
	public Node next(){
		Node currentNode = nodeArrayList.get(currentIndex++);
		//currentIndex在当前nodeArrayList遍历完成时清零，开启新一轮的BFS
		if (currentIndex == nodeArrayList.size()) {
			ArrayList<Node> newNodes = new ArrayList<Node>();
			for (Node aNode : nodeArrayList) {
				for (Edge edge : aNode.edges) {
					if (!visitedSet.contains(edge.toNode.index)) {
						visitedSet.add(edge.toNode.index);
						newNodes.add(edge.toNode);
					}
				}
			}
			nodeArrayList = newNodes;
			currentIndex = 0;
		}
		
		return currentNode;
	}
}

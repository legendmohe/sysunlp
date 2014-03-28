package org.sysu.nlp.graph;

import java.util.ArrayList;

public class Node {
	public int index;
	public ArrayList<Edge> edges;
	
	public Node(){
		index = -1;
		edges = new ArrayList<Edge>();
	}
}

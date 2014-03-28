package org.sysu.nlp.segmentation;

import org.sysu.nlp.graph.Edge;

public class NSPEdge extends Edge implements Comparable<NSPEdge> {
	public char[] words;
	public NSPNode fromNode;
	
	public NSPEdge(double weight) {
		super(weight);
	}

	public void setWords(String word) {
		words = word.toCharArray();
	}

	@Override
	public int compareTo(NSPEdge o) {
		double weight1 = this.fromNode.shortestWeight + this.weight;
		double weight2 = o.fromNode.shortestWeight + o.weight;
		if (weight1 > weight2) {
			return 1;
		}else if(weight1 < weight2){
			return -1;
		}
		return 0;
	}

}

package org.sysu.nlp.segmentation;

import org.sysu.nlp.graph.Node;
import org.sysu.nlp.tree.BinaryHeap;

public class NSPNode extends Node implements Comparable<NSPNode> {
	public double shortestWeight;
	private BinaryHeap<NSPEdge> edgeHeap;
	public int backtrackingNodeNumber;
	
	public NSPNode(){
		backtrackingNodeNumber = 0;
		shortestWeight = Double.MAX_VALUE;
		edgeHeap = new BinaryHeap<NSPEdge>(5);
	}

	public NSPEdge peekBacktrackingEdge() {
		return edgeHeap.peek();
	}
	public NSPEdge popBacktrackingEdge() {
		return edgeHeap.popBack();
	}
	public void addBacktrackingEdge(NSPEdge backtrackingEdge) {
		edgeHeap.insert(backtrackingEdge);
	}
	public int edgeHeapSize() {
		return edgeHeap.size();
	}
	public void rebuildHeap() {
		edgeHeap.rebuildHeap();
	}
	public NSPEdge lastPopBackEdge() {
		return edgeHeap.get(edgeHeapSize() + 1);
	}
	public void cleanHeap(){
		edgeHeap.clear();
	}

	@Override
	public int compareTo(NSPNode o) {// minimum heap
		if (shortestWeight > o.shortestWeight) {
			return 1;
		}else if(shortestWeight < o.shortestWeight){
			return -1;
		}
		return 0;
	}
}

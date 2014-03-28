package org.sysu.nlp.segmentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.sysu.nlp.atom.AtomToken;
import org.sysu.nlp.atom.BasicFilter;
import org.sysu.nlp.atom.LetterFilter;
import org.sysu.nlp.atom.NumberAndDateFilter;
import org.sysu.nlp.graph.Edge;
import org.sysu.nlp.graph.Graph;
import org.sysu.nlp.graph.Node;
import org.sysu.nlp.tree.BinaryHeap;
import org.sysu.nlp.tree.TrieNode;
import org.sysu.nlp.tree.TrieTree;

public class NSPGraph extends Graph{
	
	public TrieTree dictionaryTree;
	BinaryHeap<NSPNode> minHeap;
	ArrayList<String> atomWordsArrayList;
	int currentSize;
	int endIndex;
	
	public NSPGraph(TrieTree dictionaryTree){
		this.dictionaryTree = dictionaryTree;
		currentSize = 0;//start from -1;
		
		atomWordsArrayList = new ArrayList<String>(10);
	}
	
	public NSPGraph prepare(String sourceString) {
		char[] charArray = sourceString.toCharArray();
		this.initGraph(charArray);
		
		StringBuffer stringBuffer = new StringBuffer();
		int size = this.atomWordsArrayList.size();
		for (int i = 0; i < size; i++) {
			
			stringBuffer.append(this.atomWordsArrayList.get(i));//StringBuffer?
			
			for (int j = i + 1; j < size; j++) {
				stringBuffer.append(this.atomWordsArrayList.get(j));
				
				TrieNode trieNode = dictionaryTree.search(stringBuffer.toString());
				if (trieNode != null && trieNode.isEnd) {
					
					NSPEdge newEdge = new NSPEdge(1.0);
					newEdge.toNode = this.getNode(j + 1);
					newEdge.words = stringBuffer.toString().toCharArray();
					newEdge.fromNode = (NSPNode) this.getNode(i);
					
					this.getNode(i).edges.add(newEdge);
					this.insertEdgeIndex(newEdge);
				}
				//cut branch
				if (trieNode != null && (trieNode.childrenNumber() == 0
											|| (j + 1 < size 
													&& trieNode.childrenSet().contains(this.atomWordsArrayList.get(j + 1))))) {
					break;
				}else if (trieNode == null) {
					break;
				}
			}
			stringBuffer.setLength(0);
		}
		
		return this;
	}

	private void initGraph(char[] charArray) {
		NSPNode startNode = new NSPNode();
		startNode.index = currentSize++;
		startNode.shortestWeight = 0;
		this.insertToIndex(startNode);
		
		ArrayList<AtomToken> atomTokens = null;
		atomTokens = (new LetterFilter(new NumberAndDateFilter(new BasicFilter(charArray)))).filter();
		NSPNode preNode = startNode;
		int beginIndex = 0;
		for (AtomToken atomToken : atomTokens) {
			NSPNode newNode = new NSPNode();
			this.createAtomNode(atomToken.text, ++beginIndex, preNode, newNode);
			preNode = newNode;
		}
		
		this.endIndex = preNode.index;
	}

	private void createAtomNode(String text, int index,
			NSPNode preNode, NSPNode newNode) {
		newNode.index = currentSize++;
		newNode.backtrackingNodeNumber = index;

		NSPEdge newEdge = new NSPEdge(1.0);
		newEdge.toNode = newNode;
		newEdge.words = text.toCharArray();
		newEdge.fromNode = preNode;
		
		preNode.edges.add(newEdge);
		
		this.insertToIndex(newNode);
		this.insertEdgeIndex(newEdge);
		this.atomWordsArrayList.add(text);
	}
	
	public ArrayList<LinkedList<String>> getNShortestPaths(int n) {
		if (this.currentSize < 1) {
			System.err.println("empty graph.");
			return null;
		}
		this.runDijkstra();
		return this.backtracking((NSPNode) this.getNode(endIndex), n);
	}
	public LinkedList<String> getShortestPaths() {
		if (this.currentSize < 1) {
			System.err.println("empty graph.");
			return null;
		}
		this.runDijkstra();
		return this.backtracking((NSPNode) this.getNode(endIndex));
	}

	private void runDijkstra() {
		int[] visited = new int[this.currentSize];
		
		NSPNode[] heapNodes = new NSPNode[this.nodesIndex.size()];
		int index = 0;
		for (Node node : this.nodesIndex.values()) {
			heapNodes[index++] = (NSPNode) node;
		}
		minHeap = new BinaryHeap<NSPNode>(heapNodes);
		minHeap.buildHeap();
		
		while (minHeap.size() > 0) {
			
			NSPNode currentNode = minHeap.pop();
			visited[currentNode.index] = 1;
			
			for (Edge edge : currentNode.edges) {
				int nodeIndex = edge.toNode.index;
				NSPNode toNode = (NSPNode) edge.toNode;
				toNode.addBacktrackingEdge((NSPEdge) edge);
				
				if (visited[nodeIndex] == 1) {
					continue;
				}
				
				double shortValue = currentNode.shortestWeight + edge.weight;
				if (shortValue < toNode.shortestWeight) {
					toNode.shortestWeight = shortValue;
					toNode.backtrackingNodeNumber = currentNode.backtrackingNodeNumber + 1;
				}
			}
			minHeap.buildHeap();
		}
	}
	
	/* A-Star */
	private ArrayList<LinkedList<String>> backtracking(NSPNode endNode, int n) {
		ArrayList<LinkedList<String>> resultArrayList = new ArrayList<LinkedList<String>>();
		BinaryHeap<HeapObject> aHeap = new BinaryHeap<HeapObject>(200);
		aHeap.insert(new HeapObject(endNode, 0.0));

		int[] visited = new int[this.nodesIndex.size()];
		while (aHeap.size() != 0) {
			HeapObject currentHeapObject = aHeap.pop();
			NSPNode currentNode = currentHeapObject.node;
			visited[currentNode.index]++;

			if (currentNode.index == 0) {
				resultArrayList.add(currentHeapObject.pathWords);
				if (visited[0] == n) {
					return resultArrayList;
				}
				continue;
			}
			
			if (visited[currentNode.index] > n) {
				continue;
			}
			
			HashSet<Edge> edges = this.toEdgeIndex.get(currentNode.index);
			for (Edge edge : edges) {
				NSPNode aNode = ((NSPEdge)edge).fromNode;
				if (visited[aNode.index] >= n) {
					continue;
				}
				
				double g = currentHeapObject.weight + ((NSPEdge) edge).weight;
				double h = aNode.shortestWeight;
				
				HeapObject newHeapObject = new HeapObject(aNode, g + h);
				newHeapObject.pathWords.addAll(currentHeapObject.pathWords);
				newHeapObject.pathWords.add(0, String.valueOf( ((NSPEdge)edge).words));
				aHeap.insert(newHeapObject);
			}
		}
		
		return resultArrayList;
	}
	
	private LinkedList<String> backtracking(NSPNode endNode) {
		LinkedList<String> resultArrayList = new LinkedList<String>();
		while (endNode.edgeHeapSize() != 0) {
			resultArrayList.add(0, String.valueOf(endNode.peekBacktrackingEdge().words));
			endNode = endNode.peekBacktrackingEdge().fromNode;
		}
		
		return resultArrayList;
	}
	
	/*for a-star */
	private class HeapObject implements Comparable<HeapObject>{
		public NSPNode node;
		public double weight;
		public LinkedList<String> pathWords;
		
		
		public HeapObject(NSPNode aNode, double weight){
			this.node = aNode;
			this.weight = weight;
			pathWords = new LinkedList<String>();
		}
		
		@Override
		public int compareTo(HeapObject arg0) {
			
			if (this.weight > arg0.weight) {
				return 1;
			}else if (this.weight < arg0.weight) {
				return -1;
			}
			return 0;
		}
	}
}

package org.sysu.nlp.tree;


public class BinaryHeap<T extends Comparable<? super T>> {
	T[] heap;
	int currentSize;
	int totalSize;
	
	@SuppressWarnings("unchecked")
	public BinaryHeap(int size) {
		super();
		currentSize = 0;
		totalSize = 0;
		this.heap = (T[]) new Comparable[size + 1];
	}
	
	@SuppressWarnings("unchecked")
	public BinaryHeap(T[] nodes) {
		currentSize = nodes.length;
		totalSize += currentSize;
		this.heap = (T[]) new Comparable[currentSize + 1];
		
		int i = 1;
		for (T node : nodes) {
			this.heap[i++] = node;
		}
		this.buildHeap();
	}
	
	public void buildHeap() {
		for (int i = currentSize/2; i > 0; i--) {
			this.percolateDown(i);
		}
	}
	
	public void rebuildHeap() {
		currentSize = totalSize;
		
		//reverse to optimize rebuilding process
		for (int i = 1; i <= currentSize; i++) {
			T tempT = heap[i];
			heap[i] = heap[currentSize + 1 - i];
			heap[currentSize + 1 - i] = tempT;
		}
		for (int i = currentSize/2; i > 0; i--) {
			this.percolateDown(i);
		}
	}
	
	public void insert(T newNode) {
		if (currentSize >= heap.length - 1) {
			this.expandHeap(2);
		}
		totalSize++;
		heap[++currentSize] = newNode;
		this.percolateUp(currentSize);
	}
	
	public T pop() {
		if (currentSize == 0) {
			System.out.println("empty heap.");
			return null;
		}
		
		T topBinaryNode = heap[1];
		heap[1] = heap[currentSize--];//not delete it
		this.percolateDown(1);
		totalSize--;
		
		return topBinaryNode;
	}
	
	public T popBack() {
		if (currentSize == 0) {
			System.out.println("empty heap.");
			return null;
		}
		
		T topBinaryNode = heap[1];
		heap[1] = heap[currentSize--];//not delete it
		this.percolateDown(1);
		heap[currentSize + 1] = topBinaryNode;//push it back
		
		return topBinaryNode;
	}
	
	public T get(int index) {
		if (index > totalSize) {
			System.err.println("invaild index.");
			return null;
		}
		return heap[index];
	}
	
	public T peek() {
		if (currentSize == 0) {
			System.out.println("empty heap.");
			return null;
		}
		
		return heap[1];
	}
	
	public void clear() {
		totalSize = currentSize = 0;
	}
	
	public int size() {
		return currentSize;
	}
	
	@SuppressWarnings("unchecked")
	private void expandHeap(int delta) {
		System.out.println("heap expand to:" + currentSize*delta);
		
		if (delta < 1) {
			return;
		}
		T[] newHeap = (T[]) new Comparable[currentSize*delta + 1];
		for (int i = 1; i < heap.length; i++) {
			newHeap[i] = heap[i];
		}
		heap = newHeap;
	}
	
	protected void percolateDown(int hole) {
		int child;
		T lastNode = heap[hole];
		
		while (hole*2 <= currentSize) {
			child = hole*2;
			if (child != currentSize && heap[child + 1].compareTo(heap[child]) < 0) {
				child++;
			}
			
			if (heap[child].compareTo(lastNode) < 0) {
				heap[hole] = heap[child];
			}else {
				break;
			}
			hole = child;
		}
		
		heap[hole] = lastNode;
	}
	
	protected void percolateUp(int hole) {
		T lastNode = heap[hole];
		
		while (hole > 1 && lastNode.compareTo(heap[hole/2]) < 0) {
			heap[hole] = heap[hole/2];
			hole /= 2;
		}
		heap[hole] = lastNode;
	}
}

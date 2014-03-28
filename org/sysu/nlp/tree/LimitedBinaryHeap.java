package org.sysu.nlp.tree;

public class LimitedBinaryHeap<T extends Comparable<? super T>> extends BinaryHeap<T> {
	T peekNode;
	int size;
	
	public LimitedBinaryHeap(int size) {
		super(size);
		this.size = size;
	}
	
	@Override
	public void insert(T newNode) {
		if (peekNode == null || peekNode.compareTo(newNode) <= 0) {
			peekNode = newNode;
		}
		
		if (currentSize < size) {
			super.insert(newNode);
		}else {
			
			T realPeek = heap[1];
			if (realPeek != null) {
				if (newNode.compareTo(realPeek) < 0) {
					return;
				}else {
					heap[1] = newNode;
					this.percolateDown(1);
				}
			}
		}
	}
	
	@Override
	public T pop(){
		return null;
	}
	
	@Override
	public T peek(){
		return peekNode;
	}
}

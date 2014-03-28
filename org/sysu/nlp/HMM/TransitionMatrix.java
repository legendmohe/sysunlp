package org.sysu.nlp.HMM;

import org.sysu.nlp.model.Matrix;


public final class TransitionMatrix extends Matrix {
	
	public TransitionMatrix(double[][] matrix) {
		super(matrix);
	}

	public TransitionMatrix(int statesLength){
		super(statesLength);
	}
	
	public TransitionMatrix(Matrix matrix){
		super(matrix);
	}
	
	public int getStatesLength() {
		return super.iLength;
	}
}

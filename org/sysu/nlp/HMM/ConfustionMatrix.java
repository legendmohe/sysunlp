package org.sysu.nlp.HMM;

import org.sysu.nlp.model.Matrix;

public final class ConfustionMatrix extends Matrix{

	public ConfustionMatrix(double[][] matrix) {
		super(matrix);
	}

	public ConfustionMatrix(int hiddenStatesLength, int observedStatesLength){
		super(hiddenStatesLength, observedStatesLength);
	}
	
	public ConfustionMatrix(Matrix matrix){
		super(matrix);
	}
	
	public int getStatesLength() {
		return super.iLength;
	}
	
	public int getObservedsLength() {
		return super.jLength;
	}
}

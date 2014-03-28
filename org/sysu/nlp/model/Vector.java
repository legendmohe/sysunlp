package org.sysu.nlp.model;

public class Vector extends Matrix {
	public Vector(int length){
		super(1, length);
	}
	
	public Vector(double[] piVector){
		super(1, piVector.length);
		if (piVector != null && piVector.length != 0) {
			System.arraycopy(piVector, 0, this.internalMatrix[0], 0, piVector.length);
		}
	}
	
	public double get(int index) {
		if (index > this.internalMatrix[0].length - 1) {
			System.err.println("invaild index.");
			return Double.NEGATIVE_INFINITY;
		}
		return this.internalMatrix[0][index];
	}
	
	public void set(int index, double value) {
		if (index > this.internalMatrix[0].length - 1) {
			System.err.println("invaild index.");
			return;
		}
		this.internalMatrix[0][index] = value;
	}
	
	public int getLength() {
		return super.jLength;
	}
	
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("[");
		for (int i = 0; i < this.getLength(); i++) {
			if (i == 0) {
				stringBuffer.append(this.get(i));
			}else {
				stringBuffer.append(", ").append(this.get(i));
			}
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}
}

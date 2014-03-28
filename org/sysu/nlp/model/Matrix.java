package org.sysu.nlp.model;


public class Matrix {
	protected double[][] internalMatrix;
	protected int iLength = 0;
	protected int jLength = 0;
	
	public Matrix(int length){
		iLength = jLength = length;
		this.internalMatrix = new double[length][length];
	}
	
	public Matrix(int ilength, int jLength){
		this.iLength = ilength;
		this.jLength = jLength;
		this.internalMatrix = new double[ilength][jLength];
	}
	
	public Matrix(double[][] matrix){
		this.iLength = matrix.length;
		this.jLength = matrix[0].length;
		this.internalMatrix = new double[iLength][jLength];
		for (int i = 0; i < iLength; i++) {
			System.arraycopy(matrix[i], 0, this.internalMatrix[i], 0, jLength);
		}
	}
	public Matrix(Matrix matrix){
		this.iLength = matrix.getiLength();
		this.jLength = matrix.getjLength();
		this.internalMatrix = new double[iLength][jLength];
		for (int i = 0; i < iLength; i++) {
			System.arraycopy(matrix.getRow(i), 0, this.internalMatrix[i], 0, jLength);
		}
	}
	
	public double get(int i, int j) {
		if (i < 0 || i >= iLength || j < 0 || j >= jLength) {
			System.err.println("invaild params. i:" + i + " j:" + j);
			return Double.NEGATIVE_INFINITY;
		}
		return this.internalMatrix[i][j];
	}
	
	public double[] getRow(int i) {
		if (i < 0 || i >= iLength) {
			System.err.println("invaild params. i:" + i);
			return null;
		}
		return this.internalMatrix[i];
	}
	
	public void set(int i, int j, double value) {
		if (i < 0 || i >= iLength || j < 0 || j >= jLength) {
			System.err.println("invaild params. i:" + i + " j:" + j + " value:" + value);
			return;
		}
		
		this.internalMatrix[i][j] = value;
	}
	
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("[" + "\n");
		for (int i = 0; i < this.iLength; i++) {
			for (int j = 0; j < this.jLength; j++) {
				if (j == 0) {
					stringBuffer.append(this.get(i, j));
				}else {
					stringBuffer.append(", ").append(this.get(i, j));
				}
			}
			stringBuffer.append("\n");
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}
	
	public int getiLength() {
		return this.iLength;
	}
	public int getjLength() {
		return this.jLength;
	}
}

package org.sysu.nlp.HMM;

import org.sysu.nlp.model.Matrix;
import org.sysu.nlp.model.Vector;

/**
 * 
 * @author legendmohe
 * @description forwardbackward算法，已知观测序列，求HMM参数
 *
 */

public class BaumWelchProcessor {
	
	Vector observedVector;
	PiVector piVector;
	TransitionMatrix transitionMatrix;
	ConfustionMatrix confustionMatrix;
	
	Matrix alphaMatrix;
	Matrix betaMatrix;
	
	private double precision;
	private double PLambda = 0.0;
	
	public BaumWelchProcessor(Vector observedVector, PiVector piVector, TransitionMatrix transitionMatrix, ConfustionMatrix confustionMatrix){
		this.observedVector = observedVector;
		this.piVector = piVector;
		this.transitionMatrix = transitionMatrix;
		this.confustionMatrix = confustionMatrix;
		
		this.alphaMatrix = new Matrix(this.transitionMatrix.getStatesLength(), observedVector.getLength());
		this.betaMatrix = new Matrix(this.transitionMatrix.getStatesLength(), observedVector.getLength());
	
		this.setPrecision(0.1);
	}
	
	public void executeBaumWelchAlgorithm(){
//		this.init();//均值初始化
		
		PiVector newPiVector = new PiVector(this.piVector.getLength());
		TransitionMatrix newTransitionMatrix = new TransitionMatrix(this.transitionMatrix.getStatesLength());
		ConfustionMatrix newConfustionMatrix = new ConfustionMatrix(this.confustionMatrix.getStatesLength(), this.confustionMatrix.getObservedsLength());
		
		this.updateForwardValue(observedVector);
		this.updateBackwardValue(observedVector);
		
		int times = 0;
		while (true) {
			//E-step, M-step
			this.updateMatrixs(newPiVector, newTransitionMatrix, newConfustionMatrix);
			this.assignMatrixs(newTransitionMatrix, newConfustionMatrix, newPiVector);
			
			double lastLambda = PLambda;
			this.updateForwardValue(observedVector);
			this.updateBackwardValue(observedVector);
			boolean reachPresition = this.evaluateMatrixs(lastLambda, PLambda);
			if (reachPresition) {
				break;
			}
			times++;
		}
		System.out.println("迭代了" + times + "次");
	}

	private void assignMatrixs(TransitionMatrix newTransitionMatrix,
			ConfustionMatrix newConfustionMatrix,
			PiVector newPiVector) {
		//赋值
		for (int i = 0; i < transitionMatrix.getStatesLength(); i++) {
			for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
				transitionMatrix.set(i, j, newTransitionMatrix.get(i, j));
			}
			for (int k = 0; k < confustionMatrix.getObservedsLength(); k++) {
				confustionMatrix.set(i, k, newConfustionMatrix.get(i, k));
			}
		}
		for (int i = 0; i < piVector.getLength(); i++) {
			piVector.set(i, newPiVector.get(i));
		}
	}

	public void updateMatrixs(PiVector newPiVector,
			TransitionMatrix newTransitionMatrix,
			ConfustionMatrix newConfustionMatrix) {
		double[][] Xij = new double[transitionMatrix.getStatesLength()][transitionMatrix.getStatesLength()];
		double[] gammai = new double[transitionMatrix.getStatesLength()];
		double[] gammaiT_1 = new double[transitionMatrix.getStatesLength()];
		double[][] gammak = new double[transitionMatrix.getStatesLength()][confustionMatrix.getObservedsLength()];
		
		for (int i = 0; i < transitionMatrix.getStatesLength(); i++) {
			//计算
			for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
				double Xijt = 0.0;
				for (int t = 0; t < observedVector.getLength() - 1; t++) {
					int observed = (int) this.observedVector.get(t + 1);
					Xijt += alphaMatrix.get(i, t)*this.transitionMatrix.get(i, j)*this.confustionMatrix.get(j, observed)*this.betaMatrix.get(j, t + 1);
				}
				Xij[i][j] = Xijt/PLambda;
			}
			//计算
			double gammait = 0.0;
			for (int t = 0; t < observedVector.getLength(); t++) {
				int observed = (int) this.observedVector.get(t);
				double gamma =  alphaMatrix.get(i, t)*betaMatrix.get(i, t)/PLambda;
				gammait += gamma;//所有累加
				gammak[i][observed] += gamma;//观测值才累加
			}
			gammai[i] = gammait;
			gammaiT_1[i] = (gammait - alphaMatrix.get(i, observedVector.getLength() - 1)/PLambda);
			//计算初始期望值
			newPiVector.set(i, alphaMatrix.get(i, 0)*betaMatrix.get(i, 0)/PLambda) ;
		}
		
		for (int i = 0; i < transitionMatrix.getStatesLength(); i++) {
			for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
				newTransitionMatrix.set(i, j, Xij[i][j]/gammaiT_1[i]);
			}
			for (int k = 0; k < observedVector.getLength(); k++) {
//				System.err.println("gammak[k]:" + gammak[i][k] + " | " + "gammai[i]:" + gammai[i]);
				int observed = (int) observedVector.get(k);
				newConfustionMatrix.set(i, observed, gammak[i][observed]/gammai[i]);
			}
		}
	}
	
	public boolean evaluateMatrixs(double lastLambda, double currentLambda) {
		
		System.out.println("last:" + lastLambda + " | current:" + currentLambda);
		if (Math.abs(Math.log(lastLambda) - Math.log(currentLambda)) < precision) {
			return true;
		}
		return false;
	}
	
	public void init() {
		double initPc = 1.0/this.confustionMatrix.getObservedsLength();//平均概率
		double initPs = 1.0/piVector.getLength();//平均概率
		for (int i = 0; i < piVector.getLength(); i++) {
			this.piVector.set(i, initPs);
		}
		for (int i = 0; i < this.transitionMatrix.getStatesLength(); i++) {
			for (int j = 0; j < this.transitionMatrix.getStatesLength(); j++) {
				this.transitionMatrix.set(i, j, initPs);
			}
			for (int k = 0; k < this.confustionMatrix.getObservedsLength(); k++) {
				this.confustionMatrix.set(i, k, initPc);
			}
		}
	}
	
	public void updateForwardValue(Vector observedVector) {
		int stateLength = transitionMatrix.getStatesLength();
		//遍历观测值
		for (int t = 0; t < observedVector.getLength(); t++) {
			int observed = (int) observedVector.get(t);
			if (t == 0) {//t=1时，局部概率=初始概率*混淆概率
				for (int j = 0; j < stateLength; j++) {
					alphaMatrix.set(j, t, piVector.get(j)*confustionMatrix.get(j, observed));
				}
			}else {//t > 1时，局部概率= (t-1时各个节点到达该节点的转移概率之和)*混淆概率
				
				//临时数组保存概率和
				double[] alphaT = new double[stateLength];
				for (int j = 0; j < stateLength; j++) {
					for (int k = 0; k < stateLength; k++) {
						alphaT[j] += alphaMatrix.get(k, t - 1)*transitionMatrix.get(k, j);
					}
					alphaT[j] = alphaT[j]*confustionMatrix.get(j, observed);
				}
				for (int i = 0; i < alphaT.length; i++) {
					alphaMatrix.set(i, t, alphaT[i]);
				}
			}
		}
		
		updatePLambda();
	}

	private void updatePLambda() {
		//更新P(O|Lambda)
		PLambda = 0.0;
		for (int i = 0; i < this.transitionMatrix.getStatesLength(); i++) {
			PLambda += alphaMatrix.get(i, this.observedVector.getLength() - 1);
		}
	}
	public void updateBackwardValue(Vector observedVector){
		int T = observedVector.getLength() - 1;
		int stateLength = transitionMatrix.getStatesLength();
		//遍历观测值
		for (int t = T; t >= 0; t--) {
			if (t == T) {//t=T时，后向局部概率 = 1.0
				for (int s = 0; s < stateLength; s++) {
					betaMatrix.set(s, t, 1.0);
				}
			}else {//t < T时，局部概率 = 当前点到Ot+1...Ot+N的概率的和
				//上一次的Ot+1
				int observed = (int) observedVector.get(t + 1);
				//临时数组保存概率和
				double[] betaT = new double[stateLength];
				for (int i = 0; i < stateLength; i++) {
					for (int j = 0; j < stateLength; j++) {
						//注意observed+1
						betaT[i] += betaMatrix.get(j, t + 1)*transitionMatrix.get(i, j)*confustionMatrix.get(j, observed);
					}
				}
				for (int i = 0; i < betaT.length; i++) {
					betaMatrix.set(i, t, betaT[i]);
				}
			}
		}
	}
	
	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}
}

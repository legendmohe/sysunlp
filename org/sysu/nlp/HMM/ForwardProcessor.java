package org.sysu.nlp.HMM;


/**
 * 
 * @author legendmohe
 * @description 向前算法，求已知HMM参数情况下，某个观测序列的出现的概率
 *
 */

public class ForwardProcessor {

	int[] observedVector;
	PiVector piVector;
	TransitionMatrix transitionMatrix;
	ConfustionMatrix confustionMatrix;
	
	public ForwardProcessor(int[] observedVector, PiVector piVector, TransitionMatrix transitionMatrix, ConfustionMatrix confustionMatrix){
		this.observedVector = observedVector;
		this.piVector = piVector;
		this.transitionMatrix = transitionMatrix;
		this.confustionMatrix = confustionMatrix;
	}
	
	public double executeForwardAlgorithm() {
		//保存上一次节点的局部概率
		double[] alphaT_1 = new double[transitionMatrix.getStatesLength()];
		//遍历观测值
		for (int t = 0; t < this.observedVector.length; t++) {
			int observed = this.observedVector[t];
			if (t == 0) {//t=1时，局部概率=初始概率*混淆概率
				for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
					alphaT_1[j] = piVector.get(j)*confustionMatrix.get(j, observed);
				}
			}else {//t > 1时，局部概率= (t-1时各个节点到达该节点的转移概率之和)*混淆概率
				//临时数组保存概率和
				double[] alphaT = new double[alphaT_1.length];
				for (int j = 0; j < alphaT_1.length; j++) {
					for (int k = 0; k < alphaT_1.length; k++) {
						alphaT[j] += alphaT_1[k]*transitionMatrix.get(k, j);
					}
					alphaT[j] = alphaT[j]*confustionMatrix.get(j, observed);
				}
				for (int j = 0; j < alphaT.length; j++) {
					alphaT_1[j] = alphaT[j];
				}
			}
		}
		
		//输出到达该节点的局部概率之和
		double result = 0.0;
		for (int j = 0; j < alphaT_1.length; j++) {
			result += alphaT_1[j];
		}
		
		return result;
	}
}

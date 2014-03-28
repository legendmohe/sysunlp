package org.sysu.nlp.HMM;


/**
 * 
 * @author legendmohe
 * @description 后向算法，求已知HMM参数情况和t-1时刻某个状态发生了的情况下，t+1...t+N观测序列的出现的概率
 *
 */

public class BackwardProcessor {
	int[] observedVector;
	PiVector piVector;
	TransitionMatrix transitionMatrix;
	ConfustionMatrix confustionMatrix;
	
	public BackwardProcessor(int[] observedVector, PiVector piVector, TransitionMatrix transitionMatrix, ConfustionMatrix confustionMatrix){
		this.observedVector = observedVector;
		this.piVector = piVector;
		this.transitionMatrix = transitionMatrix;
		this.confustionMatrix = confustionMatrix;
	}
	
	public double executeBackwardAlgorithm() {
		int T = this.observedVector.length - 1;
		//保存上一次节点的局部概率
		double[] betaT_1 = new double[transitionMatrix.getStatesLength()];
		//遍历观测值
		for (int t = T; t >= 0; t--) {
			if (t == T) {//t=T时，后向局部概率 = 1.0
				for (int s = 0; s < betaT_1.length; s++) {
					betaT_1[s] = 1.0;
				}
			}else {//t < T时，局部概率 = 当前点到Ot+1...Ot+N的概率的和
				//上一次的Ot+1
				int observed = this.observedVector[t + 1];
				//临时数组保存概率和
				double[] betaT = new double[betaT_1.length];
				for (int i = 0; i < betaT_1.length; i++) {
					for (int j = 0; j < betaT_1.length; j++) {
						//注意observed+1
						betaT[i] += betaT_1[j]*transitionMatrix.get(i, j)*confustionMatrix.get(j, observed);
					}
				}
				for (int i = 0; i < betaT.length; i++) {
					betaT_1[i] = betaT[i];
				}
			}
		}
		
		//输出到达该节点的局部概率之和
		double result = 0.0;
		//t=0时的观测值
		int observed = this.observedVector[0];
		for (int i = 0; i < betaT_1.length; i++) {
			//初始概率*混淆概率*后向局部概率
			result += this.piVector.get(i)*confustionMatrix.get(i, observed)*betaT_1[i];
		}
		
		return result;
	}
}

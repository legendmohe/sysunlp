package org.sysu.nlp.HMM;

import org.sysu.nlp.model.Vector;

/**
 * 
 * @author legendmohe
 * @description 维特比算法，求已知HMM参数情和某个观测序列的情况下，最可能的隐藏状态序列
 *
 */

public class ViterbiProcessor {
	PiVector piVector;
	TransitionMatrix transitionMatrix;
	ConfustionMatrix confustionMatrix;
	
	public ViterbiProcessor(PiVector piVector, TransitionMatrix transitionMatrix, ConfustionMatrix confustionMatrix){
		this.piVector = piVector;
		this.transitionMatrix = transitionMatrix;
		this.confustionMatrix = confustionMatrix;
	}
	
	public Vector executeViterbiAlgorithm(int[] observedVector) {
		
		//保存上一次节点的局部概率
		double[] prePr = new double[transitionMatrix.getStatesLength()];
		//回溯矩阵
		double[][] preMaxNode = new double[transitionMatrix.getStatesLength()][observedVector.length];
		//当前最大值，最大的序号
		double maxValue = 0.0;
		int maxNode = 0;
		//遍历观测值
		for (int t = 0; t < observedVector.length; t++) {
			int observed = observedVector[t];
			if (t == 0) {//t=1时，局部概率=初始概率*混淆概率
				for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
					prePr[j] = piVector.get(j)*confustionMatrix.get(j, observed);
					preMaxNode[j][t] = j;//到达节点的回溯点为它本身
				}
			}else {//t > 1时，局部概率= max(t-1时各个节点到达该节点的转移概率)*混淆概率
				//临时数组保存概率和
				double[] income = new double[prePr.length];
				//遍历状态
				for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
					double maxPreIncome = 0.0;
					int maxPreNode = 0;
					for (int k = 0; k < prePr.length; k++) {
						//在进入下一个观测状态前，要保持prePr[k]的值不变
						double stateTrans = prePr[k]*transitionMatrix.get(k, j);
						if (stateTrans > maxPreIncome) {//选出max
							maxPreIncome = stateTrans;
							maxPreNode = k;//保存每一次局部概率最大的节点号
						}
					}
					income[j] = maxPreIncome*confustionMatrix.get(j, observed);
					preMaxNode[j][t] = maxPreNode;
				}
				
				//保存上一次结果&保存当前最大值
				for (int j = 0; j < income.length; j++) {
					prePr[j] = income[j];
					if (maxValue < prePr[j]) {
						maxNode = j;
						maxValue = prePr[j];
					}
				}
				maxValue = 0.0;
			}
		}
		
		Vector probStates = new Vector(observedVector.length);
		
		//回溯
		for (int i = 0; i < observedVector.length; i++) {
			probStates.set(observedVector.length - 1 - i, maxNode);
			maxNode = (int) preMaxNode[maxNode][observedVector.length - 1 - i];//注意，这里最后一次要忽略掉
		}
		
		return probStates;
	}
}

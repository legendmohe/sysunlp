package org.sysu.nlp.HMM;

import org.sysu.nlp.model.Vector;

/**
 * 
 * @author legendmohe
 * @description ά�ر��㷨������֪HMM�������ĳ���۲����е�����£�����ܵ�����״̬����
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
		
		//������һ�νڵ�ľֲ�����
		double[] prePr = new double[transitionMatrix.getStatesLength()];
		//���ݾ���
		double[][] preMaxNode = new double[transitionMatrix.getStatesLength()][observedVector.length];
		//��ǰ���ֵ���������
		double maxValue = 0.0;
		int maxNode = 0;
		//�����۲�ֵ
		for (int t = 0; t < observedVector.length; t++) {
			int observed = observedVector[t];
			if (t == 0) {//t=1ʱ���ֲ�����=��ʼ����*��������
				for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
					prePr[j] = piVector.get(j)*confustionMatrix.get(j, observed);
					preMaxNode[j][t] = j;//����ڵ�Ļ��ݵ�Ϊ������
				}
			}else {//t > 1ʱ���ֲ�����= max(t-1ʱ�����ڵ㵽��ýڵ��ת�Ƹ���)*��������
				//��ʱ���鱣����ʺ�
				double[] income = new double[prePr.length];
				//����״̬
				for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
					double maxPreIncome = 0.0;
					int maxPreNode = 0;
					for (int k = 0; k < prePr.length; k++) {
						//�ڽ�����һ���۲�״̬ǰ��Ҫ����prePr[k]��ֵ����
						double stateTrans = prePr[k]*transitionMatrix.get(k, j);
						if (stateTrans > maxPreIncome) {//ѡ��max
							maxPreIncome = stateTrans;
							maxPreNode = k;//����ÿһ�ξֲ��������Ľڵ��
						}
					}
					income[j] = maxPreIncome*confustionMatrix.get(j, observed);
					preMaxNode[j][t] = maxPreNode;
				}
				
				//������һ�ν��&���浱ǰ���ֵ
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
		
		//����
		for (int i = 0; i < observedVector.length; i++) {
			probStates.set(observedVector.length - 1 - i, maxNode);
			maxNode = (int) preMaxNode[maxNode][observedVector.length - 1 - i];//ע�⣬�������һ��Ҫ���Ե�
		}
		
		return probStates;
	}
}

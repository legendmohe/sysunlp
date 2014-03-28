package org.sysu.nlp.HMM;


/**
 * 
 * @author legendmohe
 * @description ��ǰ�㷨������֪HMM��������£�ĳ���۲����еĳ��ֵĸ���
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
		//������һ�νڵ�ľֲ�����
		double[] alphaT_1 = new double[transitionMatrix.getStatesLength()];
		//�����۲�ֵ
		for (int t = 0; t < this.observedVector.length; t++) {
			int observed = this.observedVector[t];
			if (t == 0) {//t=1ʱ���ֲ�����=��ʼ����*��������
				for (int j = 0; j < transitionMatrix.getStatesLength(); j++) {
					alphaT_1[j] = piVector.get(j)*confustionMatrix.get(j, observed);
				}
			}else {//t > 1ʱ���ֲ�����= (t-1ʱ�����ڵ㵽��ýڵ��ת�Ƹ���֮��)*��������
				//��ʱ���鱣����ʺ�
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
		
		//�������ýڵ�ľֲ�����֮��
		double result = 0.0;
		for (int j = 0; j < alphaT_1.length; j++) {
			result += alphaT_1[j];
		}
		
		return result;
	}
}

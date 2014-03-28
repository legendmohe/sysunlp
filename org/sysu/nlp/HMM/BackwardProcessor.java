package org.sysu.nlp.HMM;


/**
 * 
 * @author legendmohe
 * @description �����㷨������֪HMM���������t-1ʱ��ĳ��״̬�����˵�����£�t+1...t+N�۲����еĳ��ֵĸ���
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
		//������һ�νڵ�ľֲ�����
		double[] betaT_1 = new double[transitionMatrix.getStatesLength()];
		//�����۲�ֵ
		for (int t = T; t >= 0; t--) {
			if (t == T) {//t=Tʱ������ֲ����� = 1.0
				for (int s = 0; s < betaT_1.length; s++) {
					betaT_1[s] = 1.0;
				}
			}else {//t < Tʱ���ֲ����� = ��ǰ�㵽Ot+1...Ot+N�ĸ��ʵĺ�
				//��һ�ε�Ot+1
				int observed = this.observedVector[t + 1];
				//��ʱ���鱣����ʺ�
				double[] betaT = new double[betaT_1.length];
				for (int i = 0; i < betaT_1.length; i++) {
					for (int j = 0; j < betaT_1.length; j++) {
						//ע��observed+1
						betaT[i] += betaT_1[j]*transitionMatrix.get(i, j)*confustionMatrix.get(j, observed);
					}
				}
				for (int i = 0; i < betaT.length; i++) {
					betaT_1[i] = betaT[i];
				}
			}
		}
		
		//�������ýڵ�ľֲ�����֮��
		double result = 0.0;
		//t=0ʱ�Ĺ۲�ֵ
		int observed = this.observedVector[0];
		for (int i = 0; i < betaT_1.length; i++) {
			//��ʼ����*��������*����ֲ�����
			result += this.piVector.get(i)*confustionMatrix.get(i, observed)*betaT_1[i];
		}
		
		return result;
	}
}

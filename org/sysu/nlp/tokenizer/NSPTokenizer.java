package org.sysu.nlp.tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.sysu.nlp.HMM.ConfustionMatrix;
import org.sysu.nlp.HMM.TransitionMatrix;
import org.sysu.nlp.corpus.HMMCorpus;
import org.sysu.nlp.segmentation.NSPGraph;

public class NSPTokenizer {
	
	ConfustionMatrix confustionMatrix;
	TransitionMatrix transitionMatrix;
	HashMap<Character, Integer> char2IndexhaHashMap;
	double[] piArray;
	
	public NSPTokenizer(String transPath, String confPath, String indexPath, String piPath){
		try {
			this.transitionMatrix = new TransitionMatrix(HMMCorpus.loadMatrixFromFilePath(transPath));
			this.confustionMatrix = new ConfustionMatrix(HMMCorpus.loadMatrixFromFilePath(confPath));
			this.char2IndexhaHashMap = this.generateIndex(HMMCorpus.loadIndexFromFilePath(indexPath));
			this.piArray = HMMCorpus.loadPiArrayFromFilePath(piPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public NSPTokenizer(TransitionMatrix transitionMatrix, ConfustionMatrix confustionMatrix, Character[] charIndexesArray, double[] piArray){
		this.confustionMatrix = confustionMatrix;
		this.transitionMatrix = transitionMatrix;
		this.char2IndexhaHashMap = this.generateIndex(charIndexesArray);
		this.piArray = piArray;
	}
	
	private HashMap<Character, Integer> generateIndex(Character[] charIndexesArray) {
		HashMap<Character, Integer> resultHashMap = new HashMap<Character, Integer>();
		for (int i = 0; i < charIndexesArray.length; i++) {
			resultHashMap.put(charIndexesArray[i], i);
		}
		return resultHashMap;
	}
	
	//main
	public LinkedList<String> tokenize(NSPGraph graph, String sourceString) {
		
		if (sourceString == null || sourceString.length() == 0) {
			System.err.println("empty sourceString.");
			return null;
		}
		
		LinkedList<String> tokenArrayList = null;
		double minWeight = Double.MAX_VALUE;
		ArrayList<LinkedList<String>> resultStrings = graph.prepare(sourceString).getNShortestPaths(8);
		int[] observedArray = this.observedArrayFromString(sourceString);
		
		for (LinkedList<String> nshorts : resultStrings) {
			
			int[] statusArray = this.statusArrayFromString(nshorts, observedArray.length);
			
			double weight = this.getWeight(observedArray, statusArray);
			if (weight <= minWeight) {
				minWeight = weight;
				tokenArrayList = nshorts;
			}
		}
		
		return tokenArrayList;
	}
	
	public double getWeight(int[] observedArray, int[] statusArray) {
		double weight = 0.0;
		double wc0 = this.confustionMatrix.get(statusArray[0], observedArray[0]);
		double c0 = this.piArray[statusArray[0]];
		weight += -Math.log(wc0)-Math.log(c0);
		
		for (int i = 1; i < observedArray.length; i++) {
			double wc = this.confustionMatrix.get(statusArray[i], observedArray[i]);
			double c = this.transitionMatrix.get(statusArray[i - 1], statusArray[i]);
			weight += -Math.log(wc)-Math.log(c);
		}
		return weight;
	}
	
	public int[] observedArrayFromString(String sourceString) {
		char[] chars = sourceString.toCharArray();
		int[] observed = new int[chars.length];
 		for (int i = 0; i < chars.length; i++) {
 			Integer index = this.char2IndexhaHashMap.get(chars[i]);
 			if (index == null) {
 				index = this.char2IndexhaHashMap.get("#".charAt(0));
			}
 			observed[i] = index;
		}
 		
 		return observed;
	}
	public int[] statusArrayFromString(LinkedList<String> sourceStrings, int size) {
		int[] status = new int[size];
 		int index = 0;
		for (String word : sourceStrings) {
			if (word.length() == 1) {
				status[index++] = HMMCorpus.S;
			}else {
				char[] chars = word.toCharArray();
				status[index++] = HMMCorpus.B;
				for (int i = 1; i < chars.length - 1; i++) {
					status[index++] = HMMCorpus.M;
				}
				status[index++] = HMMCorpus.E;
			}
		}
		
 		return status;
	}
}

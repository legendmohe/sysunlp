package org.sysu.nlp.tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.sysu.nlp.HMM.ConfustionMatrix;
import org.sysu.nlp.HMM.PiVector;
import org.sysu.nlp.HMM.TransitionMatrix;
import org.sysu.nlp.HMM.ViterbiProcessor;
import org.sysu.nlp.corpus.HMMCorpus;
import org.sysu.nlp.model.Vector;

public class HMMTokenizer {
	ConfustionMatrix confustionMatrix;
	TransitionMatrix transitionMatrix;
	HashMap<Character, Integer> char2IndexhaHashMap;
	double[] piArray;
	
	ViterbiProcessor viterbiProcessor;
	
	public HMMTokenizer(String transPath, String confPath, String indexPath, String piPath){
		try {
			this.transitionMatrix = new TransitionMatrix(HMMCorpus.loadMatrixFromFilePath(transPath));
			this.confustionMatrix = new ConfustionMatrix(HMMCorpus.loadMatrixFromFilePath(confPath));
			this.char2IndexhaHashMap = this.generateIndex(HMMCorpus.loadIndexFromFilePath(indexPath));
			this.piArray = HMMCorpus.loadPiArrayFromFilePath(piPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PiVector piVector = new PiVector(piArray);
		this.viterbiProcessor = new ViterbiProcessor(piVector, transitionMatrix, confustionMatrix);
	}
	
	public HMMTokenizer(TransitionMatrix transitionMatrix, ConfustionMatrix confustionMatrix, Character[] charIndexesArray, double[] piArray){
		this.confustionMatrix = confustionMatrix;
		this.transitionMatrix = transitionMatrix;
		this.char2IndexhaHashMap = this.generateIndex(charIndexesArray);
		this.piArray = piArray;
		
		PiVector piVector = new PiVector(piArray);
		this.viterbiProcessor = new ViterbiProcessor(piVector, transitionMatrix, confustionMatrix);
	}
	
	private HashMap<Character, Integer> generateIndex(Character[] charIndexesArray) {
		HashMap<Character, Integer> resultHashMap = new HashMap<Character, Integer>();
		for (int i = 0; i < charIndexesArray.length; i++) {
			resultHashMap.put(charIndexesArray[i], i);
		}
		return resultHashMap;
	}
	
	//main
	public ArrayList<String> process(String sourceString) {
		int[] observedArray = this.observedArrayFromString(sourceString);
		Vector resultVector = this.viterbiProcessor.executeViterbiAlgorithm(observedArray);
		ArrayList<String> tokenArrayList = this.vectorToStrings(resultVector, sourceString);
		
		return tokenArrayList;
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
	
	private ArrayList<String> vectorToStrings(Vector vector, String sourceString) {
		ArrayList<String> resultArrayList = new ArrayList<String>();
		char[] chars = sourceString.toCharArray();
		
		StringBuffer buffer = new StringBuffer(chars.length);
		
 		for (int i = 0; i < vector.getLength(); i++) {
			int tag = (int)vector.get(i);
			char currentChar = chars[i];
			if (tag == HMMCorpus.B) {
				buffer.append(currentChar);
			}else if(tag == HMMCorpus.M) {
				buffer.append(currentChar);
			}else if(tag == HMMCorpus.E) {
				buffer.append(currentChar);
				resultArrayList.add(buffer.toString());
				
				buffer.setLength(0);
			}else {
				resultArrayList.add(String.valueOf(currentChar));
			}
		}
 		if (buffer.length() != 0) {
 			resultArrayList.add(buffer.toString());	
 		}
 		
 		return resultArrayList;
	}
}

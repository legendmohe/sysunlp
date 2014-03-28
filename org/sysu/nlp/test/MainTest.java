package org.sysu.nlp.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import org.sysu.nlp.HMM.BackwardProcessor;
import org.sysu.nlp.HMM.BaumWelchProcessor;
import org.sysu.nlp.HMM.ConfustionMatrix;
import org.sysu.nlp.HMM.ForwardProcessor;
import org.sysu.nlp.HMM.PiVector;
import org.sysu.nlp.HMM.TransitionMatrix;
import org.sysu.nlp.HMM.ViterbiProcessor;
import org.sysu.nlp.constants.Constants;
import org.sysu.nlp.corpus.HMMCorpus;
import org.sysu.nlp.corpus.ResultParser;
import org.sysu.nlp.dictionary.Dictionary;
import org.sysu.nlp.model.Vector;
import org.sysu.nlp.segmentation.NSPGraph;
import org.sysu.nlp.segmentation.NSPNode;
import org.sysu.nlp.tokenizer.NSPTokenizer;
import org.sysu.nlp.tokenizer.SegmentationTokenizer;
import org.sysu.nlp.tree.BinaryHeap;
import org.sysu.nlp.tree.TrieTree;
import org.sysu.nlp.util.FileUtils;

public class MainTest {

	static final int Sunny = 0;
	static final int Cloudy = 1;
	static final int Rainy = 2;
	
	static final int Dry = 0;
	static final int Dryish = 1;
	static final int Damp = 2;
	static final int Soggy = 3;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		testJahmm();
//		testForward();
//		testBackward();
//		testViterbi();
//		testBaumWelch();
		
//		testTrie();
		
//		testHeap();
		
//		testNSP();
//		generateDictionaryFile();
		
//		trainNSPHMM();
		testNSPHMM();
	}
	
	public static void trainNSPHMM() {
		HMMCorpus preprocessor = new HMMCorpus();
		preprocessor.trainFileToHMMFile(Constants.TRAIN_FILE_PATH, 
				Constants.TRANSITION_FILE_PATH, 
				Constants.CONFUSE_FILE_PATH, 
				Constants.INDEX_FILE_PATH, 
				Constants.PI_FILE_PATH);
	}
	
	public static void testNSPHMM() throws IOException {
		String[] dictionaryPaths = {Constants.TRAIN_FILE_PATH};
		TrieTree trieTree = Dictionary.loadTreeFromFilePath(dictionaryPaths);
		
		File testFile = FileUtils.fileFromPath(Constants.TEST_FILE_PATH, false);
		String testContent = FileUtils.contentOfFileContent(testFile, true, "UTF-8");
		
		ArrayList<String> resultArrayList = new ArrayList<String>();
		NSPTokenizer tokenizer = new NSPTokenizer(Constants.TRANSITION_FILE_PATH, 
				Constants.CONFUSE_FILE_PATH, 
				Constants.INDEX_FILE_PATH, 
				Constants.PI_FILE_PATH);
		
		/* begin */
		Date startDate = new Date();
		ArrayList<String> contents = SegmentationTokenizer.doSentenceSegmentation(testContent);
		int count = 0;
		for (String content : contents) {
			NSPGraph testGraph = new NSPGraph(trieTree);
			LinkedList<String> resultStrings = tokenizer.tokenize(testGraph, content);
			resultArrayList.addAll(resultStrings);
//			for (String token : resultStrings) {
//				System.out.print(token + " | ");
//			}
//
//			System.out.println("\n---");
//			if (count++ > 1) {
//				break;
//			}
		}
		Date endDate = new Date();
		/* end */
		
		System.out.println(resultArrayList);
		System.out.println("分词时间：" + (endDate.getTime() - startDate.getTime()));
		System.out.println("文章长度：" + testContent.length());
		ResultParser.saveResultToFilePath(resultArrayList, Constants.RESULT_FILE_PATH);
	}

	private static void testNSP() throws IOException {
		String[] dictionaryPaths = {"./pku_training_words.utf8"};
		TrieTree trieTree = Dictionary.loadTreeFromFilePath(dictionaryPaths);
		
		File testFile = FileUtils.fileFromPath("./pku_test.utf8", false);
		String testContent = FileUtils.contentOfFileContent(testFile, true, "UTF-8");
		
		Date startDate = new Date();
		ArrayList<String> resultArrayList = new ArrayList<String>();
		ArrayList<String> contents = SegmentationTokenizer.doSentenceSegmentation(testContent);
		int count = 0;
		for (String content : contents) {
			NSPGraph testGraph = new NSPGraph(trieTree);
//			testGraph.prepare("2010年三月，胡锦涛同志在会上进行了讲话");
			testGraph.prepare(content);
//			System.out.println(content);

			LinkedList<String> resultStrings = testGraph.getShortestPaths();
			for (String token : resultStrings) {
//				System.out.print(token + "|");
				resultArrayList.add(token);
			}
			
//			System.out.println("\n---");
//			if (count++ > 1) {
//				break;
//			}
		}
		Date endDate = new Date();
//		System.out.println(resultArrayList);
		System.out.println("分词时间：" + (endDate.getTime() - startDate.getTime()));
		System.out.println("文章长度：" + testContent.length());
		ResultParser.saveResultToFilePath(resultArrayList, "./pku_result.txt");
	}
	
	public static void generateDictionaryFile() throws IOException {
		String dictionaryPath = "./bf_training.utf8";
		HashSet<String> wordsHashSet = Dictionary.loadWordSetFromFilePath(dictionaryPath);
		Dictionary.saveWordSetToFile("./bf_dictionary.utf8", wordsHashSet);
	}

	private static void testHeap() {
		
		NSPNode[] testArray = new NSPNode[4];
		for (int i = 0; i < testArray.length; i++) {
			NSPNode testNode = new NSPNode();
			testNode.shortestWeight = 10 - i;
			testArray[i] = testNode;
		}
		
		BinaryHeap<NSPNode> testHeap = new BinaryHeap<NSPNode>(testArray);
		
		int size = testHeap.size();
		for (int i = 0; i < size; i++) {
			System.out.println(testHeap.pop().shortestWeight);
		}
	}

	public static void testTrie() throws IOException {
		File file = FileUtils.fileFromPath("./words.dic", false);
		String[] words = FileUtils.contentOfFileContent(file, true, "UTF-8").split("\n");
		
		TrieTree trieTree = new TrieTree();
		for (String word : words) {
			trieTree.insert(word);
		}
		
		String searchValue = "天天";
		ArrayList<String> retrieveResult = trieTree.retrieve(searchValue);
		if (retrieveResult.size() != 0) {
			System.out.println(retrieveResult + ": hit");
		}else {
			System.out.println(searchValue + ": not existed.");
		}
	}

	public static void testForward() {
//		double[] testPi = {0.63, 0.17, 0.20};//Sunny, Cloudy, Rainy
//		double[][] transition = {{0.5, 0.375, 0.125}, {0.25, 0.125, 0.625}, {0.25, 0.375, 0.375}};//Sunny, Cloudy, Rainy
//		double[][] confusion = {{0.6, 0.2, 0.15, 0.05}, {0.25, 0.25, 0.25, 0.25}, {0.05, 0.10, 0.35, 0.5}};//Dry, Dryish, Damp, Soggy
		
		double[] testPi = {0.3333, 0.3333, 0.3333};//Sunny, Cloudy, Rainy
		double[][] transition = {{0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}};//Sunny, Cloudy, Rainy
		double[][] confusion = {{0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}};//Dry, Dryish, Damp, Soggy
		int[] testObserved = {Dry, Dry, Dry};
		
//		int[] testObserved = {Dry, Damp, Soggy};
		PiVector piVector = new PiVector(testPi);
		TransitionMatrix transitionMatrix = new TransitionMatrix(transition);
		ConfustionMatrix confustionMatrix = new ConfustionMatrix(confusion);
		ForwardProcessor forwardProcessor = new ForwardProcessor(testObserved, piVector, transitionMatrix, confustionMatrix);
	
		double forwardResult = forwardProcessor.executeForwardAlgorithm();
		System.out.println("forwardResult:" + forwardResult);
	}
	
	public static void testBackward() {
//		double[] testPi = {0.63, 0.17, 0.20};//Sunny, Cloudy, Rainy
//		double[][] transition = {{0.5, 0.375, 0.125}, {0.25, 0.125, 0.625}, {0.25, 0.375, 0.375}};//Sunny, Cloudy, Rainy
//		double[][] confusion = {{0.6, 0.2, 0.15, 0.05}, {0.25, 0.25, 0.25, 0.25}, {0.05, 0.10, 0.35, 0.5}};//Dry, Dryish, Damp, Soggy
		
		double[] testPi = {0.3333, 0.3333, 0.3333};//Sunny, Cloudy, Rainy
		double[][] transition = {{0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}};//Sunny, Cloudy, Rainy
		double[][] confusion = {{0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}};//Dry, Dryish, Damp, Soggy
		int[] testObserved = {Dry, Dry, Dry};
		
//		int[] testObserved = {Dry, Damp, Soggy};
		PiVector piVector = new PiVector(testPi);
		TransitionMatrix transitionMatrix = new TransitionMatrix(transition);
		ConfustionMatrix confustionMatrix = new ConfustionMatrix(confusion);
		BackwardProcessor backwardProcessor = new BackwardProcessor(testObserved, piVector, transitionMatrix, confustionMatrix);
	
		double backwardResult = backwardProcessor.executeBackwardAlgorithm();
		System.out.println("backwardResult:" + backwardResult);
	}

	public static void testViterbi() {
		double[] testPi = {0.333, 0.333, 0.333};
		double[][] transition = {{0.333, 0.333, 0.333}, {0.333, 0.333, 0.333}, {0.333, 0.333, 0.333}};
		double[][] confusion = {{0.5, 0.5}, {0.75, 0.25}, {0.25, 0.75}};
		
		int[] testObserved = {0, 0, 0, 0, 1, 0, 1, 1, 1, 1};
		PiVector piVector = new PiVector(testPi);
		TransitionMatrix transitionMatrix = new TransitionMatrix(transition);
		ConfustionMatrix confustionMatrix = new ConfustionMatrix(confusion);
	
		ViterbiProcessor viterbiProcessor = new ViterbiProcessor(piVector, transitionMatrix, confustionMatrix);
		Vector viterbiResult = viterbiProcessor.executeViterbiAlgorithm(testObserved);
		System.out.println("viterbiResult:" + viterbiResult);
	}
	
	public static void testBaumWelch() {
//		double[] testPi = {0.63, 0.17, 0.20};//Sunny, Cloudy, Rainy
//		double[][] transition = {{0.5, 0.375, 0.125}, {0.25, 0.125, 0.625}, {0.25, 0.375, 0.375}};//Sunny, Cloudy, Rainy
//		double[][] confusion = {{0.6, 0.2, 0.15, 0.05}, {0.25, 0.25, 0.25, 0.25}, {0.05, 0.10, 0.35, 0.5}};//Dry, Dryish, Damp, Soggy
		
		double[] testPi = {0.25, 0.5, 0.25};//Sunny, Cloudy, Rainy
		double[][] transition = {{0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}};//Sunny, Cloudy, Rainy
		double[][] confusion = {{0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}, {0.3333, 0.3333, 0.3333}};//Dry, Dryish, Damp, Soggy
		double[] testObserved = {Dry, Dryish, Damp, Dry};
		
		
		Vector testObservedVector = new Vector(testObserved);
		PiVector piVector = new PiVector(testPi);
		TransitionMatrix transitionMatrix = new TransitionMatrix(transition);
		ConfustionMatrix confustionMatrix = new ConfustionMatrix(confusion);
	
		BaumWelchProcessor viterbiProcessor = new BaumWelchProcessor(testObservedVector, piVector, transitionMatrix, confustionMatrix);
		viterbiProcessor.executeBaumWelchAlgorithm();
		
		System.out.println(piVector.toString());
		System.out.println(transitionMatrix.toString());
		System.out.println(confustionMatrix.toString());
	}
}

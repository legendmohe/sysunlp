package org.sysu.nlp.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sysu.nlp.HMM.ConfustionMatrix;
import org.sysu.nlp.HMM.TransitionMatrix;
import org.sysu.nlp.model.Matrix;
import org.sysu.nlp.util.FileUtils;

public class HMMCorpus {
	
	static final public Integer B = 0;
	static final public Integer M = 1;
	static final public Integer E = 2;
	static final public Integer S = 3;
	
	static final private int K = 5;

	public void trainFileToHMMFile(String trainPath, String transPath, String confPath, String indexPath, String piPath) {
		HashMap<Character, TagObject> tagObjectHashMap = new HashMap<Character, HMMCorpus.TagObject>();
		HashMap<Integer, Integer[]> tagHashMap = new HashMap<Integer, Integer[]>();
		for (int i = 0; i < 4; i++) {
			Integer[] jIntegers = new Integer[4];
			for (int j = 0; j < jIntegers.length; j++) {
				jIntegers[j] = 0;
			}
			tagHashMap.put(i, jIntegers);
		}
		
		BufferedReader sourceReader = null;
		try {
			sourceReader = FileUtils.bufferedReaderFromPath(trainPath, "UTF-8");
			
			this.generateStatisticData(tagObjectHashMap, tagHashMap, sourceReader);
			
			int[] tagSum = new int[4];
			int totalTagSum = 0;
			for (int fromTag = 0; fromTag < tagSum.length; fromTag++) {
				
				int iTagSum = 0;
				Integer[] toTags = tagHashMap.get(fromTag);
				for (int i = 0; i < toTags.length; i++) {
					iTagSum += toTags[i];
				}
				
				tagSum[fromTag] = iTagSum;
				totalTagSum += iTagSum;
			}
			
			ArrayList<Map.Entry<Character, TagObject>> entries = new ArrayList<Map.Entry<Character,TagObject>>();
			entries.addAll(tagObjectHashMap.entrySet());
			
			
			Character[] charIndexArray = generateIndexArray(entries);
			TransitionMatrix aijMatrix = generateTransitionMatrix(tagHashMap,
					tagSum);
			ConfustionMatrix bijmMatrix = generateConfuseMatrix(tagSum, entries);
			double[] piArray = generatePiVector(tagHashMap, tagSum, totalTagSum);
			
			this.saveMatrixToFile(transPath, aijMatrix);
			this.saveMatrixToFile(confPath, bijmMatrix);
			this.saveIndexToFile(indexPath, charIndexArray);
			this.savePiArrayToFile(piPath, piArray);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sourceReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Character[] generateIndexArray(
			ArrayList<Map.Entry<Character, TagObject>> entries) {
		Character[] charIndexArray = new Character[entries.size() + 1];
		int index = 0;
		for (Entry<Character, TagObject> entry : entries) {
			charIndexArray[index] = entry.getKey();
			index++;
		}
		charIndexArray[index] = '#';
		return charIndexArray;
	}

	private double[] generatePiVector(HashMap<Integer, Integer[]> tagHashMap,
			int[] tagSum, int totalTagSum) {
		double[] piArray = new double[tagHashMap.size()];
		for (int i = 0; i < piArray.length; i++) {
			piArray[i] = tagSum[i]*1.0/totalTagSum;
		}
		return piArray;
	}

	private ConfustionMatrix generateConfuseMatrix(int[] tagSum,
			ArrayList<Map.Entry<Character, TagObject>> entries) {
		ConfustionMatrix bijmMatrix = new ConfustionMatrix(4, entries.size() + 1);//
		//Good-Turing smooth
		ArrayList<HashMap<Integer, Integer>> nArrayList = new ArrayList<HashMap<Integer,Integer>>();
		for (int i = 0; i < bijmMatrix.getStatesLength(); i++) {
			nArrayList.add(new HashMap<Integer, Integer>());
		}
		
		int freqSum = 0;
		for (Entry<Character, TagObject> entry : entries) {
			TagObject tagObject = entry.getValue();
			int tempFreqSum = 0;
			for (int i = 0; i < bijmMatrix.getStatesLength(); i++) {
				int occourence = tagObject.ci[i];
				if (occourence != 0) {
					Integer countInteger = nArrayList.get(i).get(occourence);
					if (countInteger == null) {
						countInteger = 0;
					}
					nArrayList.get(i).put(occourence, countInteger + 1);
				}
				
				tempFreqSum += occourence;
			}
			
			freqSum += tempFreqSum;
		}
		
		//generate bij
		int index = 0;
		double unseenWiSum = 0.0;
		double[] sumP = new double[bijmMatrix.getStatesLength()];
		for (Entry<Character, TagObject> entry : entries) {
			TagObject tagObject = entry.getValue();
			
			boolean isUnseen = false;
			for (int i = 0; i < bijmMatrix.getStatesLength(); i++) {
				HashMap<Integer, Integer> nHashMap = nArrayList.get(i);
				double bijValue = 0.0;
				int occourence = tagObject.ci[i];
				if (occourence == 0) {
					occourence += 1.0;
				}
				bijValue = occourence*1.0/tagSum[i];//+1 smooth
				/*
				if (occourence == 0) {
					isUnseen = true;
					continue;
				}else if (occourence > CorpusPreprocessor.K) {//
					bijValue = occourence*1.0/tagSum[i];//
				}else {
					double r = (occourence + 1.0)/occourence*(nHashMap.get(occourence + 1)*1.0/nHashMap.get(occourence));
					double k = (K + 1.0)*nHashMap.get(K + 1)/nHashMap.get(1);
					bijValue = occourence*1.0/tagSum[i];
					bijValue = bijValue*Math.abs(r - k)/Math.abs(1.0 - k)/tagSum[i];
				}
				
				if (bijValue < 0) {
					System.out.println(bijValue + " < 0 :" + tagObject.text);
				}
				*/
				bijmMatrix.set(i, index, bijValue);
				sumP[i] += bijValue;
			}
			index++;
			
			if (isUnseen) {
				unseenWiSum += tagObject.getFreq()*1.0/freqSum;
			}
		}
		
		//smooth
		/*
		index = 0;
		for (Entry<Character, TagObject> entry : entries) {
			TagObject tagObject = entry.getValue();
			for (int j = 0; j < bijmMatrix.getStatesLength(); j++) {
				int occourence = tagObject.ci[j];
				if (occourence == 0) {
					double otherwiseValue = ((1 - sumP[j])/unseenWiSum)*(tagObject.getFreq()*1.0/freqSum);
					bijmMatrix.set(j, index, otherwiseValue);
				}
			}
			index++;
		}
		*/
		for (int i = 0; i < bijmMatrix.getStatesLength(); i++) {
			double sum = 0.0;
			for (int j = 0; j < bijmMatrix.getObservedsLength(); j++) {
				sum += bijmMatrix.get(i, j);
			}
			System.out.println("sumP : " + sum);
		}
		for (int i = 0; i < bijmMatrix.getiLength(); i++) {
//			bijmMatrix.set(i, bijmMatrix.getObservedsLength() - 1,  ((1 - sumP[i])/unseenWiSum)*(1.0/freqSum));//unseen
			bijmMatrix.set(i, bijmMatrix.getObservedsLength() - 1,  1.0/freqSum);//+1 unseen
		}
		
		return bijmMatrix;
	}

	private TransitionMatrix generateTransitionMatrix(
			HashMap<Integer, Integer[]> tagHashMap, int[] tagSum) {
		TransitionMatrix aijMatrix = new TransitionMatrix(4);
		for (int fromTag = 0; fromTag < aijMatrix.getStatesLength(); fromTag++) {
			
			Integer[] toTags = tagHashMap.get(fromTag);
			double ci = tagSum[fromTag];
			for (int toTag = 0; toTag < toTags.length; toTag++) {
				double AijValue = toTags[toTag]*1.0/ci;
				aijMatrix.set(fromTag, toTag, AijValue);
			}
		}
		return aijMatrix;
	}

	private void generateStatisticData(
			HashMap<Character, TagObject> tagObjectHashMap,
			HashMap<Integer, Integer[]> tagHashMap, BufferedReader sourceReader)
			throws IOException {
		String readLineString = null;
		Integer fromTag = null;
		while ((readLineString = sourceReader.readLine()) != null) {
//			readLineString += "\n";
			String[] tokensStrings = readLineString.split("  ");
			for (String terms : tokensStrings) {
				char[] charArray = terms.toCharArray();
				if (charArray.length == 1) {
					char currentChar = charArray[0];
					TagObject tagObject = tagObjectHashMap.get(currentChar);
					if (tagObject == null) {
						tagObject = new TagObject();
						tagObject.text = currentChar;
						tagObjectHashMap.put(currentChar, tagObject);
					}
					tagObject.ci[S]++;
					if (fromTag != null) {//ä»Žä¸Šä¸?¸ªåˆ°è¿™ä¸?						
						tagHashMap.get(fromTag)[S]++;
					}
					fromTag = S;
				}else {
					for (int i = 0; i < charArray.length; i++) {
						char currentChar = charArray[i];
						TagObject tagObject = tagObjectHashMap.get(currentChar);
						if (tagObject == null) {
							tagObject = new TagObject();
							tagObject.text = currentChar;
							tagObjectHashMap.put(currentChar, tagObject);
						}
						
						Integer tag = null;
						if (i == 0) {
							tagObject.ci[B]++;
							tag = B;
						}else if (i == charArray.length - 1) {
							tagObject.ci[E]++;
							tag = E;
						}else {
							tagObject.ci[M]++;
							tag = M;
						}
						
						if (fromTag != null) {
							tagHashMap.get(fromTag)[tag]++;
						}
						fromTag = tag;
					}
				}
			}
		}
	}

	private void saveIndexToFile(String confPath, Character[] charIndexArray)
			throws IOException {
		File saveFileile = FileUtils.fileFromPath(confPath, true);
		FileWriter fileWriter = FileUtils.fileWriteFormFile(saveFileile, false);
		fileWriter.write(charIndexArray.length + "\n");
		for (int i = 0; i < charIndexArray.length; i++) {
			Character character = charIndexArray[i];
			fileWriter.write(character);
			fileWriter.write("\n");
		}
		fileWriter.close();
	}
	
	private void savePiArrayToFile(String confPath, double[] piArray)
			throws IOException {
		File saveFileile = FileUtils.fileFromPath(confPath, true);
		FileWriter fileWriter = FileUtils.fileWriteFormFile(saveFileile, false);
		fileWriter.write(piArray.length + "\n");
		for (int i = 0; i < piArray.length; i++) {
			double pi = piArray[i];
			fileWriter.write(String.valueOf(pi));
			fileWriter.write("\n");
		}
		fileWriter.close();
	}

	private void saveMatrixToFile(String saveFilePath, Matrix matrix)
			throws IOException {
		File saveFileile = FileUtils.fileFromPath(saveFilePath, true);
		FileWriter fileWriter = FileUtils.fileWriteFormFile(saveFileile, false);
		fileWriter.write(matrix.getiLength() + "," + matrix.getjLength() + "\n");
		for (int i = 0; i < matrix.getiLength(); i++) {
			for (int j = 0; j < matrix.getjLength(); j++) {
				fileWriter.write(String.valueOf(matrix.get(i, j)));
				fileWriter.write(" ");
			}
			fileWriter.write("\n");
		}
		
		fileWriter.close();
	}
	
	public static Matrix loadMatrixFromFilePath(String filePath) throws IOException {
		BufferedReader bufferedReader = FileUtils.bufferedReaderFromPath(filePath, "UTF-8");
		String readLineString = null;
		String[] ijLengthString = bufferedReader.readLine().split(",");
		Matrix matrix = new Matrix(Integer.valueOf(ijLengthString[0]), Integer.valueOf(ijLengthString[1]));

		int index = 0;
		while ((readLineString = bufferedReader.readLine()) != null) {
			String[] jArrayStrings = readLineString.split(" ");
			for (int j = 0; j < jArrayStrings.length; j++) {
				matrix.set(index, j, Double.valueOf(jArrayStrings[j]));
			}
			index++;
		}
		
		return matrix;
	}
	
	public static Character[] loadIndexFromFilePath(String filePath) throws IOException {
		BufferedReader bufferedReader = FileUtils.bufferedReaderFromPath(filePath, "UTF-8");
		String readLineString = null;
		String LengthString = bufferedReader.readLine();
		Character[] resultCharacters = new Character[Integer.valueOf(LengthString)];
		
		int index = 0;
		while ((readLineString = bufferedReader.readLine()) != null) {
			char[] charArray = readLineString.toCharArray();
			if (charArray.length != 0) {
				resultCharacters[index++] = charArray[0];
			}
		}
		
		return resultCharacters;
	}
	
	public static double[] loadPiArrayFromFilePath(String filePath) throws IOException {
		BufferedReader bufferedReader = FileUtils.bufferedReaderFromPath(filePath, "UTF-8");
		String readLineString = null;
		String LengthString = bufferedReader.readLine();
		double[] resultArray = new double[Integer.valueOf(LengthString)];
		
		int index = 0;
		while ((readLineString = bufferedReader.readLine()) != null) {
			resultArray[index++] = Double.valueOf(readLineString);
		}
		
		return resultArray;
	}
	
	public class TagObject {
		public char text;
		public int[] ci;
		
		private int freq;
		
		public TagObject() {
			text = 0;
			freq = 0;
			ci = new int[4];
		}
		
		public int getFreq() {
			if (freq == 0) {
				for (int i = 0; i < ci.length; i++) {
					freq += ci[i];
				}
			}
			
			return freq;
		}
	}
}

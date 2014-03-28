package org.sysu.nlp.tokenizer;

import java.util.ArrayList;
import java.util.HashSet;

public class SegmentationTokenizer {
	
	static final public char[] lineSeperator = {';' , '?', '!', '¡£', '£»', '£¿', '£¡', '\n', ' ', '£»'};
	/*
	public static ArrayList<String> doSegmentation(String sourceString) {
		ArrayList<String> resultArrayList = new ArrayList<String>();
		char[] chars = sourceString.toCharArray();
		
		StringBuffer buffer = new StringBuffer(chars.length);
		
		CharType previousType = null;
 		for (int i = 0; i < chars.length; i++) {
			char currentChar = chars[i];
			CharType charType = CheckTypeUtils.checkType(currentChar);
			if (previousType != charType && buffer.length() != 0) {
				resultArrayList.add(buffer.toString());
				buffer.setLength(0);//
			}
			
			buffer.append(currentChar);
			previousType = charType;
		}
 		
 		resultArrayList.add(buffer.toString());//		
 		return resultArrayList;
	}
	*/
	
	public static ArrayList<String> doSentenceSegmentation(String sourceString) {
		ArrayList<String> resultArrayList = new ArrayList<String>();
		char[] chars = sourceString.toCharArray();
		
		HashSet<Character> lineSeperatorSet = new HashSet<Character>();
		for (int i = 0; i < lineSeperator.length; i++) {
			lineSeperatorSet.add(lineSeperator[i]);
		}
		
		StringBuffer buffer = new StringBuffer(chars.length);
 		for (int i = 0; i < chars.length; i++) {
			char currentChar = chars[i];
			if (lineSeperatorSet.contains(currentChar)) {
				if (buffer.length() != 0) {
					resultArrayList.add(buffer.toString());
				}
				resultArrayList.add(String.valueOf(currentChar));
				buffer.setLength(0);//clear
				continue;
			}
			buffer.append(currentChar);
		}
 		if (buffer.length() != 0) {
 			resultArrayList.add(buffer.toString());//	
		}
 		return resultArrayList;
	}
}

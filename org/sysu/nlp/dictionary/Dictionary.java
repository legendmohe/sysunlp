package org.sysu.nlp.dictionary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.sysu.nlp.tree.TrieTree;
import org.sysu.nlp.util.FileUtils;

public class Dictionary {

	public static HashSet<String> loadWordSetFromFilePath(String filePath) {
		File file = null;
		HashSet<String> resultHashSet = null;
		try {
			file = FileUtils.fileFromPath(filePath, false);
			resultHashSet = Dictionary.loadWordSetFromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultHashSet;
	}
	
	public static HashSet<String> loadWordSetFromFile(File file) throws IOException {
		HashSet<String> resultHashSet = new HashSet<String>();
		String[] words = FileUtils.contentOfFileContent(file, true, "UTF-8").split("\n|(\\s+)");
		for (String word : words) {
			resultHashSet.add(word);
		}
		return resultHashSet;
	}
	
	public static TrieTree loadTreeFromFilePath(String[] filePaths) {
		HashSet<String> wordSet = new HashSet<String>(1000);
		for (String path : filePaths) {
			wordSet.addAll(Dictionary.loadWordSetFromFilePath(path));
		}
		TrieTree trieTree = new TrieTree();
		for (String word : wordSet) {
			trieTree.insert(word);
		}
		
		System.out.println("load dictionary: " + wordSet.size() + " words");
		
		return trieTree;
	}
	
	public static void saveWordSetToFile(String filePath, HashSet<String> wordSet) throws IOException{
		File file = FileUtils.fileFromPath(filePath, true);
		FileWriter writer = FileUtils.fileWriteFormFile(file, false);
		for (String word : wordSet) {
			writer.write(word);
			writer.write("\n");
		}
		writer.close();
	}
}

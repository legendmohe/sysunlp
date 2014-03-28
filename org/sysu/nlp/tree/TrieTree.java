package org.sysu.nlp.tree;

import java.util.ArrayList;
import java.util.Stack;


public class TrieTree {
	
	public TrieNode root;
	
	public TrieTree(){
		root = new TrieNode();
	}
	
	public boolean insert(String nodeValue) {
		if (nodeValue.length() == 0) {
			System.err.println("insert empty String");
			return false;
		}
		
		//position
		TrieNode currentNode = root;
		char[] chars = nodeValue.toCharArray();
		int index = 0;
		while(index < chars.length) {
			char aChar = chars[index];
			
			TrieNode childNode = currentNode.getChild(aChar);
			if (childNode == null) {
				break;
			}
			
			currentNode = childNode;
			index++;
		}
		
		if (index == chars.length) {//don't -1
			if (currentNode.isEnd) {
				System.out.println(nodeValue + " is already existed.");
				return false;//already existed
			}
			currentNode.isEnd = true;
			return true;
		}
		
		//insert
		for (int i = index; i < chars.length; i++) {
			char aChar = chars[i];
			TrieNode newNode = new TrieNode();
			newNode.setNodeValue(aChar);
			newNode.setParentNode(currentNode);
			currentNode.addChild(newNode);
			currentNode = newNode;
		}
		currentNode.isEnd = true;
		
		return true;
	}
	
	public boolean delete(String searchValue, boolean match) {
		
		TrieNode endNode = this.searchNode(searchValue, root);

		if (endNode == root) {
			return false;
		}
		if (!endNode.isEnd && !match) {
			return false;
		}
		
		endNode.getParentNode().removeChild(endNode);
		return true;
	}
	
	public TrieNode search(String searchValue) {
		TrieNode endNode = this.searchNode(searchValue, root);
		return endNode;
	}
	
	public ArrayList<String> retrieve(String searchValue){
		Stack<TrieNode> nodeStack = new Stack<TrieNode>();
		ArrayList<String> resultArrayList = new ArrayList<String>();
		StringBuffer retrieveBuffer = new StringBuffer();
		
		TrieNode endNode = this.searchNode(searchValue, root);//find the end node
		if (endNode == root || endNode == null) {
			return resultArrayList;
		}
		String prefixString = this.backtrackingValue(endNode);
		retrieveBuffer.append(prefixString);
		
		if (endNode.isEnd) {
			resultArrayList.add(retrieveBuffer.toString());
		}
		
		//DFS
		for (TrieNode child : endNode.childrenSet()) {
			nodeStack.add(child);
		}
		while (!nodeStack.isEmpty()) {
			TrieNode currentNode = nodeStack.pop();
			for (TrieNode child : currentNode.childrenSet()) {
				nodeStack.add(child);
			}
			
			char currentChar = currentNode.getNodeValue();
			retrieveBuffer.append(currentChar);
			if (currentNode.isEnd) {
				resultArrayList.add(retrieveBuffer.toString());//add searchValue
				if (currentNode.childrenNumber() == 0 && nodeStack.size() != 0) {
					TrieNode parentNode = currentNode;
					while ((parentNode = parentNode.getParentNode()) != null) {//backtracking
						retrieveBuffer.deleteCharAt(retrieveBuffer.length() - 1);//remove last char
						if (parentNode == nodeStack.peek().getParentNode()) {
							break;
						}
					}
				}
			}
		}
		
		return resultArrayList;
	}
	
	public boolean contains(String searchValue) {
		TrieNode endNode = this.searchNode(searchValue, root);
		return endNode.isEnd ? true : false;
	}

	public String backtrackingValue(TrieNode endNode) {
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append(endNode.getNodeValue());
		
		TrieNode parentNode = endNode;
		while((parentNode = parentNode.getParentNode()) != root){
			resultBuffer.append(parentNode.getNodeValue());
		};
		
		String resultString = resultBuffer.reverse().toString();
		return resultString;
	}
	
	//private
	
	private TrieNode searchNode(String nodeValue, TrieNode rootNode) {
		if (rootNode.childrenNumber() == 0) {
			return rootNode;
		}
		
		TrieNode currentNode = rootNode;
		char[] chars = nodeValue.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char aChar = chars[i];
			
			TrieNode childNode = currentNode.getChild(aChar);
			if (childNode == null) {
				return null;
			}
			
			currentNode = childNode;
		}
		return currentNode;
	}
}

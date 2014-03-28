package org.sysu.nlp.tree;

import java.util.Collection;
import java.util.HashMap;

public class TrieNode {
	
	public boolean isEnd;
	private Character nodeValue;
	private TrieNode parentNode;
	private HashMap<Character, TrieNode> childrenHashMap;
	
	public TrieNode(){
		isEnd = false;
	}
	
	private HashMap<Character, TrieNode> getChildrenHashMap() {
		if (this.childrenHashMap == null) {
			childrenHashMap = new HashMap<Character, TrieNode>();
		}
		return childrenHashMap;
	}
	
	public void addChild(TrieNode childNode) {
		this.getChildrenHashMap().put(childNode.getNodeValue(), childNode);
	}
	public TrieNode getChild(char childValue) {
		return this.getChildrenHashMap().get(childValue);
	}
	public void removeChild(TrieNode childNode) {
		this.getChildrenHashMap().remove(childNode.getNodeValue());
	}
	public int childrenNumber() {
		if (this.childrenHashMap == null) {
			return 0;
		}else{
			return this.childrenHashMap.size();
		}
	}
	
	public Collection<TrieNode> childrenSet() {
		return this.getChildrenHashMap().values();
	}
	
	public Character getNodeValue() {
		return nodeValue;
	}
	public void setNodeValue(Character nodeValue) {
		this.nodeValue = nodeValue;
	}
	public TrieNode getParentNode() {
		return parentNode;
	}
	public void setParentNode(TrieNode parentNode) {
		this.parentNode = parentNode;
	}
}

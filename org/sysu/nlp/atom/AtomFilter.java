package org.sysu.nlp.atom;

import java.util.ArrayList;

public abstract class AtomFilter {
	protected AtomFilter atomFilter;
	protected ArrayList<AtomToken> input;
	
	public AtomFilter(AtomFilter filter) {
		this.atomFilter = filter;
	}
	public AtomFilter(ArrayList<AtomToken> input) {
		this.input = input;
	}
	public AtomFilter() {
		
	}
	
	abstract public ArrayList<AtomToken> filter();
}

package org.sysu.nlp.atom;

import java.util.ArrayList;

public class BasicFilter extends AtomFilter {

	public BasicFilter(char[] charArray) {
		super();
		input = new ArrayList<AtomToken>();
		for (int i = 0; i < charArray.length; i++) {
			input.add(new AtomToken(charArray[i]));
		}
	}

	@Override
	public ArrayList<AtomToken> filter() {
		return this.input;
	}
}

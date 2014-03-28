package org.sysu.nlp.atom;

import java.util.ArrayList;

import org.sysu.nlp.atom.AtomToken.AtomType;

public class LetterFilter extends AtomFilter {

	public LetterFilter(AtomFilter filter) {
		super(filter);
	}
	
	@Override
	public ArrayList<AtomToken> filter() {
		if (this.atomFilter == null) {
			System.err.println("empty decorator filter.");
			return null;
		}
		input = this.atomFilter.filter();
		
		ArrayList<AtomToken> outputArrayList = new ArrayList<AtomToken>();
		StringBuffer stringBuffer = new StringBuffer();
		for (AtomToken token : input) {
			
			if (token.type == AtomType.LETTER) {
				stringBuffer.append(token.text);
				continue;
			}else {
				if (stringBuffer.length() != 0) {
					outputArrayList.add(new AtomToken(stringBuffer.toString(), AtomType.LETTER));
				}
				
				outputArrayList.add(token);
				stringBuffer.setLength(0);
			}
		}
		if (stringBuffer.length() != 0) {
			outputArrayList.add(new AtomToken(stringBuffer.toString(), AtomType.LETTER));
		}
		return outputArrayList;
	}

}

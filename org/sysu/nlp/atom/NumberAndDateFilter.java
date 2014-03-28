package org.sysu.nlp.atom;

import java.util.ArrayList;

import org.sysu.nlp.atom.AtomToken.AtomType;

public class NumberAndDateFilter extends AtomFilter {

	public NumberAndDateFilter(AtomFilter filter) {
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
			
			if (token.type == AtomType.NUMBER) {
				stringBuffer.append(token.text);
				continue;
			}else {
				if (stringBuffer.length() != 0) {
					if (token.type == AtomType.DATE) {
						stringBuffer.append(token.text);
						if (!token.text.equals(":") && !token.text.equals("£º")) {
							outputArrayList.add(new AtomToken(stringBuffer.toString(), AtomType.NUMBER));
							stringBuffer.setLength(0);
						}
						continue;
					}
					outputArrayList.add(new AtomToken(stringBuffer.toString(), AtomType.NUMBER));
				}
				
				outputArrayList.add(token);
				stringBuffer.setLength(0);
			}
		}
		if (stringBuffer.length() != 0) {
			outputArrayList.add(new AtomToken(stringBuffer.toString(), AtomType.NUMBER));
		}
		return outputArrayList;
	}

}

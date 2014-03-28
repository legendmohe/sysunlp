package org.sysu.nlp.atom;

import java.util.HashSet;

import org.sysu.nlp.util.CheckTypeUtils;
import org.sysu.nlp.util.CheckTypeUtils.CharType;

public class AtomToken {
	
	private static HashSet<String> numberSet;
	private static HashSet<String> dateSet;
	private static final String numberIndicator = ".%��-1234567890һ�����������߰˾�ʮ��ǧ�򣱣���������������������Ҽ��������½��ƾ�ʰ��Ǫ�f��";
	private static final String[] dateIndicator = {"��","��","��","ʱ","��","��", ":", "��"};
	
	public enum AtomType {
		DATE, //2010��
	    NUMBER, //1234 333
	    LETTER, //we you
	    CHINESE,// ��������
	    SEPERATOR,// �ָ���
	    OTHER
	}
	
	public String text;
	public AtomType type;
	
	public AtomToken(String text, AtomType type) {
		this.text = text;
		this.type = type;
	}
	
	public AtomToken(char aChar) {
		this.text = String.valueOf(aChar);
		
		if (AtomToken.getNumberIndocators().contains(this.text)) {
			this.type = AtomType.NUMBER;
		}else if (AtomToken.getDateIndocators().contains(this.text)) {
			this.type = AtomType.DATE;
		}else {
			CharType charType = CheckTypeUtils.checkType(aChar);
			switch (charType) {
			case NUM:
				this.type = AtomType.NUMBER;
				break;
			case CHINESE:
				this.type = AtomType.CHINESE;
				break;
			case LETTER:
				this.type = AtomType.LETTER;
				break;
			case DELIMITER:
				this.type = AtomType.SEPERATOR;
				break;
			default:
				this.type = AtomType.OTHER;
				break;
			}
		}
	}
	
	private static HashSet<String> getNumberIndocators() {
		if (numberSet == null) {
			numberSet = new HashSet<String>();
			for (int i = 0; i < numberIndicator.length(); i++) {
				numberSet.add(String.valueOf(numberIndicator.charAt(i)));
			}
		}
		return numberSet;
	}
	private static HashSet<String> getDateIndocators() {
		if (dateSet == null) {
			dateSet = new HashSet<String>();
			for (int i = 0; i < dateIndicator.length; i++) {
				dateSet.add(String.valueOf(dateIndicator[i]));
			}
		}
		return dateSet;
	}
	
	public String toString() {
		return this.text;
	}
}

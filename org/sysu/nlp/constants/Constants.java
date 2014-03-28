package org.sysu.nlp.constants;

import java.io.File;

public class Constants {
	public static final String DICTIONARY_FILE_PATH =  "." + File.separator + "words.dic" ;
	public static final String TRAIN_FILE_PATH =  "." + File.separator + "pku_training.utf8" ;
	public static final String TEST_FILE_PATH =  "." + File.separator + "pku_test.utf8" ;
	public static final String RESULT_FILE_PATH =  "." + File.separator + "pku_result.txt" ;
	
	public static final String INDEX_FILE_PATH =  "." + File.separator + "HMMData" + File.separator + "Char2Index.txt" ;
	public static final String TRANSITION_FILE_PATH =  "." + File.separator + "HMMData" + File.separator + "transition.txt" ;
	public static final String CONFUSE_FILE_PATH =  "." + File.separator + "HMMData" + File.separator + "confusion.txt" ;
	public static final String PI_FILE_PATH =  "." + File.separator + "HMMData" + File.separator + "pi.txt" ;
}

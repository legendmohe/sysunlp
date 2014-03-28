package org.sysu.nlp.corpus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.sysu.nlp.util.FileUtils;

public class ResultParser {
	public static void saveResultToFilePath(ArrayList<String> resultArrayList, String filePath) {
		File resultFile = null;
		FileWriter fileWriter = null ;
		try {
			resultFile = FileUtils.fileFromPath(filePath, true);
			fileWriter = FileUtils.fileWriteFormFile(resultFile, false);
			
			for (String token : resultArrayList) {
				fileWriter.append(token);
				if (!token.equals("\n")) {
					fileWriter.append("  ");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

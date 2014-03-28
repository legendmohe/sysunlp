package org.sysu.nlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.sysu.nlp.model.Matrix;

public class ModelUtils {
	public static Matrix loadMatrixFromFilePath(String filePath) throws IOException {
		BufferedReader bufferedReader = FileUtils.bufferedReaderFromPath(filePath, "UTF-8");
		String readLineString = null;
		String[] ijLengthString = bufferedReader.readLine().split(",");
		Matrix matrix = new Matrix(Integer.valueOf(ijLengthString[0]), Integer.valueOf(ijLengthString[1]));

		int index = 0;
		while ((readLineString = bufferedReader.readLine()) != null) {
			String[] jArrayStrings = readLineString.split(" ");
			for (int j = 0; j < jArrayStrings.length; j++) {
				matrix.set(index, j, Double.valueOf(jArrayStrings[j]));
			}
			index++;
		}
		
		return matrix;
	}
	public static void saveMatrixToFile(String saveFilePath, Matrix matrix)
			throws IOException {
		File saveFileile = FileUtils.fileFromPath(saveFilePath, true);
		FileWriter fileWriter = FileUtils.fileWriteFormFile(saveFileile, false);
		fileWriter.write(matrix.getiLength() + "," + matrix.getjLength() + "\n");
		for (int i = 0; i < matrix.getiLength(); i++) {
			for (int j = 0; j < matrix.getjLength(); j++) {
				fileWriter.write(String.valueOf(matrix.get(i, j)));
				fileWriter.write(" ");
			}
			fileWriter.write("\n");
		}
		
		fileWriter.close();
	}
}

package sqlenumerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CSVReader {

	private BufferedReader reader;
	private String[] columns;

	public CSVReader(File file) throws IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "MS932"));
	}

	public boolean readLine() throws IOException {
		String line = reader.readLine();
		if (line == null) {
			return false;
		}
		columns = line.split(",");
		for (int i = 0; i < columns.length; ++i) {
			String str = columns[i];
			if (str.startsWith("\"") && str.endsWith("\"")) {
				columns[i] = str.substring(1, str.length() - 1);
			}
		}
		return true;
	}

	public String column(int i) {
		if (i >= columns.length || i < 0) {
			throw new IndexOutOfBoundsException(String.format("specified index:%d but line has %d column(s)."));
		}
		return columns[i];
	}
}

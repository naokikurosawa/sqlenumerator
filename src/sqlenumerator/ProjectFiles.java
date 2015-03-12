package sqlenumerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;

public class ProjectFiles {
	private Map<String, String> files = new HashMap<String, String>();

	private class JavaFileReader {
		StringBuilder b = new StringBuilder();
		char[] buf = new char[1024];

		String read(File file) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			b.setLength(0);
			int len;
			while ((len = reader.read(buf, 0, buf.length)) >= 0) {
				b.append(buf, 0, len);
			}
			reader.close();
			return b.toString();
		}
	}

	public ProjectFiles(Project project, String projectName, List<FileSet> filesets) throws IOException {
		JavaFileReader reader = new JavaFileReader();
		for (FileSet fileset : filesets) {
			for (Iterator<Resource> iter = fileset.iterator(); iter.hasNext();) {
				String name = iter.next().getName();
				if (!name.startsWith(projectName)) {
					continue;
				}
				files.put(name, reader.read(new File(project.getBaseDir(), name)));
			}

		}
	}

	public int count(String id) {
		Pattern p = Pattern.compile("\\." + id + "\\(");
		int count = 0;
		for (String text : files.values()) {
			Matcher m = p.matcher(text);
			while (m.find()) {
				++count;
			}
		}
		return count;
	}
}

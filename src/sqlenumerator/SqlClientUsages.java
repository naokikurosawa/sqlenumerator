package sqlenumerator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class SqlClientUsages extends Task {

	private List<FileSet> filesets = new LinkedList<FileSet>();
	private File sqllist;
	private File outfile;
	private PrintWriter writer;

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void setOutfile(File file) {
		outfile = file;
	}

	public void setSqllist(File file) {
		sqllist = file;
	}

	@Override
	public void execute() throws BuildException {
		try {
			CSVReader reader = new CSVReader(sqllist);
			writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outfile)),
				"MS932"));

			ProjectFiles projectFiles = null;
			String projectName = "";
			while (reader.readLine()) {
				String filename = reader.column(0);
				String nextProjectName = filename.split("\\\\")[0];
				if (!projectName.equals(nextProjectName)) {
					projectFiles = changeProject(nextProjectName);
					projectName = nextProjectName;
				}

				String id = reader.column(1);
				int count = projectFiles.count(id);

				writer.format("\"%s\",\"%s\",%d", filename, id, count);
				writer.println();
			}

			writer.flush();
			writer.close();

		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	protected ProjectFiles changeProject(String projectName) throws IOException {
		return new ProjectFiles(getProject(), projectName, filesets);
	}

}

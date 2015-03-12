package sqlenumerator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;

public class EnumFiles extends Task {

	private File outfile;
	private List<FileSet> filesets = new LinkedList<FileSet>();

	public void setOutfile(File file) {
		outfile = file;
	}

	@Override
	public void execute() throws BuildException {
		try {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(
				outfile)), "MS932"));
			int count = 0;
			for (FileSet fileset : filesets) {
				for (Iterator<Resource> iter = fileset.iterator(); iter.hasNext();) {
					Resource res = iter.next();
					writer.print(res.getName());
					writer.println();
					++count;
				}
			}
			writer.format("%d files", count);
			writer.println();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}
}

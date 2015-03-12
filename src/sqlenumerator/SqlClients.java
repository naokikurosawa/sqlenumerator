package sqlenumerator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SqlClients extends Task {

	private List<FileSet> filesets = new LinkedList<FileSet>();
	private File outfile;
	private PrintWriter writer;

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void setOutfile(File file) {
		outfile = file;
	}

	@Override
	public void execute() throws BuildException {
		try {
			writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outfile)),
				"MS932"));

			for (FileSet fileset : filesets) {
				doFileSet(fileset);
			}
			writer.flush();
			writer.close();

		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	public void doFileSet(FileSet fileset) throws Exception {
		for (Iterator<Resource> iter = fileset.iterator(); iter.hasNext();) {
			Resource res = iter.next();
			doResource(res);
		}
	}

	public void doResource(Resource resource) throws Exception {
		File targetFile = new File(getProject().getBaseDir(), resource.getName());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(targetFile);
		Element mapper = doc.getDocumentElement();
		if (!mapper.getNodeName().equals("mapper")) {
			throw new BuildException(resource.getName() + " root element is not \"mapper\"");
		}
		NodeList children = mapper.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child instanceof Element) {
				writer.format("\"%s\",\"%s\",\"%s\"", resource.getName(), child.getAttributes().getNamedItem("id")
					.getTextContent(), child.getNodeName());
				writer.println();
			}
		}
	}
}

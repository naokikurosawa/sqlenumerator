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

public class ContextFiles extends Task {

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
		Element root = doc.getDocumentElement();
		if (!root.getNodeName().equals("beans")) {
			throw new BuildException(resource.getName() + " root element is not \"beans\"");
		}

		NodeList beans = root.getElementsByTagName("bean");

		for (int i = 0; i < beans.getLength(); ++i) {
			Node bean = beans.item(i);
			if (!(bean instanceof Element)) {
				continue;
			}
			NodeList properties = ((Element) bean).getElementsByTagName("property");
			for (int j = 0; j < properties.getLength(); ++j) {
				Node property = properties.item(j);
				if (!(property instanceof Element)) {
					continue;
				}
				if (!property.getAttributes().getNamedItem("name").getTextContent().equals("sql")) {
					continue;
				}
				NodeList values = ((Element) property).getElementsByTagName("value");
				for (int k = 0; k < values.getLength(); ++k) {
					// Node value = values.item(k);
					writer.format("\"%s\",\"%s\"", resource.getName(), bean.getAttributes().getNamedItem("id")
						.getTextContent());
					writer.println();
				}
			}
		}
	}
}

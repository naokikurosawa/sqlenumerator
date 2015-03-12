package sqlenumerator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SampleTask extends Task {

	private String msg = "";

	public void setMessage(String m) {
		msg = m;
	}

	@Override
	public void execute() throws BuildException {
		System.out.println(msg);
	}

}

package eclipse_closure_builder.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.google.javascript.jscomp.BasicErrorManager;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.JSError;

public class ReportingErrorManager extends BasicErrorManager {

	private static final String MARKER_TYPE = "Eclipse_Closure_Builder.xmlProblem";
	
	final IFile file;
	
	public ReportingErrorManager(final IFile file){
		this.file = file;
	}
	
	@Override
	protected void printSummary() {
		// NO-OP

	}

	@Override
	public void println(CheckLevel level, JSError error) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, error.description);
			marker.setAttribute(IMarker.SEVERITY, getSeverity(level));
			marker.setAttribute(IMarker.LINE_NUMBER, error.lineNumber);
		} catch (CoreException e) {
			// TODO remove after testing
			e.printStackTrace();
		}
	}

	
	public int getSeverity(CheckLevel level){		
	    switch (level) {
	      case ERROR:
	    	return IMarker.SEVERITY_ERROR;
	      case WARNING:
	        return IMarker.SEVERITY_WARNING;
	      case OFF:
	      default:
	    	  return IMarker.SEVERITY_INFO;
	    }
	}
}

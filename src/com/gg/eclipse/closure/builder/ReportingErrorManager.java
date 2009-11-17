package com.gg.eclipse.closure.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.ErrorManager;
import com.google.javascript.jscomp.JSError;

/**
 * Implements ErrorManager to provide compiler error feedback to the user.
 * 
 * The compiler notifies us of compilation errors and we create the appropriate
 * file marker to inform the user of the error.
 * <br>
 * <br><b>Note:</b>
 * This implementation does not provide the full ErrorManager implementation
 * since we are only interested in being notified of errors and do not require
 * full error enumeration.
 * 
 * @author gg
 */
public class ReportingErrorManager implements ErrorManager {

	public static final String MARKER_TYPE = "com.gg.eclipse.closure.jsProblem";
	
	final IFile file;

	private double typedPercent;
	
	public ReportingErrorManager(final IFile file){
		this.file = file;
	}
	

	@Override
	public void report(CheckLevel level, JSError error) {
		System.out.println("println, reporting level: "+level+" error level: "+error.level);
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, error.description);
			marker.setAttribute(IMarker.SEVERITY, getMarkerSeverity(level));
			marker.setAttribute(IMarker.LINE_NUMBER, error.lineNumber);
		} catch (CoreException e) {}
		
	}

	/**
	 * Maps the compiler error level to the equivalent marker severity level.
	 * 
	 * @param level
	 *            The compiler error level.
	 * @return The equivalent marker severity.
	 */
	public int getMarkerSeverity(CheckLevel level){		
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

	@Override
	public void generateReport() {
	}

	@Override
	public int getErrorCount() {
		return 0;
	}

	@Override
	public JSError[] getErrors() {
		return new JSError[0];
	}

	@Override
	public double getTypedPercent() {
		return typedPercent;
	}

	@Override
	public int getWarningCount() {
		return 0;
	}

	@Override
	public JSError[] getWarnings() {
		return new JSError[0];
	}


	@Override
	public void setTypedPercent(double percent) {
		typedPercent = percent;
	}
}

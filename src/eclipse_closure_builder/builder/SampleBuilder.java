package eclipse_closure_builder.builder;

import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.WarningLevel;


public class SampleBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkJS(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkJS(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkJS(resource);
			//return true to continue visiting children.
			return true;
		}
	}

	
	public static final String BUILDER_ID = "Eclipse_Closure_Builder.sampleBuilder";

	private static final String MARKER_TYPE = "Eclipse_Closure_Builder.xmlProblem";


/*
	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("unchecked")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	
	private void checkJS(IResource resource){
		if (resource instanceof IFile && !resource.isDerived()
				&& resource.getName().endsWith(".js")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			runClosureCompile(file);
		}
	}
	
	private void runClosureCompile(IFile file){
		
		Compiler.setLoggingLevel(Level.SEVERE);
		Compiler jsCompiler = new Compiler(new ReportingErrorManager(file));
		
		CompilerOptions options = getCompilerOptions();
		
		final JSSourceFile source;
		try {
			source = JSSourceFile.fromInputStream(file.getName(), file.getContents());
		} catch (Exception e) {
			// TODO remove after testing
			e.printStackTrace();
			return;
		}
		
		jsCompiler.compile(new JSSourceFile[]{}, new JSSourceFile[]{source}, options);
	}


	/**
	 * @return
	 */
	private CompilerOptions getCompilerOptions() {
		CompilerOptions options = new CompilerOptions();
		WarningLevel.VERBOSE.setOptionsForWarningLevel(options);
		//options.ideMode = true;
		return options;
	}


	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
		}
	}


	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
}

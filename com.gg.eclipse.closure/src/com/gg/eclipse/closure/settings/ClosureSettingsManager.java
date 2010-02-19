package com.gg.eclipse.closure.settings;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

/**
 * The settings manager handles the persistence of IClosureSettings
 * on a specified IResource.
 * 
 * The settings are persisted on an IResource using the persistent properties
 * of that resource.
 */
public class ClosureSettingsManager {

	private static final String DEFAULT_OUTPUT_FILE = "compiled.js";
	private static final String DEFAULT_OUTPUT_FOLDER = "bin";
	
	private static final String QUALIFIER = "com.gg.eclipse.closure.settings";
	private static final String OUTPUT_PATH_PROPERTY = "CLOSURE_OUTPUT_PATH";
	private static final String OUTPUT_FILE_PROPERTY = "CLOSURE_OUTPUT_FILE";

	private static final QualifiedName OUTPUT_PATH_QNAME = new QualifiedName(QUALIFIER, OUTPUT_PATH_PROPERTY);
	private static final QualifiedName OUTPUT_FILE_QNAME = new QualifiedName(QUALIFIER, OUTPUT_FILE_PROPERTY);
	
	private final IResource rsrc;
	
	/**
	 * Constructs an instance of ClosureSettingsManager for
	 * the specified resource.
	 * 
	 * @param resource The IResource to use for persistence.
	 */
	public ClosureSettingsManager(final IResource resource){
		this.rsrc = resource;
	}
	
	/**
	 * Provides the default IClosureSettings.
	 * 
	 * @return The default IClosureSettings.
	 */
	public static IClosureSettings getDefaultSettings(){
		final IPath outputFolder = Path.fromPortableString(DEFAULT_OUTPUT_FOLDER);
		final String outputFile = DEFAULT_OUTPUT_FILE;
		
		return new ClosureSettings(outputFolder, outputFile);
	}
	
	/**
	 * Retrieves the IClosureSettings of the provided IResource.
	 * 
	 * @return The IClosureSettings for the provided IResource.
	 * @throws CoreException If there is a problem retrieving
	 * the properties from the provided IResource.
	 */
	public IClosureSettings getSettings() throws CoreException{
		final IPath outputFolder = getOutputFolder(rsrc, rsrc.getFullPath());
		final String outputFile = getOutputFile(rsrc); 
		
		return new ClosureSettings(outputFolder, outputFile);
	}
	
	/**
	 * Stores the IClosureSettings of the provided IResource.
	 * 
	 * @param settings The IClosureSettings to be stored for the provided IResource.
	 * @throws CoreException If there is a problem storing the
	 * properties on the provided IResource.
	 */
	public void storeSettings(final IClosureSettings settings) throws CoreException{
		setOutputFolder(rsrc, settings.getOutputFolder());
		setOutputFile(rsrc, settings.getOutputFile());
	}
	
	private IPath getOutputFolder(final IResource resource, final IPath basePath) throws CoreException{
		String outputFolder = resource.getPersistentProperty(OUTPUT_PATH_QNAME);
		if(outputFolder == null){
			outputFolder = DEFAULT_OUTPUT_FOLDER;
		}
		
		final IPath relativeOutput = Path.fromPortableString(outputFolder);
		return relativeOutput;
	}

	private void setOutputFolder(final IResource resource, final IPath relativePath) throws CoreException{
		resource.setPersistentProperty(OUTPUT_PATH_QNAME, relativePath.toPortableString());
	}
	
	private String getOutputFile(final IResource resource) throws CoreException{
		String outputFile = resource.getPersistentProperty(OUTPUT_FILE_QNAME);
		if(outputFile == null){
			outputFile = DEFAULT_OUTPUT_FILE;
		}
		
		return outputFile;
	}
	
	private void setOutputFile(final IResource resource, final String outputFile) throws CoreException{
		resource.setPersistentProperty(OUTPUT_FILE_QNAME, outputFile);
	}
}

package com.gg.eclipse.closure.settings;

import org.eclipse.core.runtime.IPath;

/**
 * The default implementation of the IClosureSettings interface.
 */
public class ClosureSettings implements IClosureSettings {

	private final IPath outputFolder;
	private final String outputFile;
	
	
	public ClosureSettings(final IPath outputFolder, final String outputFile){
		this.outputFolder = outputFolder;
		this.outputFile = outputFile;
	}
	
	public IPath getOutputFolder(){
		return outputFolder;
	}
	
	public String getOutputFile(){
		return outputFile;
	}
}

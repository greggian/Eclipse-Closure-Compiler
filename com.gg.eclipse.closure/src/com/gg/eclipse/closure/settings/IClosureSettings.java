package com.gg.eclipse.closure.settings;

import org.eclipse.core.runtime.IPath;

/**
 * Closure settings are the configurable compiler properties accepted
 * by the closure compiler. These properties may be the standard defaults
 * or configured by the user.
 */
public interface IClosureSettings {

	/**
	 * The output folder is the path, relative to the current project,
	 * to use for the output of the compiler.
	 * 
	 * @return The path to use for compiler output.
	 */
	public IPath getOutputFolder();

	/**
	 * The output file is the file name, with extension, to use
	 * for the output of the compiler. 
	 * 
	 * @return The filename to use for compiler output.
	 */
	public String getOutputFile();

}
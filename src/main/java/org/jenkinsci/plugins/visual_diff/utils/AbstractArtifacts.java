package org.jenkinsci.plugins.visual_diff.utils;

import hudson.FilePath;
import hudson.model.BuildListener;

import java.io.IOException;

/**
 * Abstract-class to handle file access and file management
 *
 * @author Marcel Erz
 */
abstract public class AbstractArtifacts {

    /**
     * Creates a folder if it does not exist
     *
     * @param path Path for folder
     * @param listener Listener for console
     * @throws InterruptedException
     * @throws java.io.IOException
     */
    protected void _createFolderIfNotExist(FilePath path, BuildListener listener) throws InterruptedException, IOException {
        if (!path.exists()) {
            listener.getLogger().println("Create folder: " + path.getRemote());
            path.mkdirs();
        }
    }
}

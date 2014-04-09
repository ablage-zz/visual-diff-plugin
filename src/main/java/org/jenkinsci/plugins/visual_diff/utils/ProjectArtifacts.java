package org.jenkinsci.plugins.visual_diff.utils;

import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;

import java.io.IOException;

/**
 * Class to handle file access and file management for project artifacts
 *
 * @author Marcel Erz
 */
public class ProjectArtifacts extends AbstractArtifacts {

    /**
     * Current project
     */
    private final AbstractProject<?,?> project;


    /**
     * Initializes the project artifacts utility class
     *
     * @param project Current project
     */
    public ProjectArtifacts(AbstractProject<?, ?> project) {
        this.project = project;
    }


    /**
     * Path to visual-diff project folder
     *
     * @return Path to vdiff project folder
     */
    public FilePath getPath() {
        return new FilePath(project.getRootDir()).child("vDiff");
    }

    /**
     * Gets a list of paths for all screens
     *
     * @return Array of screen paths
     * @throws InterruptedException
     * @throws IOException
     */
    public FilePath[] getScreens() throws InterruptedException, IOException {
        return getPath().list("*");
    }


    /**
     * Gets the screens
     * @param name Name of screen
     * @return Path for screen
     */
    public FilePath getScreenPath(String name) {
        return getPath().child(name);
    }


    /**
     * Does screen exist?
     *
     * @param name Name of screen
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasScreen(String name) throws InterruptedException, IOException {
        return getScreenPath(name).exists();
    }

    /**
     * Does screen exist?
     *
     * @param file Path of screen
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasScreen(FilePath file) throws InterruptedException, IOException {
        return hasScreen(file.getName());
    }


    /**
     * Creates all required folders
     *
     * @param listener Listener for console
     * @throws InterruptedException
     * @throws IOException
     */
    public void createFolders(BuildListener listener) throws InterruptedException, IOException {
        _createFolderIfNotExist(getPath(), listener);
    }
}

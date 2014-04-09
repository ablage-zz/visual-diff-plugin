package org.jenkinsci.plugins.visual_diff.utils;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.io.IOException;

/**
 * Class to handle file access and file management for build artifacts
 *
 * @author Marcel Erz
 */
public class BuildArtifacts extends AbstractArtifacts {

    /**
     * Current build
     */
    private final AbstractBuild<?,?> build;


    /**
     * Initializes the build artifacts utility class
     *
     * @param build Current build
     */
    public BuildArtifacts(AbstractBuild<?, ?> build) {
        this.build = build;
    }


    /**
     * Path to visual-diff build folder
     *
     * @return Path
     */
    public FilePath getPath() {
        return new FilePath(build.getRootDir()).child("vDiff");
    }


    /**
     * Path to build screens folder
     *
     * @return Path
     */
    public FilePath getBuildScreensPath() {
        return getPath().child("build");
    }

    /**
     * Path to build diffs folder
     *
     * @return Path
     */
    public FilePath getBuildDiffsPath() {
        return getPath().child("diff");
    }

    /**
     * Path to approved screens folder
     *
     * @return Path
     */
    public FilePath getApprovedScreensPath() {
        return getPath().child("approved");
    }



    /**
     * Gets a list of paths for all build-screens
     *
     * @return Array of screen paths
     * @throws InterruptedException
     * @throws IOException
     */
    public FilePath[] getBuildScreens() throws InterruptedException, IOException {
        return getBuildScreensPath().list("*");
    }

    /**
     * Gets a list of paths for all diffs
     *
     * @return Array of screen paths
     * @throws InterruptedException
     * @throws IOException
     */
    public FilePath[] getBuildDiffs() throws InterruptedException, IOException {
        return getBuildDiffsPath().list("*");
    }

    /**
     * Gets a list of paths for all approved-screens
     *
     * @return Array of screen paths
     * @throws InterruptedException
     * @throws IOException
     */
    public FilePath[] getApprovedScreens() throws InterruptedException, IOException {
        return getApprovedScreensPath().list("*");
    }


    /**
     * Path to build screen
     *
     * @param name Name of build-screen
     * @return Path
     */
    public FilePath getBuildScreenPath(String name) {
        return getBuildScreensPath().child(name);
    }

    /**
     * Path to build diff
     *
     * @param name Name of diff
     * @return Path
     */
    public FilePath getBuildDiffPath(String name) {
        return getBuildDiffsPath().child(name);
    }

    /**
     * Path to approved screen
     *
     * @param name Name of approved-screen
     * @return Path
     */
    public FilePath getApprovedScreenPath(String name) {
        return getApprovedScreensPath().child(name);
    }


    /**
     * Does build-screen exist?
     *
     * @param name Name of build-screen
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasBuildScreen(String name) throws InterruptedException, IOException {
        return getBuildScreenPath(name).exists();
    }

    /**
     * Does build-screen exist?
     *
     * @param file Path of build-screen
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasBuildScreen(FilePath file) throws InterruptedException, IOException {
        return hasBuildScreen(file.getName());
    }


    /**
     * Does diff exist?
     *
     * @param name Name of diff
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasDiffScreen(String name) throws InterruptedException, IOException {
        return getBuildDiffPath(name).exists();
    }

    /**
     * Does diff exist?
     *
     * @param file Path of diff
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasDiffScreen(FilePath file) throws InterruptedException, IOException {
        return hasDiffScreen(file.getName());
    }


    /**
     * Does approved-screen exist?
     *
     * @param name Name of approved-screen
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasApprovedScreen(String name) throws InterruptedException, IOException {
        return getApprovedScreenPath(name).exists();
    }

    /**
     * Does approved-screen exist?
     *
     * @param file Path of approved-screen
     * @return Exists?
     * @throws InterruptedException
     * @throws IOException
     */
    public boolean hasApprovedScreen(FilePath file) throws InterruptedException, IOException {
        return hasApprovedScreen(file.getName());
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

        _createFolderIfNotExist(getBuildScreensPath(), listener);
        _createFolderIfNotExist(getBuildDiffsPath(), listener);
        _createFolderIfNotExist(getApprovedScreensPath(), listener);
    }


    /**
     * Archives all screens from the build
     *
     * @param buildFiles List of all files from the build
     * @throws InterruptedException
     * @throws IOException
     */
    public void archiveBuildResults(FilePath[] buildFiles) throws InterruptedException, IOException {
        FilePath dstPath = getBuildScreensPath();
        for(FilePath file : buildFiles) {
            file.copyTo(dstPath.child(file.getName()));
        }
    }

    /**
     * Copies all approved project screens into the build archive
     */
    public void duplicateApprovedProjectScreen() throws InterruptedException, IOException {

        ProjectArtifacts projectArtifacts = new ProjectArtifacts(build.getProject());
        FilePath[] approvedFiles = projectArtifacts.getScreens();

        FilePath dstPath = getApprovedScreensPath();
        for(FilePath file : approvedFiles) {
            file.copyTo(dstPath.child(file.getName()));
        }
    }
}

package org.jenkinsci.plugins.visual_diff.comparison;

import hudson.DescriptorExtensionList;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;

import org.jenkinsci.plugins.visual_diff.data.Screen;
import org.jenkinsci.plugins.visual_diff.data.ScreenList;
import org.jenkinsci.plugins.visual_diff.utils.BuildArtifacts;
import org.jenkinsci.plugins.visual_diff.utils.ProjectArtifacts;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.Serializable;

/**
 * Comparison entry - describable
 *
 * @author Marcel Erz
 */
public abstract class ComparisonDescribable implements Serializable, Describable<ComparisonDescribable> {

    public static final String FAILED = "failed";
    public static final String UNSTABLE = "unstable";
    public static final String NOTHING = "nothing";


    /**
     * Path to screens
     */
    private final String screensPath;

    /**
     * Auto-approve new screens
     */
    private final Boolean autoApprove;

    /**
     * Mark build as...
     */
    private final String markAs;

    /**
     * Number of differences for build result change to trigger
     */
    private final int numberOfDifferences;


    /**
     * Comparison
     *
     * @param screensPath Path to screens
     * @param autoApprove Auto-approve
     * @param markAs Mark build as ...
     * @param numberOfDifferences Number of differences when build result changes
     */
    @DataBoundConstructor
    public ComparisonDescribable(String screensPath,
                                 Boolean autoApprove,
                                 String markAs,
                                 int numberOfDifferences) {
        this.screensPath = screensPath;
        this.autoApprove = autoApprove;
        this.markAs = markAs;
        this.numberOfDifferences = numberOfDifferences;
    }


    /**
     * Screens path
     *
     * @return Path of screens
     */
    public String getScreensPath() {
        return screensPath;
    }

    /**
     * Auto approve new images
     *
     * @return Auto-approval
     */
    public Boolean getAutoApprove() {
        return autoApprove;
    }

    /**
     * Mark build as ..
     *
     * @return Result
     */
    public String getMarkAs() {
        return markAs;
    }

    /**
     * Number of differences when build result changes
     *
     * @return Number of differences
     */
    public int getNumberOfDifferences() {
        return numberOfDifferences;
    }


    /**
     * Processes all screens for comparison
     *
     * @param build Current build
     * @param launcher Launcher
     * @param listener Listener for console
     * @return List of screens that were processed
     * @throws InterruptedException
     * @throws IOException
     */
    public ScreenList processAll(AbstractBuild build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        ScreenList screenList = new ScreenList();
        BuildArtifacts buildArtifacts = new BuildArtifacts(build);

        listener.getLogger().println("Copy build results...");
        buildArtifacts.archiveBuildResults(build.getWorkspace().list(screensPath));

        // Walk through all screens
        listener.getLogger().println("Compare screens...");
        FilePath[] buildResultPaths = buildArtifacts.getBuildScreens();
        for(FilePath buildResultPath : buildResultPaths) {
            screenList.add(processOne(build, launcher, listener, buildResultPath.getName()));
        }

        int failedScreens = (screenList.getExistingDifferentAboveThresholdScreens().length +
                             screenList.getNewUnApprovedScreens().length);

        // Too many differences?
        if (failedScreens >= numberOfDifferences) {

            listener.getLogger().println("Too many differences.");

            // Mark build as requested
            if ((markAs != null) && markAs.equals(FAILED)) {
                build.setResult(Result.FAILURE);

            } else if ((markAs == null) || markAs.equals((UNSTABLE))) {

                Result result = build.getResult();
                if ((result == null) || result.isBetterThan(Result.UNSTABLE)) {
                    build.setResult(Result.UNSTABLE);
                }
            }
        }

        return screenList;
    }

    /**
     * Processes one screen
     *
     * @param build Current build
     * @param launcher Launcher
     * @param listener Listener for console
     * @param screenName Name of a screen in results
     * @return Screen info
     * @throws InterruptedException
     * @throws IOException
     */
    public Screen processOne(AbstractBuild build, Launcher launcher, BuildListener listener, String screenName)
            throws InterruptedException, IOException {

        ProjectArtifacts projectArtifacts = new ProjectArtifacts(build.getProject());
        BuildArtifacts buildArtifacts = new BuildArtifacts(build);

        Screen screen = new Screen(screenName);
        screen.buildImage();

        FilePath buildScreenPath = buildArtifacts.getBuildScreenPath(screenName);

        // Has a previous screenshot (with the same name) been approved?
        if (buildArtifacts.hasApprovedScreen(screenName)) {
            screen.approvedImage();

            FilePath approvedScreenPath = buildArtifacts.getApprovedScreenPath(screenName);
            FilePath buildDiffPath = buildArtifacts.getBuildDiffPath(screenName);

            Boolean differenceFound = compareScreens(build, launcher, listener,
                                                     buildScreenPath, approvedScreenPath, buildDiffPath);

            if (differenceFound) {
                screen.existingDifferentAboveThresholdScreen();
                listener.getLogger().println("Difference found in screen " + screenName);

            } else if (buildDiffPath.exists()) {
                screen.existingDifferentBelowThresholdScreen();
                listener.getLogger().println("Difference found in screen " + screenName + ", but below threshold");

            } else {
                screen.existingEqualScreen();
            }

        } else { // Screenshot never seen

            if (autoApprove) {
                screen.newAutoApprovedScreen();

                // Approve screenshot by copying it as-is to the artifacts folder
                listener.getLogger().println("Screen " + screenName + " does not exist. Auto-approve screen...");

                // Copy to build and project folders
                buildScreenPath.copyTo(projectArtifacts.getScreenPath(screenName));
                buildScreenPath.copyTo(buildArtifacts.getApprovedScreenPath(screenName));

                // Mark as approved
                screen.approve();

            } else {
                screen.newUnApprovedScreen();
                listener.getLogger().println("Unknown screen found " + screenName + ".");
            }
        }

        return screen;
    }

    /**
     * Abstract method to compare two images
     *
     * @param build Current build
     * @param launcher Launcher
     * @param listener Listener for console
     * @param screenPath Build screen
     * @param approvedPath Approved screen
     * @param diffOutputPath Path to output file for differences
     * @return Differences found?
     * @throws InterruptedException
     * @throws IOException
     */
    public abstract Boolean compareScreens(AbstractBuild build, Launcher launcher, BuildListener listener,
                                           FilePath screenPath, FilePath approvedPath, FilePath diffOutputPath)
            throws InterruptedException, IOException;


    /**
     * Convenience method for descriptor
     *
     * @return Descriptor
     */
    public Descriptor<ComparisonDescribable> getDescriptor() {
        return (ComparisonDescriptor) Hudson.getInstance().getDescriptor(getClass());
    }

    /**
     * Returns a list of all known descriptors of this type
     *
     * @return List
     */
    public static DescriptorExtensionList<ComparisonDescribable, Descriptor<ComparisonDescribable>> all() {
        return Hudson.getInstance().<ComparisonDescribable, Descriptor<ComparisonDescribable>> getDescriptorList(ComparisonDescribable.class);
    }
}
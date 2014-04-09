package org.jenkinsci.plugins.visual_diff;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;

import hudson.util.FormValidation;
import org.apache.commons.lang.math.NumberUtils;
import org.jenkinsci.plugins.visual_diff.comparison.ComparisonDescribable;
import org.jenkinsci.plugins.visual_diff.data.Screen;
import org.jenkinsci.plugins.visual_diff.data.ScreenList;
import org.jenkinsci.plugins.visual_diff.utils.BuildArtifacts;
import org.jenkinsci.plugins.visual_diff.utils.ProjectArtifacts;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Builder
 *
 * @author Marcel Erz
 */
public class Builder extends hudson.tasks.Builder {

    public static final String FAILED = "failed";
    public static final String UNSTABLE = "unstable";
    public static final String NOTHING = "nothing";


    /**
     * List of all comparisons
     */
    private final List<ComparisonDescribable> comparisons;

    /**
     * Change build result to...
     */
    private final String markAs;

    /**
     * Number of missing screens until build result changes trigger
     */
    private final int numberOfMissing;


    /**
     * Constructor for Builder
     *
     * @param comparisons List of comparisons
     */
    @DataBoundConstructor
    public Builder(List<ComparisonDescribable> comparisons, String markAs, int numberOfMissing) {
        this.comparisons = comparisons;
        this.markAs = markAs;
        this.numberOfMissing = numberOfMissing;
    }


    /**
     * Gets all comparisons
     *
     * @return Comparisons
     */
    public List<ComparisonDescribable> getComparisons() {
        return comparisons;
    }

    /**
     * Change build result to...
     *
     * @return Build Result
     */
    public String getMarkAs() {
        return markAs;
    }

    /**
     * Number of missing screens until build result changes
     *
     * @return Number of missing screens
     */
    public int getNumberOfMissing() {
        return numberOfMissing;
    }



    /**
     * Performing build
     *
     * @param build Current build
     * @param launcher Launcher
     * @param listener Listener for console
     */
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        ProjectArtifacts projectArtifacts = new ProjectArtifacts(build.getProject());
        projectArtifacts.createFolders(listener);

        BuildArtifacts buildArtifacts = new BuildArtifacts(build);
        buildArtifacts.createFolders(listener);

        // Copy all approved screens
        listener.getLogger().println("Copy approved screens...");
        buildArtifacts.duplicateApprovedProjectScreen();

        // Run through all comparisons
        DataAction data = new DataAction();
        ScreenList completeList = data.getScreenList();
        for(ComparisonDescribable comparison : comparisons) {

            // Process all screens
            ScreenList list = comparison.processAll(build, launcher, listener);
            completeList.addAll(list);
        }

        // Find missing screens
        listener.getLogger().println("Find missing screens...");
        int missingApprovedScreens = 0;
        FilePath[] approvedScreens = projectArtifacts.getScreens();
        for(FilePath approvedScreen : approvedScreens) {

            String screenName = approvedScreen.getName();

            // Screen not found in build-lists
            if (!completeList.hasScreenName(screenName)) {

                Screen newScreen = new Screen(screenName);
                newScreen.approvedImage();
                newScreen.approve();

                completeList.add(newScreen);

                missingApprovedScreens++;
            }
        }

        // Too many differences?
        if (missingApprovedScreens >= numberOfMissing) {

            listener.getLogger().println("Too many missing.");

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

        // Add all action
        build.addAction(data);
        build.addAction(new CompareAction(build));

        return true;
    }


    /**
     * Get descriptor
     *
     * @return Descriptor
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }


    /**
     * Descriptor for {@link Builder}.
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<hudson.tasks.Builder> {

        /**
         * Descriptor implementation
         */
        public DescriptorImpl() {
            load();
        }


        /**
         * Gets the title of the Builder descriptor
         *
         * @return Title
         */
        public String getDisplayName() {
            return "Visual Diff";
        }


        /**
         * Does apply to all project-types
         *
         * @param aClass Class of project
         * @return Does apply?
         */
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * Determines a list of available ComparisonDescribable descriptors
         *
         * @param p Project
         * @return List of descriptors
         */
        public List<Descriptor<? extends ComparisonDescribable>> getComparisonDescribables(AbstractProject<?, ?> p) {
            List<Descriptor<? extends ComparisonDescribable>> list = new LinkedList<Descriptor<? extends ComparisonDescribable>>();

            for(Descriptor<? extends ComparisonDescribable> rs : ComparisonDescribable.all()) {
                list.add(rs);
            }

            return list;
        }


        /**
         * Will be called when numberOfMissing field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckNumberOfMissing(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toInt(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if (number < 1)
                return FormValidation.error("The value should be greater than or equal to one.");

            return FormValidation.ok();
        }
    }
}


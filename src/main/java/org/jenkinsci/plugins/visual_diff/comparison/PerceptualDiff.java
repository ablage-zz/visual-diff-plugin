package org.jenkinsci.plugins.visual_diff.comparison;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.util.FormValidation;

import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;

import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Perceptual-diff - describable
 *
 * @author Marcel Erz
 */
public class PerceptualDiff extends ComparisonDescribable {

    /**
     * Verbose output
     */
    private final Boolean verbose;

    /**
     * Field-of-view for comparison
     */
    private final float fov;

    /**
     * Threshold when comparison fails
     */
    private final int threshold;

    /**
     * Gama
     */
    private final float gamma;

    /**
     * Luminance
     */
    private final float luminance;

    /**
     * Use luminance only
     */
    private final Boolean luminanceOnly;

    /**
     * Color-factor
     */
    private final float colorFactor;

    /**
     * Down-sample image, comparing lower resolution images
     */
    private final int downSample;


    /**
     * Initializes perceptual-diff
     *
     * @param screensPath Path to screens
     * @param autoApprove Auto-approve
     * @param markAs Mark build as...
     * @param numberOfDifferences Number of differences until build result changes
     * @param verbose Verbose
     * @param fov Field-of-view
     * @param threshold Threshold
     * @param gamma Gamma
     * @param luminance Luminance
     * @param luminanceOnly Luminance-only
     * @param colorFactor Color-factor
     * @param downSample Down-sample
     */
    @DataBoundConstructor
    public PerceptualDiff(String screensPath,
                          Boolean autoApprove,
                          String markAs,
                          int numberOfDifferences,
                          Boolean verbose,
                          float fov,
                          int threshold,
                          float gamma,
                          float luminance,
                          Boolean luminanceOnly,
                          float colorFactor,
                          int downSample) {

        super(screensPath, autoApprove, markAs, numberOfDifferences);

        this.verbose = verbose;
        this.fov = fov;
        this.threshold = threshold;
        this.gamma = gamma;
        this.luminance = luminance;
        this.luminanceOnly = luminanceOnly;
        this.colorFactor = colorFactor;
        this.downSample = downSample;
    }


    /**
     * Get verbose
     *
     * @return Verbose
     */
    public Boolean getVerbose() {
        return verbose;
    }

    /**
     * Get field-of-view
     *
     * @return Fov
     */
    public float getFov() {
        return fov;
    }

    /**
     * Get threshold
     *
     * @return Threshold
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Get gamma
     *
     * @return Gamma
     */
    public float getGamma() {
        return gamma;
    }

    /**
     * Get luminance
     *
     * @return Luminance
     */
    public float getLuminance() {
        return luminance;
    }

    /**
     * Get luminance only
     *
     * @return Luminance only
     */
    public Boolean getLuminanceOnly() {
        return luminanceOnly;
    }

    /**
     * Get color-factor
     *
     * @return Color-factor
     */
    public float getColorFactor() {
        return colorFactor;
    }

    /**
     * Get down-sample rate
     *
     * @return Sample-rate
     */
    public int getDownSample() {
        return downSample;
    }


    /**
     * Compare two images
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
    public Boolean compareScreens(AbstractBuild build, Launcher launcher, BuildListener listener,
                                  FilePath screenPath, FilePath approvedPath, FilePath diffOutputPath)
            throws InterruptedException, IOException {

        StringBuilder sb = new StringBuilder();

        sb.append(getDescriptor().getBinaryPath());

        if (verbose) {
            sb.append(" -verbose");
        }

        sb.append(" -fov " + fov);
        sb.append(" -threshold " + threshold);
        sb.append(" -gamma " + gamma);
        sb.append(" -luminance " + luminance);

        if (luminanceOnly) {
            sb.append(" -luminanceonly");
        }

        sb.append(" -colorfactor " + colorFactor);
        sb.append(" -downsample " + downSample);

        sb.append(" -output " + diffOutputPath.getRemote());
        sb.append(" " + approvedPath.getRemote());
        sb.append(" " + screenPath.getRemote());

        String cmd = sb.toString();

        // Run comparison and wait until finished
        listener.getLogger().println(cmd);
        int exitCode = launcher.launch(cmd, build.getEnvVars(), listener.getLogger(), build.getWorkspace()).join();

        return (exitCode != 0);
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
     * Descriptor for comparison
     */
    @Extension
    public static class DescriptorImpl extends ComparisonDescriptor {

        /**
         * Path to binary
         */
        private String binaryPath;


        /**
         * Gets the path of the binary
         *
         * @return Binary path
         */
        public String getBinaryPath() {
            return binaryPath;
        }


        /**
         * Name of comparison
         *
         * @return Name
         */
        @Override
        public String getDisplayName() {
            return "Perceptual Diff";
        }


        /**
         * Will be triggered when the configuration is saved
         *
         * @param req Request
         * @param formData Form-data
         * @return Success?
         * @throws FormException
         */
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            binaryPath = formData.getString("binaryPath");

            save();

            return super.configure(req, formData);
        }


        /**
         * Will be called when colorFactor field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckColorFactor(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toFloat(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if ((number < 0.0) || (number > 1.0))
                return FormValidation.error("The value should be in the range of 0.0 to 1.0.");

            return FormValidation.ok();
        }

        /**
         * Will be called when downSample field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckDownSample(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toFloat(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if (number < 0)
                return FormValidation.error("The value should be greater than or equal to zero.");

            return FormValidation.ok();
        }

        /**
         * Will be called when fov field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckFov(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toFloat(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if ((number < 0.1) || (number > 89.9))
                return FormValidation.error("The value should be in the range of 0.1 to 89.9.");

            return FormValidation.ok();
        }

        /**
         * Will be called when gamma field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckGamma(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toFloat(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if (number < 0)
                return FormValidation.error("The value should be greater than or equal to zero.");

            return FormValidation.ok();
        }

        /**
         * Will be called when luminance field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckLuminance(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toFloat(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if (number < 0)
                return FormValidation.error("The value should be greater than or equal to zero.");

            return FormValidation.ok();
        }

        /**
         * Will be called when threshold field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckThreshold(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toInt(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if (number < 0)
                return FormValidation.error("The value should be greater than or equal to zero.");

            return FormValidation.ok();
        }

        /**
         * Will be called when numberOfDifferences field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckNumberOfDifferences(@QueryParameter String value)
                throws IOException, InterruptedException, ServletException {

            float number = NumberUtils.toInt(value, -1);

            if (number == -1)
                return FormValidation.error("Please enter a number!");

            if (number < 1)
                return FormValidation.error("The value should be greater than or equal to one.");

            return FormValidation.ok();
        }


        /**
         * Will be called when screensPath field is validated
         *
         * @param value Value of field
         * @return Validation result
         * @throws IOException
         * @throws InterruptedException
         * @throws ServletException
         */
        public FormValidation doCheckScreensPath(@QueryParameter String value, @AncestorInPath AbstractProject project)
                throws IOException, InterruptedException, ServletException {

            if (value.length() == 0)
                return FormValidation.error("Please define a pattern for the screens!");

            FilePath workspace = project.getWorkspace();

            if ((workspace == null) || !workspace.exists())
                return FormValidation.warning("No workspace available.");

            if (workspace.list(value).length == 0)
                return FormValidation.warning("No files found.");

            return FormValidation.ok();
        }
    }
}

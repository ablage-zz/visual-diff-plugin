package org.jenkinsci.plugins.visual_diff.data;

import java.io.Serializable;

/**
 * Information about a specific screen
 *
 * @author Marcel Erz
 */
public class Screen implements Serializable {

    public static final String NEW_NONE = "none";
    public static final String NEW_AUTO_APPROVED = "autoApproved";
    public static final String NEW_UNAPPROVED = "unApproved";

    public static final String EXISTING_NONE = "none";
    public static final String EXISTING_EQUAL = "equal";
    public static final String EXISTING_DIFFERENT_ABOVE_THRESHOLD = "aboveThreshold";
    public static final String EXISTING_DIFFERENT_BELOW_THRESHOLD = "belowThreshold";


    /**
     * New screen
     */
    private String newScreen = NEW_NONE;

    /**
     * Screen exists, it is the equal as previously approved
     */
    private String existing = EXISTING_NONE;


    /**
     * Name of the image
     */
    private String imageName = null;


    /**
     * Has an approved image
     */
    private boolean approvedImage = false;

    /**
     * Has a build image
     */
    private boolean buildImage = false;


    /**
     * Is screen approved?
     */
    private boolean approved = false;


    /**
     * Initializes screen
     *
     * @param name Name of screen
     */
    public Screen(String name) {
        imageName = name;
    }


    /**
     * Is it a new screen?
     *
     * @return True/False
     */
    public boolean isNewScreen() {
        return !newScreen.equals(NEW_NONE);
    }


    /**
     * Is it a new auto-approved screen?
     *
     * @return True/False
     */
    public boolean isNewAutoApprovedScreen() {
        return newScreen.equals(NEW_AUTO_APPROVED);
    }

    /**
     * Sets a new auto-approved screen
     */
    public void newAutoApprovedScreen() {
        newScreen = NEW_AUTO_APPROVED;
    }


    /**
     * Is it a new un-approved screen?
     *
     * @return True/False
     */
    public boolean isNewUnApprovedScreen() {
        return newScreen.equals(NEW_UNAPPROVED);
    }

    /**
     * Sets a new un-approved screen
     */
    public void newUnApprovedScreen() {
        newScreen = NEW_UNAPPROVED;
    }


    /**
     * Is it an existing screen?
     *
     * @return True/False
     */
    public boolean isExistingScreen() {
        return !existing.equals(EXISTING_NONE);
    }


    /**
     * Is it an existing screen that is the same as previously approved?
     *
     * @return True/False
     */
    public boolean isExistingEqualScreen() {
        return existing.equals(EXISTING_EQUAL);
    }

    /**
     * Sets an existing screen that is the same and that was previously approved
     */
    public void existingEqualScreen() {
        existing = EXISTING_EQUAL;
    }


    /**
     * Is it an existing screen that is different to previously approved, and above threshold?
     *
     * @return True/False
     */
    public boolean isExistingDifferentAboveThresholdScreen() {
        return existing.equals(EXISTING_DIFFERENT_ABOVE_THRESHOLD);
    }

    /**
     * Sets an existing screen that is different than was previously approved, and above threshold
     */
    public void existingDifferentAboveThresholdScreen() {
        existing = EXISTING_DIFFERENT_ABOVE_THRESHOLD;
    }


    /**
     * Is it an existing screen that is different to previously approved, but below threshold?
     *
     * @return True/False
     */
    public boolean isExistingDifferentBelowThresholdScreen() {
        return existing.equals(EXISTING_DIFFERENT_BELOW_THRESHOLD);
    }

    /**
     * Sets an existing screen that is different than was previously approved, but below threshold
     */
    public void existingDifferentBelowThresholdScreen() {
        existing = EXISTING_DIFFERENT_BELOW_THRESHOLD;
    }


    /**
     * Gets the name of the image
     *
     * @return Image name
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Sets the name of the image
     *
     * @param value image name
     */
    public void setImageName(String value) {
        imageName = value;
    }


    /**
     * Has a approved image
     *
     * @return True/False
     */
    public boolean hasApprovedImage() {
        return approvedImage || isExistingScreen();
    }

    /**
     * Sets that it has an approved image
     */
    public void approvedImage() {
        approvedImage = true;
    }


    /**
     * Has a build image
     *
     * @return True/False
     */
    public boolean hasBuildImage() {
        return buildImage || isNewScreen() || isExistingScreen();
    }

    /**
     * Sets that it has a build image
     */
    public void buildImage() {
        buildImage = true;
    }


    /**
     * Has a difference image
     *
     * @return True/False
     */
    public boolean hasDifferenceImage() {
        return isExistingDifferentAboveThresholdScreen() || isExistingDifferentBelowThresholdScreen();
    }


    /**
     * Checks if screen is approved
     *
     * @return Approved?
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * Approves this screen
     */
    public void approve() {
        approved = true;
    }
}

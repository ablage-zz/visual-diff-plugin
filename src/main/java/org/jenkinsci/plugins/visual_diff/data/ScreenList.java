package org.jenkinsci.plugins.visual_diff.data;

import java.util.ArrayList;

/**
 * Manages a list if screens
 *
 * @author Marcel Erz
 */
public class ScreenList extends ArrayList<Screen> {

    /**
     * Gets all screens with approved-screens
     *
     * @return Approved-screens
     */
    public Screen[] getApprovedScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.hasApprovedImage()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all screens with build-screens
     *
     * @return Build-screens
     */
    public Screen[] getBuildScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.hasBuildImage()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all screens with difference-screens
     *
     * @return Difference-screens
     */
    public Screen[] getDifferenceScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.hasDifferenceImage()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }


    /**
     * Gets all active screens
     *
     * @return Active-screens
     */
    public Screen[] getActiveScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.hasApprovedImage() && screen.hasBuildImage()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all inactive screens
     *
     * @return Inactive-screens
     */
    public Screen[] getInactiveScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.hasApprovedImage() && !screen.hasBuildImage()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }


    /**
     * Gets all new screens
     *
     * @return New-screens
     */
    public Screen[] getNewScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.isNewScreen()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all new auto-approved screens
     *
     * @return New auto-approved screens
     */
    public Screen[] getNewAutoApprovedScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.isNewAutoApprovedScreen()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all new un-approved screens
     *
     * @return New un-approved screens
     */
    public Screen[] getNewUnApprovedScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.isNewUnApprovedScreen()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }


    /**
     * Gets all existing screens
     *
     * @return New existing screens
     */
    public Screen[] getExistingScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.isExistingScreen()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all existing screens
     *
     * @return New existing screens
     */
    public Screen[] getExistingEqualScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.isExistingEqualScreen()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all existing screens
     *
     * @return New existing screens
     */
    public Screen[] getExistingDifferentBelowThresholdScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.isExistingDifferentBelowThresholdScreen()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }

    /**
     * Gets all existing screens
     *
     * @return New existing screens
     */
    public Screen[] getExistingDifferentAboveThresholdScreens() {
        ArrayList<Screen> list = new ArrayList<Screen>();

        for(Screen screen : this) {
            if (screen.isExistingDifferentAboveThresholdScreen()) list.add(screen);
        }

        return list.toArray(new Screen[list.size()]);
    }


    /**
     * Gets screen by image name
     *
     * @param name Name of image
     * @return Screen
     */
    public Screen getScreenByName(String name) {

        for(Screen screen : this) {
            if (screen.getImageName().equals(name)) {
                return screen;
            }
        }

        return null;
    }

    /**
     * Does list have the image name?
     *
     * @param name Name of image
     * @return Has image name?
     */
    public boolean hasScreenName(String name) {
        return (getScreenByName(name) != null);
    }
}

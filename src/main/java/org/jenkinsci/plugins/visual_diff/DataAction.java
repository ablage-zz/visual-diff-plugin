package org.jenkinsci.plugins.visual_diff;

import hudson.model.*;
import org.jenkinsci.plugins.visual_diff.data.ScreenList;

import java.io.Serializable;

/**
 * Action to save build data
 *
 * @author Marcel Erz
 */
public class DataAction extends InvisibleAction implements Serializable {

    /**
     * Serialization identifier
     */
    private static final long serialVersionUID = -5986348230372792724L;


    /**
     * List of screens
     */
    private ScreenList screenList = new ScreenList();


    /**
     * Gets all screens as list
     *
     * @return Screen list
     */
    public ScreenList getScreenList() {
        return screenList;
    }

    /**
     * Gets the JSON data
     *
     * @return JSON
     */
    public String getJSONData() {
        return screenList.toJSON().toString();
    }
}


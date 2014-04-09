package org.jenkinsci.plugins.visual_diff.comparison;

import hudson.model.AbstractProject;
import hudson.model.Descriptor;

/**
 * Comparison entry - descriptor
 *
 * @author Marcel Erz
 */
public abstract class ComparisonDescriptor extends Descriptor<ComparisonDescribable> {

    /**
     * Initializes descriptor
     */
    public ComparisonDescriptor() {
        load();
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

}

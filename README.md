visual-diff-plugin
==================

A Jenkins plug-in to manage differences in screenshots

Screenshots that are taken during a build are compared with previously approved images and possibly trigger build status changes. The screenshots can be compared with each other through a comparison page which highlights differences.


Differences chart:

![Image](images/Chart.png?raw=true)



Screenshot comparison reports:

![Image](images/Report.png?raw=true)



Highlights to show differences:

![Image](images/Difference_Highlighting.png?raw=true)

# Work in Progress
The current plugin is a WIP and will be in a working state shortly. For the time being, you can use the visual-diff.hpi in the root for testing purposes, but please be aware that this is not a production version and that the configuration will change. The configuration will not be backwards compatible!

# External tools needed
The current version still needs the perceptualdiff bin to do the differences. In a future version, this may be replaced with a native java version. 
Install the perceptualdiff package with:

```yum install perceptualdiff```

Add the path of the binary to the system configuration for the visual-diff plugin, and the plugin should be ready to run.

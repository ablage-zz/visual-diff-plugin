package org.jenkinsci.plugins.visual_diff;

import hudson.FilePath;
import hudson.model.*;
import hudson.util.*;

import org.jenkinsci.plugins.visual_diff.utils.BuildArtifacts;
import org.jenkinsci.plugins.visual_diff.utils.ProjectArtifacts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

import org.kohsuke.stapler.*;

import javax.servlet.ServletException;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Comparison Action Class
 *
 * This class is added to the build during the Builder::perform method.
 * It will add a link to the Project and it serves all images and information to render
 * the comparison page. It serves also the approve call to approve a screen.
 *
 * @author Marcel Erz
 */
public class CompareAction implements Action, Serializable {

    /**
     * Serialization identifier
     */
    private static final long serialVersionUID = -5986348230372792726L;


    /**
     * Build of action
     */
    private final AbstractBuild<?,?> build;


    /**
     * Initializes
     *
     * @param build Build of action
     */
    public CompareAction(AbstractBuild<?, ?> build) {
        this.build = build;
    }


    /**
     * Gets the title of this action
     *
     * @return Title
     */
    public String getDisplayName() {
        return "Visual-Diff Report";
    }

    /**
     * Gets the icon for the action
     *
     * @return Icon filename
     */
    public String getIconFileName() {
        return "graph.gif";
    }

    /**
     * Gets the url for the action
     *
     * @return Url
     */
    public String getUrlName() {
        return "vdiff";
    }


    /**
     * Gets the project artifacts utility instance
     *
     * @return Artifacts utilities
     */
    public ProjectArtifacts getProjectArtifacts() {
        return new ProjectArtifacts(build.getProject());
    }

    /**
     * Gets the build artifacts utility instance
     *
     * @return Artifacts utilities
     */
    public BuildArtifacts getBuildArtifacts() {
        return new BuildArtifacts(build);
    }


    /**
     * Gets the data for current build
     *
     * @return Data
     */
    public DataAction getData() {
        return build.getAction(DataAction.class);
    }


    /**
     * Serves build screens
     *
     * @param req Request
     * @param rsp Response
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doBuildScreens(StaplerRequest req, StaplerResponse rsp) throws InterruptedException, IOException, ServletException {
        DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, "Build-Screens");
        dbs.serveFile(req, rsp, getBuildArtifacts().getBuildScreensPath(), "graph.gif", false);
    }

    /**
     * Serves build diffs
     *
     * @param req Request
     * @param rsp Response
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doBuildDiffs(StaplerRequest req, StaplerResponse rsp) throws InterruptedException, IOException, ServletException {
        DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, "Build-Diffs");
        dbs.serveFile(req, rsp, getBuildArtifacts().getBuildDiffsPath(), "graph.gif", false);
    }

    /**
     * Serves approved screens
     *
     * @param req Request
     * @param rsp Response
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doApprovedScreens(StaplerRequest req, StaplerResponse rsp) throws InterruptedException, IOException, ServletException {
        DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, "Approved-Screens");
        dbs.serveFile(req, rsp, getBuildArtifacts().getApprovedScreensPath(), "graph.gif", false);
    }


    /**
     * Approves a screen
     *
     * @param name Name of the screen
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doApprove(@QueryParameter String name) throws InterruptedException, IOException, ServletException {
        getBuildArtifacts().getBuildScreenPath(name).copyTo(getProjectArtifacts().getScreenPath(name));
        getData().getScreenList().getScreenByName(name).approve();
    }


    /**
     * Deletes an approved screen
     *
     * @param name Name of the screen
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doDelete(@QueryParameter String name) throws InterruptedException, IOException, ServletException {
        getProjectArtifacts().getScreenPath(name).delete();
    }

    /**
     * Deletes all approved screens
     *
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doDeleteAll(@QueryParameter String name) throws InterruptedException, IOException, ServletException {
        FilePath[] approvedScreens = getProjectArtifacts().getScreens();

        for(FilePath approvedScreen : approvedScreens) {
            approvedScreen.delete();
        }
    }


    /**
     * Creates a screens chart
     *
     * @param dataSet Data-set for chart
     * @param title Title of chart
     * @return Chart
     */
    protected JFreeChart createScreensChart(CategoryDataset dataSet, String title) {

        JFreeChart chart = ChartFactory.createLineChart(
                title, // chart-title
                null, // unused
                "Screens", // range axis label
                dataSet, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        final LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseStroke(new BasicStroke(2.0f));
        ColorPalette.apply(renderer);

        return chart;
    }

    /**
     * Create active/inactive data-set
     *
     * @return Data-set
     */
    protected CategoryDataset createActiveInactiveDataSet() throws InterruptedException, IOException {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        List<? extends AbstractBuild<?, ?>> builds = build.getProject().getBuilds();
        for (ListIterator iterator = builds.listIterator(builds.size()); iterator.hasPrevious();) {
            AbstractBuild<?, ?> currentBuild = (AbstractBuild<?, ?>)iterator.previous();

            DataAction dataAction = currentBuild.getAction(DataAction.class);
            if (dataAction == null) continue;

            Integer number = currentBuild.number;
            String buildNumber = "#" + number;

            ds.addValue(dataAction.getScreenList().getActiveScreens().length, "active", buildNumber);
            ds.addValue(dataAction.getScreenList().getInactiveScreens().length, "inactive", buildNumber);
        }

        return ds;
    }

    /**
     * Create build data-set
     *
     * @return Data-set
     */
    protected CategoryDataset createBuildDataSet() throws InterruptedException, IOException {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        List<? extends AbstractBuild<?, ?>> builds = build.getProject().getBuilds();
        for (ListIterator iterator = builds.listIterator(builds.size()); iterator.hasPrevious();) {
            AbstractBuild<?, ?> currentBuild = (AbstractBuild<?, ?>)iterator.previous();

            DataAction dataAction = currentBuild.getAction(DataAction.class);
            if (dataAction == null) continue;

            Integer number = currentBuild.number;
            String buildNumber = "#" + number;

            ds.addValue(dataAction.getScreenList().getApprovedScreens().length, "approved", buildNumber);
            ds.addValue(dataAction.getScreenList().getBuildScreens().length, "build", buildNumber);
            ds.addValue(dataAction.getScreenList().getDifferenceScreens().length, "diff", buildNumber);
        }

        return ds;
    }

    /**
     * Create active/inactive data-set
     *
     * @return Data-set
     */
    protected CategoryDataset createBuildDetailsDataSet() throws InterruptedException, IOException {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        List<? extends AbstractBuild<?, ?>> builds = build.getProject().getBuilds();
        for (ListIterator iterator = builds.listIterator(builds.size()); iterator.hasPrevious();) {
            AbstractBuild<?, ?> currentBuild = (AbstractBuild<?, ?>)iterator.previous();

            DataAction dataAction = currentBuild.getAction(DataAction.class);
            if (dataAction == null) continue;

            Integer number = currentBuild.number;
            String buildNumber = "#" + number;

            ds.addValue(dataAction.getScreenList().getExistingScreens().length, "known", buildNumber);
            ds.addValue(dataAction.getScreenList().getExistingEqualScreens().length, "known =", buildNumber);
            ds.addValue(dataAction.getScreenList().getExistingDifferentBelowThresholdScreens().length, "known <", buildNumber);
            ds.addValue(dataAction.getScreenList().getExistingDifferentAboveThresholdScreens().length, "known >", buildNumber);

            ds.addValue(dataAction.getScreenList().getNewScreens().length, "new", buildNumber);
            ds.addValue(dataAction.getScreenList().getNewAutoApprovedScreens().length, "new auto", buildNumber);
            ds.addValue(dataAction.getScreenList().getNewUnApprovedScreens().length, "new fail", buildNumber);
        }

        return ds;
    }


    /**
     * Prints active/inactive chart to response
     *
     * @param req Request
     * @param rsp Response
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doActiveInactiveChart(StaplerRequest req, StaplerResponse rsp) throws InterruptedException, IOException, ServletException {
        ChartUtil.generateGraph(req, rsp, createScreensChart(createActiveInactiveDataSet(), "Active/Inactive"), 300, 200);
    }

    /**
     * Prints build chart to response
     *
     * @param req Request
     * @param rsp Response
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doBuildChart(StaplerRequest req, StaplerResponse rsp) throws InterruptedException, IOException, ServletException {
        ChartUtil.generateGraph(req, rsp, createScreensChart(createBuildDataSet(), "Build Summary"), 300, 200);
    }

    /**
     * Prints build-details chart to response
     *
     * @param req Request
     * @param rsp Response
     * @throws InterruptedException
     * @throws IOException
     * @throws ServletException
     */
    public void doBuildDetailsChart(StaplerRequest req, StaplerResponse rsp) throws InterruptedException, IOException, ServletException {
        ChartUtil.generateGraph(req, rsp, createScreensChart(createBuildDetailsDataSet(), "Build Details"), 300, 200);
    }
}


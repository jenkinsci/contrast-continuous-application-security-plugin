package com.aspectsecurity.contrast.contrastjenkins.plots;

import com.aspectsecurity.contrast.contrastjenkins.VulnerabilityFrequencyAction;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.util.Graph;
import hudson.util.RunList;
import hudson.util.ShiftedCategoryAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;


public class SeverityFrequencyPlot extends Graph {

    private AbstractProject<?, ?> project;

    public SeverityFrequencyPlot(AbstractProject<?, ?> project) {
        super(Calendar.getInstance(), 500, 200);
        this.project = project;
    }

    @Override
    protected JFreeChart createGraph() {
        DefaultCategoryDataset dataset = createSeverityFrequencyDataset();

        JFreeChart chart = ChartFactory.createStackedBarChart(
                null, // title
                "Build Number", // x axis
                "Severity Count", // y axis
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // legend
                true,  // tooltips
                false); //urls

        chart.setBackgroundPaint(Color.white);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis("Build Number");
        plot.setDomainAxis(domainAxis);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);

        plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));
        return chart;
    }

    private DefaultCategoryDataset createSeverityFrequencyDataset() {
        java.util.List<VulnerabilityFrequencyAction> actions = new ArrayList<>();
        RunList<?> builds = project.getBuilds().limit(10);

        // Get all build actions
        for (Run<?, ?> run : builds) {
            VulnerabilityFrequencyAction action = run.getAction(VulnerabilityFrequencyAction.class);

            if (action == null) {
                continue;
            }
            actions.add(action);
        }

        // put them in chronological order
        Collections.reverse(actions);

        // build data setup
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (VulnerabilityFrequencyAction action : actions) {
            Map<String, Integer> result = action.getResult().getSeverityResult();

            for (String severity : result.keySet()) {
                ds.addValue(result.get(severity), severity, Integer.toString(action.getBuildNumber()));
            }
        }
        return ds;
    }
}

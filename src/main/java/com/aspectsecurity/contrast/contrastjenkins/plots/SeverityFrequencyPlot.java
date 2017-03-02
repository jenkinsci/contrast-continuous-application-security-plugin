package com.aspectsecurity.contrast.contrastjenkins.plots;

import com.aspectsecurity.contrast.contrastjenkins.VulnerabilityFrequencyAction;
import com.aspectsecurity.contrast.contrastjenkins.VulnerabilityTrendHelper;
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
import org.jfree.chart.renderer.category.BarRenderer;
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

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        setColors(renderer);

        // Set colors here
        plot.setRenderer(renderer);

        CategoryAxis domainAxis = new ShiftedCategoryAxis("Build Number");
        plot.setDomainAxis(domainAxis);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);

        plot.setInsets(new RectangleInsets(0, 5.0, 0, 5.0));
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

            String buildNumber = Integer.toString(action.getBuildNumber());

            ds.addValue(result.get("Note"), "Note", buildNumber);
            ds.addValue(result.get("Low"), "Low", buildNumber);
            ds.addValue(result.get("Medium"), "Medium", buildNumber);
            ds.addValue(result.get("High"), "High", buildNumber);
            ds.addValue(result.get("Critical"), "Critical", buildNumber);

            for (String severity: VulnerabilityTrendHelper.SEVERITIES) {
                ds.addValue(result.get(severity), severity, buildNumber);
            }
        }

        return ds;
    }


    private java.util.List<Color> setColors(BarRenderer renderer) {
        java.util.List<Color> colors = new ArrayList<>();

        renderer.setSeriesPaint(0, new Color(232, 232, 232));
        renderer.setSeriesPaint(1 ,new Color(186, 186, 186));
        renderer.setSeriesPaint(2, new Color(247, 182, 0));
        renderer.setSeriesPaint(3, new Color(247, 138, 49));
        renderer.setSeriesPaint(4, new Color(230, 48, 37));

        return colors;
    }
}
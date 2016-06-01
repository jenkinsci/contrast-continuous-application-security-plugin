package com.aspectsecurity.contrast.contrastjenkins;

import com.aspectsecurity.contrast.contrastjenkins.plots.SeverityFrequencyPlot;
import com.aspectsecurity.contrast.contrastjenkins.plots.VulnerabilityFrequencyPlot;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.Graph;

import java.util.ArrayList;
import java.util.List;

public class ContrastPluginProjectAction implements Action {

    private AbstractProject<?, ?> project;

    public ContrastPluginProjectAction(final AbstractProject<?, ?> project) {
        this.project = project;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/testExample/img/project_icon.png";
    }

    @Override
    public String getDisplayName() {
        return "Vulnerability Trend Charts";
    }

    @Override
    public String getUrlName() {
        return "contrastProjectAction";
    }

    public AbstractProject<?, ?> getProject() {
        return this.project;
    }

    // used in index.jelly
    public Graph getVulnerabilityGraph() {
        return new VulnerabilityFrequencyPlot(project);
    }

    // used in index.jelly
    public Graph getSeverityGraph() {
        return new SeverityFrequencyPlot(project);
    }

    public String getProjectName() {
        return this.project.getName();
    }


    // TODO remove?
    public List<String> getBuildResult() {
        List<String> projectMessages = new ArrayList<String>();
        List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();
        String projectMessage;

        final Class<VulnerabilityFrequencyAction> buildClass = VulnerabilityFrequencyAction.class;

        for (AbstractBuild<?, ?> currentBuild : builds) {
            projectMessage = "Build #" + currentBuild.getAction(buildClass).getBuildNumber()
                    + ": " + currentBuild.getAction(buildClass).toString(); // TODO print results

            projectMessages.add(projectMessage);
        }

        return projectMessages;
    }
}
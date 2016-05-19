package com.aspectsecurity.contrast.contrastjenkins;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class AbstractContrastPlugin extends JobProperty<Job<?, ?>> {

    private String username;

    private String apiKey;

    private String serviceKey;

    private String orgUuid;

    private String teamServerUrl;

    @DataBoundConstructor
    public AbstractContrastPlugin(String username, String apiKey, String serviceKey, String teamServerUrl, String orgUuid) {
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.teamServerUrl = teamServerUrl;
        this.orgUuid = orgUuid;
    }

    @Override
    public AbstrastContrastPluginDescriptor getDescriptor() {
        return (AbstrastContrastPluginDescriptor) Jenkins.getInstance().getDescriptor(getClass());
    }

    public static AbstrastContrastPluginDescriptor getAbstractContrastPluginDescriptor() {
        return (AbstrastContrastPluginDescriptor) Jenkins.getInstance().getDescriptor(AbstractContrastPlugin.class);
    }

    public String getUsername() {
        return username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public String getOrgUuid() {
        return orgUuid;
    }

    public String getTeamServerUrl() {
        return teamServerUrl;
    }

    @Extension
    public static final class AbstrastContrastPluginDescriptor extends JobPropertyDescriptor {

        private String username;

        private String apiKey;

        private String serviceKey;

        private String orgUuid;

        private String teamServerUrl;

        public AbstrastContrastPluginDescriptor() {
            super(AbstractContrastPlugin.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

        @DataBoundConstructor
        public AbstrastContrastPluginDescriptor(String username, String apiKey, String serviceKey, String teamServerUrl, String orgUuid) {
            this.username = username;
            this.apiKey = apiKey;
            this.serviceKey = serviceKey;
            this.teamServerUrl = teamServerUrl;
            this.orgUuid = orgUuid;
        }

        @Override
        public String getDisplayName() {
            return "Contrast Plugin Configuration";
        }

        public String getUsername() {
            return username;
        }

        public String getApiKey() {
            return apiKey;
        }

        public String getServiceKey() {
            return serviceKey;
        }

        public String getOrgUuid() {
            return orgUuid;
        }

        public String getTeamServerUrl() {
            return teamServerUrl;
        }
    }
}
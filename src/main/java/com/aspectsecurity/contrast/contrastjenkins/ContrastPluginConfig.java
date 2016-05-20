package com.aspectsecurity.contrast.contrastjenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

public class ContrastPluginConfig extends JobProperty<AbstractProject<?, ?>> {
    private String username;

    private String apiKey;

    private String serviceKey;

    private String orgUuid;

    private String teamServerUrl;

    private String applicationId;

    @DataBoundConstructor
    public ContrastPluginConfig(String username, String apiKey, String serviceKey, String teamServerUrl, String orgUuid, String applicationId) {
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.teamServerUrl = teamServerUrl;
        this.orgUuid = orgUuid;
        this.applicationId = applicationId;
    }

    @Override
    public ContrastPluginConfigDescriptor getDescriptor() {
        return (ContrastPluginConfigDescriptor) Jenkins.getInstance().getDescriptor(getClass());
    }

    public static ContrastPluginConfigDescriptor getContrastConfigDescriptor() {
        return (ContrastPluginConfigDescriptor) Jenkins.getInstance().getDescriptor(ContrastPluginConfig.class);
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

    public String getApplicationId() {
        return applicationId;
    }

    @Extension
    public static final class ContrastPluginConfigDescriptor extends JobPropertyDescriptor {

        public ContrastPluginConfigDescriptor() {
            super(ContrastPluginConfig.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

        /**
         * Validation of the 'username' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * @throws IOException      Invalid value
         * @throws ServletException Jenkins error
         */
        public FormValidation doCheckUsername(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a username.");
            return FormValidation.ok();
        }

        /**
         * Validation of the 'apiKey' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * @throws IOException      Invalid value
         * @throws ServletException Jenkins error
         */
        public FormValidation doCheckApiKey(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set an API Key.");
            return FormValidation.ok();
        }

        /**
         * Validation of the 'serviceKey' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * @throws IOException      Invalid value
         * @throws ServletException Jenkins error
         */
        public FormValidation doCheckServiceKey(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a Service Key.");
            return FormValidation.ok();
        }

        /**
         * Validation of the 'orgUuid' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * @throws IOException      Invalid value
         * @throws ServletException Jenkins error
         */
        public FormValidation doCheckOrgUuid(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set an Organization Uuid.");
            return FormValidation.ok();
        }

        /**
         * Validation of the 'teamServerUrl' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * @throws IOException      Invalid value
         * @throws ServletException Jenkins error
         */
        public FormValidation doCheckTeamServerUrl(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a TeamServer Url.");
            return FormValidation.ok();
        }

        /**
         * Validation of the 'applicationId' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * @throws IOException      Invalid value
         * @throws ServletException Jenkins error
         */
        public FormValidation doCheckApplicationId(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set an Application Id");
            return FormValidation.ok();
        }

        @Override
        public String getDisplayName() {
            return "Contrast Plugin Configuration";
        }
    }
}
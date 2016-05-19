package com.aspectsecurity.contrast.contrastjenkins;

import com.contrastsecurity.sdk.ContrastSDK;
import hudson.AbortException;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * TeamServer Connection Builder
 *
 * Used to test the connection to TeamServer in the pre-build step of jenkins.
 */
public class TeamServerConnectionBuilder extends Builder implements SimpleBuildStep {

    private String username;

    private String apiKey;

    private String serviceKey;

    private String orgUuid;

    private String teamServerUrl;

    @DataBoundConstructor
    public TeamServerConnectionBuilder(String username, String apiKey, String serviceKey, String orgUuid, String teamServerUrl) {
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.orgUuid = orgUuid;
        this.teamServerUrl = teamServerUrl;
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

    @Override
    public void perform(@Nonnull Run<?, ?> build, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
        logMessage(listener, "Testing the connection to the configured TeamServer.");
        ContrastSDK contrastSDK;

        try {
            contrastSDK = new ContrastSDK(username, serviceKey, apiKey, teamServerUrl);

            logMessage(listener, "Establishing connection to TeamServer.");

            contrastSDK.getProfileDefaultOrganizations();

            logMessage(listener, "Successfully verified the connection to TeamServer!");

        } catch (Exception e) {
            throw new AbortException("Unable to connect to TeamServer. Failing the build!");
        }
    }

    /**
     * Helper method for logging messages.
     *
     * @param listener Listener
     * @param msg String to log
     */
    private void logMessage(TaskListener listener, String msg) {
        listener.getLogger().println("[Contrast - TeamServerConnectionBuilder] - " + msg);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link TeamServerConnectionBuilder}.
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        /**
         * Validation of the 'username' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
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
         */
        public FormValidation doCheckTeamServerUrl(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a TeamServer Url.");
            return FormValidation.ok();
        }

        /**
         * Allows this builder to be available for all classes.
         *
         * @param aClass Passed in class.
         * @return true
         */
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Test TeamServer Connection";
        }

        /**
         * Save's the publisher's configuration data.
         *
         * @param req StaplerRequest
         * @param formData Json of the form for this Publisher
         * @return
         * @throws FormException
         */
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

            save();

            return super.configure(req, formData);
        }
    }
}


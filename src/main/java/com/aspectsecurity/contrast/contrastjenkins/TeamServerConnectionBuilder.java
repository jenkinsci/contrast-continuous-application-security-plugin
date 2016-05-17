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

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * TeamServer Connection Builder
 *
 * Used to test the connection to TeamServer in the pre-build step of jenkins.
 *
 * @author Justin Leo
 */
public class TeamServerConnectionBuilder extends Builder implements SimpleBuildStep {

    private final String username;

    private final String apiKey;

    private final String serviceKey;

    private final String orgUuid;

    private final String teamServerUrl;

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
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) throws AbortException {
        if (getDescriptor().getEnabled()) {
            listener.getLogger().println("Testing the connection to the configured TeamServer.");
            ContrastSDK contrastSDK;

            try {
                contrastSDK = new ContrastSDK(username, serviceKey, apiKey, teamServerUrl);

                contrastSDK.getProfileDefaultOrganizations();
            } catch (Exception e) {
                throw new AbortException("Unable to connect to TeamServer. Failing the build!");
            }
        } else {
            listener.getLogger().println("TeamServer Connection builder is not enabled.");
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link TeamServerConnectionBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.

     * See <tt>src/main/resources/com/aspectsecurity/contrast/contrastjenkins/TeamServerConnectionBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean enabled;

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
                return FormValidation.error("Please set a Username.");
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
         * Validation of the 'orgUuid form Field.
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

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // FreeStyleProject.class.isAssignableFrom(aClass);
            return true;
        }

        public String getDisplayName() {
            return "Test TeamServer Connection";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            enabled = formData.getBoolean("enabled");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this)
            save();
            return super.configure(req, formData);
        }
        
        public boolean getEnabled() {
            return enabled;
        }
    }
}


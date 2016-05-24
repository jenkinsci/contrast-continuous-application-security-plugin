package com.aspectsecurity.contrast.contrastjenkins;

import com.contrastsecurity.sdk.ContrastSDK;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

/**
 * TeamServer Connection Builder
 * <p>
 * Used to test the connection to TeamServer in the pre-build step of jenkins.
 */
public class TeamServerConnectionBuilder extends Builder implements SimpleBuildStep {

    @DataBoundConstructor
    public TeamServerConnectionBuilder() {

    }

    @Override
    public void perform(@Nonnull Run<?, ?> build, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws AbortException {
        logMessage(listener, "Testing the connection to the configured TeamServer. 11");
        ContrastSDK contrastSDK;

        TeamServerProfile profile = build.getParent().getProperty(ContrastPluginConfig.class).getProfile();

        try {
            contrastSDK = new ContrastSDK(profile.getUsername(), profile.getServiceKey(), profile.getApiKey(), profile.getTeamServerUrl());

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
     * @param msg      String to log
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
         * Save's the builder's configuration data.
         *
         * @param req      StaplerRequest
         * @param formData Json of the form for this Publisher
         * @return if the config saved successfully
         * @throws FormException invalid form
         */
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

            save();

            return super.configure(req, formData);
        }
    }
}


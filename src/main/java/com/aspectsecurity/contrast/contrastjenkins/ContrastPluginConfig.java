package com.aspectsecurity.contrast.contrastjenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Contrast Plugin Configuration
 * <p>
 * Adds the necessary configuration options to a job's properties. Used in TeamServerConnectionBuilder
 * and VulnerabilityTrendRecorder
 */
public class ContrastPluginConfig extends JobProperty<AbstractProject<?, ?>> {
    private String username;

    private String apiKey;

    private String serviceKey;

    private String orgUuid;

    private String teamServerUrl;

    private String applicationId;

    private String teamServerProfileName;

    @Extension
    public static final ContrastPluginConfigDescriptor DESCRIPTOR = new ContrastPluginConfigDescriptor();

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

    public TeamServerProfile getProfile() {
        return getProfile(teamServerProfileName);
    }

    public static TeamServerProfile getProfile(String profileName) {
        final TeamServerProfile[] profiles = DESCRIPTOR.getTeamServerProfiles();

        if (profileName == null && profiles.length > 0)
            return profiles[0];

        for (TeamServerProfile profile : profiles) {
            if (profile.getName().equals(profileName))
                return profile;
        }
        return null;
    }

    @Extension
    public static class ContrastPluginConfigDescriptor extends JobPropertyDescriptor {

        private CopyOnWriteList<TeamServerProfile> teamServerProfiles = new CopyOnWriteList<>();

        public ContrastPluginConfigDescriptor() {
            super(ContrastPluginConfig.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            final JSONArray array = json.optJSONArray("profile");

            System.out.println(json.toString());

            if (array != null) {
                System.out.println(array.toString());
                teamServerProfiles.replaceBy(req.bindJSONToList(TeamServerProfile.class, array));
            } else {
                System.out.println("here");
                if (json.keySet().isEmpty()) {
                    teamServerProfiles = new CopyOnWriteList<>();
                } else {
                    teamServerProfiles.replaceBy(req.bindJSON(TeamServerProfile.class, json.getJSONObject("profile")));
                }
            }

            save();

            return true;
        }

        public TeamServerProfile[] getTeamServerProfiles() {
            final TeamServerProfile[] profileArray = new TeamServerProfile[teamServerProfiles.size()];
            return teamServerProfiles.toArray(profileArray);
        }

        @SuppressWarnings("unused")
        public ListBoxModel doFillTeamServerProfileNameItems() {
            final ListBoxModel model = new ListBoxModel();

            for (TeamServerProfile profile : teamServerProfiles) {
                model.add(profile.getName(), profile.getName());
            }

            return model;
        }

        /**
         * Validation of the 'username' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckUsername(@QueryParameter String value) {
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
        public FormValidation doCheckApiKey(@QueryParameter String value) {
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
        public FormValidation doCheckServiceKey(@QueryParameter String value) {
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
        public FormValidation doCheckOrgUuid(@QueryParameter String value) {
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
        public FormValidation doCheckTeamServerUrl(@QueryParameter String value) {
            if (value.length() == 0)
                return FormValidation.error("Please set a TeamServer Url.");
            return FormValidation.ok();
        }

        /**
         * Validation of the 'applicationId' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckApplicationId(@QueryParameter String value) {
            if (value.length() == 0)
                return FormValidation.error("Please set an Application Id");
            return FormValidation.ok();
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req,
                                          JSONObject formData) throws FormException {
            String username = (String) formData.get("username");
            String apiKey = (String) formData.get("apiKey");
            String serviceKey = (String) formData.get("serviceKey");
            String teamServerUrl = (String) formData.get("teamServerUrl");
            String orgUuid = (String) formData.get("orgUuid");
            String applicationId = (String) formData.get("applicationId");

            return new ContrastPluginConfig(username, apiKey, serviceKey, teamServerUrl, orgUuid, applicationId);
        }

        @Override
        public String getDisplayName() {
            return "Contrast Plugin Configuration";
        }
    }
}
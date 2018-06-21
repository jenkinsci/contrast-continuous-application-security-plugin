package com.aspectsecurity.contrast.contrastjenkins;

import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.models.Organizations;
import com.contrastsecurity.sdk.ContrastSDK;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Result;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Contrast Plugin Configuration
 * <p>
 * Adds the necessary configuration options to a job's properties. Used in VulnerabilityTrendRecorder
 */
public class ContrastPluginConfig extends JobProperty<AbstractProject<?, ?>> {
    private String teamServerProfileName;

    @DataBoundConstructor
    public ContrastPluginConfig() {

    }

    @Override
    public ContrastPluginConfigDescriptor getDescriptor() {
        Jenkins instance = Jenkins.getInstance();

        if (instance != null) {
            return (ContrastPluginConfigDescriptor) instance.getDescriptor(getClass());
        } else {
            return null;
        }
    }

    public TeamServerProfile getProfile() {
        return getProfile(teamServerProfileName);
    }

    public static TeamServerProfile getProfile(String profileName) {
        final TeamServerProfile[] profiles = new ContrastPluginConfigDescriptor().getTeamServerProfiles();

        if (profileName == null && ArrayUtils.isNotEmpty(profiles)) {
            return profiles[0];
        }

        for (TeamServerProfile profile : profiles) {

            if (StringUtils.equals(profileName, profile.getName())) {
                return profile;
            }
        }
        return null;
    }

    @Extension
    public static class ContrastPluginConfigDescriptor extends JobPropertyDescriptor {

        private CopyOnWriteList<TeamServerProfile> teamServerProfiles = new CopyOnWriteList<>();

        private CopyOnWriteList<GlobalThresholdCondition> globalThresholdConditions = new CopyOnWriteList<>();

        public ContrastPluginConfigDescriptor() {
            super(ContrastPluginConfig.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            final JSONArray array = json.optJSONArray("profile");

            if (array != null) {
                teamServerProfiles.replaceBy(req.bindJSONToList(TeamServerProfile.class, array));
            } else {
                if (json.keySet().isEmpty()) {
                    teamServerProfiles = new CopyOnWriteList<>();
                } else {
                    teamServerProfiles.replaceBy(req.bindJSON(TeamServerProfile.class, json.getJSONObject("profile")));
                }
            }

            // refresh all org rules
            for (TeamServerProfile teamServerProfile : teamServerProfiles) {
                ContrastSDK contrastSDK = VulnerabilityTrendHelper.createSDK(teamServerProfile.getUsername(), teamServerProfile.getServiceKey(),
                        teamServerProfile.getApiKey(), teamServerProfile.getTeamServerUrl());

                teamServerProfile.setVulnerabilityTypes(VulnerabilityTrendHelper.saveRules(contrastSDK, teamServerProfile.getOrgUuid()));
            }


            final JSONArray globalThresholdConditionJsonArray = json.optJSONArray("globalThresholdCondition");

            if (globalThresholdConditionJsonArray != null) {
                globalThresholdConditions.replaceBy(req.bindJSONToList(GlobalThresholdCondition.class, globalThresholdConditionJsonArray));
            } else {
                if (json.keySet().isEmpty()) {
                    globalThresholdConditions = new CopyOnWriteList<>();
                } else {
                    globalThresholdConditions.replaceBy(req.bindJSON(GlobalThresholdCondition.class, json.getJSONObject("globalThresholdCondition")));
                }
            }

            save();

            return true;
        }

        /**
         * Validates the configured TeamServer profile by attempting to get the default profile for the username.
         *
         * @param username      String username of the TeamServer user
         * @param apiKey        String apiKey of the TeamServer user
         * @param serviceKey    String serviceKey of the TeamServer user
         * @param teamServerUrl String TeamServer Url
         * @return FormValidation
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doTestTeamServerConnection(@QueryParameter("username") final String username,
                                                         @QueryParameter("apiKey") final String apiKey,
                                                         @QueryParameter("serviceKey") final String serviceKey,
                                                         @QueryParameter("teamServerUrl") final String teamServerUrl) throws IOException, ServletException {
            if (StringUtils.isEmpty(username)) {
                return FormValidation.error("TeamServer Connection error: Username cannot be empty.");
            }

            if (StringUtils.isEmpty(apiKey)) {
                return FormValidation.error("TeamServer Connection error: Api Key cannot be empty.");
            }

            if (StringUtils.isEmpty(serviceKey)) {
                return FormValidation.error("TeamServer Connection error: Service Key cannot be empty");
            }

            if (StringUtils.isEmpty(teamServerUrl)) {
                return FormValidation.error("TeamServer Connection error: TeamServer URL cannot be empty.");
            }

            if (!teamServerUrl.endsWith("/Contrast/api")) {
                return FormValidation.error("TeamServer Connection error: TeamServer URL does not end with /Contrast/api.");
            }

            try {
                ContrastSDK contrastSDK = VulnerabilityTrendHelper.createSDK(username, serviceKey, apiKey, teamServerUrl);

                Organizations organizations = contrastSDK.getProfileDefaultOrganizations();

                if (organizations == null || organizations.getOrganization() == null) {
                    return FormValidation.error("TeamServer Connection error: No organization found, Check your credentials and URL.");
                }

                return FormValidation.ok("Successfully verified the connection to TeamServer!");
            } catch (IOException | UnauthorizedException e) {
                return FormValidation.error("TeamServer Connection error: Unable to connect to TeamServer.");
            }
        }

        public TeamServerProfile[] getTeamServerProfiles() {
            final TeamServerProfile[] profileArray = new TeamServerProfile[teamServerProfiles.size()];

            return teamServerProfiles.toArray(profileArray);
        }

        public GlobalThresholdCondition[] getGlobalThresholdConditions() {
            final GlobalThresholdCondition[] globalThresholdConditionArray = new GlobalThresholdCondition[globalThresholdConditions.size()];

            return globalThresholdConditions.toArray(globalThresholdConditionArray);
        }

        /**
         * Fills the Threshold Severity select drop down with severities for the configured application.
         *
         * @return ListBoxModel filled with severities.
         */
        public ListBoxModel doFillThresholdSeverityItems() {
            return VulnerabilityTrendHelper.getSeverityListBoxModel();
        }

        /**
         * Validation of the 'thresholdCount' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckThresholdCount(@QueryParameter String value) {

            if (!value.isEmpty()) {
                try {
                    int temp = Integer.parseInt(value);

                    if (temp < 0) {
                        return FormValidation.error("Please enter a positive integer.");
                    }

                } catch (NumberFormatException e) {
                    return FormValidation.error("Please enter a valid integer.");
                }
            } else {
                return FormValidation.error("Please enter a positive integer.");
            }

            return FormValidation.ok();
        }

        /**
         * Validation of the 'name' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckProfileName(@QueryParameter String value) {
            if (value.length() == 0)
                return FormValidation.error("Please set a profile name.");

            return FormValidation.ok();
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
         * Validation of the 'thresholdSeverity' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckThresholdSeverity(@QueryParameter String value) {
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
         * Validation of the 'applicationName' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckApplicationName(@QueryParameter String value) {
            if (value.length() == 0)
                return FormValidation.error("Please set an Application Name.");
            return FormValidation.ok();
        }

        @Override
        public String getDisplayName() {
            return "Contrast Plugin Configuration";
        }


        public ListBoxModel doFillVulnerableBuildResultItems() {
            ListBoxModel items = new ListBoxModel();

            items.add(Result.FAILURE.toString());
            items.add(Result.SUCCESS.toString());
            items.add(Result.UNSTABLE.toString());
            items.add(Result.NOT_BUILT.toString());
            items.add(Result.ABORTED.toString());

            return items;
        }
    }
}
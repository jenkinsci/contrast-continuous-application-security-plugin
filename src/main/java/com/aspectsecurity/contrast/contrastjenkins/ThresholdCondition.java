package com.aspectsecurity.contrast.contrastjenkins;


import com.contrastsecurity.exceptions.UnauthorizedException;
import com.contrastsecurity.sdk.ContrastSDK;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringJoiner;

/**
 * ThresholdCondition class contains the variables and logic to populate the conditions when verifying for vulnerabilities.
 */
public class ThresholdCondition extends AbstractDescribableImpl<ThresholdCondition> {

    @Setter
    @Getter
    private Integer thresholdCount;

    @Setter
    @Getter
    private String thresholdSeverity;

    @Setter
    @Getter
    private String thresholdVulnType;

    @Setter
    @Getter
    private ApplicationDefinition applicationDefinition;

    @Getter
    private String applicationId;

    @DataBoundSetter
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    //// Compatibility fix for plugin versions <=2.6
    @Setter
    @Getter
    private String applicationName;

    /**
     * Name that was used to instrument the agent
     */
    @Setter
    @Getter
    private String applicationOriginName;

    /**
     * Type of agent used to instrument the application
     */
    @Setter
    @Getter
    private String agentType;

    /**
     * Only gets set when application is not initiated
     */
    @Setter
    @Getter
    private boolean failOnAppNotFound;

    /**
     * 0 = instrumented
     * 1 = not instrumented
     */
    @Setter
    @Getter
    private int applicationState;

    @Setter
    @Getter
    private MatchBy matchBy;

    @Setter
    @Getter
    private boolean autoRemediated;
    @Setter
    @Getter
    private boolean confirmed;
    @Setter
    @Getter
    private boolean suspicious;
    @Setter
    @Getter
    private boolean notAProblem;
    @Setter
    @Getter
    private boolean remediated;
    @Setter
    @Getter
    private boolean reported;

    @Setter
    @Getter
    private boolean fixed;
    @Setter
    @Getter
    private boolean beingTracked;
    @Setter
    @Getter
    private boolean untracked;

    @DataBoundConstructor
    public ThresholdCondition(Integer thresholdCount, String thresholdSeverity, String thresholdVulnType, int applicationState, ApplicationDefinition applicationDefinition,
                              String applicationId, boolean autoRemediated, boolean confirmed, boolean suspicious,
                              boolean notAProblem, boolean remediated, boolean reported, boolean fixed,
                              boolean beingTracked, boolean untracked) {

        this.thresholdCount = thresholdCount;
        this.thresholdSeverity = thresholdSeverity;
        this.thresholdVulnType = thresholdVulnType;
        this.applicationDefinition = applicationDefinition;
        this.applicationId = applicationId;
        this.applicationState = applicationState;
        if(applicationState == 0) {
            this.matchBy = MatchBy.APPLICATION_ID;
        }else if(applicationDefinition != null) {
            this.matchBy = applicationDefinition.getMatchBy();
            this.applicationOriginName = applicationDefinition.getApplicationOriginName();
            this.agentType = applicationDefinition.getAgentType();
            this.failOnAppNotFound = applicationDefinition.isFailOnAppNotFound();
        }

        this.autoRemediated = autoRemediated;
        this.confirmed = confirmed;
        this.suspicious = suspicious;
        this.notAProblem = notAProblem;
        this.remediated = remediated;
        this.reported = reported;
        this.fixed = fixed;
        this.beingTracked = beingTracked;
        this.untracked = untracked;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("count is ").append(thresholdCount);

        if (thresholdSeverity != null) {
            sb.append(", severity is ").append(thresholdSeverity);
        }

        if (thresholdVulnType != null) {
            sb.append(", rule type is ").append(thresholdVulnType);
        }

        if (applicationId != null) {
            sb.append(", application ID is ").append(applicationId);
        }

        sb.append(".");

        return sb.toString();
    }

    /**
     * Returns the description of a condition that has been overridden with a job outcome policy.
     * @return
     */
    public String getStringForOverriden() {
        StringJoiner sj = new StringJoiner(", ");
        String preString = "[";
        String postString = "]";

        if(applicationOriginName != null) {
            sj.add("name='"+applicationOriginName+"'");
        }

        if(agentType != null) {
            sj.add("language='"+agentType+"'");
        }

        if(applicationId != null) {
            sj.add("applicationId='"+applicationId+"'");
        }

        return preString + sj.toString() + postString;
    }

    /**
     * Descriptor for {@link ThresholdCondition}.
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<ThresholdCondition> {

        Calendar lastAppsRefresh;
        int appsRefreshIntervalMinutes = 1;

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
         * Validation of the 'thresholdSeverity' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckThresholdSeverity(@QueryParameter String value) {
            return FormValidation.ok();
        }

        /**
         * Validation of the 'thresholdCategory' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckThresholdVulnType(@QueryParameter String value) {
            return FormValidation.ok();
        }

        /**
         * Validation of the 'applicationId' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckApplicationId(@QueryParameter("teamServerProfileName") @RelativePath("..") final String teamServerProfileName, @QueryParameter String value) {
            if (VulnerabilityTrendHelper.appExistsInProfile(teamServerProfileName, value)) {
                TeamServerProfile profile = VulnerabilityTrendHelper.getProfile(teamServerProfileName);
                ContrastSDK contrastSDK = VulnerabilityTrendHelper.createSDK(profile.getUsername(), profile.getServiceKey(), profile.getApiKey(), profile.getTeamServerUrl());
                String appId = value;
                if(value.contains("(") && value.contains(")")) {
                    appId = VulnerabilityTrendHelper.getAppIdFromAppTitle(value);
                }
                try {
                    if (VulnerabilityTrendHelper.isApplicableEnabledJobOutcomePolicyExist(contrastSDK, profile.getOrgUuid(), appId)) {
                        return FormValidation.warning("Your Contrast administrator has set a policy for vulnerability thresholds for this application. The Contrast policy overrides Jenkins vulnerability security controls and 'query vulnerabilities by' selection.");
                    }
                } catch (IOException | UnauthorizedException e) {
                    return FormValidation.warning("Unable to make connection with Contrast: " + e.getMessage());
                }
            } else if(!value.isEmpty()) {
                return FormValidation.warning("Application not found.");
            }
            return FormValidation.ok();

        }

        /**
         * Fills the Threshold Category combo box with application ids.
         *
         * @return ComboBoxModel filled with application ids.
         */
        public ComboBoxModel doFillApplicationIdItems(@QueryParameter("teamServerProfileName") @RelativePath("..") final String teamServerProfileName) {

            // Refresh apps every ${appsRefreshIntervalMinutes} minutes before filling in the combobox
            if (lastAppsRefresh == null || (Calendar.getInstance().getTimeInMillis() - lastAppsRefresh.getTimeInMillis()) / 60000 >= appsRefreshIntervalMinutes) {
                refreshApps(teamServerProfileName);
                lastAppsRefresh = Calendar.getInstance();
            }

            return VulnerabilityTrendHelper.getApplicationIdsComboBoxModel(teamServerProfileName);
        }

        public void refreshApps(String teamServerProfileName) {
            Jenkins jenkins = Jenkins.getInstance();
            if (jenkins != null) {
                ContrastPluginConfig.ContrastPluginConfigDescriptor contrastPluginConfigDescriptor = jenkins.getDescriptorByType(ContrastPluginConfig.ContrastPluginConfigDescriptor.class);
                TeamServerProfile teamServerProfile = VulnerabilityTrendHelper.getProfile(teamServerProfileName, contrastPluginConfigDescriptor);

                ContrastSDK contrastSDK = VulnerabilityTrendHelper.createSDK(teamServerProfile.getUsername(), teamServerProfile.getServiceKey(),
                        teamServerProfile.getApiKey(), teamServerProfile.getTeamServerUrl());

                teamServerProfile.setApps(VulnerabilityTrendHelper.saveApplicationIds(contrastSDK, teamServerProfile.getOrgUuid()));
                contrastPluginConfigDescriptor.save();
            }
        }

        public ListBoxModel doFillAgentTypeItems() {
            return VulnerabilityTrendHelper.getAgentTypeListBoxModel();
        }

        /**
         * Fills the Threshold Category select drop down with vulnerability types for the configured profile.
         *
         * @return ListBoxModel filled with vulnerability types.
         */
        public ListBoxModel doFillThresholdVulnTypeItems(@QueryParameter("teamServerProfileName") @RelativePath("..") final String teamServerProfileName) throws IOException {
            return VulnerabilityTrendHelper.getVulnerabilityTypes(teamServerProfileName);
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
         * Display name in the Build Action dropdown.
         *
         * @return String
         */
        public String getDisplayName() {
            return "Threshold Condition";
        }
    }

    public List<String> getVulnerabilityStatuses() {
        List<String> status = new ArrayList();
        if (autoRemediated) {
            status.add(Constants.VULNERABILITY_STATUS_AUTO_REMEDIATED);
        }
        if (confirmed) {
            status.add(Constants.VULNERABILITY_STATUS_CONFIRMED);
        }
        if (suspicious) {
            status.add(Constants.VULNERABILITY_STATUS_SUSPICIOUS);
        }
        if (notAProblem) {
            status.add(Constants.VULNERABILITY_STATUS_NOT_A_PROBLEM);
        }
        if (remediated) {
            status.add(Constants.VULNERABILITY_STATUS_REMEDIATED);
        }
        if (reported) {
            status.add(Constants.VULNERABILITY_STATUS_REPORTED);
        }
        if (fixed) {
            status.add(Constants.VULNERABILITY_STATUS_FIXED);
        }
        if (beingTracked) {
            status.add(Constants.VULNERABILITY_STATUS_BEING_TRACKED);
        }
        if (untracked) {
            status.add(Constants.VULNERABILITY_STATUS_UNTRACKED);
        }
        return status;
    }
}

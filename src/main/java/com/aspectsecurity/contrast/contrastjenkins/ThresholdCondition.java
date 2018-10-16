package com.aspectsecurity.contrast.contrastjenkins;


import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ThresholdCondition class contains the variables and logic to populate the conditions when verifying for vulnerabilities.
 */
@Getter
public class ThresholdCondition extends AbstractDescribableImpl<ThresholdCondition> {

    @Setter
    private Integer thresholdCount;

    @Setter
    private String thresholdSeverity;

    @Setter
    private String thresholdVulnType;

    private String applicationId;

    @DataBoundSetter
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    //// Compatibility fix for plugin versions <=2.6
    @Setter
    private String applicationName;

    @Setter
    private boolean autoRemediated;
    @Setter
    private boolean confirmed;
    @Setter
    private boolean suspicious;
    @Setter
    private boolean notAProblem;
    @Setter
    private boolean remediated;
    @Setter
    private boolean reported;

    @Setter
    private boolean fixed;
    @Setter
    private boolean beingTracked;
    @Setter
    private boolean untracked;

    @DataBoundConstructor
    public ThresholdCondition(Integer thresholdCount, String thresholdSeverity, String thresholdVulnType,
                              String applicationId, boolean autoRemediated, boolean confirmed, boolean suspicious,
                              boolean notAProblem, boolean remediated, boolean reported, boolean fixed,
                              boolean beingTracked, boolean untracked) {

        this.thresholdCount = thresholdCount;
        this.thresholdSeverity = thresholdSeverity;
        this.thresholdVulnType = thresholdVulnType;
        this.applicationId = applicationId;

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
     * Descriptor for {@link ThresholdCondition}.
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<ThresholdCondition> {

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
        public FormValidation doCheckApplicationId(@QueryParameter String value) {
            return FormValidation.ok();
        }

        /**
         * Fills the Threshold Category select drop down with application ids.
         *
         * @return ListBoxModel filled with application ids.
         */
        public ListBoxModel doFillApplicationIdItems(@QueryParameter("teamServerProfileName") @RelativePath("..") final String teamServerProfileName) throws IOException {
            return VulnerabilityTrendHelper.getApplicationIds(teamServerProfileName);
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

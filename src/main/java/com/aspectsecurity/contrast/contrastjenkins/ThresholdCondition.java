package com.aspectsecurity.contrast.contrastjenkins;


import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.Arrays;
import java.util.List;

public class ThresholdCondition extends AbstractDescribableImpl<ThresholdCondition> {

    private String thresholdCount;

    private String thresholdSeverity;

    private String thresholdVulnType;

    @DataBoundConstructor
    public ThresholdCondition(String thresholdCount, String thresholdSeverity, String thresholdVulnType) {
        this.thresholdCount = thresholdCount;
        this.thresholdSeverity = thresholdSeverity;
        this.thresholdVulnType = thresholdVulnType;
    }

    public String getThresholdCount() {
        return thresholdCount;
    }

    public String getThresholdSeverity() {
        return thresholdSeverity;
    }

    public String getThresholdVulnType() {
        return thresholdVulnType;
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

            if (value.length() > 0) {
                try {
                    int temp = Integer.parseInt(value);

                    if (temp < 0) {
                        return FormValidation.error("Please enter a positive integer.");
                    }

                } catch (NumberFormatException e) {
                    return FormValidation.error("Please enter a valid integer.");
                }
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


        public List<String> getSeverities() {
            return SEVERITIES;
        }

        // TODO fix with rules
        public List<String> getVulnTypes() {
            return VULN_TYPES;
        }

        /**
         * Fills the Threshold Category select drop down with categories for the configured application.
         *
         * @return ListBoxModel filled with categories.
         */
        public ListBoxModel doFillThresholdVulnTypeItems() {
            ListBoxModel items = new ListBoxModel();

            items.add(EMPTY_SELECT, null);

            for (String type : VULN_TYPES) {
                items.add(type, type);
            }

            return items;
        }

        /**
         * Fills the Threshold Severity select drop down with severities for the configured application.
         *
         * @return ListBoxModel filled with severities.
         */
        public ListBoxModel doFillThresholdSeverityItems() {
            ListBoxModel items = new ListBoxModel();
            items.add(EMPTY_SELECT, null);

            for (String severity : SEVERITIES) {
                items.add(severity, severity);
            }

            return items;
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

    private static final List<String> SEVERITIES = Arrays.asList("Note", "Low", "Medium", "High", "Critical");
    private static final List<String> VULN_TYPES = Arrays.asList("Sql Injection", "CSRF");
    private static final String EMPTY_SELECT = "None";
}

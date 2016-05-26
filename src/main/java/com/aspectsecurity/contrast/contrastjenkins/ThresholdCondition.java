package com.aspectsecurity.contrast.contrastjenkins;


import org.kohsuke.stapler.DataBoundConstructor;

public class ThresholdCondition {

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

}

package com.aspectsecurity.contrast.contrastjenkins;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GlobalThresholdCondition {

    private Integer thresholdCount;

    private String thresholdSeverity;


    private boolean autoRemediated;
    private boolean confirmed;
    private boolean suspicious;
    private boolean notAProblem;
    private boolean remediated;
    private boolean reported;
    private boolean fixed;
    private boolean beingTracked;
    private boolean untracked;

    @DataBoundConstructor
    public GlobalThresholdCondition(Integer thresholdCount, String thresholdSeverity, boolean autoRemediated,
                                    boolean confirmed, boolean suspicious, boolean notAProblem, boolean remediated,
                                    boolean reported, boolean fixed, boolean beingTracked, boolean untracked) {
        this.thresholdCount = thresholdCount;
        this.thresholdSeverity = thresholdSeverity;
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

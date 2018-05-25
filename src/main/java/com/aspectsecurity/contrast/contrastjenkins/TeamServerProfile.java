package com.aspectsecurity.contrast.contrastjenkins;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TeamServerProfile {

    private String name;

    private String username;

    private String apiKey;

    private String serviceKey;

    private String orgUuid;

    private String teamServerUrl;

    private String applicationName;

    private List<VulnerabilityType> vulnerabilityTypes;

    private boolean failOnWrongApplicationName;

    private String vulnerableBuildResult;

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
    public TeamServerProfile(String name, String username, String apiKey, String serviceKey, String orgUuid,
                             String teamServerUrl, String applicationName, boolean failOnWrongApplicationName,
                             String vulnerableBuildResult, boolean autoRemediated, boolean confirmed, boolean suspicious,
                             boolean notAProblem, boolean remediated, boolean reported, boolean fixed,
                             boolean beingTracked, boolean untracked) {
        this.name = name;
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.orgUuid = orgUuid;
        this.teamServerUrl = teamServerUrl;
        this.applicationName = applicationName;
        this.failOnWrongApplicationName = failOnWrongApplicationName;
        this.vulnerableBuildResult = vulnerableBuildResult;
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

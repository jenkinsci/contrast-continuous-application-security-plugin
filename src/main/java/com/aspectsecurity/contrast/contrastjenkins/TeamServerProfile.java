package com.aspectsecurity.contrast.contrastjenkins;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;

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

    private boolean failOnWrongApplicationId;

    private String vulnerableBuildResult;

    private boolean allowGlobalThresholdConditionsOverride;

    @DataBoundConstructor
    public TeamServerProfile(String name, String username, String apiKey, String serviceKey, String orgUuid,
                             String teamServerUrl, String applicationName, boolean failOnWrongApplicationId,
                             String vulnerableBuildResult, boolean allowGlobalThresholdConditionsOverride) {
        this.name = name;
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.orgUuid = orgUuid;
        this.teamServerUrl = teamServerUrl;
        this.applicationName = applicationName;
        this.failOnWrongApplicationId = failOnWrongApplicationId;
        this.vulnerableBuildResult = vulnerableBuildResult;
        this.allowGlobalThresholdConditionsOverride = allowGlobalThresholdConditionsOverride;
    }
}

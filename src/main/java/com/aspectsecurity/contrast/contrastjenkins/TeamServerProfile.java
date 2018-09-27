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

    private List<VulnerabilityType> vulnerabilityTypes;

    private boolean failOnWrongApplicationId;

    /////// Compatibility fix for plugin versions <=2.6
    private Boolean failOnWrongApplicationName;
    ///////

    private String vulnerableBuildResult;

    private boolean allowGlobalThresholdConditionsOverride;

    private List<App> apps;

    @DataBoundConstructor
    public TeamServerProfile(String name, String username, String apiKey, String serviceKey, String orgUuid,
                             String teamServerUrl, boolean failOnWrongApplicationId,
                             String vulnerableBuildResult, boolean allowGlobalThresholdConditionsOverride) {
        this.name = name;
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.orgUuid = orgUuid;
        this.teamServerUrl = teamServerUrl;
        this.failOnWrongApplicationId = failOnWrongApplicationId;
        this.vulnerableBuildResult = vulnerableBuildResult;
        this.allowGlobalThresholdConditionsOverride = allowGlobalThresholdConditionsOverride;
    }
}

package com.aspectsecurity.contrast.contrastjenkins;

import hudson.util.Secret;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

@Setter
public class TeamServerProfile {

    @Getter
    private String name;

    @Getter
    private String username;

    private Secret apiKey;

    private Secret serviceKey;

    @Getter
    private String orgUuid;

    @Getter
    private String teamServerUrl;

    @Getter
    private String applicationName;

    @Getter
    private List<VulnerabilityType> vulnerabilityTypes;

    @Getter
    private boolean failOnWrongApplicationName;
    @Getter
    private String vulnerableBuildResult;
    @Getter
    private boolean allowGlobalThresholdConditionsOverride;

    @DataBoundConstructor
    public TeamServerProfile(String name, String username, String apiKey, String serviceKey, String orgUuid,
                             String teamServerUrl, String applicationName, boolean failOnWrongApplicationName,
                             String vulnerableBuildResult, boolean allowGlobalThresholdConditionsOverride) {
        this.name = name;
        this.username = username;
        this.apiKey = Secret.fromString(apiKey);
        this.serviceKey = Secret.fromString(serviceKey);
        this.orgUuid = orgUuid;
        this.teamServerUrl = teamServerUrl;
        this.applicationName = applicationName;
        this.failOnWrongApplicationName = failOnWrongApplicationName;
        this.vulnerableBuildResult = vulnerableBuildResult;
        this.allowGlobalThresholdConditionsOverride = allowGlobalThresholdConditionsOverride;
    }

    public Secret getSecretApiKey() {
        return apiKey;
    }

    public Secret getSecretServiceKey() {
        return serviceKey;
    }

    String getApiKey() {
        return apiKey.getPlainText();
    }

    String getServiceKey() {
        return serviceKey.getPlainText();
    }
}

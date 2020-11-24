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

    private boolean applyVulnerableBuildResultOnContrastError;

    @Getter
    private String vulnerableBuildResult;

    @Getter
    private boolean allowGlobalThresholdConditionsOverride;

    @Getter
    private List<App> apps;

    //Compatibility fix for plugin versions <= 3.6
    /**
     * @Deprecated
     * Use {@link TeamServerProfile#applyVulnerableBuildResultOnContrastError}
     */
    @Getter
    @Deprecated
    private boolean failOnWrongApplicationId;

    /////// Compatibility fix for plugin versions <=2.6
    /**
     * @Deprecated
     * Use {@link TeamServerProfile#applyVulnerableBuildResultOnContrastError}
     */
    @Getter
    @Deprecated
    private boolean failOnWrongApplicationName;

    @DataBoundConstructor
    public TeamServerProfile(String name, String username, String apiKey, String serviceKey, String orgUuid,
                             String teamServerUrl, boolean applyVulnerableBuildResultOnContrastError,
                             String vulnerableBuildResult, boolean allowGlobalThresholdConditionsOverride) {
        this.name = name;
        this.username = username;
        this.apiKey = Secret.fromString(apiKey);
        this.serviceKey = Secret.fromString(serviceKey);
        this.orgUuid = orgUuid;
        this.teamServerUrl = teamServerUrl;
        this.applyVulnerableBuildResultOnContrastError = applyVulnerableBuildResultOnContrastError;
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

    public boolean isApplyVulnerableBuildResultOnContrastError() {
      //backwards compatability fix for profiles with old configurations
      if(!this.applyVulnerableBuildResultOnContrastError && (this.isFailOnWrongApplicationId() || this.isFailOnWrongApplicationName())) {
        return true;
      }
      return this.applyVulnerableBuildResultOnContrastError;
    }
}

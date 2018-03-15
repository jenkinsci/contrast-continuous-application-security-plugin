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

    private boolean failOnWrongApplicationName;

    @DataBoundConstructor
    public TeamServerProfile(String name, String username, String apiKey, String serviceKey,
                             String teamServerUrl, String orgUuid, String applicationName, boolean failOnWrongApplicationName) {
        this.name = name;
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.teamServerUrl = teamServerUrl;
        this.orgUuid = orgUuid;
        this.applicationName = applicationName;
        this.failOnWrongApplicationName = failOnWrongApplicationName;
    }
}

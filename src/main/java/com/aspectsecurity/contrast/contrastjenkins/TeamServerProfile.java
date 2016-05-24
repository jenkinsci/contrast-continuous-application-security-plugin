package com.aspectsecurity.contrast.contrastjenkins;

import org.kohsuke.stapler.DataBoundConstructor;

public class TeamServerProfile {

    private String name;

    private String username;

    private String apiKey;

    private String serviceKey;

    private String orgUuid;

    private String teamServerUrl;

    private String applicationId;

    @DataBoundConstructor
    public TeamServerProfile(String name, String username, String apiKey, String serviceKey, String teamServerUrl, String orgUuid, String applicationId) {
        this.name = name;
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.teamServerUrl = teamServerUrl;
        this.orgUuid = orgUuid;
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public String getOrgUuid() {
        return orgUuid;
    }

    public String getTeamServerUrl() {
        return teamServerUrl;
    }

    public String getApplicationId() {
        return applicationId;
    }
}

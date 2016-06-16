package com.aspectsecurity.contrast.contrastjenkins;

import org.kohsuke.stapler.DataBoundConstructor;

public class TeamServerProfile {

    private String name;

    private String username;

    private String apiKey;

    private String serviceKey;

    private String orgUuid;

    private String teamServerUrl;

    private String applicationName;

    private String serverName;

    @DataBoundConstructor
    public TeamServerProfile(String name, String serverName, String username, String apiKey, String serviceKey, String teamServerUrl, String orgUuid, String applicationName) {
        this.name = name;
        this.serverName = serverName;
        this.username = username;
        this.apiKey = apiKey;
        this.serviceKey = serviceKey;
        this.teamServerUrl = teamServerUrl;
        this.orgUuid = orgUuid;
        this.applicationName = applicationName;
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

    public String getApplicationName() {
        return applicationName;
    }

    public String getServerName() {
        return serverName;
    }
}

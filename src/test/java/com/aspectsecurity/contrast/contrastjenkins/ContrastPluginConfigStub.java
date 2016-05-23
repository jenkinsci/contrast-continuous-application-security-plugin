package com.aspectsecurity.contrast.contrastjenkins;


public class ContrastPluginConfigStub extends ContrastPluginConfig {

    public ContrastPluginConfigStub(String username, String apiKey, String serviceKey, String teamServerUrl, String orgUuid, String applicationId) {
        super(username, apiKey, serviceKey, teamServerUrl, orgUuid, applicationId);
    }

    public static class ContrastPluginConfigDescriptorStub extends ContrastPluginConfig.ContrastPluginConfigDescriptor {

        @Override
        public synchronized void load() {
        }
    }
}
package com.aspectsecurity.contrast.contrastjenkins;

import hudson.util.ListBoxModel;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;

@Getter
@Setter
public class ApplicationDefinition {
    /**
     * 0 = match using application id
     * 1 = match using application orgin name and agent type
     * 2 = match using application short name
     */
    private int value;
    private String applicationId;
    private String applicationOriginName;
    private String agentType;
    private boolean isInstrumented;
    private boolean failOnAppNotFound;
    private String applicationShortName;

    @DataBoundConstructor
    public ApplicationDefinition(int value, String applicationId, String applicationOriginName, String applicationShortName, String agentType, boolean doNotFailIfAppNotFound) {
        this.value = value;
        this.applicationId = applicationId;
        this.isInstrumented = (value == 0);
        this.applicationOriginName = applicationOriginName;
        this.applicationShortName = applicationShortName;
        this.agentType = agentType;
        this.failOnAppNotFound = doNotFailIfAppNotFound;
    }
}

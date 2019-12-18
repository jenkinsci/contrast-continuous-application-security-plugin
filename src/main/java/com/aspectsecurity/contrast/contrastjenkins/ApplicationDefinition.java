package com.aspectsecurity.contrast.contrastjenkins;

import hudson.util.ListBoxModel;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;

@Getter
@Setter
public class ApplicationDefinition {

    private MatchBy matchBy;
    private String applicationId;
    private String applicationOriginName;
    private String agentType; //Application Language in UI
    private boolean failOnAppNotFound;
    private String applicationShortName; //Application Code in UI

    @DataBoundConstructor
    public ApplicationDefinition(String applicationId, String applicationOriginName, String applicationShortName, String agentType, boolean failOnAppNotFound) {
        this.applicationId = applicationId;
        this.applicationOriginName = applicationOriginName;
        this.applicationShortName = applicationShortName;
        this.agentType = agentType;
        this.failOnAppNotFound = failOnAppNotFound;
        if(applicationOriginName != null) {
            this.matchBy = MatchBy.APPLICATION_ORIGIN_NAME;
        } else if(applicationShortName != null) {
            this.matchBy = MatchBy.APPLICATION_SHORT_NAME;
        }
    }
}

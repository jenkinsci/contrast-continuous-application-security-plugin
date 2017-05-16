<!--Jenkins Cat -->
![Jenkins Cat](img/jenkins-cat.png "Jenkins Cat" )

# Contrast Jenkins Plugin

Repository for the Contrast Jenkins plugin. This plugin adds the ability to configure a connection to a Jenkins Build.

## Variables

| Parameter                   | Description                                             |
|-----------------------------|---------------------------------------------------------|
| Contrast Username         | Username/email for your account in Contrast |
| Contrast API Key          | API Key found in **Organization Settings**                |
| Contrast Service Key      | Service Key found in **Organization Settings**             |
| Contrast URL          | API URL to your Contrast instance <BR> Use *https://app.contrastsecurity.com/Contrast/api* if you're a SaaS customer; all others use the URL of your Contrast UI (e.g., *http://contrastserver:8080/Contrast/api*). |
| Organization UUID | Organization UUID of the configured user found in **Organization Settings** <BR> You can also copy it from the URL when viewing the home page in Contrast. |

---

## Workflow

There are currently 2 build items added by this plugin:

* #### Test TeamServer Connection

    This will verify the Jenkins can connect to TeamServer with the configured variables. It will fail the build if the plugin is unable to connect. This test can be found as a button when adding a TeamServer profile.

* #### Verify Vulnerability Threshold

    Available as a step and a post build action.

    This will check TeamServer for the number of vulnerabilities in the application. The only required variable is `Threshold Count` which must be a positive integer. The other two variables `Threshold Severity` and `Threshold Vulnerability Type` are not required but can be useful if you want to filter the threshold count.
    
    **NOTE:** The variables in the Threshold Condition form are required to run this build action.
    
    This verification will fail the Jenkins build even if your build succeeded.

	## Test for Vulnerabilities

In order for the Jenkins plugin to get accurate information, you must add a unique identifier built from the Jenkins CI configuration as an agent property. The corresponding property for the Java agent is `contrast.override.appversion`. Also, the job name must match your application name or you must override your application name with another property to ensure that Contrast tests for the correct information.

The plugin uses the unique identifier `${JOB_NAME}-${BUILD_NUMBER}` to filter vulnerabilities and check conditions. `JOB_NAME` and `BUILD_NUMBER` are available as Jenkins environment <a href="https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project">properties</a>.
    
## Charts

There are 2 charts that are generated after each build `Vulnerability Trends Across Builds` and `Severity Trends Across Builds`.

Here are two examples of the charts:

![Severity Trends Across Builds](img/severity_trends.png)

![Vulnerability Trends Across Builds](img/vuln_trends.png)

## Exported Configurations

[TeamServer Profile Config](contrastPluginConfig.xml)

[Threshold Condition Config](vulnerabilityTrendRecorderConfig.xml)


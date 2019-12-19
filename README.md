<!--Jenkins Cat -->
![Jenkins Cat](img/jenkins-cat.png "Jenkins Cat" )

# Contrast Jenkins Plugin - Version 3.1

Repository for the Contrast Jenkins plugin. This plugin adds the ability to configure a connection to a Jenkins Build.

## Requirements
* Jenkins version >= 2.60.3
> Note: for Jenkins versions between 1.625.3 and 2.60.3, use plugin version [2.12.1](https://github.com/jenkinsci/contrast-continuous-application-security-plugin/releases/tag/contrast-continuous-application-security-2.12.1)

## Variables

| Parameter                   | Description                                             | Since |
|-----------------------------|---------------------------------------------------------|-------|
| Contrast Username         | Username/email for your account in Contrast | 
| Contrast API Key          | API Key found in **Organization Settings**                | 
| Contrast Service Key      | Service Key found in **Organization Settings**             |
| Contrast URL          | API URL to your Contrast instance <BR> Use *https://app.contrastsecurity.com/Contrast/api* if you're a SaaS customer; all others use the URL of your Contrast UI (e.g., *http://contrastserver:8080/Contrast/api*). |
| Organization ID | Organization ID of the configured user found in **Organization Settings** <BR> You can also copy it from the URL when viewing the home page in Contrast. |
|ignoreContrastFindings | Jenkins boolean build parameter. If set to true, builds will not be failed when Vulnerability Threshold Conditions are not met. | 2.3 |
| Result of a vulnerable build | Contrast TeamServer profile configuration parameter allowing to choose the result of a build that does not meet the Vulnerability Threshold Conditions. | 2.3 |
---

## Workflow

There are currently 2 build items added by this plugin:

* #### Test Contrast Connection

    This will verify the Jenkins can connect to TeamServer with the configured variables. It will fail the build if the plugin is unable to connect. This test can be found as a button when adding a TeamServer profile.

* #### Contrast Assess

    Available as a step and a post build action.

    This will check Contrast for the number of vulnerabilities in the application. The required variables are `Threshold Count` which must be a positive integer and `Application` either using `Application Name` or chosen from `Choose your application` dropdown. The other two variables `Threshold Severity` and `Threshold Vulnerability Type` are not required but can be useful if you want to filter the threshold count.
    
    **NOTE:** The variables in the Threshold Condition form are required to run this build action.
    
    This verification will fail the Jenkins build even if your build succeeded.

## Test for Vulnerabilities

In order for the Jenkins plugin to get accurate information, you must add a unique identifier built from the Jenkins CI configuration as an agent property. The corresponding property for the Java agent is `contrast.override.appversion`. Also, for each Threshold Condition, you have to specify the Application Name to ensure that Contrast tests for the correct information.

   
## Charts

There are 2 charts that are generated after each build `Vulnerability Trends Across Builds` and `Severity Trends Across Builds`.

Here are two examples of the charts:

![Severity Trends Across Builds](img/severity_trends.png)

![Vulnerability Trends Across Builds](img/vuln_trends.png)

## Exported Configurations

[TeamServer Profile Config](contrastPluginConfig.xml)

[Threshold Condition Config](vulnerabilityTrendRecorderConfig.xml)

## Building the plugin

`mvn clean install`

## Running Locally

`./run.sh`


<!--Jenkins Cat -->
![Jenkins Cat](img/jenkins-cat.png "Jenkins Cat" )

# Contrast Jenkins Plugin

Repository for the Contrast Jenkins plugin. This plugin adds the ability to configure a connection to a Jenkins Build.

## Requirements
* Jenkins version >= 2.60.3
> Note: for Jenkins versions between 1.625.3 and 2.60.3, use plugin version [2.12.1](https://github.com/jenkinsci/contrast-continuous-application-security-plugin/releases/tag/contrast-continuous-application-security-2.12.1)

## Variables

|**Parameter** | **Description** | **Since** |
|:-------------|:----------------|:----------|
| Contrast Username | Username/email for your account in Contrast | |
| Contrast API Key | Log in to your Contrast account and go to **Your Account**. Look under **YOUR KEYS**. | |
| Contrast Service Key | Log in to your Contrast account and go to **Your Account**. Look under **YOUR KEYS**. | |
| Contrast URL | API URL to your Contrast instance Use https://app.contrastsecurity.com/Contrast/api if you're a SaaS customer; all others use the URL of your Contrast UI (e.g., https://contrastserver/Contrast/api). | |
| Organization ID | Organization ID of the configured user found in Organization Settings | |
| ignoreContrastFindings | Jenkins boolean build parameter. If set to true, builds will not be failed when Contrast Vulnerability Security Controls are not met. | 2.3 |
| Result of a vulnerable build | Contrast Connection configuration parameter allowing to choose the result of a build that does not meet the Contrast Vulnerability Security Controls. | 2.3 |
| Fail build if application is not found in Contrast | This option allows to fail a build if the application is not found in the Contrast application. | 2.4 |
| Allow global Contrast Vulnerability Security Controls to be overridden in a Job configuration | Choose if global Contrast Vulnerability Security Controls can be overridden in post-build actions. (See the Global Contrast Vulnerability Security Controls section for more details.) | 2.5 |

---

## Workflow

There are currently 2 build items added by this plugin:

* #### Test Contrast Connection

    This will verify the Jenkins can connect to Contrast with the configured variables. It will fail the build if the plugin is unable to connect. This test can be found as a button when adding a Contrast Connection.

* #### Contrast Assess

    Available as a step and a post build action.

    This will check Contrast for the number of vulnerabilities in the application. The required variables are `Number of Allowed Vulnerabilities` which must be a positive integer and `Application` either using `Application Name` or chosen from `Choose your application` dropdown. The other two variables `Vulnerability Severity` and `Vulnerability Type` are not required but can be useful if you want to filter the Number of Allowed Vulnerabilities.
    
    **NOTE:** The variables in the Contrast Vulnerability Security Controls form are required to run this build action.
    
    This verification will fail the Jenkins build even if your build succeeded.

## Test for Vulnerabilities

In order for the Jenkins plugin to get accurate information, you must add a unique identifier built from the Jenkins CI configuration as an agent property. The corresponding property for the Java agent is `contrast.override.appversion`. Also, for each Contrast Vulnerability Security Control, you have to specify the Application Name to ensure that Contrast tests for the correct information.

   
## Charts

There are 2 charts that are generated after each build `Vulnerability Trends Across Builds` and `Severity Trends Across Builds`.

Here are two examples of the charts:

![Severity Trends Across Builds](img/severity_trends.png)

![Vulnerability Trends Across Builds](img/vuln_trends.png)

> **Note:** The Vulnerability Report is not supported by the pipeline step and jobs that have applications with overridden Vulnerability Security Controls. Your Contrast admin can override the Vulnerability Security Controls for certain applications using the Job Outcome Policies in Contrast. 

## Exported Configurations

[TeamServer Profile Config](contrastPluginConfig.xml)

[Contrast Vulnerability Security Controls Config](vulnerabilityTrendRecorderConfig.xml)

## Building the plugin

`mvn clean install`

## Running Locally

`./run.sh`


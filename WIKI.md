# About

This plugin verifies vulnerability conditions by checking a build's
vulnerabilities found against configured filters. The plugin also graphs
history of vulnerability detection found during each projects build.

This plugin supports a post build action and a step in the pipeline
build process.

# Use the Plugin

You can view the plugin code in Jenkins' [Github
repository](https://github.com/jenkinsci/contrast-continuous-application-security-plugin).
In the Jenkins dashboard, go to **Manage Jenkins** in the left sidebar,
and select the **Configure System** page to find a new Contrast
Connections section.

# Requirements

Jenkins Version >= 2.60.3

# Contrast Documentation
For more information, you can view our [Contrast Docs](https://docs.contrastsecurity.com/en/jenkins.html).

# Contrast API Settings

Contrast API settings enable the plugin to connect to Contrast and query
for results. The plugin leverages these result to authenticate to
Contrast and make API calls in post-build actions. Among the following
requirements, you'll need a unique profile name to identify your
configuration and use it in a specific job.

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



<a href="img/Jenkins_Contrast_Connection.png" rel="lightbox" title="Jenkins Contrast"><img class="thumbnail" src="img/Jenkins_Contrast_Connection.png" height="400"/></a>

### Test the connection

When you add a Contrast Connection, use the validation button to test your
connection and make sure that all the fields are accurate. Contrast
prompts you if the test is successful or gives an error message if it
fails.

### Global Contrast Vulnerability Security Controls (VSC)

Once a connection is made, complete the following fields for **Contrast Vulnerability Security Controls**.

-   Select a **Connection** from the dropdown.
-   Add a **Number of Allowed Vulnerabilities**. The count is exclusive; if you set a count for "5",
    it fails on six or more vulnerabilities. This field is **required**.
-   Choose a **Vulnerability Severity** from the options in the dropdown menu (Note,
    Low, Medium, High or Critical). The plugin sets a filter in the API
    call for all vulnerabilities greater than or equal to this field.
    This field is recommended to reduce your results, but not required. 
-   Choose a **Vulnerability Type** (rule name) from the dropdown menu.
    If you specify a single rule for which to filter, the plugin checks
    for the number of vulnerabilities with the rule type and compares it
    to the count. This field is recommended to reduce your results, but
    not required. 
-   Choose from the list of **Vulnerability Statuses**. Statues aren't
    required, but can be helpful if you want to exclude vulnerabilities
    with certain statuses - for example, "Not a Problem" - from the
    results. If you don't select any statuses, the plugin won't filter
    vulnerabilities by statuses.

You can add as many rules as you like. The plugin fails on
the **first** bad condition and tells you on which condition it failed.

> **Note**: Even if your build succeeds, the plugin fails the overall
> build if the test finds a bad condition.

<a href="img/Jenkins_Global_Vulnerability_Security_Control.png" rel="lightbox" title="Jenkins Global Contrast Vulnerability Security Controls
"><img class="thumbnail" src="img/Jenkins_Global_Vulnerability_Security_Control.png" height="400"/></a>


### Contrast Vulnerability Security Controls in a post-build action

Complete the following fields for **Post-Build Actions**.

-   Select a **Profile** from the dropdown.
-   Select **Query vulnerabilities by**. By default, the plugin uses the
    first option: "appVersionTag, format: applicationId-buildNumber".
-   If the profile is configured to allow the global VSCs to be overridden, you can choose to do so.
-   Choose your application. This field is **required**.
    -   If your application has been instrumented, select your application from the **Choose your application** dropdown.
    -   If your application has not yet been instrumented, define your application using the **Application Name** and **Application Language** fields.

-   If you chose to override the global VSCs, fill in
    the rest of the fields,
    including **Count**,**Severity**, **Vulnerability Type**,
    and **Vulnerability Statuses** similarly to the global VSCs described above.

#### Instrumented
<a href="img/Jenkins_build_appId.png" rel="lightbox" title="VSC configuration with application"><img class="thumbnail" src="img/Jenkins_build_appId.png" height="250"/></a>

#### Not yet instrumented
<a href="img/Jenkins_build_appName.png" rel="lightbox" title="VSC configuration with application name"><img class="thumbnail" src="img/Jenkins_build_appName.png" height="250"/></a>


### Contrast Vulnerability Security Controls in a Pipeline step

When you add a Pipeline step with the name `contrastVerification`, it
follows the same principles as the post-build action but in a newer
format for Jenkins 2.0 improvements.

Pipeline configuration:

    contrastVerification applicationId: '1e6ad9c6-89d4-4f06-bdf6-92c569ec89de', count: 1, profile: 'new-profile', queryBy: 3, rule: 'cache-controls-missing', severity: 'High'

or 
    
    contrastVerification applicationName: 'MyApp', agentType: 'Java', count: 1, profile: 'new-profile', queryBy: 3, rule: 'cache-controls-missing', severity: 'High'
    
# Test for Vulnerabilities

For the Jenkins plugin to get accurate information, you must add a
unique identifier built from the Jenkins CI configuration as an agent
property. The corresponding property for the Java agent is
`contrast.override.appversion`. For example, when starting the Contrast
agent, add the following property:
"-Dcontrast.override.appversion=${applicationVersion}".

The plugin can use either the unique identifier `appVersionTag` or the
`startDate` to filter vulnerabilities and check conditions. You can
change the format used by the plugin to create `appVersionTag` or set
the plugin to use `startDate` using `queryBy` pipeline parameter. Three
options are available:  

-   appVersionTag, format: `applicationId-${BUILD_NUMBER}` (default)
-   appVersionTag, format: `applicationId-${JOB_NAME}-${BUILD_NUMBER}`
-   `startDate` (Build timestamp)
-   Parameter: APPVERSIONTAG

> **Note:** The queryBy option should match the contrast.override.appversion parameter you pass to the Contrast Java agent when running your application. If you use the third option (startDate), you aren't required to pass the contrast.override.appversion parameter to the Java agent.

Both `JOB_NAME` and `BUILD_NUMBER` are available as Jenkins environment
[properties](https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project).You can specify your own text for APPVERSIONTAG by selecting the fourth queryBy option and exporting APPVERSIONTAG as an environment property within your Jenkins job.


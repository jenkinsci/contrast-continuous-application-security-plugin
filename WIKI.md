# About

This plugin verifies vulnerability conditions by checking a build's
vulnerabilities found against configured filters. The plugin also graphs
history of vulnerability detection found during each projects build.

This plugin supports a post build action and a step in the pipeline
build process.

# Use the Plugin

You can view the plugin code in Jenkins' [Github
repository](https://github.com/jenkinsci/contrast-continuous-application-security-plugin){.external-link}.
In the Jenkins dashboard, go to **Manage Jenkins** in the left sidebar,
and select the **Configure System** page to find a new Contrast
TeamServer profiles section.

# Contrast API Settings

Contrast API settings enable the plugin to connect to Contrast and query
for results. The plugin leverages these result to authenticate to
Contrast and make API calls in post-build actions. Among the following
requirements, you'll need a unique profile name to identify your
configuration and use it in a specific job.

[TABLE]

  

  

![](https://wiki.jenkins.io/download/attachments/99058675/Jenkins_ts_profile.png?version=2&modificationDate=1534378875000&api=v2){.confluence-embedded-image
height="400"}

  

### Test the connection

When you add a Contrast profile, use the validation button to test your
connection and make sure that all the fields are accurate. Contrast
prompts you if the test is successful or gives an error message if it
fails.

### Global threshold conditions

Once a connection is made, complete the following fields for **Contrast
Vulnerability Threshold Conditions**.

-   Select a **Profile** from the dropdown.
-   Add a **Count**. The count is exclusive; if you set a count for "5",
    it fails on six or more vulnerabilities. This field is **required**.
-   Choose a **Severity** from the options in the dropdown menu (Note,
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

### ![](https://wiki.jenkins.io/download/attachments/99058675/Jenkins_global_threshold_condition.png?version=1&modificationDate=1534380027000&api=v2){.confluence-embedded-image height="400"}

  

### Threshold conditions in a post-build action

Complete the following fields for **Post-Build Actions**.

-   Select a **Profile** from the dropdown.
-   Select **Query vulnerabilities by**. By default, the plugin uses the
    first option: "appVersionTag, format: applicationId-buildNumber".
-   If the profile is configured to allow the global threshold
    conditions to be overridden, you can choose to do so.
-   Select the **Application Id** from the dropdown menu. This field is
    **required**.
-   If you chose to override the global threshold conditions, fill in
    the rest of the fields,
    including **Count**,**Severity**, **Vulnerability Type**,
    and **Vulnerability Statuses** similarly to the global threshold
    conditions described above.

![](https://wiki.jenkins.io/download/attachments/99058675/Jenkins_threshold_condition.png?version=4&modificationDate=1539121408000&api=v2){.confluence-embedded-image
height="250"}

### Threshold conditions in a Pipeline step

When you add a Pipeline step with the name `contrastVerification`, it
follows the same principles as the post-build action but in a newer
format for Jenkins 2.0 improvements.

Pipeline configuration:

    contrastVerification applicationId: '1e6ad9c6-89d4-4f06-bdf6-92c569ec89de', count: 1, profile: 'new-profile', queryBy: 3, rule: 'cache-controls-missing', severity: 'High'

# Test for Vulnerabilities

For the Jenkins plugin to get accurate information, you must add a
unique identifier built from the Jenkins CI configuration as an agent
property. The corresponding property for the Java agent is
`contrast.override.appversion`. For example, when starting Contrast
agent add the following property:
"-Dcontrast.appname=${applicationName}".

The plugin can use either the unique identifier `appVersionTag` or the
`startDate` to filter vulnerabilities and check conditions. You can
change the format used by the plugin to create `appVersionTag` or set
the plugin to use `startDate` using `queryBy` pipeline parameter. Three
options are available:  

-   appVersionTag, format: `applicationId-${BUILD_NUMBER}` (default)
-   appVersionTag, format: `applicationId-${JOB_NAME}-${BUILD_NUMBER}`
-   `startDate` (Build timestamp)

Both `JOB_NAME` and `BUILD_NUMBER` are available as Jenkins environment
[properties](https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project){.external-link}.
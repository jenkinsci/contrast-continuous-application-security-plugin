# Contrast Jenkins Plugin

Repository for the Contrast Maven plugin. This plugin adds the ability to configure a connection toa Jenkins Build.

## Variables

* `username`     : TeamServer user's username
* `apiKey`       : TeamServer user's apiKey; found in Organization settings
* `serviceKey`   : TeamServer user's serviceKey; found in Organization settings
* `orgUuid`      : TeamServer user's orgUUid; found in Organization settings
* `teamServerUrl`: TeamServer URL; example is 'http://app.contrastsecurity.com/Contrast/api'
* `applicationId`: ID of application in TeamServer

## Workflow

There are currently 2 build items added by this plugin:

### Test TeamServer Connection

This will verify the Jenkins can connect to TeamServer with the configured variables.

It will fail the build if the plugin is unable to connect.

This test is most useful as a pre-build step.

### Verify Vulnerability Threshold 

This will check TeamServer for the number of vulnerabilities in the application.

The only required variable is `Threshold Count` which must be a positive integer.

The other two variables `Threshold Severity` and `Threshold Category` are not required but can be useful if you want to filter the threshold count.

**NOTE:** The variables in the Jenkins job properties are required to run this build action.

This action is a post-build action and will fail the Jenkins build even if your build ran successfully.

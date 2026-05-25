package com.aspectsecurity.contrast.contrastjenkins;

import com.contrastsecurity.sdk.ContrastSDK;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.acegisecurity.AccessDeniedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

// Secret.class is included in @PrepareForTest so we can mock Secret.fromString,
// which otherwise requires a running Jenkins ConfidentialStore to decrypt values.
@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, VulnerabilityTrendHelper.class, Secret.class})
public class ContrastPluginConfigSecurityTest {

    @Mock
    private Jenkins jenkins;

    @Mock
    private Secret mockSecret;

    @Mock
    private Item item;

    private ContrastPluginConfig.ContrastPluginConfigDescriptor descriptor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.mockStatic(VulnerabilityTrendHelper.class);
        PowerMockito.mockStatic(Secret.class);

        when(Jenkins.getActiveInstance()).thenReturn(jenkins);

        // Return a mock Secret from fromString to avoid ConfidentialStore dependency.
        // getPlainText() must return non-empty so the empty-value guards in the method pass.
        when(Secret.fromString(anyString())).thenReturn(mockSecret);
        when(mockSecret.getPlainText()).thenReturn("test-value");

        descriptor = new ContrastPluginConfigStub.ContrastPluginConfigDescriptorStub();
    }

    @Test
    public void testDoTestConnectionRejectsMalformedUrl() throws Exception {
        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "not-a-valid-url");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue(result.getMessage().contains("not a valid URL"));
    }

    @Test
    public void testDoTestConnectionRejectsNonHttpProtocol() throws Exception {
        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "file:///etc/passwd/Contrast/api");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue(result.getMessage().contains("http or https"));
    }

    @Test
    public void testDoTestConnectionRejectsUrlWithoutContrastApiPath() throws Exception {
        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "https://example.com/wrong/path");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue(result.getMessage().contains("/Contrast/api"));
    }

    @Test
    public void testDoTestConnectionAcceptsValidHttpsUrl() throws Exception {
        // URL validation passes; SDK call fails with IOException (no real server).
        // The error from the connection attempt proves URL validation did not block the request.
        ContrastSDK mockSDK = mock(ContrastSDK.class);
        given(VulnerabilityTrendHelper.createSDK(anyString(), anyString(), anyString(), anyString()))
                .willReturn(mockSDK);
        given(mockSDK.getProfileDefaultOrganizations()).willThrow(new IOException("Connection refused"));

        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "https://contrast.example.com/Contrast/api");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue(result.getMessage().contains("Unable to connect to Contrast"));
    }

    @Test
    public void testDoTestConnectionAcceptsValidHttpUrl() throws Exception {
        ContrastSDK mockSDK = mock(ContrastSDK.class);
        given(VulnerabilityTrendHelper.createSDK(anyString(), anyString(), anyString(), anyString()))
                .willReturn(mockSDK);
        given(mockSDK.getProfileDefaultOrganizations()).willThrow(new IOException("Connection refused"));

        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "http://contrast.internal.company.com/Contrast/api");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue(result.getMessage().contains("Unable to connect to Contrast"));
    }

    @Test(expected = AccessDeniedException.class)
    public void testDoTestConnectionDeniedForNonAdmin() throws Exception {
        doThrow(new AccessDeniedException("Access denied"))
                .when(jenkins).checkPermission(Jenkins.ADMINISTER);

        descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "https://contrast.example.com/Contrast/api");
    }

    @Test
    public void doFillTeamServerProfileNameItemsReturnsEmptyWhenNoItemAndNotAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        ListBoxModel result = descriptor.doFillTeamServerProfileNameItems(null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillTeamServerProfileNameItemsReturnsProfilesWhenNoItemAndAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        given(VulnerabilityTrendHelper.getProfileNames()).willReturn(expected);

        ListBoxModel result = descriptor.doFillTeamServerProfileNameItems(null);

        assertEquals(expected, result);
    }

    @Test
    public void doFillTeamServerProfileNameItemsReturnsEmptyWhenItemPresentButNoConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        ListBoxModel result = descriptor.doFillTeamServerProfileNameItems(item);

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillTeamServerProfileNameItemsReturnsProfilesWhenItemPresentAndHasConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        given(VulnerabilityTrendHelper.getProfileNames()).willReturn(expected);

        ListBoxModel result = descriptor.doFillTeamServerProfileNameItems(item);

        assertEquals(expected, result);
    }

    @Test
    public void doFillThresholdVulnTypeItemsReturnsEmptyWhenNoItemAndNotAdmin() throws IOException {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(null, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillThresholdVulnTypeItemsReturnsTypesWhenNoItemAndAdmin() throws IOException {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        given(VulnerabilityTrendHelper.getVulnerabilityTypes("profile")).willReturn(expected);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(null, "profile");

        assertEquals(expected, result);
    }

    @Test
    public void doFillThresholdVulnTypeItemsReturnsEmptyWhenItemPresentButNoConfigurePermission() throws IOException {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(item, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillThresholdVulnTypeItemsReturnsTypesWhenItemPresentAndHasConfigurePermission() throws IOException {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        given(VulnerabilityTrendHelper.getVulnerabilityTypes("profile")).willReturn(expected);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(item, "profile");

        assertEquals(expected, result);
    }

    @Test
    public void testDoTestConnectionRejectsUrlWithBlankHost() throws Exception {
        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "https:///Contrast/api");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue(result.getMessage().contains("not a valid URL"));
    }

    @Test
    public void testDoTestConnectionPathErrorUsesUppercaseURL() throws Exception {
        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "https://example.com/wrong/path");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue("expected message to use 'URL' not 'Url': " + result.getMessage(),
                result.getMessage().contains("URL does not end with"));
    }
}

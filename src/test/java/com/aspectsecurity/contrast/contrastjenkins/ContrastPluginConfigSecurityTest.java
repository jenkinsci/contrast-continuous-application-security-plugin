package com.aspectsecurity.contrast.contrastjenkins;

import com.contrastsecurity.sdk.ContrastSDK;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Secret.class is mocked statically to avoid ConfidentialStore dependency at test time.
@RunWith(MockitoJUnitRunner.Silent.class)
public class ContrastPluginConfigSecurityTest {

    @Mock
    private Jenkins jenkins;

    @Mock
    private Secret mockSecret;

    @Mock
    private Item item;

    private ContrastPluginConfig.ContrastPluginConfigDescriptor descriptor;

    private MockedStatic<Jenkins> mockedJenkins;
    private MockedStatic<VulnerabilityTrendHelper> mockedHelper;
    private MockedStatic<Secret> mockedSecret;

    @Before
    public void setUp() {
        mockedJenkins = Mockito.mockStatic(Jenkins.class);
        mockedHelper = Mockito.mockStatic(VulnerabilityTrendHelper.class);
        mockedSecret = Mockito.mockStatic(Secret.class);

        mockedJenkins.when(Jenkins::getActiveInstance).thenReturn(jenkins);

        // Return a mock Secret from fromString to avoid ConfidentialStore dependency.
        // getPlainText() must return non-empty so the empty-value guards in the method pass.
        mockedSecret.when(() -> Secret.fromString(anyString())).thenReturn(mockSecret);
        when(mockSecret.getPlainText()).thenReturn("test-value");

        descriptor = new ContrastPluginConfigStub.ContrastPluginConfigDescriptorStub();
    }

    @After
    public void tearDown() {
        mockedJenkins.close();
        mockedHelper.close();
        mockedSecret.close();
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
        ContrastSDK mockSDK = mock(ContrastSDK.class);
        mockedHelper.when(() -> VulnerabilityTrendHelper.createSDK(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockSDK);
        when(mockSDK.getProfileDefaultOrganizations()).thenThrow(new IOException("Connection refused"));

        FormValidation result = descriptor.doTestTeamServerConnection(
                "user", Secret.fromString("apikey"), Secret.fromString("svckey"),
                "https://contrast.example.com/Contrast/api");

        assertEquals(FormValidation.Kind.ERROR, result.kind);
        assertTrue(result.getMessage().contains("Unable to connect to Contrast"));
    }

    @Test
    public void testDoTestConnectionAcceptsValidHttpUrl() throws Exception {
        ContrastSDK mockSDK = mock(ContrastSDK.class);
        mockedHelper.when(() -> VulnerabilityTrendHelper.createSDK(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockSDK);
        when(mockSDK.getProfileDefaultOrganizations()).thenThrow(new IOException("Connection refused"));

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
        mockedHelper.when(VulnerabilityTrendHelper::getProfileNames).thenReturn(expected);

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
        mockedHelper.when(VulnerabilityTrendHelper::getProfileNames).thenReturn(expected);

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
        mockedHelper.when(() -> VulnerabilityTrendHelper.getVulnerabilityTypes("profile")).thenReturn(expected);

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
        mockedHelper.when(() -> VulnerabilityTrendHelper.getVulnerabilityTypes("profile")).thenReturn(expected);

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
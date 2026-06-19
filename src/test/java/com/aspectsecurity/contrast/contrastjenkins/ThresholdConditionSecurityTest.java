package com.aspectsecurity.contrast.contrastjenkins;

import hudson.model.Item;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Calendar;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThresholdConditionSecurityTest {

    @Mock
    private Jenkins jenkins;

    @Mock
    private Item item;

    private ThresholdCondition.DescriptorImpl descriptor;

    private MockedStatic<Jenkins> mockedJenkins;
    private MockedStatic<VulnerabilityTrendHelper> mockedHelper;

    @BeforeEach
    void setUp() {
        mockedJenkins = Mockito.mockStatic(Jenkins.class);
        mockedHelper = Mockito.mockStatic(VulnerabilityTrendHelper.class);

        mockedJenkins.when(Jenkins::getActiveInstance).thenReturn(jenkins);
        mockedJenkins.when(Jenkins::getInstance).thenReturn(jenkins);

        descriptor = new ThresholdConditionStub.ThresholdConditionDescriptorStub();
    }

    @AfterEach
    void tearDown() {
        mockedJenkins.close();
        mockedHelper.close();
    }

    // -------- doCheckApplicationId --------

    @Test
    void doCheckApplicationIdSkipsLookupWhenNoItemAndNotAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(null, "profile", "app");

        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    void doCheckApplicationIdSkipsLookupWhenItemPresentButNoConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(item, "profile", "app");

        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    void doCheckApplicationIdRunsLookupWhenItemHasConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        mockedHelper.when(() -> VulnerabilityTrendHelper.appExistsInProfile("profile", "")).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(item, "profile", "");

        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    void doCheckApplicationIdReturnsAppNotFoundWarningWhenAuthorized() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        mockedHelper.when(() -> VulnerabilityTrendHelper.appExistsInProfile("profile", "nope")).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(item, "profile", "nope");

        assertEquals(FormValidation.Kind.WARNING, result.kind);
        assertTrue(result.getMessage().contains("Application not found"));
    }

    // -------- doFillApplicationIdItems --------

    @Test
    void doFillApplicationIdItemsReturnsEmptyWhenNoItemAndAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(true);
        descriptor.lastAppsRefresh = Calendar.getInstance();
        ComboBoxModel expected = new ComboBoxModel();
        mockedHelper.when(() -> VulnerabilityTrendHelper.getApplicationIdsComboBoxModel("profile")).thenReturn(expected);

        ComboBoxModel result = descriptor.doFillApplicationIdItems(null, "profile");

        assertEquals(expected, result);
    }

    @Test
    void doFillApplicationIdItemsReturnsEmptyGracefullyWhenProfileNotFound() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        mockedHelper.when(() -> VulnerabilityTrendHelper.getProfile("unknown", null)).thenReturn(null);
        mockedHelper.when(() -> VulnerabilityTrendHelper.getApplicationIdsComboBoxModel("unknown")).thenReturn(new ComboBoxModel());

        ComboBoxModel result = descriptor.doFillApplicationIdItems(item, "unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void doFillApplicationIdItemsReturnsEmptyWhenNoItemAndNotAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        ComboBoxModel result = descriptor.doFillApplicationIdItems(null, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    void doFillApplicationIdItemsReturnsEmptyWhenItemPresentButNoConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        ComboBoxModel result = descriptor.doFillApplicationIdItems(item, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    void doFillApplicationIdItemsReturnsAppsWhenItemHasConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        descriptor.lastAppsRefresh = Calendar.getInstance(); // skip refreshApps()
        ComboBoxModel expected = new ComboBoxModel();
        mockedHelper.when(() -> VulnerabilityTrendHelper.getApplicationIdsComboBoxModel("profile")).thenReturn(expected);

        ComboBoxModel result = descriptor.doFillApplicationIdItems(item, "profile");

        assertEquals(expected, result);
    }

    // -------- doFillThresholdVulnTypeItems --------

    @Test
    void doFillThresholdVulnTypeItemsReturnsEmptyWhenNoItemAndNotAdmin() throws IOException {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(null, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    void doFillThresholdVulnTypeItemsReturnsEmptyWhenItemPresentButNoConfigurePermission() throws IOException {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(item, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    void doFillThresholdVulnTypeItemsReturnsTypesWhenItemHasConfigurePermission() throws IOException {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        mockedHelper.when(() -> VulnerabilityTrendHelper.getVulnerabilityTypes("profile")).thenReturn(expected);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(item, "profile");

        assertEquals(expected, result);
    }
}
package com.aspectsecurity.contrast.contrastjenkins;

import hudson.model.Item;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ThresholdConditionSecurityTest {

    @Mock
    private Jenkins jenkins;

    @Mock
    private Item item;

    private ThresholdCondition.DescriptorImpl descriptor;

    private MockedStatic<Jenkins> mockedJenkins;
    private MockedStatic<VulnerabilityTrendHelper> mockedHelper;

    @Before
    public void setUp() {
        mockedJenkins = Mockito.mockStatic(Jenkins.class);
        mockedHelper = Mockito.mockStatic(VulnerabilityTrendHelper.class);

        mockedJenkins.when(Jenkins::getActiveInstance).thenReturn(jenkins);
        mockedJenkins.when(Jenkins::getInstance).thenReturn(jenkins);

        descriptor = new ThresholdConditionStub.ThresholdConditionDescriptorStub();
    }

    @After
    public void tearDown() {
        mockedJenkins.close();
        mockedHelper.close();
    }

    // -------- doCheckApplicationId --------

    @Test
    public void doCheckApplicationIdSkipsLookupWhenNoItemAndNotAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(null, "profile", "app");

        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    public void doCheckApplicationIdSkipsLookupWhenItemPresentButNoConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(item, "profile", "app");

        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    public void doCheckApplicationIdRunsLookupWhenItemHasConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        mockedHelper.when(() -> VulnerabilityTrendHelper.appExistsInProfile("profile", "")).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(item, "profile", "");

        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    public void doCheckApplicationIdReturnsAppNotFoundWarningWhenAuthorized() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        mockedHelper.when(() -> VulnerabilityTrendHelper.appExistsInProfile("profile", "nope")).thenReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(item, "profile", "nope");

        assertEquals(FormValidation.Kind.WARNING, result.kind);
        assertTrue(result.getMessage().contains("Application not found"));
    }

    // -------- doFillApplicationIdItems --------

    @Test
    public void doFillApplicationIdItemsReturnsEmptyWhenNoItemAndNotAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        ComboBoxModel result = descriptor.doFillApplicationIdItems(null, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillApplicationIdItemsReturnsEmptyWhenItemPresentButNoConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        ComboBoxModel result = descriptor.doFillApplicationIdItems(item, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillApplicationIdItemsReturnsAppsWhenItemHasConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        descriptor.lastAppsRefresh = Calendar.getInstance(); // skip refreshApps()
        ComboBoxModel expected = new ComboBoxModel();
        mockedHelper.when(() -> VulnerabilityTrendHelper.getApplicationIdsComboBoxModel("profile")).thenReturn(expected);

        ComboBoxModel result = descriptor.doFillApplicationIdItems(item, "profile");

        assertEquals(expected, result);
    }

    // -------- doFillThresholdVulnTypeItems --------

    @Test
    public void doFillThresholdVulnTypeItemsReturnsEmptyWhenNoItemAndNotAdmin() throws IOException {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(null, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillThresholdVulnTypeItemsReturnsEmptyWhenItemPresentButNoConfigurePermission() throws IOException {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(item, "profile");

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillThresholdVulnTypeItemsReturnsTypesWhenItemHasConfigurePermission() throws IOException {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        mockedHelper.when(() -> VulnerabilityTrendHelper.getVulnerabilityTypes("profile")).thenReturn(expected);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(item, "profile");

        assertEquals(expected, result);
    }
}
package com.aspectsecurity.contrast.contrastjenkins;

import hudson.model.Item;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, VulnerabilityTrendHelper.class})
public class ThresholdConditionSecurityTest {

    @Mock
    private Jenkins jenkins;

    @Mock
    private Item item;

    private ThresholdCondition.DescriptorImpl descriptor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.mockStatic(VulnerabilityTrendHelper.class);

        when(Jenkins.getActiveInstance()).thenReturn(jenkins);
        when(Jenkins.getInstance()).thenReturn(jenkins);

        descriptor = new ThresholdConditionStub.ThresholdConditionDescriptorStub();
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
        given(VulnerabilityTrendHelper.appExistsInProfile("profile", "")).willReturn(false);

        FormValidation result = descriptor.doCheckApplicationId(item, "profile", "");

        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    public void doCheckApplicationIdReturnsAppNotFoundWarningWhenAuthorized() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        given(VulnerabilityTrendHelper.appExistsInProfile("profile", "nope")).willReturn(false);

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
        given(VulnerabilityTrendHelper.getApplicationIdsComboBoxModel("profile")).willReturn(expected);

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
        given(VulnerabilityTrendHelper.getVulnerabilityTypes("profile")).willReturn(expected);

        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems(item, "profile");

        assertEquals(expected, result);
    }
}

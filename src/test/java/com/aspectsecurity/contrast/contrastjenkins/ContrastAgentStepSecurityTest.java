package com.aspectsecurity.contrast.contrastjenkins;

import hudson.model.Item;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, VulnerabilityTrendHelper.class})
public class ContrastAgentStepSecurityTest {

    @Mock
    private Jenkins jenkins;

    @Mock
    private Item item;

    private ContrastAgentStep.ContrastAgentStepDescriptorImpl descriptor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.mockStatic(VulnerabilityTrendHelper.class);

        when(Jenkins.getActiveInstance()).thenReturn(jenkins);

        descriptor = new ContrastAgentStep.ContrastAgentStepDescriptorImpl();
    }

    @Test
    public void doFillProfileItemsReturnsEmptyWhenNoItemAndNotAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(false);

        ListBoxModel result = descriptor.doFillProfileItems(null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillProfileItemsReturnsProfilesWhenNoItemAndAdmin() {
        when(jenkins.hasPermission(Jenkins.ADMINISTER)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        given(VulnerabilityTrendHelper.getProfileNames()).willReturn(expected);

        ListBoxModel result = descriptor.doFillProfileItems(null);

        assertEquals(expected, result);
    }

    @Test
    public void doFillProfileItemsReturnsEmptyWhenItemPresentButNoConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(false);

        ListBoxModel result = descriptor.doFillProfileItems(item);

        assertTrue(result.isEmpty());
    }

    @Test
    public void doFillProfileItemsReturnsProfilesWhenItemPresentAndHasConfigurePermission() {
        when(item.hasPermission(Item.CONFIGURE)).thenReturn(true);
        ListBoxModel expected = new ListBoxModel();
        given(VulnerabilityTrendHelper.getProfileNames()).willReturn(expected);

        ListBoxModel result = descriptor.doFillProfileItems(item);

        assertEquals(expected, result);
    }
}
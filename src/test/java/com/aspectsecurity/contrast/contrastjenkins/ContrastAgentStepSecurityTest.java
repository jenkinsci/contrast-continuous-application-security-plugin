package com.aspectsecurity.contrast.contrastjenkins;

import hudson.model.Item;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ContrastAgentStepSecurityTest {

    @Mock
    private Jenkins jenkins;

    @Mock
    private Item item;

    private ContrastAgentStep.ContrastAgentStepDescriptorImpl descriptor;

    private MockedStatic<Jenkins> mockedJenkins;
    private MockedStatic<VulnerabilityTrendHelper> mockedHelper;

    @Before
    public void setUp() {
        mockedJenkins = Mockito.mockStatic(Jenkins.class);
        mockedHelper = Mockito.mockStatic(VulnerabilityTrendHelper.class);

        mockedJenkins.when(Jenkins::getActiveInstance).thenReturn(jenkins);

        descriptor = new ContrastAgentStep.ContrastAgentStepDescriptorImpl();
    }

    @After
    public void tearDown() {
        mockedJenkins.close();
        mockedHelper.close();
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
        mockedHelper.when(VulnerabilityTrendHelper::getProfileNames).thenReturn(expected);

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
        mockedHelper.when(VulnerabilityTrendHelper::getProfileNames).thenReturn(expected);

        ListBoxModel result = descriptor.doFillProfileItems(item);

        assertEquals(expected, result);
    }
}
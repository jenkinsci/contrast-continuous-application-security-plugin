package com.aspectsecurity.contrast.contrastjenkins;

import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ThresholdConditionTest extends TestCase {

    private ThresholdConditionStub.ThresholdConditionDescriptorStub descriptor;

    @Before
    @Override
    public void setUp() {
        descriptor = new ThresholdConditionStub.ThresholdConditionDescriptorStub();
    }

    // @Test
    public void testDoFillThresholdVulnTypeItems() throws IOException {
        ListBoxModel result = descriptor.doFillThresholdVulnTypeItems("Test");

        assertTrue(result.size() > 0);
    }

    @Test
    public void testDoFillThresholdSeverityItems() {
        ListBoxModel result = descriptor.doFillThresholdSeverityItems();
        assertTrue(result.size() > 0);
    }

    @Test
    public void testDoCheckThresholdCountValid() {
        FormValidation result = descriptor.doCheckThresholdCount("10");
        assertEquals(result.kind, FormValidation.Kind.OK);
    }

    @Test
    public void testDoCheckThresholdCountInvalid() {
        FormValidation result = descriptor.doCheckThresholdCount("blah");
        assertEquals(result.kind, FormValidation.Kind.ERROR);
    }
}
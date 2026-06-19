package com.aspectsecurity.contrast.contrastjenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThresholdConditionTest {

    private ThresholdConditionStub.ThresholdConditionDescriptorStub descriptor;

    @BeforeEach
    void setUp() {
        descriptor = new ThresholdConditionStub.ThresholdConditionDescriptorStub();
    }

    @Test
    void testDoFillThresholdSeverityItems() {
        ListBoxModel result = descriptor.doFillThresholdSeverityItems();
        assertFalse(result.isEmpty());
    }

    @Test
    void testDoCheckThresholdCountValid() {
        FormValidation result = descriptor.doCheckThresholdCount("10");
        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    void testDoCheckThresholdCountInvalid() {
        FormValidation result = descriptor.doCheckThresholdCount("blah");
        assertEquals(FormValidation.Kind.ERROR, result.kind);
    }
}
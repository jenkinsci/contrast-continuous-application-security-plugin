package com.aspectsecurity.contrast.contrastjenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hudson.util.FormValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContrastPluginConfigTest {

    private ContrastPluginConfig.ContrastPluginConfigDescriptor descriptor;

    @BeforeEach
    void setUp() {
        descriptor = new ContrastPluginConfigStub.ContrastPluginConfigDescriptorStub();
    }

    @Test
    void testDoCheckUsernameValid() {
        FormValidation result = descriptor.doCheckUsername("contrast_admin");
        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    void testDoCheckUsernameInvalid() {
        FormValidation result = descriptor.doCheckUsername("");
        assertEquals(FormValidation.Kind.ERROR, result.kind);
    }

    @Test
    void testDoCheckApiKeyValid() {
        FormValidation result = descriptor.doCheckApiKey("ABCDEFG");
        assertEquals(FormValidation.Kind.OK, result.kind);
    }

    @Test
    void testDoCheckApiKeyInvalid() {
        FormValidation result = descriptor.doCheckApiKey("");
        assertEquals(FormValidation.Kind.ERROR, result.kind);
    }
}
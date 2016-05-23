package com.aspectsecurity.contrast.contrastjenkins;

import hudson.util.FormValidation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class ContrastPluginConfigTest extends TestCase {

    private ContrastPluginConfig.ContrastPluginConfigDescriptor descriptor;

    @Before
    @Override
    public void setUp() {
        descriptor = new ContrastPluginConfigStub.ContrastPluginConfigDescriptorStub();
    }

    @Test
    public void testDoCheckUsernameValid() {
        FormValidation result = descriptor.doCheckUsername("contrast_admin");
        assertEquals(result.kind, FormValidation.Kind.OK);
    }

    @Test
    public void testDoCheckUsernameInvalid() {
        FormValidation result = descriptor.doCheckUsername("");
        assertEquals(result.kind, FormValidation.Kind.ERROR);
    }

    @Test
    public void testDoCheckApiKeyValid() {
        FormValidation result = descriptor.doCheckApiKey("ABCDEFG");
        assertEquals(result.kind, FormValidation.Kind.OK);
    }

    @Test
    public void testDoCheckApiKeyInvalid() {
        FormValidation result = descriptor.doCheckApiKey("");
        assertEquals(result.kind, FormValidation.Kind.ERROR);
    }
}
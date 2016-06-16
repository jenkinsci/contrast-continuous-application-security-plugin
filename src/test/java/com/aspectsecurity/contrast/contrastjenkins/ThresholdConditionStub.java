package com.aspectsecurity.contrast.contrastjenkins;


public class ThresholdConditionStub extends ThresholdCondition {

    public ThresholdConditionStub() {
        super("test", "test", "test");
    }

    public static class ThresholdConditionDescriptorStub extends ThresholdCondition.DescriptorImpl {

        @Override
        public synchronized void load() {
        }
    }
}
package com.aspectsecurity.contrast.contrastjenkins;


public class ThresholdConditionStub extends ThresholdCondition {

    public ThresholdConditionStub() {
        super(0, "test", "test", 0,null,"test",
                false, false,false, false, false, false,
                false, false, false);
    }

    public static class ThresholdConditionDescriptorStub extends ThresholdCondition.DescriptorImpl {

        @Override
        public synchronized void load() {
        }
    }
}
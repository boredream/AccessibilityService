package com.boredream.accessibilityservice;

import java.util.List;

public class TaskStep {

    public static final String TYPE_CLICK = "click";
    public static final String TYPE_DELAY = "delay";

    private String type;
    private String value;
    private List<Rule> rule;

    public static class Rule {

        public static final String TYPE_CLASSNAME = "ClassName";
        public static final String TYPE_BOUNDS_IN_PARENT = "BoundsInParent";
        public static final String TYPE_POSITION = "Position";

        private String ruleType;
        private String ruleValue;

        public String getRuleType() {
            return ruleType;
        }

        public void setRuleType(String ruleType) {
            this.ruleType = ruleType;
        }

        public String getRuleValue() {
            return ruleValue;
        }

        public void setRuleValue(String ruleValue) {
            this.ruleValue = ruleValue;
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Rule> getRule() {
        return rule;
    }

    public void setRule(List<Rule> rule) {
        this.rule = rule;
    }
}

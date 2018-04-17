package model.sgd;

import core.Condition;
import core.Rule;

public class StochasticGradientDescent {
    private double total;
    private String observedData;
    int pos;
    
    private final double LEARNING_RATE = 0.1;
    
    public StochasticGradientDescent() {
        this.total = 0;
        this.observedData = null;
        pos = 0;
    }
    
    public Rule getUpdatedRule(Rule rule, String observedData) {
        this.observedData = observedData;
        pos = getPreAndActMatchingPosition(rule);
        initializeTotal(rule);
        setGradientForEachPostConditions(rule);
        // For normalization
        double sum = 0;
        for (Condition cond : rule.getPostConditions()) {
            sum += cond.updateValue(LEARNING_RATE);
        }
        for (Condition cond : rule.getPostConditions()) {
            cond.setValue(cond.getValue()/sum); 
        }
        return rule;
    }
    
    private int getPreAndActMatchingPosition(Rule rule) {
        int i = 0;
        for (Condition cond : rule.getPostConditions()) {
            if (this.observedData.equals(cond.getName())) {
                return i;
            }
            i++;
        }
        return -1;
    }
    
    private void initializeTotal(Rule rule) {
        this.total = 0;
        for (Condition cond : rule.getPostConditions()) {
            this.total += cond.getValue();
        }
    }
    
    private void setGradientForEachPostConditions(Rule rule) {
        for (Condition cond : rule.getPostConditions()) {
            cond.setGradient(computeGradient(rule, cond));
        }
    }
    
    private double computeGradient(Rule rule, Condition cond) {
        double pb = getProbability(rule);
        if (observedData.equals(cond.getName())) {
            return getGradInternal(rule, pb);
        } else {
            return getGradExternal(rule, pb);
        }
    }
    
    private double getProbability(Rule rule) {
        return rule.getPostCondition(pos).getValue()/total;
    }
    
    private double getGradInternal(Rule rule, double pb) {
        return -2*(1-pb)*(total-rule.getPostCondition(pos).getValue())/(total*total);
    }
    
    private double getGradExternal(Rule rule, double pb) {
        return 2*(1-pb)*(rule.getPostCondition(pos).getValue())/(total*total);
    }
}

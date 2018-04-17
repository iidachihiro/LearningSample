package model.sgd;

import core.Condition;
import core.Rule;

public class StochasticGradientDescent {
    private double base;
    private int num;
    private String observedData;
    
    private final double LEARNING_RATE = 0.1;
    
    public StochasticGradientDescent() {
        this.base = 0;
        this.num = 0;
        this.observedData = null;
    }
    
    public Rule getUpdatedRule(Rule rule, String observedData) {
        this.num = 0;
        this.observedData = observedData;
        for (Condition cond : rule.getPostConditions()) {
            if (observedData.equals(cond.getName())) {
                break;
            }
            num++;
        }
        compute(rule);
        return rule;
    }
    
    private void compute(Rule rule) {
        initializeBase(rule);
        double sum = 0;
        for (Condition cond : rule.getPostConditions()) {
            cond.setGradient(computeGradient(rule, cond));
            sum += cond.updateValue(LEARNING_RATE);
        }
        for (Condition cond : rule.getPostConditions()) {
            cond.setValue(cond.getValue()/sum);
        }
    }
    
    private void initializeBase(Rule rule) {
        base = 0;
        for (Condition cond : rule.getPostConditions()) {
            base += cond.getValue();
        }
    }
    
    private double computeGradient(Rule rule, Condition cond) {
        double pb = getProbability(rule);
        if (observedData.equals(cond.getName())) {
            return getGradInternal(cond, pb);
        } else {
            return getGradExternal(rule, pb);
        }
    }
    
    private double getProbability(Rule rule) {
        return rule.getPostCondition(num).getValue()/base;
    }
    
    private double getGradInternal(Condition cond, double pb) {
        return -2*(1-pb)*(base-cond.getValue())/(base*base);
    }
    
    private double getGradExternal(Rule rule, double pb) {
        return -2*(1-pb)*(-1*rule.getPostCondition(num).getValue())/(base*base);
    }
}

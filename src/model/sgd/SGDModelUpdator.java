package model.sgd;

import java.util.ArrayList;
import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.ModelUpdator;

public class SGDModelUpdator extends ModelUpdator {
    private final double THRESHOLD = 1.0e-14;
    
    public SGDModelUpdator(List<Rule> rules) {
        super(rules);
    }
    
    public void learn(List<ActionSet> sets) {
        for (ActionSet as : sets) {
            update(as);
        }
        rules = removeVerySmallPostCondition();
    }
    
    public void update(ActionSet as) {
        StochasticGradientDescent sgd = new StochasticGradientDescent();
        for (Rule rule : rules) {
            if (rule.isSameKind(as)) {
                rules.set(rules.indexOf(rule), sgd.getUpdatedRule(rule, as.getPostMonitorableAction()));
                updatePreValue();
            }
        }
    }
    
    private void updatePreValue() {
        for (Rule rule : rules) {
            for (Condition cond : rule.getPostConditions()) {
                cond.setPreValue(cond.getValue());
            }
        }
    }
    
    private List<Rule> removeVerySmallPostCondition() {
        List<Rule> rules_ = new ArrayList<>();
        for (Rule rule : rules) {
            Rule rule_ = new Rule(rule.getPreCondition(), rule.getAction());
            for (Condition cond : rule.getPostConditions()) {
                if (cond.getValue() >= THRESHOLD) {
                    rule_.addNewPostCondition(cond);
                }
            }
            rules_.add(rule_);
        }
        return rules_;
    }
}

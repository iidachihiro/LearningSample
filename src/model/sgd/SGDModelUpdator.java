package model.sgd;

import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.ModelUpdator;

public class SGDModelUpdator extends ModelUpdator {
    public SGDModelUpdator(List<Rule> rules) {
        super(rules);
    }
    
    public void learn(ActionSet as) {
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
}

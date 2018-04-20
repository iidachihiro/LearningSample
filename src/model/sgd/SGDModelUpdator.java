package model.sgd;

import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.DomainModelGenerator;
import model.ModelUpdator;
import util.Utils;

public class SGDModelUpdator extends ModelUpdator {
    private final double THRESHOLD = 0.1;
    
    public SGDModelUpdator(List<Rule> rules) {
        super(rules);
    }
    
    public void learn(List<ActionSet> sets) {
        for (ActionSet as : sets) {
            if (update(as)) {
                Utils.outputResult(this.rules, THRESHOLD);
                DomainModelGenerator generator = new DomainModelGenerator();
                generator.generate(this.rules, THRESHOLD);
            }
        }
    }
    
    public boolean update(ActionSet as) {
        boolean flag = false;
        StochasticGradientDescent sgd = new StochasticGradientDescent();
        for (Rule rule : rules) {
            if (rule.isSameKind(as)) {
                Rule updatedRule = sgd.getUpdatedRule(rule, as.getPostMonitorableAction());
                rules.set(rules.indexOf(rule), updatedRule);
                if (isAffectedByThreshold(rule)) {
                    flag = true;
                }
                updatePreValue();
            }
        }
        return flag;
    }
    
    private void updatePreValue() {
        for (Rule rule : rules) {
            for (Condition cond : rule.getPostConditions()) {
                cond.setPreValue(cond.getValue());
            }
        }
    }
    
    private boolean isAffectedByThreshold(Rule rule) {
        for (Condition cond : rule.getPostConditions()) {
            if ((cond.getPreValue()-THRESHOLD)*(cond.getValue()-THRESHOLD) < 0)  {
                return true;
            }
        }
        return false;
    }
}

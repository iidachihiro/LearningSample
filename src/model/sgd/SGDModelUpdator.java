package model.sgd;

import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.DomainModelGenerator;
import model.ModelUpdator;
import util.PUtils;
import util.Utils;

public class SGDModelUpdator extends ModelUpdator {
    private double THRESHOLD;
    
    public SGDModelUpdator(List<Rule> rules) {
        super(rules);
        this.THRESHOLD = Utils.readThreshold();
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
    
    public void Plearn(List<ActionSet> sets) {
        for (ActionSet as : sets) {
            int updatedId = Pupdate(as);
            if (updatedId > 0) {
                PUtils.outputResult(this.rules, THRESHOLD);
                DomainModelGenerator generator = new DomainModelGenerator();
                generator.Pgenerate(this.rules, THRESHOLD, updatedId);
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
    
    public int Pupdate(ActionSet as) {
        int id = -1;
        StochasticGradientDescent sgd = new StochasticGradientDescent();
        for (Rule rule : rules) {
            if (rule.isSameKind(as)) {
                Rule updatedRule = sgd.getUpdatedRule(rule, as.getPostMonitorableAction());
                rules.set(rules.indexOf(rule), updatedRule);
                if (isAffectedByThreshold(rule)) {
                    id = PUtils.getRobotId(rule);
                }
                updatePreValue();
            }
        }
        return id;
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

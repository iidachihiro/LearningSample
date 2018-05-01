package core;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private Condition preCondition;
    private String action;
    private List<Condition> postConditions;
    
    public Rule(Condition pre, String act) {
        this.preCondition = pre;
        this.action = act;
        this.postConditions = new ArrayList<>();
    }
    
    public Rule(ActionSet as) {
        this.preCondition = new Condition(as.getPreMonitorableAction());
        this.action = as.getControllableAction();
        this.postConditions = new ArrayList<Condition>();
        postConditions.add(new Condition(as.getPostMonitorableAction()));
    }
    
    public Rule(Rule rule) {
        this.preCondition = rule.getPreCondition();
        this.action = rule.getAction();
        this.postConditions = rule.getPostConditions();
    }
    
    public Condition getPreCondition() {
        return this.preCondition;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public List<Condition> getPostConditions() {
        return this.postConditions;
    }
    
    public Condition getPostCondition(int i) {
        return this.postConditions.get(i);
    }
    
    public void addNewPostCondition(Condition postCondition) {
        this.postConditions.add(postCondition);
    }
    
    public boolean isSameKind(ActionSet as) {
        return this.preCondition.getName().equals(as.getPreMonitorableAction()) 
                && this.action.equals(as.getControllableAction());
    }
    
    public void removePostCondition(Condition cond_) {
        for (Condition cond : this.postConditions) {
            if (cond.equals(cond_)) {
                this.postConditions.remove(cond);
                break;
            }
        }
    }
}

package core;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private int count;
    private Condition preCondition;
    private String action;
    private List<Condition> postConditions;
    
    public Rule(ActionSet as) {
        this.preCondition = new Condition(as.getPreMonitorableAction());
        this.action = as.getControllableAction();
        this.postConditions = new ArrayList<Condition>();
        postConditions.add(new Condition(as.getPostMonitorableAction()));
        count = 0;
    }
    
    public int getCount() {
        return this.count;
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
}

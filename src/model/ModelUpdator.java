package model;

import java.util.ArrayList;
import java.util.List;

import core.ActionSet;
import core.Rule;

public abstract class ModelUpdator {
    protected List<Rule> rules;
    
    public ModelUpdator(List<Rule> rules) { 
        this.rules = new ArrayList<Rule>(rules);
    }
    
    public List<Rule> getRules() {
        return this.rules;
    }
    
    public abstract void learn(ActionSet as);
}

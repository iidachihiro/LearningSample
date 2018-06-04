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
    
    public abstract boolean update(ActionSet as);
    
    public abstract void learn(List<ActionSet> sets);
    
    public abstract void Plearn(List<ActionSet> sets);
    
    // for research/probability
    public abstract List<List<Rule>> getTmp();
}

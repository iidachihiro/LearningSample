package main;

import java.util.List;

import core.ActionSet;
import core.Rule;
import model.ModelUpdator;
import model.sgd.SGDModelUpdator;
import util.PUtils;

public class PSampleMain {
    private static final int PNUM = 10;
    
    public static void main(String[] args) {
        PUtils.generateNBaseRules(PNUM);
        PUtils.generateParallelTraces(PNUM);
        List<Rule> rules = PUtils.readNBaseRules(PNUM);
        List<ActionSet> sets = PUtils.readParallelTraces(PNUM);
        // for sgd
        PUtils.reflesh();
        System.out.println("learning starts");
        ModelUpdator SGDUpdator = new SGDModelUpdator(rules);
        SGDUpdator.Plearn(sets);
        System.out.println("learning finished.");
    }
}

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
        List<List<Rule>> allRules = PUtils.readNBaseRules(PNUM);
        List<List<ActionSet>> allSets = PUtils.readParallelTraces(PNUM);
        // for sgd
        for (int i = 0; i < PNUM; i++) {
            ModelUpdator SGDupdator = new SGDModelUpdator(allRules.get(i));
            SGDupdator.learn(allSets.get(i), i);
            System.out.println("learning finished");
        }
    }
}

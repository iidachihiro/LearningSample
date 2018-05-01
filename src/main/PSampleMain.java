package main;

import java.util.List;

import core.ActionSet;
import core.Rule;
import util.PUtils;

public class PSampleMain {
    private static final int PNUM = 10;
    
    public static void main(String[] args) {
        PUtils.generateNBaseRules(PNUM);
        PUtils.generateParallelTraces(PNUM);
        List<List<Rule>> allRules = PUtils.readNBaseRules(PNUM);
        List<List<ActionSet>> allSets = PUtils.readParallelTraces(PNUM);
        
    }
}

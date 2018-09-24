package main;

import java.util.List;

import core.ActionSet;
import core.Rule;
import model.ModelUpdator;
import model.sgd.SGDModelUpdator;
import util.Utils;

public class SampleMain {
    public static void main(String[] args) {
        boolean OUTPUT_PROBABILITY_TABLE = false;
        if (args.length >= 1) {
            if (isIn("o", args) >= 0) {
                OUTPUT_PROBABILITY_TABLE = true;
            }
        }
        new Utils();
        Utils.reflesh();
        List<Rule> rules = Utils.readBaseRules();
        List<ActionSet> sets = Utils.readTraces();
        // for sgd
        ModelUpdator SGDUpdator = new SGDModelUpdator(rules);
        SGDUpdator.learn(sets, OUTPUT_PROBABILITY_TABLE);
        System.out.println("learning finished.");
    }
    
    public static int isIn(String target, String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}

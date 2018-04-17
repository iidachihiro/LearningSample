package main;

import java.util.List;

import core.ActionSet;
import core.Rule;
import model.sgd.SGDModelUpdator;
import util.Utils;

public class Main {
    public static void main(String[] args) {
        List<Rule> rules = Utils.readBaseRules();
        List<ActionSet> sets = Utils.readTraces();
        SGDModelUpdator updator = new SGDModelUpdator(rules);
        for (ActionSet set : sets) {
            updator.learn(set);
        }
        Utils.outputResult(updator.getRules());
        System.out.println("learning finished.");
    }
}

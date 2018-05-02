package main;

import java.util.List;

import core.ActionSet;
import core.Rule;
import model.ModelUpdator;
import model.sgd.SGDModelUpdator;
import util.Utils;

public class ESampleMain {
    public static void main(String[] args) {
        List<Rule> rules = Utils.readBaseActions();
        List<ActionSet> sets = Utils.readTraces();
        // for sgd
        ModelUpdator SGDUpdator = new SGDModelUpdator(rules);
        SGDUpdator.learn(sets);
        System.out.println("learning finished.");
    }
}

package main;

import java.util.List;

import core.ActionSet;
import core.Rule;
import model.DomainModelGenerator;
import model.ModelUpdator;
import model.sgd.SGDModelUpdator;
import util.Utils;

public class Main {
    public static void main(String[] args) {
        List<Rule> rules = Utils.readBaseRules();
        List<ActionSet> sets = Utils.readTraces();
        // for sgd
        ModelUpdator SGDUpdator = new SGDModelUpdator(rules);
        SGDUpdator.learn(sets);
        Utils.outputResult(SGDUpdator.getRules());
        System.out.println("learning finished.");
        DomainModelGenerator generator = new DomainModelGenerator();
        generator.generate(SGDUpdator.getRules());
    }
}

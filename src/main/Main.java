package main;

import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.DomainModelGenerator;
import model.ModelUpdator;
import model.sgd.SGDModelUpdator;
import util.Utils;

public class Main {
    public static void main(String[] args) {
        List<Rule> rules = Utils.readBaseRules();
        List<ActionSet> sets = Utils.readTraces();
        printRules(rules);
        // for sgd
        ModelUpdator SGDUpdator = new SGDModelUpdator(rules);
        SGDUpdator.learn(sets);
        Utils.outputResult(SGDUpdator.getRules());
        System.out.println("learning finished.");
        printRules(SGDUpdator.getRules());
        DomainModelGenerator generator = new DomainModelGenerator();
        generator.generate(SGDUpdator.getRules());
    }
    
    static void printRules(List<Rule> rules) {
        String tab = "  ";
        int i = 0;
        System.out.println("----------");
        for (Rule rule : rules) {
            System.out.println("Rule "+i);
            System.out.println(tab+"preCondition: "+rule.getPreCondition().getName());
            System.out.println(tab+"action: "+rule.getAction());
            System.out.println(tab+"postConditions: ");
            for (Condition cond : rule.getPostConditions()) {
                System.out.println(tab+tab+cond.getName()+": "+cond.getValue());
            }
            i++;
        }
    }
}

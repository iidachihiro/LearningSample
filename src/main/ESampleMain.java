package main;

import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.ModelUpdator;
import model.sgd.SGDModelUpdator;
import util.Utils;

public class ESampleMain {
    public static void main(String[] args) {
        boolean OUTPUT_PROBABILITY_TABLE = false;
        if (args.length >= 1) {
            if (isIn("o", args) >= 0) {
                OUTPUT_PROBABILITY_TABLE = true;
            }
        }
        new Utils();
        List<Rule> rules = Utils.readBaseActions();
        List<ActionSet> sets = Utils.readTraces();
        
        // for cutting useless post condition
        System.out.println("cutting rules starts.");
        for (Rule rule : rules) {
            String pre = rule.getPreCondition().getName();
            String act = rule.getAction();
            boolean[] list = new boolean[rule.getPostConditions().size()];
            int index = 0;
            for (Condition postCond : rule.getPostConditions()) {
                String post = postCond.getName();
                boolean flag = false;
                for (ActionSet as : sets) {
                    if (as.getPreMonitorableAction().equals(pre) 
                            && as.getControllableAction().equals(act)
                            && as.getPostMonitorableAction().equals(post)) {
                        flag = true;
                        break;
                    }
                }
                list[index++] = flag;
            }
            for (int i = index-1; i >= 0; i--) {
                if (!list[i]) {
                    rule.removePostCondition(i);
                }
            }
        }
        int sum = 0;
        for (Rule rule : rules) {
            sum += rule.getPostConditions().size();
        }
        System.out.println("cutting rules finished. The sum of post conditions of all rules are "+sum+".");
        
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

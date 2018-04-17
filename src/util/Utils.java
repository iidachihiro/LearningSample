package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;

public class Utils {
    private static String originalPath = "/Users/iidachihiro/workspace/LearningSample/";
    private static String baseRulesPath = originalPath+"resources/BaseRules.txt";
    private static String tracesPath = originalPath+"resources/Traces.txt";
    private static String resultPath = originalPath+"Result.csv";
    
    private final static String tab = "  ";
    
    public static List<Rule> readBaseRules() {
        List<Rule> rules = new ArrayList<>();
        try {
            File file = new File(baseRulesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                ActionSet as = new ActionSet(line.split(",", 0));
                boolean isNewRule = true;
                for (Rule rule : rules) {
                    if (rule.isSameKind(as)) {
                        rule.addNewPostCondition(new Condition(as.getPostMonitorableAction()));
                        isNewRule = false;
                        break;
                    }
                }
                if (isNewRule) {
                    rules.add(new Rule(as));
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return rules;
    }
    
    public static void printBaseRules() {
        List<Rule> rules = readBaseRules();
        int i = 0;
        for (Rule rule : rules) {
            System.out.println("Rule "+i);
            System.out.println(tab+"preCondition: "+rule.getPreCondition().getName());
            System.out.println(tab+"action: "+rule.getAction());
            System.out.println(tab+"postConditions: ");
            for (Condition cond : rule.getPostConditions()) {
                System.out.println(tab+tab+cond.getName());
            }
            i++;
        }
    }
    
    public static List<ActionSet> readTraces() {
        List<ActionSet> sets = new ArrayList<>();
        try {
            File file = new File(tracesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String pre = br.readLine();
            String act = null;
            boolean actFlag = true;
            while ((line = br.readLine()) != null) {
                if (actFlag) {
                    act = line;
                    actFlag = false;
                } else {
                    sets.add(new ActionSet(pre, act, line));
                    pre = line;
                    actFlag = true;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return sets;
    }
    
    public static void printActionSets() {
        List<ActionSet> sets = readTraces();
        for (ActionSet set : sets) {
            System.out.println(set.getPreMonitorableAction()+","
                    +set.getControllableAction()+","+set.getPostMonitorableAction());
        }
    }
    
    public static void outputResult(List<Rule> rules) {
        try {
            File file = new File(resultPath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Rule rule : rules) {
                for (Condition cond : rule.getPostConditions()) {
                    bw.write(rule.getPreCondition().getName()+","+rule.getAction()+","+cond.getName()+","+cond.getValue());
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}

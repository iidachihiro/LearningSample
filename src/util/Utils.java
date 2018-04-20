package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.fsp.FSPSentence;
import model.fsp.Process;

public class Utils {
    private static String originalPath = "/Users/iidachihiro/workspace/LearningSample/";
    private static String baseRulesPath = originalPath+"resources/BaseRules.txt";
    private static String tracesPath = originalPath+"resources/Traces.txt";
    private static String resultPath = originalPath+"Result.txt";
    private static String modelPath = originalPath+"Domain.txt";
    
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
    
    public static void outputResult(List<Rule> rules, double threshold) {
        try {
            File file = new File(resultPath);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            int index = 0;
            for (Rule rule : rules) {
                pw.println("Rule "+index+":");
                pw.println(tab+"PreCondition: "+rule.getPreCondition().getName());
                pw.println(tab+"Action: "+rule.getAction());
                pw.println(tab+"PostConditions: ");
                for (Condition cond : rule.getPostConditions()) {
                    if (cond.getValue() < threshold) {
                        continue;
                    }
                    pw.println(tab+tab+cond.getName()+tab+cond.getValue());
                }
                index++;
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    public static void outputDomainModel(List<FSPSentence> fsps) {
        try {
            File file = new File(modelPath);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            for (int i = 0; i < fsps.size(); i++) {
                FSPSentence fsp = fsps.get(i);
                pw.println(fsp.getMap()+"=");
                pw.print("(");
                for (int j = 0; j < fsp.getProcesses().size(); j++) {
                    Process process = fsp.getProcess(j);
                    pw.print(process.getAction()+" -> (");
                    for (int k = 0; k < process.getPosts().size(); k++) {
                        pw.print(process.getPosts().get(k));
                        if (k < process.getPosts().size()-1) {
                            pw.print("|");
                        }
                    }
                    pw.println(")");
                    if (j < fsp.getProcesses().size()-1) {
                        pw.print("|");   
                    }
                }
                if (i < fsps.size()-1) {
                    pw.println("),");
                } else {
                    pw.println(").");
                }
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}

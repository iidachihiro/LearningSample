package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.fsp.FSPSentence;
import model.fsp.Process;

public class Utils {
    private static String originalPath = "../";
    private static String resourcesPath = originalPath+"resources/";
    private static String baseRulesPath = resourcesPath+"BaseRules.txt";
    private static String tracesPath = resourcesPath+"Traces.txt";
    private static String baseActionsPath = resourcesPath+"BaseActions.txt";
    private static String resultPath = originalPath+"Result.txt";
    private static String domainPath = originalPath+"Domain.txt";
    private static String configPath = originalPath+"resources/parameters.config";
    
    // for research/probability
    private static String probabilityPath = originalPath+"probability/";
    
    private final static String tab = "  ";
    
    public static void reflesh() {
        String[] paths = {resultPath, domainPath};
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
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
    
    public static void printRules(List<Rule> rules) {
        int i = 0;
        for (Rule rule : rules) {
            System.out.println("Rule "+i);
            System.out.println(tab+"preCondition: "+rule.getPreCondition().getName());
            System.out.println(tab+"action: "+rule.getAction());
            System.out.println(tab+"postConditions: ");
            for (Condition cond : rule.getPostConditions()) {
                System.out.println(tab+tab+cond.getName()+tab+cond.getValue());
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
                if (rule.neverUpdated()) {
                    continue;
                }
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
    
    public static List<Rule> readBaseActions() {
        List<Rule> rules = new ArrayList<>();
        List<String> monitorable = new ArrayList<>();
        List<String> controllable = new ArrayList<>();
        try {
            File file = new File(baseActionsPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("#Monitorable")) {
                    while ((line = br.readLine()).length() > 0) {
                        String[] strs = line.split(",", 0);
                        monitorable.addAll(Arrays.asList(strs));
                    }
                } else if (line.equals("#Controllable")) {
                    while ((line = br.readLine()) != null && line.length() > 0) {
                        String[] strs = line.split(",", 0);
                        controllable.addAll(Arrays.asList(strs));
                    }
                }
            }
            br.close();
            for (String pre : monitorable) {
                for (String act : controllable) {
                    Rule rule = new Rule(new Condition(pre), act);
                    for (String post : monitorable) {
                        rule.addNewPostCondition(new Condition(post));
                    }
                    rules.add(rule);
                }
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return rules;
    }
    
    public static void outputDomainModel(List<FSPSentence> fsps) {
        try {
            File file = new File(domainPath);
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
    
    public static double readLearningRate() {
        double rate = 0.1;
        try {
            File file = new File(configPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] str = line.split(" ", 0);
                if (str[0].equals("Learning_Rate")) {
                    rate = Double.parseDouble(str[2]);
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return rate;
    }
    
    public static double readThreshold() {
        double threshold = 0.1;
        try {
            File file = new File(configPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] str = line.split(" ", 0);
                if (str[0].equals("Threshold")) {
                    threshold = Double.parseDouble(str[2]);
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
       return threshold;
    }
    
    //for research/probability
    public static void writeProbabilities(List<List<Rule>> tmp) {
        File dir = new File(probabilityPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<String> names = new ArrayList<>();
        for (Rule rule : tmp.get(0)) {
            String pre = rule.getPreCondition().getName();
            String act = rule.getAction();
            for (Condition cond : rule.getPostConditions()) {
                String post = cond.getName();
                names.add(pre+"_"+act+"_"+post);
            }
        }
        double[][] probabilities = new double[names.size()][tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            List<Rule> rules = tmp.get(i);
            int count = 0;
            for (Rule rule : rules) {
                for (Condition cond : rule.getPostConditions()) {
                    probabilities[count][i] = cond.getValue();
                    count++;
                }
            }
        }
        
        try {
            for (int i = 0; i < names.size(); i++) {
                if (isValueNeverChanged(probabilities, i)) {
                    continue;
                }
                File file = new File(probabilityPath+names.get(i)+".csv");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                for (int j = 0; j < probabilities[i].length; j++) {
                    pw.println(j+","+probabilities[i][j]);
                }
                pw.close();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        
    }
    
    private static boolean isValueNeverChanged(double[][] probs, int i) {
        for (int j = 0; j < probs[i].length; j++) {
            if (probs[i][j] != 0.5) {
                return false;
            }
        }
        return true;
    }
}

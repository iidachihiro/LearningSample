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
    private static String originalPath;
    private static String resourcesPath;
    private static String baseRulesPath;
    private static String tracesPath;
    private static String baseActionsPath;
    private static String outputPath;
    private static String resultPath;
    private static String domainPath;
    private static String probabilityTablePath;
    private static String configPath;
    
    private static String traceFileName = "Traces.txt";
    private static String baseRulesFileName = "BaseRules.txt";
    private static String baseActionsFileName = "BaseActions.txt";
    private static double learningRate = 0.1;
    private static double threshold = 0.1;
    
    
    private final static String tab = "  ";
    
    public Utils() {
        originalPath = System.getProperty("user.dir");
        originalPath += "/";
        resourcesPath = originalPath+"resources/";
        outputPath = originalPath+"output/";
        configPath = resourcesPath+"learning.config";
        
        setConfig();
        tracesPath = resourcesPath+traceFileName;
        baseRulesPath = resourcesPath+baseRulesFileName;
        baseActionsPath = resourcesPath+baseActionsFileName;
        resultPath = outputPath+"Result_"+makeIdentificationPart()+".txt";
        domainPath = outputPath+"Domain_"+makeIdentificationPart()+".txt";
        probabilityTablePath = outputPath+"ProbabilityTable_"+makeIdentificationPart()+".csv";
    }    
    
    private static void setConfig() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(configPath)));
            String line;
            while ((line = br.readLine()) != null) {
                line = removeSpace(line);
                if (line.startsWith("TraceFileName")) {
                    traceFileName = line.substring("TraceFileName=".length());
                } else if (line.startsWith("BaseRulesFileName")) {
                    baseRulesFileName = line.substring("BaseRulesFileName=".length());
                } else if (line.startsWith("BaseActionsFileName")) {
                    baseActionsFileName = line.substring("BaseACtionsFileName=".length());
                } else if (line.startsWith("#SGD")) {
                    boolean flagLR = false, flagTH = false;
                    while ((line = br.readLine()) != null) {
                        if (flagLR && flagTH) { 
                            break;
                        }
                        line = removeSpace(line);
                        if (line.startsWith("LearningRate")) {
                            learningRate = Double.valueOf(line.substring("LearningRate=".length()));
                            flagLR = true;
                        } else if (line.startsWith("Threshold")) {
                            threshold = Double.valueOf(line.substring("Threshold=".length()));
                            flagTH = true;
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    public static String getBasePath() {
        return originalPath;
    }
    
    public static String getResourcesPath() {
        return resourcesPath;
    }
    
    public static String getBaseRulesPath() {
        return baseRulesPath;
    }
    
    public static String getOutputPath() {
        return outputPath;
    }
    
    public static String getProbabilityTablePath() {
        return probabilityTablePath;
    }
    
    public static String getConfigPath() {
        return configPath;
    }
    
    public static String getDomainPath() {
        return domainPath;
    }
    
    public static String getResultPath() {
        return resultPath;
    }
    
    public static double getLearningRate() {
        return learningRate;
    }
    
    public static double getThreshold() {
        return threshold;
    }
    
    public static String getTraceFileName() {
        return traceFileName;
    }
    
    public static void reflesh() {
        String[] paths = {resultPath, domainPath, probabilityTablePath};
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
    public static String removeSpace(String line) {
        line = line.replaceAll(" ", "");
        line = line.replaceAll("\t", "");
        return line;
    }
    
    public static List<Rule> readBaseRules() {
        List<Rule> rules = new ArrayList<>();
        try {
            File file = new File(baseRulesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.substring(0, 2).equals("//")) {
                    continue;
                }
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
        System.out.println("Read base rules file.");
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
        System.out.println("Read traces file.");
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
            int index = 1;
            
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
        System.out.println("Generate Result.txt");
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
        System.out.println("Read base actions file.");
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
        System.out.println("Generate Domain.txt");
    }
    
    public static void prepareProbabilityTable(List<Rule> rules) {
        try {
            File file = new File(probabilityTablePath);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.print("ActionSet,");
            for (Rule rule : rules) {
                for (Condition postCond : rule.getPostConditions()) {
                    pw.print(rule.getPreCondition().getName()+" "+rule.getAction()+" "+postCond.getName()+",");
                }
            }
            pw.println();
            pw.print("0,");
            for (Rule rule : rules) {
                for (Condition postCond : rule.getPostConditions()) {
                    pw.print(postCond.getValue()+",");
                }
            }
            pw.println();
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        System.out.println("Generate probability table file.");
    }
    
    public static void updateProbabilityTable(List<Rule> rules, int index) {
        try {
            File file = new File(probabilityTablePath);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.print(index+",");
            for (Rule rule : rules) {
                for (Condition postCond : rule.getPostConditions()) {
                    pw.print(postCond.getValue()+",");
                }
            }
            pw.println();
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    public static String makeIdentificationPart() {
        String result = "";
        result += traceFileName.substring(0,  traceFileName.length()-4); // ".txt" is removed.
        result += "_";
        result += learningRate;
        result += "_";
        result += threshold;
        return result;
    }
}

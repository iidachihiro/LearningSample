package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import core.ActionSet;
import core.Condition;
import core.Rule;
import model.fsp.FSPSentence;
import model.fsp.Process;

public class PUtils {
    private static String originalPath = "../";
    private static String resourcesPath = originalPath+"resources/";
    private static String baseRulesPath = resourcesPath+"BaseRules.txt";
    private static String tracesPath = resourcesPath+"Traces.txt";
    private static String parallelTracesPath = resourcesPath+"ParallelTraces.txt";
    private static String configPath = originalPath+"resources/parameters.config";
    private static String resultPath = originalPath+"Result.txt";
    private static String domainPath = originalPath+"Domain.txt";
    private static String logPath = originalPath+"updatedLog";
    
    private final static String tab = "  ";
    
    public static void reflesh() {
        String[] paths = {resultPath, domainPath, logPath};
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
    public static void generateNBaseRules(int n) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(baseRulesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            for (int i = 0; i < n; i++) {
                File _file = new File(resourcesPath+i+"_BaseRules.txt");
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(_file)));
                for (String _line : lines) {
                    String[] strs = _line.split(",", 0);
                    for (int j = 0; j < 3; j++) {
                        strs[j] = i+"_"+strs[j];
                    }
                    pw.println(String.join(",", strs));
                }
                pw.close();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    public static void generateParallelTraces(int n) {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(tracesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            List<Queue<String>> allTraces = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                allTraces.add(new ArrayDeque<>());
                Queue<String> traces = allTraces.get(i);
                for (String _line : lines) {
                    traces.add(i+"_"+_line);
                }
            }
            List<String> parallelTraces = new ArrayList<>();
            while (allTraces.size() > 0) {
                Random rnd = new Random();
                int rvalue = rnd.nextInt(allTraces.size());
                parallelTraces.add(allTraces.get(rvalue).poll());
                if (allTraces.get(rvalue).size() == 0) {
                    allTraces.remove(rvalue);
                }
            }
            File _file = new File(parallelTracesPath);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(_file)));
            for (String _line : parallelTraces) {
                pw.println(_line);
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    public static List<Rule> readNBaseRules(int n) {
        List<Rule> rules = new ArrayList<>();
        try {
            for (int i = 0; i < n; i++) {
                File file = new File(resourcesPath+i+"_BaseRules.txt");
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
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return rules;
    }
    
    public static List<ActionSet> readParallelTraces(int n) {
        List<ActionSet> allSets = new ArrayList<>();
        String[] pres = new String[n];
        Arrays.fill(pres, "");
        String[] acts = new String[n];
        Arrays.fill(acts, null);
        boolean[] actFlags = new boolean[n];
        Arrays.fill(actFlags, false);
        try {
            File file = new File(parallelTracesPath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                int id = Integer.valueOf(line.substring(0, 1));
                if (actFlags[id]) {
                    acts[id] = line;
                    actFlags[id] = false;
                } else {
                    if (!pres[id].equals("")) {
                        allSets.add(new ActionSet(pres[id], acts[id], line));
                    }
                    pres[id] = line;
                    actFlags[id] = true;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return allSets;
    }
    
    public static void outputResult(List<Rule> rules, double threshold) {
        try {
            File file = new File(resultPath);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            int index = 0;
            int robotId = -1;
            for (Rule rule : rules) {
                if (robotId != getRobotId(rule)) {
                    index = 0;
                    robotId = getRobotId(rule);
                    pw.print("####################");
                    pw.print("The rules of No. "+robotId);
                    pw.println("####################");
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
    
    public static void updateLog(int id) {
        try {
            File file = new File(logPath);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.println("No. "+id+" Domain model is updated.");
            pw.close();
        } catch (IOException e) {
            System.err.print(e.toString());
        }
    }
    
    public static int getRobotId(Rule rule) {
        String name = rule.getPreCondition().getName();
        return Integer.valueOf(name.substring(0, name.indexOf('_')));
    }
}

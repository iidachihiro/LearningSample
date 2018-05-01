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

public class PUtils {
    private static String originalPath = "../";
    private static String resourcesPath = originalPath+"resources/";
    private static String baseRulesPath = resourcesPath+"BaseRules.txt";
    private static String tracesPath = resourcesPath+"Traces.txt";
    private static String parallelTracesPath = resourcesPath+"ParallelTraces.txt";
    private static String configPath = originalPath+"resources/parameters.config";
    
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
            System.out.println(parallelTraces.size());
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
    
    public static List<List<Rule>> readNBaseRules(int n) {
        List<List<Rule>> allRules = new ArrayList<>();
        try {
            for (int i = 0; i < n; i++) {
                allRules.add(new ArrayList<>());
                List<Rule> rules = allRules.get(i);
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
        return allRules;
    }
    
    public static List<List<ActionSet>> readParallelTraces(int n) {
        List<List<ActionSet>> allSets = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            allSets.add(new ArrayList<>());
        }
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
                        allSets.get(id).add(new ActionSet(pres[id], acts[id], line));
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
}

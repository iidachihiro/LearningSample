package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import util.Utils;

public class Experiment {
    private static String trueProbabilityFileName = "TrueProbability_1_2.csv";
    private static String errorFileName = "error.csv";
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please input the mode.");
            return;
        }
        if (args[0].equals("prepare")) {
            prepare();
        } else if (args[0].equals("error")) {
            outputErrorFile();
        }
    }
    
    private static void prepare() {
        new Utils();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(Utils.getBaseRulesPath())));
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(Utils.getResourcesPath()+trueProbabilityFileName))));
            pw.println("Pre,Act,Post,0,5000");
            String line;
            while((line = br.readLine()) != null) {
                pw.println(line+","+" ,"+" ");
            }
            br.close();
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        System.out.println("Generated "+trueProbabilityFileName+".");
    }
    
    private static void outputErrorFile() {
        new Utils();
        double[] LRs = {0.001, 0.005, 0.01, 0.05, 0.1, 0.5};
        List<List<Double>> errorsLists = new ArrayList<>();
        for (double LR : LRs) {
            try {
                // change the learning rate in parameters.config
                BufferedReader br0 = new BufferedReader(new FileReader(new File(Utils.getConfigPath())));
                PrintWriter pw0 = new PrintWriter(new FileWriter(new File(Utils.getResourcesPath()+"tmp.txt")));
                String line;
                while ((line = br0.readLine()) != null) {
                    if (line.startsWith("Learning Rate")) {
                        pw0.println("Learning Rate = "+LR);
                    } else {
                        pw0.println(line);
                    }
                }
                br0.close();
                pw0.close();
                File f = new File(Utils.getResourcesPath()+"tmp.txt");
                f.renameTo(new File(Utils.getConfigPath()));
                
                // run main
                String[] args = {"o"};
                SampleMain.main(args);
                
                int NOBR = getNumberOfBaseRules();
                BufferedReader br1 = new BufferedReader(new FileReader(new File(Utils.getResourcesPath()+trueProbabilityFileName)));
                line = br1.readLine();
                String[] strs = line.split(",");
                int N = Integer.valueOf(strs[4]);
                double[][] probabilities = new double[NOBR][2];
                int index = 0;
                while((line = br1.readLine()) != null) {
                    if (line.substring(0, 2).equals("//")) {
                        continue;
                    }
                    strs = line.split(",");
                    probabilities[index][0] = Double.valueOf(strs[3]);
                    probabilities[index][1] = Double.valueOf(strs[4]);
                    index++;
                }
                br1.close();
                
                /*
                BufferedReader br2 = new BufferedReader(new FileReader(new File(Utils.getProbabilityTablePath())));
                PrintWriter pw1 = new PrintWriter(new BufferedWriter(new FileWriter(new File(Utils.getOutputPath()+errorFileName))));
                br2.readLine();
                while ((line = br2.readLine()) != null) {
                    strs = line.split(",");
                    int id = Integer.valueOf(strs[0]);
                    double error = 0;
                    int j = 0;
                    for (int i = 1; i < strs.length; i++) {
                        error += Math.abs(probabilities[i-1][0+j]-Double.valueOf(strs[i]));
                    }
                    error = error/NOR;
                    pw1.println(id+","+error);
                    if (id == N/2) {
                        j++;
                    }
                }
                br2.close();
                pw1.close();
                */
                
                List<Double> errorsList = new ArrayList<>();
                BufferedReader br2 = new BufferedReader(new FileReader(new File(Utils.getProbabilityTablePath())));
                br2.readLine();
                while ((line = br2.readLine()) != null) {
                    strs = line.split(",");
                    int id = Integer.valueOf(strs[0]);
                    double error = 0;
                    int j = 0;
                    for (int i = 1; i < strs.length; i++) {
                        error += Math.abs(probabilities[i-1][0+j]-Double.valueOf(strs[i]));
                    }
                    error = error/NOBR;
                    errorsList.add(error);
                    if (id == N/2) {
                        j++;
                    }
                }
                br2.close();
                errorsLists.add(errorsList);
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }
        // output ErrorFile
        try {
            String tmp = Utils.getTraceFileName();
            tmp = tmp.substring(0, tmp.length()-4);
            errorFileName = "error_"+tmp+".csv";
            PrintWriter pw = new PrintWriter(new FileWriter(new File(Utils.getOutputPath()+errorFileName)));
            pw.println("id,0.001,0.005,0.01,0.05,0.1,0.5");
            for (int i = 0; i < errorsLists.get(0).size(); i++) {
                pw.println(i+","+errorsLists.get(0).get(i)+","+errorsLists.get(1).get(i)+","
                        +errorsLists.get(2).get(i)+","+errorsLists.get(3).get(i)+","
                        +errorsLists.get(4).get(i)+","+errorsLists.get(5).get(i));
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
    
    private static int getNumberOfBaseRules() {
        int sum = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(Utils.getBaseRulesPath())));
            String line;
            while((line = br.readLine()) != null) {
                if (line.substring(0, 2).equals("//"));
                sum++;
            }
            br.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return sum;
    }
}

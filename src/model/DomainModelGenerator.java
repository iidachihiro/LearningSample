package model;

import java.util.ArrayList;
import java.util.List;

import core.Condition;
import core.Rule;
import model.fsp.FSPSentence;
import model.fsp.Process;
import util.Utils;

public class DomainModelGenerator {
    private List<FSPSentence> fsps;
    
    public DomainModelGenerator() {
        this.fsps = new ArrayList<>();
    }
    
    public void generate(List<Rule> rules, double threshold) {
        for (Rule rule : rules) {
            String map = translateMAP(translateCondition(rule.getPreCondition()));
            String action = translateAction(rule.getAction());
            List<String> posts = new ArrayList<>();
            for (Condition cond : rule.getPostConditions()) {
                if (cond.getValue() < threshold) {
                    continue;
                }
                String post = translateCondition(cond);
                if (!posts.contains(post)) {
                    posts.add(post+" -> "+translateMAP(post));
                }
            }
            int index = getIndexOf(map);
            if (index < 0) { // fsps don't have same map
                this.fsps.add(new FSPSentence(map, new Process(action, posts)));
            } else {
                int index_ = fsps.get(index).getIndexOf(action);
                if (index_ < 0) { // fsps have same map, but don't have same action
                    this.fsps.get(index).addProcess(new Process(action, posts));
                } else { // fsps have same map and action
                    this.fsps.get(index).getProcess(index_).addPosts(posts);
                }
            }
        }
        Utils.outputDomainModel(fsps);
        System.out.println("Domain model is updated.");
    }
    
    
    private int getIndexOf(String map) {
        for (int i = 0; i < this.fsps.size(); i++) {
            FSPSentence fsp = this.fsps.get(i);
            if (fsp.getMap().equals(map)) {
                return i;
            }
        }
        return -1;
    }
    
    // for sample traces
    
    private String translateCondition(Condition cond) {
        String name = cond.getName();
        switch (name) {
            case "arrive.e":
                return "arrive['e]";
            case "pickupfail":
                return "pickupfail";
            case "pickupsuccess":
                return "pickupsuccess";
            case "arrive.m":
                return "arrive['m]";
            case "arrive.w":
                return "arrive['w]";
            case "putsuccess":
                return "putsuccess";
            case "putfail":
                return "putfail";
            default:
                return null;
        }
    }
    
    private String translateMAP(String pre) {
        final String[] maps = {"MAP['e]", "MAP['m]", "MAP['w]"};
        switch (pre) {
            case "arrive['e]":
                return maps[0];
            case "pickupfail":
                return maps[0];
            case "pickupsuccess":
                return maps[0];
            case "arrive['m]":
                return maps[1];
            case "arrive['w]":
                return maps[2];
            case "putsuccess":
                return maps[2];
            case "putfail":
                return maps[2];
            default:
                return null;
        }
    }
    
    private String translateAction(String action) {
        switch (action) {
            case "move.e":
                return "move['e]";
            case "move.m":
                return "move['m]";
            case "move.w":
                return "move['w]";
            case "pickup":
                return "pickup";
            case "putdown":
                return "putdown";
            default:
                return null;
        }
    }
}

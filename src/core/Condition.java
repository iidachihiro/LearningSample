package core;

public class Condition {
    private String name;
    private double value;
    private double preValue;
    private double gradient;
    private int count;
    
    public Condition(String name) {
        this.name = name;
        this.value = 0.5;
        this.preValue = 0.5;
        this.gradient = 0;
        this.count = 0;
    }
    
    public String getName() {
        return this.name;
    }
    
    public double getValue() {
        return this.value;
    }
    
    public double getPreValue() {
        return this.preValue;
    }
    
    public double getGradient() {
        return this.gradient;
    }
    
    public int getCount() {
        return this.count;
    }
}

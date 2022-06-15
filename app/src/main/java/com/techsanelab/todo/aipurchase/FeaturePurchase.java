package com.techsanelab.todo.aipurchase;

import android.util.ArraySet;

import java.util.Set;

public abstract class FeaturePurchase {

    public enum Scope {
        HIGH_CHANCE,
        SHOW,
        PROMOTION,
        DOWN,
        SINUSOID
    }

    protected int MIN_AVG;
    protected int MAX_AVG;
    protected int GROWTH;
    private float currentAvg;
    private int acc = 0;
    private int periodAcc = 0;
    private int index = 0;
    private Scope scope;
    private Set<String> periodItems;
    private OnScopeHappened onScopeHappened;
    private SaveFeatureInterface saveFeature;
    private Logger logger = null;

    public FeaturePurchase(int minAvg, int maxAvg, int growth) {
        MIN_AVG = minAvg;
        MAX_AVG = maxAvg;
        GROWTH = growth;
        currentAvg = (float) ((MIN_AVG + MAX_AVG) / 2);
        periodItems = new ArraySet<>();
    }

    public void query(int feature, Object item){
        if (feature > 0) {
            periodItems.add((String) item);
            index++;
            logger.log("Add new item with feature",String.valueOf(feature));
        } else if (feature < 0 && periodItems.contains(item.toString())) {
            periodItems.remove(item.toString());
            index--;
            logger.log("Remove an item with feature",String.valueOf(feature));
        } else if (feature != 0) {
            return;
        }

        acc += feature;
        periodAcc += feature;
        float diff = periodAcc - currentAvg;

        if (diff > GROWTH && currentAvg == MAX_AVG) {
            onScopeHappened.highChance();
            scope = Scope.HIGH_CHANCE;
            logger.log("Scope","High chance to sell");
        } else if (diff <= -MIN_AVG && currentAvg == MIN_AVG) {
            onScopeHappened.promotion();
            scope = Scope.PROMOTION;
            logger.log("Scope","Need promotion");
        } else if (diff > GROWTH) {
            onScopeHappened.show();
            scope = Scope.SHOW;
            logger.log("Scope","Show offers");
        } else if (diff <= -GROWTH) {
            onScopeHappened.down();
            scope = Scope.DOWN;
            logger.log("Scope","Doesn't use your app");
        } else {
            onScopeHappened.sinusoid();
            scope = Scope.SINUSOID;
            logger.log("Scope","There isn't any growth");
        }
        save();
    }

    public Scope getScope() {
        if (scope != null)
            return scope;

        float diff = periodAcc - currentAvg;
        if (diff > GROWTH && currentAvg == MAX_AVG) {
            scope = Scope.HIGH_CHANCE;
        } else if (diff <= -MIN_AVG && currentAvg == MIN_AVG) {
            scope = Scope.PROMOTION;
        } else if (diff > GROWTH) {
            scope = Scope.SHOW;
        } else if (diff <= -GROWTH) {
            scope = Scope.DOWN;
        } else {
            scope = Scope.SINUSOID;
        }
        return scope;
    }

    public void update() {
        updateAvg();
        if (saveFeature.save())
            logger.log("Save","Data saved successfully");
        else
            logger.log("Save","There is a problem in saving data");
        saveFeature.resetOnPeriod();
        logger.log("Reset","All data resets");
    }

    public void updateAvg(){
        currentAvg = (float) acc / index;
    }

    public boolean save(){
        return saveFeature.save();
    }

    public void reset(){
        index = 0;
        periodAcc = 0;
        periodItems.clear();
    }

    public void setSaveFeature(SaveFeatureInterface saveFeature) {
        this.saveFeature = saveFeature;
    }

    public void setOnScopeHappened(OnScopeHappened onScopeHappened) {
        this.onScopeHappened = onScopeHappened;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public int getPeriodAcc() {
        return periodAcc;
    }

    public void setPeriodAcc(int periodAcc) {
        this.periodAcc = periodAcc;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Set<String> getPeriodItems() {
        return periodItems;
    }

    public void setPeriodItems(Set<String> periodItems) {
        this.periodItems = periodItems;
    }
}

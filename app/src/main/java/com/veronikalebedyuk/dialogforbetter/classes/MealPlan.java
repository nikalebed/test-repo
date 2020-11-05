package com.veronikalebedyuk.dialogforbetter.classes;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;


import com.veronikalebedyuk.dialogforbetter.R;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

public class MealPlan{
    private int dayNorm;
    private int kkPerKg;
    private int fisActivityLevel;
    private int bodyMassIndex;
    public int getKkPerKg() {
         if(bodyMassIndex<18) return 25;
        else if(bodyMassIndex<25) return 20;
        else if(bodyMassIndex<30) return 17;
        return 0;
    }

    public int getDayNorm() {
        return dayNorm;
    }

    public void setDayNorm(int dayNorm) {
        this.dayNorm = dayNorm;
    }

    public void setKkPerKg(int kkPerKg) {
        this.kkPerKg = kkPerKg;
    }

    public int getFisActivityLevel() {
        return fisActivityLevel;
    }

    public void setFisActivityLevel(int fisActivityLevel) {
        this.fisActivityLevel = fisActivityLevel;
    }

    public int getBodyMassIndex() {
        return bodyMassIndex;
    }

    public void setBodyMassIndex(int bodyMassIndex) {
        this.bodyMassIndex = bodyMassIndex;
    }

    public String createMealPlan(){
        String threeMealPlan = "С учетом индекса массы тела ( приблизительно " +(int)bodyMassIndex+ ") и " + level()+" уровнем физической активности предлагаю составить план питания так: \n" +
                "ЗАВТРАК\n" + (int)0.25*dayNorm +" Кк\n"+
                "ОБЕД\n" +(int)0.4*dayNorm +" Кк\n"+
                "УЖИН\n" + (int)0.25*dayNorm +" Кк\n"+
                "перекусы\n" + (int)0.1*dayNorm +" Кк";
        String sixMealPlan = "С учетом индекса массы тела ( приблизительно " +(int)bodyMassIndex+ ") и " + level()+" ровнем физической активности " +
                "предлагаю составить план питания так (рекоммендовано дробное питание):\n" +
                "ЗАВТРАК\n" + (int)250 +" Кк\n"+
                "второй завтрак\n" + (int)250 +" Кк\n"+
                "ОБЕД\n" + (int)250 +" Кк\n"+
                "полдник\n" + (int)250 +" Кк\n"+
                "УЖИН\n" + (int)250+" Кк\n"+
                "второй ужин\n"+ (int)250 +" Кк\n";
        if(bodyMassIndex<25) return threeMealPlan;
        return sixMealPlan;
    }

    public MealPlan(){}
    public MealPlan(int fisActivityLevel, int bodyMassIndex, int dayNorm) {
        this.fisActivityLevel = fisActivityLevel;
        this.bodyMassIndex = bodyMassIndex;
        this.dayNorm = bodyMassIndex;
    }

    public String mealType() {
        Date time = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        int h =Integer.parseInt(sdf.format(time));
        if(h<12 && h>5){
            return "завтрака\n";
        }
        else if(h<15){
            return "обеда\n";
        }
        else if(h<19){
            return "ужина\n";
        }
        else{
            return "не располагает к плотному приему пищи, лучше cделать перекус\n";
        }
    }
    private String level(){
        if(fisActivityLevel == 11)return "низким";
        if(fisActivityLevel == 13)return "средним";
        return "высоким";
    }
}


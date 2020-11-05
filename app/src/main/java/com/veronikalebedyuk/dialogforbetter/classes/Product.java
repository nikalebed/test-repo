package com.veronikalebedyuk.dialogforbetter.classes;

public class Product {
    String name;
    double value;

    public Product(String name, double value) {
        this.name = name;
        this.value = value;
    }
    public Product(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

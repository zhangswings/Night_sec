package com.example.swings.night_sec;

import org.litepal.crud.DataSupport;

/**
 * Created by swings on 2015/9/17.
 */
public class Reader extends DataSupport {
    private String name;

    private float price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Reader{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}

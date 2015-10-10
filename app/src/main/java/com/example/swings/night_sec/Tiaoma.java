package com.example.swings.night_sec;

import org.litepal.crud.DataSupport;

/**
 * Created by swings on 2015-10-10.
 */
public class Tiaoma extends DataSupport {
    private String tiaoma_id;
    private String bianma_id;
    private String weight;
    private String length;
    private Bianma bianma;

    @Override
    public String toString() {
        return "Tiaoma{" +
                "tiaoma_id='" + tiaoma_id + '\'' +
                ", bianma_id='" + bianma_id + '\'' +
                ", weight='" + weight + '\'' +
                ", length='" + length + '\'' +
                ", bianma=" + bianma +
                '}';
    }

    public String getTiaoma_id() {
        return tiaoma_id;
    }

    public void setTiaoma_id(String tiaoma_id) {
        this.tiaoma_id = tiaoma_id;
    }

    public String getBianma_id() {
        return bianma_id;
    }

    public void setBianma_id(String bianma_id) {
        this.bianma_id = bianma_id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Bianma getBianma() {
        return bianma;
    }

    public void setBianma(Bianma bianma) {
        this.bianma = bianma;
    }
}

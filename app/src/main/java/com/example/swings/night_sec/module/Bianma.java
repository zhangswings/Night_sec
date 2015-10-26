package com.example.swings.night_sec.module;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swings on 2015-10-10.
 */
public class Bianma extends DataSupport {
    private String bianma_id;
    private String wuliao;
    private String kezhong;
    private String fukuan;
    private String pan_id;
    private Pan pan;
    private int nums = 0;
    private List<Tiaoma> bianma_tiaoma = new ArrayList<Tiaoma>();

    @Override
    public String toString() {
        return "Bianma{" +
                "bianma_id='" + bianma_id + '\'' +
                ", wuliao='" + wuliao + '\'' +
                ", kezhong='" + kezhong + '\'' +
                ", fukuan='" + fukuan + '\'' +
                ", pan_id='" + pan_id + '\'' +
                ", pan=" + pan +
                ", nums=" + nums +
                ", bianma_tiaoma=" + bianma_tiaoma +
                '}';
    }

    public String getPan_id() {
        return pan_id;
    }

    public void setPan_id(String pan_id) {
        this.pan_id = pan_id;
    }

    public String getBianma_id() {
        return bianma_id;
    }

    public void setBianma_id(String bianma_id) {
        this.bianma_id = bianma_id;
    }

    public String getWuliao() {
        return wuliao;
    }

    public void setWuliao(String wuliao) {
        this.wuliao = wuliao;
    }

    public String getKezhong() {
        return kezhong;
    }

    public void setKezhong(String kezhong) {
        this.kezhong = kezhong;
    }

    public String getFukuan() {
        return fukuan;
    }

    public void setFukuan(String fukuan) {
        this.fukuan = fukuan;
    }

    public Pan getPan() {
        return pan;
    }

    public void setPan(Pan pan) {
        this.pan = pan;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public List<Tiaoma> getBianma_tiaoma() {
        return bianma_tiaoma;
    }

    public void setBianma_tiaoma(List<Tiaoma> bianma_tiaoma) {
        this.bianma_tiaoma = bianma_tiaoma;
    }
}

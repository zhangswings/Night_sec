package com.example.swings.night_sec;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by swings on 2015-10-10.
 */
public class Pan extends DataSupport {
    private String pan_id;
    private Date date;
    private String cangku;
    private String user;
    private List<Bianma> pan_bianma = new ArrayList<Bianma>();
    private String status;

    @Override
    public String toString() {
        return "Pan{" +
                "pan_id='" + pan_id + '\'' +
                ", date=" + date +
                ", cangku='" + cangku + '\'' +
                ", user='" + user + '\'' +
                ", pan_bianma=" + pan_bianma +
                ", status='" + status + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPan_id() {
        return pan_id;
    }

    public void setPan_id(String pan_id) {
        this.pan_id = pan_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCangku() {
        return cangku;
    }

    public void setCangku(String cangku) {
        this.cangku = cangku;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<Bianma> getPan_bianma() {
        return pan_bianma;
    }

    public void setPan_bianma(List<Bianma> pan_bianma) {
        this.pan_bianma = pan_bianma;
    }
}

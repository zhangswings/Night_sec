package com.example.swings.night_sec.module;

import org.litepal.crud.DataSupport;

/**
 * Created by swings on 2015/9/18.
 * 纸品信息类extends DataSupport（Litepal数据库框架）
 */
public class Papers extends DataSupport {
    //编码
    private String paper_code;
    //条码
    private String paper_tiaoma;
    //名称
    private String paper_name;
    //  幅宽
    private String paper_fukuan;
    //    克重
    private String paper_kezhong;
    //    重量
    private String paper_weight;
    //长度
    private String paper_length;
    //车间
    private String paper_chejian;
    //   班组
    private String paper_banzu;
    //条码时间
    private String paper_code_time;
    //用户
    private String paper_user;
    //状态，0未上传，1已上传
    private String paper_status;

    @Override
    public String toString() {
        return "Papers{" +
                "paper_code='" + paper_code + '\'' +
                ", paper_tiaoma='" + paper_tiaoma + '\'' +
                ", paper_name='" + paper_name + '\'' +
                ", paper_fukuan='" + paper_fukuan + '\'' +
                ", paper_kezhong='" + paper_kezhong + '\'' +
                ", paper_weight='" + paper_weight + '\'' +
                ", paper_length='" + paper_length + '\'' +
                ", paper_chejian='" + paper_chejian + '\'' +
                ", paper_banzu='" + paper_banzu + '\'' +
                ", paper_code_time='" + paper_code_time + '\'' +
                ", paper_user='" + paper_user + '\'' +
                ", paper_status='" + paper_status + '\'' +
                '}';
    }

    public void setPaper_status(String paper_status) {
        this.paper_status = paper_status;
    }

    public String getPaper_status() {
        return paper_status;
    }

    public String getPaper_code() {
        return paper_code;
    }

    public void setPaper_code(String paper_code) {
        this.paper_code = paper_code;
    }

    public String getPaper_tiaoma() {
        return paper_tiaoma;
    }

    public void setPaper_tiaoma(String paper_tiaoma) {
        this.paper_tiaoma = paper_tiaoma;
    }

    public String getPaper_name() {
        return paper_name;
    }

    public void setPaper_name(String paper_name) {
        this.paper_name = paper_name;
    }

    public String getPaper_fukuan() {
        return paper_fukuan;
    }

    public void setPaper_fukuan(String paper_fukuan) {
        this.paper_fukuan = paper_fukuan;
    }

    public String getPaper_kezhong() {
        return paper_kezhong;
    }

    public void setPaper_kezhong(String paper_kezhong) {
        this.paper_kezhong = paper_kezhong;
    }

    public String getPaper_weight() {
        return paper_weight;
    }

    public void setPaper_weight(String paper_weight) {
        this.paper_weight = paper_weight;
    }

    public String getPaper_length() {
        return paper_length;
    }

    public void setPaper_length(String paper_length) {
        this.paper_length = paper_length;
    }

    public String getPaper_chejian() {
        return paper_chejian;
    }

    public void setPaper_chejian(String paper_chejian) {
        this.paper_chejian = paper_chejian;
    }

    public String getPaper_banzu() {
        return paper_banzu;
    }

    public void setPaper_banzu(String paper_banzu) {
        this.paper_banzu = paper_banzu;
    }

    public String getPaper_code_time() {
        return paper_code_time;
    }

    public void setPaper_code_time(String paper_code_time) {
        this.paper_code_time = paper_code_time;
    }

    public String getPaper_user() {
        return paper_user;
    }

    public void setPaper_user(String paper_user) {
        this.paper_user = paper_user;
    }
}


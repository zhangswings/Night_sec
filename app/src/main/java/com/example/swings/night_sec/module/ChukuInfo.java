package com.example.swings.night_sec.module;

import org.litepal.crud.DataSupport;

/**
 * Created by swings on 2015-10-29.
   * 出库信息类extends DataSupport（Litepal数据库框架）
 */
public class ChukuInfo extends DataSupport {
    //客户
    private String kehu;
    //客户编码
    private String ghs;

    //车牌号
    private String chepai;
    //车间、仓库
    private String chejian;
    //班组
    private String banzu;


    //名称
    private String nama;
    //编码
    private String bianma;
    //条码
    private String tiaoma;
    //幅宽
    private String fukuan;
    //重量
    private String weight;
    //克重
    private String kezhong;
    //长度
    private String lenght;
    //状态
    private String status = "0";
    //操作人
    private String caozuoren;
    //时间
    private String time;

    @Override
    public String toString() {
        return "ChukuInfo{" +
                "kehu='" + kehu + '\'' +
                ", ghs='" + ghs + '\'' +
                ", chepai='" + chepai + '\'' +
                ", chejian='" + chejian + '\'' +
                ", banzu='" + banzu + '\'' +
                ", nama='" + nama + '\'' +
                ", bianma='" + bianma + '\'' +
                ", tiaoma='" + tiaoma + '\'' +
                ", fukuan='" + fukuan + '\'' +
                ", weight='" + weight + '\'' +
                ", kezhong='" + kezhong + '\'' +
                ", lenght='" + lenght + '\'' +
                ", status='" + status + '\'' +
                ", caozuoren='" + caozuoren + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getGhs() {
        return ghs;
    }

    public void setGhs(String ghs) {
        this.ghs = ghs;
    }

    public String getBanzu() {
        return banzu;
    }

    public void setBanzu(String banzu) {
        this.banzu = banzu;
    }

    public String getKehu() {
        return kehu;
    }

    public void setKehu(String kehu) {
        this.kehu = kehu;
    }

    public String getChepai() {
        return chepai;
    }

    public void setChepai(String chepai) {
        this.chepai = chepai;
    }

    public String getChejian() {
        return chejian;
    }

    public void setChejian(String chejian) {
        this.chejian = chejian;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getBianma() {
        return bianma;
    }

    public void setBianma(String bianma) {
        this.bianma = bianma;
    }

    public String getTiaoma() {
        return tiaoma;
    }

    public void setTiaoma(String tiaoma) {
        this.tiaoma = tiaoma;
    }

    public String getFukuan() {
        return fukuan;
    }

    public void setFukuan(String fukuan) {
        this.fukuan = fukuan;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getKezhong() {
        return kezhong;
    }

    public void setKezhong(String kezhong) {
        this.kezhong = kezhong;
    }

    public String getLenght() {
        return lenght;
    }

    public void setLenght(String lenght) {
        this.lenght = lenght;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCaozuoren() {
        return caozuoren;
    }

    public void setCaozuoren(String caozuoren) {
        this.caozuoren = caozuoren;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

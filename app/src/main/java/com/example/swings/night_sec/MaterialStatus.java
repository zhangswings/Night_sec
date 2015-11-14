package com.example.swings.night_sec;

/**
 * 物料信息（未使用）
 */
public class MaterialStatus {
    private String StoreId;

    private String time;

    private String OperaName;

    private String status;

    private String supplierName;

    public void setStoreId(String StoreId) {
        this.StoreId = StoreId;
    }

    public String getStoreId() {
        return this.StoreId;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return this.time;
    }

    public void setOperaName(String OperaName) {
        this.OperaName = OperaName;
    }

    public String getOperaName() {
        return this.OperaName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierName() {
        return this.supplierName;
    }

    @Override
    public String toString() {
        return "MaterialStatus{" +
                "StoreId='" + StoreId + '\'' +
                ", time='" + time + '\'' +
                ", OperaName='" + OperaName + '\'' +
                ", status='" + status + '\'' +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}
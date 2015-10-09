package com.example.swings.night_sec;

/**
 * Created by swings on 2015/9/17.
 */
public class Material {

    /**
     * weight : 828
     * MaterialSerialId : 00
     * MaterialType : ss
     * Id : 1
     * Remark : ss
     * MaterialName : ss
     * MaterialCount : 36
     */
    private int weight;
    private String MaterialSerialId;
    private String MaterialType;
    private int Id;
    private String Remark;
    private String MaterialName;
    private int MaterialCount;

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setMaterialSerialId(String MaterialSerialId) {
        this.MaterialSerialId = MaterialSerialId;
    }

    public void setMaterialType(String MaterialType) {
        this.MaterialType = MaterialType;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public void setMaterialName(String MaterialName) {
        this.MaterialName = MaterialName;
    }

    public void setMaterialCount(int MaterialCount) {
        this.MaterialCount = MaterialCount;
    }

    public int getWeight() {
        return weight;
    }

    public String getMaterialSerialId() {
        return MaterialSerialId;
    }

    public String getMaterialType() {
        return MaterialType;
    }

    public int getId() {
        return Id;
    }

    public String getRemark() {
        return Remark;
    }

    public String getMaterialName() {
        return MaterialName;
    }

    public int getMaterialCount() {
        return MaterialCount;
    }

    @Override
    public String toString() {
        return "Material{" +
                "weight=" + weight +
                ", MaterialSerialId='" + MaterialSerialId + '\'' +
                ", MaterialType='" + MaterialType + '\'' +
                ", Id=" + Id +
                ", Remark='" + Remark + '\'' +
                ", MaterialName='" + MaterialName + '\'' +
                ", MaterialCount=" + MaterialCount +
                '}';
    }
}

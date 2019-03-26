package com.bt.andy.rongbei.messegeInfo;

/**
 * @创建者 AndyYan
 * @创建时间 2019/3/22 11:13
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class SelectGoodsInfo {

    /**
     * isChecked : false
     */

    private boolean isChecked;
    /**
     * fid : 1015
     * FIndex : 1
     * fnumber : 9999.091.001
     * fname : ES仿威图柜
     * fmodel : 900*1700*500
     * FQty : 1.0
     * fdate : 2019-03-19
     * F_106 : 防护等级IP44,柜体及安装板材质碳钢喷川田标准色（方管，门锁，锁杆，铰链，柜门限位支撑件，加强筋空心螺母，吊环）以上材质为镀锌不喷涂，柜内所有固定螺栓，垫片，弹垫材质为不锈钢
     */

    private int     fid;
    private int    FIndex;
    private String fnumber;
    private String fname;
    private String fmodel;
    private double FQty;
    private String fdate;
    private String F_106;

    public boolean isIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getFIndex() {
        return FIndex;
    }

    public void setFIndex(int FIndex) {
        this.FIndex = FIndex;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFmodel() {
        return fmodel;
    }

    public void setFmodel(String fmodel) {
        this.fmodel = fmodel;
    }

    public double getFQty() {
        return FQty;
    }

    public void setFQty(double FQty) {
        this.FQty = FQty;
    }

    public String getFdate() {
        return fdate;
    }

    public void setFdate(String fdate) {
        this.fdate = fdate;
    }

    public String getF_106() {
        return F_106;
    }

    public void setF_106(String F_106) {
        this.F_106 = F_106;
    }
}

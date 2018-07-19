package com.bt.andy.rongbei.messegeInfo;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/17 17:08
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LiuZhuanInfo {

    /**
     * flzkno : FC009
     * fnumber : 2.001
     * fname : 零件A
     * fmodel :
     * fjhqty : 10.0
     * fszqty : 7.0
     * forderbillno : SEORD000005
     * fgongxu : 数控
     *
     * fgongxuno : 02
     * ficmobillno : WORK000006
     *
     * fopersn : 20
     */

    private String flzkno;//流转卡卡号
    private String fnumber;//图号
    private String fname;//名称
    private String fmodel;//规格
    private double fjhqty;//计划数
    private double fszqty;//实作数
    private String forderbillno;//项目单号
    private String fgongxu;//工序名称

    private String fgongxuno;//工序代码
    private String ficmobillno;//生产任务单号

    private int    fopersn;//工序号

    public String getFlzkno() {
        return flzkno;
    }

    public void setFlzkno(String flzkno) {
        this.flzkno = flzkno;
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

    public double getFjhqty() {
        return fjhqty;
    }

    public void setFjhqty(double fjhqty) {
        this.fjhqty = fjhqty;
    }

    public double getFszqty() {
        return fszqty;
    }

    public void setFszqty(double fszqty) {
        this.fszqty = fszqty;
    }

    public String getForderbillno() {
        return forderbillno;
    }

    public void setForderbillno(String forderbillno) {
        this.forderbillno = forderbillno;
    }

    public String getFgongxu() {
        return fgongxu;
    }

    public void setFgongxu(String fgongxu) {
        this.fgongxu = fgongxu;
    }

    public String getFgongxuno() {
        return fgongxuno;
    }

    public void setFgongxuno(String fgongxuno) {
        this.fgongxuno = fgongxuno;
    }

    public String getFicmobillno() {
        return ficmobillno;
    }

    public void setFicmobillno(String ficmobillno) {
        this.ficmobillno = ficmobillno;
    }

    public int getFopersn() {
        return fopersn;
    }

    public void setFopersn(int fopersn) {
        this.fopersn = fopersn;
    }
}

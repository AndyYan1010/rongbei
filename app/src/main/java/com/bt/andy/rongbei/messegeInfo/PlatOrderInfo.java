package com.bt.andy.rongbei.messegeInfo;

/**
 * @创建者 AndyYan
 * @创建时间 2019/3/26 10:31
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class PlatOrderInfo {

    /**
     * fid : 1030
     * fnote2 : 9999.091.001,9999.092.001
     * FNOTE : HD19070
     * FText : 201903-38
     */

    private int fid;
    private String fnote2;
    private String FNOTE;
    private String FText;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getFnote2() {
        return fnote2;
    }

    public void setFnote2(String fnote2) {
        this.fnote2 = fnote2;
    }

    public String getFNOTE() {
        return FNOTE;
    }

    public void setFNOTE(String FNOTE) {
        this.FNOTE = FNOTE;
    }

    public String getFText() {
        return FText;
    }

    public void setFText(String FText) {
        this.FText = FText;
    }
}

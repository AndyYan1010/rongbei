package com.bt.andy.rongbei.messegeInfo;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2019/3/8 15:50
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class SearchGXInfo {

    /**
     * FBillDate : 2019/2/19 0:00:00
     * FID : 1013
     * FModel :
     * FName : kingdee1
     * FNumber : 9999.087.kingdee1
     * FOrderBillNO : 789
     * FPlanAuxQty : 10.0000000000
     * operids : [{"fopernote":"激光切割","operidsEntry":[{"fauxqtypass":1,"fworkdate":"2019/2/19 0:00:00","fworkname1":"朱庆东","fworkname2":""},{"fauxqtypass":9,"fworkdate":"2019/2/19 0:00:00","fworkname1":"朱庆东","fworkname2":""}]},{"fopernote":"压铆","operidsEntry":[{"fauxqtypass":1,"fworkdate":"2019/2/19 0:00:00","fworkname1":"","fworkname2":""},{"fauxqtypass":9,"fworkdate":"2019/2/19 0:00:00","fworkname1":"","fworkname2":""}]},{"fopernote":"折弯","operidsEntry":[{"fauxqtypass":1,"fworkdate":"2019/2/19 0:00:00","fworkname1":"","fworkname2":""},{"fauxqtypass":9,"fworkdate":"2019/2/26 0:00:00","fworkname1":"","fworkname2":"毛伟"}]},{"fopernote":"焊接","operidsEntry":[{"fauxqtypass":1,"fworkdate":"2019/2/19 0:00:00","fworkname1":"","fworkname2":""},{"fauxqtypass":15,"fworkdate":"2019/2/26 0:00:00","fworkname1":"","fworkname2":"朱庆东"}]},{"fopernote":"打磨","operidsEntry":[{"fauxqtypass":1,"fworkdate":"2019/2/26 0:00:00","fworkname1":"","fworkname2":"朱庆东"}]},{"fopernote":"喷涂","operidsEntry":[{"fauxqtypass":0,"fworkdate":"","fworkname1":"","fworkname2":""}]}]
     */

    private String FBillDate;
    private int               FID;
    private String            FModel;
    private String            FName;
    private String            FNumber;
    private String            FOrderBillNO;
    private String            FPlanAuxQty;
    private List<OperidsBean> operids;

    public String getFBillDate() {
        return FBillDate;
    }

    public void setFBillDate(String FBillDate) {
        this.FBillDate = FBillDate;
    }

    public int getFID() {
        return FID;
    }

    public void setFID(int FID) {
        this.FID = FID;
    }

    public String getFModel() {
        return FModel;
    }

    public void setFModel(String FModel) {
        this.FModel = FModel;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getFNumber() {
        return FNumber;
    }

    public void setFNumber(String FNumber) {
        this.FNumber = FNumber;
    }

    public String getFOrderBillNO() {
        return FOrderBillNO;
    }

    public void setFOrderBillNO(String FOrderBillNO) {
        this.FOrderBillNO = FOrderBillNO;
    }

    public String getFPlanAuxQty() {
        return FPlanAuxQty;
    }

    public void setFPlanAuxQty(String FPlanAuxQty) {
        this.FPlanAuxQty = FPlanAuxQty;
    }

    public List<OperidsBean> getOperids() {
        return operids;
    }

    public void setOperids(List<OperidsBean> operids) {
        this.operids = operids;
    }

    public static class OperidsBean {
        /**
         * fopernote : 激光切割
         * operidsEntry : [{"fauxqtypass":1,"fworkdate":"2019/2/19 0:00:00","fworkname1":"朱庆东","fworkname2":""},{"fauxqtypass":9,"fworkdate":"2019/2/19 0:00:00","fworkname1":"朱庆东","fworkname2":""}]
         */

        private String fopernote;
        private List<OperidsEntryBean> operidsEntry;

        public String getFopernote() {
            return fopernote;
        }

        public void setFopernote(String fopernote) {
            this.fopernote = fopernote;
        }

        public List<OperidsEntryBean> getOperidsEntry() {
            return operidsEntry;
        }

        public void setOperidsEntry(List<OperidsEntryBean> operidsEntry) {
            this.operidsEntry = operidsEntry;
        }

        public static class OperidsEntryBean {
            /**
             * fauxqtypass : 1
             * fworkdate : 2019/2/19 0:00:00
             */

            private int fauxqtypass;
            private String fworkdate;
            /**
             * completed : 1
             */

            private String completed;

            public int getFauxqtypass() {
                return fauxqtypass;
            }

            public void setFauxqtypass(int fauxqtypass) {
                this.fauxqtypass = fauxqtypass;
            }

            public String getFworkdate() {
                return fworkdate;
            }

            public void setFworkdate(String fworkdate) {
                this.fworkdate = fworkdate;
            }

            public String getCompleted() {
                return completed;
            }

            public void setCompleted(String completed) {
                this.completed = completed;
            }
        }
    }
}

package com.zan99.guaizhangmen.Bean;

import java.util.List;

/**
 * Created by 99zan on 2018/2/7.
 */

public class WalletMxBean {

    /**
     * errcode : 0
     * totalpage : 1
     * dataList : [{"type":0,"time":"2018-02-06 11:05:07","fee":"0.1","touser":"0","nowlevel":"0","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-31 15:38:21","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 15:00:06","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 14:57:07","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 14:55:45","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 14:55:35","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 13:30:57","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 13:18:42","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 13:18:06","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 13:17:35","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 13:16:11","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-26 13:16:00","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"},{"type":1,"time":"2018-01-24 17:32:06","fee":"500","touser":"16","nowlevel":"1","member_id":26,"member_name":"田哥"}]
     */

    private int errcode;
    private int totalpage;
    private List<DataListBean> dataList;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public List<DataListBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataListBean> dataList) {
        this.dataList = dataList;
    }

    public static class DataListBean {
        /**
         * type : 0
         * time : 2018-02-06 11:05:07
         * fee : 0.1
         * touser : 0
         * nowlevel : 0
         * member_id : 26
         * member_name : 田哥
         */

        private int type;
        private String time;
        private String fee;
        private String touser;
        private String nowlevel;
        private int member_id;
        private String member_name;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getNowlevel() {
            return nowlevel;
        }

        public void setNowlevel(String nowlevel) {
            this.nowlevel = nowlevel;
        }

        public int getMember_id() {
            return member_id;
        }

        public void setMember_id(int member_id) {
            this.member_id = member_id;
        }

        public String getMember_name() {
            return member_name;
        }

        public void setMember_name(String member_name) {
            this.member_name = member_name;
        }
    }
}

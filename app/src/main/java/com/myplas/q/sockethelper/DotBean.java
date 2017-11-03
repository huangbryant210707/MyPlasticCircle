package com.myplas.q.sockethelper;

/**
 * 作者:huangshuang
 * 事件 2017/10/26 0026.
 * 邮箱： 15378412400@163.com
 */

public class DotBean {


    /**
     * err : 0
     * msg : 更新成功
     * data : {"is_socket_connected":"1","unread_supply_and_demand":"6","unread_customer":"2","unread_myorder":"0","unread_who_saw_me":"0","unread_recommend_update":"0","unread_reply_user_msg":"0","unread_reply_purchase_msg":"0","unread_plastic_msg":"0","unread_purchase_msg":"0"}
     */

    private int err;
    private String msg;
    private DataBean data;

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * is_socket_connected : 1
         * unread_supply_and_demand : 6
         * unread_customer : 2
         * unread_myorder : 0
         * unread_who_saw_me : 0
         * unread_recommend_update : 0
         * unread_reply_user_msg : 0
         * unread_reply_purchase_msg : 0
         * unread_plastic_msg : 0
         * unread_purchase_msg : 0
         */

        private String is_socket_connected;
        private String unread_supply_and_demand;
        private String unread_customer;
        private String unread_myorder;
        private String unread_who_saw_me;
        private String unread_recommend_update;
        private String unread_reply_user_msg;
        private String unread_reply_purchase_msg;
        private String unread_plastic_msg;
        private String unread_purchase_msg;

        public String getIs_socket_connected() {
            return is_socket_connected;
        }

        public void setIs_socket_connected(String is_socket_connected) {
            this.is_socket_connected = is_socket_connected;
        }

        public String getUnread_supply_and_demand() {
            return unread_supply_and_demand;
        }

        public void setUnread_supply_and_demand(String unread_supply_and_demand) {
            this.unread_supply_and_demand = unread_supply_and_demand;
        }

        public String getUnread_customer() {
            return unread_customer;
        }

        public void setUnread_customer(String unread_customer) {
            this.unread_customer = unread_customer;
        }

        public String getUnread_myorder() {
            return unread_myorder;
        }

        public void setUnread_myorder(String unread_myorder) {
            this.unread_myorder = unread_myorder;
        }

        public String getUnread_who_saw_me() {
            return unread_who_saw_me;
        }

        public void setUnread_who_saw_me(String unread_who_saw_me) {
            this.unread_who_saw_me = unread_who_saw_me;
        }

        public String getUnread_recommend_update() {
            return unread_recommend_update;
        }

        public void setUnread_recommend_update(String unread_recommend_update) {
            this.unread_recommend_update = unread_recommend_update;
        }

        public String getUnread_reply_user_msg() {
            return unread_reply_user_msg;
        }

        public void setUnread_reply_user_msg(String unread_reply_user_msg) {
            this.unread_reply_user_msg = unread_reply_user_msg;
        }

        public String getUnread_reply_purchase_msg() {
            return unread_reply_purchase_msg;
        }

        public void setUnread_reply_purchase_msg(String unread_reply_purchase_msg) {
            this.unread_reply_purchase_msg = unread_reply_purchase_msg;
        }

        public String getUnread_plastic_msg() {
            return unread_plastic_msg;
        }

        public void setUnread_plastic_msg(String unread_plastic_msg) {
            this.unread_plastic_msg = unread_plastic_msg;
        }

        public String getUnread_purchase_msg() {
            return unread_purchase_msg;
        }

        public void setUnread_purchase_msg(String unread_purchase_msg) {
            this.unread_purchase_msg = unread_purchase_msg;
        }
    }
}

package com.manager.direct.recyclerclient.models;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

    public class AccountInfo {

        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("balance")
        @Expose
        private String balance;
        @SerializedName("nonce")
        @Expose
        private String nonce;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }


}

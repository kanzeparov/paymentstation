package com.manager.direct.recyclerclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TxInfo {

    @SerializedName("txHash")
    @Expose
    private String txHash;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}

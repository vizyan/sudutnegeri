package com.qiscus.internship.sudutnegeri.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Vizyan on 1/14/2018.
 */

public class ResultListProject {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<DataProject> data = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String status) {
        this.message = message;
    }

    public List<DataProject> getData() {
        return data;
    }

    public void setData(List<DataProject> data) {
        this.data = data;
    }

}

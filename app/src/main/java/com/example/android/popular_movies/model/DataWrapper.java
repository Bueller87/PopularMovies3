package com.example.android.popular_movies.model;

public class DataWrapper<T>{
    private T data;
    private Integer errorStringId;

    private DataWrapper() {
        //hide default constructor
        //must use data or error constructor
    }

    public DataWrapper(T data) {
        this.data = data;
        this.errorStringId = null;
    }

    public DataWrapper(Integer errorMessage) {
        this.errorStringId = errorMessage;
        this.data = null;
    }
    public DataWrapper(T data, Integer errorMessage) {
        this.errorStringId = errorMessage;
        this.data = data;
    }

    public boolean getDeviceNoConnectivity() {
        return this.getErrMessage() == null && this.data == null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getErrMessage() {
        return errorStringId;
    }

    public void setErrMessage(Integer errMessage) {
        this.errorStringId = errMessage;
    }
}

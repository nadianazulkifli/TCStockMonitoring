/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */package com.tcstock.model;

public class StockCountSummary {
    private int sessionId;
    private String sessionName;
    private String countType;
    private String countDate;
    private String status;
    private int totalItemsCounted;
    private int varianceItems;

    public StockCountSummary() {
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getCountType() {
        return countType;
    }

    public void setCountType(String countType) {
        this.countType = countType;
    }

    public String getCountDate() {
        return countDate;
    }

    public void setCountDate(String countDate) {
        this.countDate = countDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalItemsCounted() {
        return totalItemsCounted;
    }

    public void setTotalItemsCounted(int totalItemsCounted) {
        this.totalItemsCounted = totalItemsCounted;
    }

    public int getVarianceItems() {
        return varianceItems;
    }

    public void setVarianceItems(int varianceItems) {
        this.varianceItems = varianceItems;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.model;

public class StockTransaction {
    private int id;
    private int itemId;
    private String itemCode;
    private String itemName;
    private int userId;
    private String userName;
    private String transactionType;

    private int qtyCtn;
    private int qtyPck;
    private int qtyPcs;
    private int quantityBase;

    private String uom;
    private String referenceNo;
    private String remarks;
    private String transactionDatetime;

    public StockTransaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }


    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }


    public int getQtyCtn() {
        return qtyCtn;
    }

    public void setQtyCtn(int qtyCtn) {
        this.qtyCtn = qtyCtn;
    }


    public int getQtyPck() {
        return qtyPck;
    }

    public void setQtyPck(int qtyPck) {
        this.qtyPck = qtyPck;
    }


    public int getQtyPcs() {
        return qtyPcs;
    }

    public void setQtyPcs(int qtyPcs) {
        this.qtyPcs = qtyPcs;
    }


    public int getQuantityBase() {
        return quantityBase;
    }

    public void setQuantityBase(int quantityBase) {
        this.quantityBase = quantityBase;
    }


    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }


    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getTransactionDatetime() {
        return transactionDatetime;
    }

    public void setTransactionDatetime(String transactionDatetime) {
        this.transactionDatetime = transactionDatetime;
    }
}
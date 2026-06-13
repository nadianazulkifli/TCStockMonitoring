/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
package com.tcstock.model;

public class StockCountDetail {
    private int id;
    private int sessionId;
    private int itemId;

    private String itemCode;
    private String itemName;
    private String baseUom;

    private int currentQuantity;
    private int unitsPerCtn;
    private int unitsPerPck;

    private int qtyCtn;
    private int qtyPck;
    private int qtyPcs;
    private int totalQuantityBase;
    private int varianceQty;

    private String remarks;

    public StockCountDetail() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
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


    public String getBaseUom() {
        return baseUom;
    }

    public void setBaseUom(String baseUom) {
        this.baseUom = baseUom;
    }


    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }


    public int getUnitsPerCtn() {
        return unitsPerCtn;
    }

    public void setUnitsPerCtn(int unitsPerCtn) {
        this.unitsPerCtn = unitsPerCtn;
    }


    public int getUnitsPerPck() {
        return unitsPerPck;
    }

    public void setUnitsPerPck(int unitsPerPck) {
        this.unitsPerPck = unitsPerPck;
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


    public int getTotalQuantityBase() {
        return totalQuantityBase;
    }

    public void setTotalQuantityBase(int totalQuantityBase) {
        this.totalQuantityBase = totalQuantityBase;
    }


    public int getVarianceQty() {
        return varianceQty;
    }

    public void setVarianceQty(int varianceQty) {
        this.varianceQty = varianceQty;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
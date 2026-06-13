/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.model;

public class Item {
    private int id;
    private String itemCode;
    private String itemName;
    private int categoryId;
    private String categoryName;
    private String packSizeDetails;
    private String baseUom;
    private int unitsPerCtn;
    private int unitsPerPck;
    private int reorderLevel;
    private int currentQuantity;
    private String status;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getPackSizeDetails() {
        return packSizeDetails;
    }

    public void setPackSizeDetails(String packSizeDetails) {
        this.packSizeDetails = packSizeDetails;
    }


    public String getBaseUom() {
        return baseUom;
    }

    public void setBaseUom(String baseUom) {
        this.baseUom = baseUom;
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


    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }


    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
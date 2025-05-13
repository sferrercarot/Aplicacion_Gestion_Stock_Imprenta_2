package com.example.aplicaciongestionstockimprenta;

public class Product {
    private int id;
    private String name;
    private int cantidad_stock;
    private boolean stock_bajo;

    public Product(int id, String name, int cantidad_stock, boolean stock_bajo) {
        this.id = id;
        this.name = name;
        this.cantidad_stock = cantidad_stock;
        this.stock_bajo = stock_bajo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCantidadStock() {
        return cantidad_stock;
    }

    public boolean isStockBajo() {
        return stock_bajo;
    }

    public void setCantidadStock(int cantidad_stock) {
        this.cantidad_stock = cantidad_stock;
    }
}

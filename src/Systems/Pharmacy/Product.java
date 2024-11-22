package Systems.Pharmacy;

import java.time.LocalDate;

public class Product {
    private String productId;
    private String name;
    private int quantity;
    private double unitPrice;
    private LocalDate expirationDate;

    public Product(String productId, String name, int quantity, double unitPrice, LocalDate expirationDate) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.expirationDate = expirationDate;
    }

    // Getters and setters for the attributes

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}
/*package Systems.Pharmacy;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Receipt {
    private String receiptNumber;
    private String hospitalId;
    private List<POSPanel.CartItem> cartItems;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private Date date;

    // Constructor that accepts double for totalPrice
    public Receipt(String receiptNumber, String hospitalId, List<POSPanel.CartItem> cartItems, double totalPrice, String paymentMethod) {
        this.receiptNumber = receiptNumber;
        this.hospitalId = hospitalId;
        this.cartItems = cartItems;
        this.totalPrice = BigDecimal.valueOf(totalPrice); // Convert double to BigDecimal
        this.paymentMethod = paymentMethod;
        this.date = new Date(); // Set to current date
    }

    // Constructor that accepts BigDecimal for totalPrice
    public Receipt(String receiptNumber, String hospitalId, List<POSPanel.CartItem> cartItems, BigDecimal totalPrice, String paymentMethod) {
        this.receiptNumber = receiptNumber;
        this.hospitalId = hospitalId;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.date = new Date(); // Set to current date
    }

    // Getters and other methods
    public String getReceiptNumber() {
        return receiptNumber;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public List<POSPanel.CartItem> getCartItems() {
        return cartItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Date getDate() {
        return date;
    }
}
*/
package model;

public class OrderItem {
    private int id;
    private int orderId;
    private int petId;
    private int quantity;

    public OrderItem() {}

    public OrderItem(int petId, int quantity) {
        this.petId = petId;
        this.quantity = quantity;
    }

    public OrderItem(int orderId, int petId, int quantity) {
        this.orderId = orderId;
        this.petId = petId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

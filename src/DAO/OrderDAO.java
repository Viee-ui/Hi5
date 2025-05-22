package DAO;

import database.ConnectDB;
import model.Order;
import model.OrderItem;

import java.sql.*;
import java.util.*;

public class OrderDAO {

    public static boolean addOrder(Order order) {
        Connection conn = ConnectDB.getConnection();
        PreparedStatement psOrder = null;
        PreparedStatement psItem = null;
        ResultSet rs = null;

        try {
            conn.setAutoCommit(false);

            String insertOrderSQL = "INSERT INTO orders (customer_id, order_date) VALUES (?, ?)";
            psOrder = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, order.getCustomerId());
            psOrder.setTimestamp(2, new Timestamp(order.getOrderDate().getTime()));
            psOrder.executeUpdate();

            rs = psOrder.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                order.setId(orderId);

                String insertItemSQL = "INSERT INTO order_items (order_id, pet_id, quantity) VALUES (?, ?, ?)";
                psItem = conn.prepareStatement(insertItemSQL);

                for (OrderItem item : order.getItems()) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getPetId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.addBatch();
                }

                psItem.executeBatch();
                conn.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psItem != null) psItem.close();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // ✅ Lấy danh sách đơn hàng
    public static List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String orderSQL = "SELECT * FROM orders";
        String itemSQL = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement psOrder = conn.prepareStatement(orderSQL);
             ResultSet rsOrder = psOrder.executeQuery()) {

            while (rsOrder.next()) {
                int orderId = rsOrder.getInt("id");
                int customerId = rsOrder.getInt("customer_id");
              java.util.Date orderDate = rsOrder.getTimestamp("order_date");

                List<OrderItem> items = new ArrayList<>();
                try (PreparedStatement psItem = conn.prepareStatement(itemSQL)) {
                    psItem.setInt(1, orderId);
                    try (ResultSet rsItem = psItem.executeQuery()) {
                        while (rsItem.next()) {
                            int petId = rsItem.getInt("pet_id");
                            int quantity = rsItem.getInt("quantity");
                            items.add(new OrderItem(petId, quantity));
                        }
                    }
                }

                list.add(new Order(orderId, customerId, orderDate, items));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🗑️ Xoá đơn hàng và item liên quan
    public static boolean deleteOrder(int orderId) {
        String deleteItems = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrder = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement psItems = conn.prepareStatement(deleteItems);
             PreparedStatement psOrder = conn.prepareStatement(deleteOrder)) {

            conn.setAutoCommit(false);

            psItems.setInt(1, orderId);
            psItems.executeUpdate();

            psOrder.setInt(1, orderId);
            int affected = psOrder.executeUpdate();

            conn.commit();
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

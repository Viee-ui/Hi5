package view;

import DAO.CustomerDAO;
import DAO.OrderDAO;
import DAO.PetDAO;
import model.Customer;
import model.Order;
import model.OrderItem;
import model.Pet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class QuanLyDonHang extends JFrame {
    private JComboBox<Customer> cboCustomer;
    private JComboBox<Pet> cboPet;
    private JTextField txtQuantity;
    private DefaultListModel<String> listModel;
    private List<OrderItem> orderItems;
    private JTable tblOrders;
    private DefaultTableModel tableModel;

    public QuanLyDonHang() {
        setTitle("Quản lý đơn hàng");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        orderItems = new ArrayList<>();
        listModel = new DefaultListModel<>();

        // Panel chọn khách + thú + số lượng
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        cboCustomer = new JComboBox<>(CustomerDAO.getAllCustomers().toArray(new Customer[0]));
        cboPet = new JComboBox<>(PetDAO.getAllPets().toArray(new Pet[0]));
        txtQuantity = new JTextField();

        topPanel.add(new JLabel("Chọn khách hàng:"));
        topPanel.add(cboCustomer);
        topPanel.add(new JLabel("Chọn thú cưng:"));
        topPanel.add(cboPet);
        topPanel.add(new JLabel("Số lượng:"));
        topPanel.add(txtQuantity);
        topPanel.setBorder(BorderFactory.createTitledBorder("Tạo đơn hàng mới"));
        add(topPanel, BorderLayout.NORTH);

        // Panel thêm vào đơn
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel addPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Thêm vào đơn");
        addPanel.add(btnAdd);

        JList<String> itemList = new JList<>(listModel);
        JScrollPane scrollPaneList = new JScrollPane(itemList);

        centerPanel.add(addPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPaneList, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn hàng hiện tại"));
        add(centerPanel, BorderLayout.WEST);

        // Panel lưu đơn và hiển thị bảng
        JPanel rightPanel = new JPanel(new BorderLayout());

        JButton btnSave = new JButton("Lưu đơn hàng");
        JButton btnDelete = new JButton("Xoá đơn đã chọn");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSave);
        btnPanel.add(btnDelete);

        // Bảng đơn hàng
        tblOrders = new JTable();
        tableModel = new DefaultTableModel(new String[]{"ID", "Khách hàng", "Ngày tạo", "Tổng mục"}, 0);
        tblOrders.setModel(tableModel);
        JScrollPane scrollPaneTable = new JScrollPane(tblOrders);

        rightPanel.add(scrollPaneTable, BorderLayout.CENTER);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Danh sách đơn hàng"));
        add(rightPanel, BorderLayout.CENTER);

        // Sự kiện
        btnAdd.addActionListener(e -> addOrderItem());
        btnSave.addActionListener(e -> saveOrder());
        btnDelete.addActionListener(e -> deleteOrder());

        // Tải đơn hàng ban đầu
        loadOrders();
    }

    private void addOrderItem() {
        Pet pet = (Pet) cboPet.getSelectedItem();
        if (pet == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thú cưng!");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
            return;
        }

        boolean found = false;
        for (OrderItem item : orderItems) {
            if (item.getPetId() == pet.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            orderItems.add(new OrderItem(pet.getId(), quantity));
        }

        updateItemList();
        txtQuantity.setText("");
    }

    private void updateItemList() {
        listModel.clear();
        for (OrderItem item : orderItems) {
            Pet p = PetDAO.getPetById(item.getPetId());
            listModel.addElement(p.getName() + " x " + item.getQuantity());
        }
    }

    private void saveOrder() {
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có thú nào trong đơn hàng!");
            return;
        }

        Customer customer = (Customer) cboCustomer.getSelectedItem();
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!");
            return;
        }

        Order order = new Order(customer.getId(), new Date(), orderItems);

        if (OrderDAO.addOrder(order)) {
            JOptionPane.showMessageDialog(this, "Thêm đơn hàng thành công!");
            orderItems.clear();
            listModel.clear();
            txtQuantity.setText("");
            cboCustomer.setSelectedIndex(0);
            cboPet.setSelectedIndex(0);
            loadOrders();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn hàng!");
        }
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        List<Order> orders = OrderDAO.getAllOrders();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Order o : orders) {
            Customer c = CustomerDAO.getCustomerById(o.getCustomerId());
            tableModel.addRow(new Object[]{
                    o.getId(),
                    c != null ? c.getName() : "Không rõ",
                    sdf.format(o.getOrderDate()),
                    o.getItems().size()
            });
        }
    }

    private void deleteOrder() {
        int selectedRow = tblOrders.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần xoá.");
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xoá đơn hàng #" + orderId + "?",
                "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (OrderDAO.deleteOrder(orderId)) {
                JOptionPane.showMessageDialog(this, "Xoá thành công!");
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Xoá thất bại!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyDonHang().setVisible(true));
    }
}

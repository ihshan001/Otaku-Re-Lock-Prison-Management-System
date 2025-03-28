import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PrisonManagement extends JFrame {
    private JTextField txtName;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd, btnUpdate, btnDelete, btnToggle;
    private int selectedPrisonId = -1;

    public PrisonManagement() {
        setTitle("Prison Management");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel panelInput = new JPanel(new GridLayout(1, 2, 10, 10));
        txtName = new JTextField();
        panelInput.add(new JLabel("Prison Name:"));
        panelInput.add(txtName);

        // Button Panel
        JPanel panelButtons = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add Prison");
        btnUpdate = new JButton("Update Prison");
        btnDelete = new JButton("Delete Prison");
        btnToggle = new JButton("Toggle Status");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnToggle);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Date Created", "Name", "Status"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(panelInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        loadPrisons();

        // Event Listeners
        btnAdd.addActionListener(e -> addPrison());
        btnUpdate.addActionListener(e -> updatePrison());
        btnDelete.addActionListener(e -> deletePrison());
        btnToggle.addActionListener(e -> toggleStatus());

        table.getSelectionModel().addListSelectionListener(event -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedPrisonId = (int) table.getValueAt(row, 0);
                txtName.setText((String) table.getValueAt(row, 2));
            }
        });
    }

    private void loadPrisons() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') AS date_created, name, status FROM prisons ORDER BY date_created DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("date_created"),
                        rs.getString("name"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPrison() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO prisons (name, created_at, status) VALUES (?, NOW(), 'Active')")) {
            ps.setString(1, name);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Prison added successfully!");
            txtName.setText("");
            loadPrisons();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePrison() {
        if (selectedPrisonId == -1) {
            JOptionPane.showMessageDialog(this, "Select a prison to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE prisons SET name=? WHERE id=?")) {
            ps.setString(1, name);
            ps.setInt(2, selectedPrisonId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Prison updated successfully!");
            txtName.setText("");
            selectedPrisonId = -1;
            loadPrisons();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deletePrison() {
        if (selectedPrisonId == -1) {
            JOptionPane.showMessageDialog(this, "Select a prison to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this prison?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM prisons WHERE id=?")) {
                ps.setInt(1, selectedPrisonId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Prison deleted successfully!");
                txtName.setText("");
                selectedPrisonId = -1;
                loadPrisons();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleStatus() {
        if (selectedPrisonId == -1) {
            JOptionPane.showMessageDialog(this, "Select a prisons to toggle status!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE prisons SET status = IF(LOWER(status) = 'active', 'Inactive', 'Active') WHERE id=?")) {
            ps.setInt(1, selectedPrisonId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Prison status updated!");
            selectedPrisonId = -1;
            loadPrisons();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrisonManagement().setVisible(true));
    }
}

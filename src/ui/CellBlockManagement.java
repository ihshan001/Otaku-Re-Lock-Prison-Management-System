import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class CellBlockManagement extends JFrame {

    private JTextField txtName;
    private JComboBox<String> comboPrison;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd, btnUpdate, btnDelete, btnToggle;
    private int selectedCellBlockId = -1;

    public CellBlockManagement() {
        setTitle("Cell Block Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel panelInput = new JPanel(new GridLayout(2, 2, 10, 10));
        
        comboPrison = new JComboBox<>();
        txtName = new JTextField();

        panelInput.add(new JLabel("Prison:"));
        panelInput.add(comboPrison);
        panelInput.add(new JLabel("Cell Block Name:"));
        panelInput.add(txtName);

        // Button Panel
        JPanel panelButtons = new JPanel(new FlowLayout());
        
        btnAdd = new JButton("Add Cell Block");
        btnUpdate = new JButton("Update Cell Block");
        btnDelete = new JButton("Delete Cell Block");
        btnToggle = new JButton("Toggle Status");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnToggle);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Date Created", "Prison", "Name", "Status"}, 0);
        table = new JTable(model);
        
        JScrollPane scrollPane = new JScrollPane(table);

        add(panelInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        loadPrisons();
        loadCellBlocks();

        // Event Listeners
        btnAdd.addActionListener(e -> addCellBlock());
        btnUpdate.addActionListener(e -> updateCellBlock());
        btnDelete.addActionListener(e -> deleteCellBlock());
        btnToggle.addActionListener(e -> toggleStatus());

        table.getSelectionModel().addListSelectionListener(event -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedCellBlockId = (int) table.getValueAt(row, 0);
                comboPrison.setSelectedItem(table.getValueAt(row, 2));
                txtName.setText((String) table.getValueAt(row, 3));
            }
        });

        // Set Font and Color
        Font font = new Font("Source Sans 3 SemiBold", Font.BOLD, 14);
        

        panelInput.setFont(font);
        panelButtons.setFont(font);
        table.setFont(font);
        btnAdd.setFont(font);
        btnUpdate.setFont(font);
        btnDelete.setFont(font);
        btnToggle.setFont(font);

        
    }

    private void loadPrisons() {
        comboPrison.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, name FROM prisons");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                comboPrison.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading prisons: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCellBlocks() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT cb.id, cb.created_at, p.name AS prison_name, cb.name AS cell_block_name, cb.status " +
                 "FROM cell_blocks cb " +
                 "JOIN prisons p ON cb.prison_id = p.id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getTimestamp("created_at"),
                        rs.getString("prison_name"),
                        rs.getString("cell_block_name"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cell blocks: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCellBlock() {
        String prisonData = (String) comboPrison.getSelectedItem();
        String name = txtName.getText().trim();

        if (prisonData == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a prison and enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int prisonId = Integer.parseInt(prisonData.split(" - ")[0]);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO cell_blocks (prison_id, name, created_at, status) VALUES (?, ?, NOW(), 'Active')")) {
            ps.setInt(1, prisonId);
            ps.setString(2, name);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cell Block added successfully!");
            txtName.setText("");
            loadCellBlocks();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding cell block: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCellBlock() {
        if (selectedCellBlockId == -1) {
            JOptionPane.showMessageDialog(this, "Select a cell block to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String prisonData = (String) comboPrison.getSelectedItem();
        String name = txtName.getText().trim();

        if (prisonData == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a prison and enter a valid name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int prisonId = Integer.parseInt(prisonData.split(" - ")[0]);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE cell_blocks SET prison_id=?, name=? WHERE id=?")) {
            ps.setInt(1, prisonId);
            ps.setString(2, name);
            ps.setInt(3, selectedCellBlockId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cell Block updated successfully!");
            txtName.setText("");
            selectedCellBlockId = -1;
            loadCellBlocks();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating cell block: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCellBlock() {
        if (selectedCellBlockId == -1) {
            JOptionPane.showMessageDialog(this, "Select a cell block to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this cell block?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM cell_blocks WHERE id=?")) {
                ps.setInt(1, selectedCellBlockId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cell Block deleted successfully!");
                txtName.setText("");
                selectedCellBlockId = -1;
                loadCellBlocks();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting cell block: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleStatus() {
        if (selectedCellBlockId == -1) {
            JOptionPane.showMessageDialog(this, "Select a cell block to toggle status!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE cell_blocks SET status = CASE WHEN status='Active' THEN 'Inactive' ELSE 'Active' END WHERE id=?")) {
            ps.setInt(1, selectedCellBlockId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cell Block status updated!");
            selectedCellBlockId = -1;
            loadCellBlocks();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        
        FlatCarbonIJTheme.setup();
        SwingUtilities.invokeLater(() -> new CellBlockManagement().setVisible(true));
    }
}
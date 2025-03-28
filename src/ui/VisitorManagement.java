import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VisitorManagement extends JFrame {
    private JTable visitorTable;
    private JTextField txtVisitorName, txtContact, txtRelation;
    private JComboBox<String> comboInmate;
    private JButton btnAddVisitor, btnEditVisitor, btnDeleteVisitor;
    private int selectedVisitorId = -1;

    public VisitorManagement() {
        // Set up frame properties
        setTitle("Visitor Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        txtVisitorName = new JTextField(20);
        txtContact = new JTextField(20);
        txtRelation = new JTextField(20);
        comboInmate = new JComboBox<>();
        btnAddVisitor = new JButton("Add Visitor");
        btnEditVisitor = new JButton("Edit Visitor");
        btnDeleteVisitor = new JButton("Delete Visitor");

        // Table to display visitor records
        visitorTable = new JTable();
        visitorTable.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "Visitor Name", "Contact", "Relation", "Inmate" }
        ));
        JScrollPane scrollPane = new JScrollPane(visitorTable);

        // Set up input panel for visitor details
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Visitor Name:"));
        inputPanel.add(txtVisitorName);
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(txtContact);
        inputPanel.add(new JLabel("Relation:"));
        inputPanel.add(txtRelation);
        inputPanel.add(new JLabel("Inmate:"));
        inputPanel.add(comboInmate);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAddVisitor);
        buttonPanel.add(btnEditVisitor);
        buttonPanel.add(btnDeleteVisitor);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load Inmates for ComboBox
        loadInmates();

        // Add Action Listeners for buttons
        btnAddVisitor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addVisitor();
            }
        });
        btnEditVisitor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editVisitor();
            }
        });
        btnDeleteVisitor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteVisitor();
            }
        });

        // Table selection listener to view and edit selected visitor
        visitorTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = visitorTable.getSelectedRow();
                if (row != -1) {
                    selectedVisitorId = (int) visitorTable.getValueAt(row, 0);
                    txtVisitorName.setText((String) visitorTable.getValueAt(row, 1));
                    txtContact.setText((String) visitorTable.getValueAt(row, 2));
                    txtRelation.setText((String) visitorTable.getValueAt(row, 3));
                }
            }
        });
    }

    // Load Inmates into ComboBox
    private void loadInmates() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, first_name, last_name FROM inmate WHERE visitor_privilege = true");
             ResultSet rs = ps.executeQuery()) {
            comboInmate.removeAllItems();
            while (rs.next()) {
                int inmateId = rs.getInt("id");
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                comboInmate.addItem(inmateId + " - " + fullName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add Visitor
    private void addVisitor() {
        String visitorName = txtVisitorName.getText().trim();
        String contact = txtContact.getText().trim();
        String relation = txtRelation.getText().trim();
        int inmateId = Integer.parseInt(((String) comboInmate.getSelectedItem()).split(" - ")[0]);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO visitor (visitor_name, contact, relation, inmate_id) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, visitorName);
            ps.setString(2, contact);
            ps.setString(3, relation);
            ps.setInt(4, inmateId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Visitor Added!");
            loadVisitors();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Edit Visitor
    private void editVisitor() {
        if (selectedVisitorId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a visitor to edit.");
            return;
        }
        String visitorName = txtVisitorName.getText().trim();
        String contact = txtContact.getText().trim();
        String relation = txtRelation.getText().trim();
        int inmateId = Integer.parseInt(((String) comboInmate.getSelectedItem()).split(" - ")[0]);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE visitor SET visitor_name = ?, contact = ?, relation = ?, inmate_id = ? WHERE id = ?")) {
            ps.setString(1, visitorName);
            ps.setString(2, contact);
            ps.setString(3, relation);
            ps.setInt(4, inmateId);
            ps.setInt(5, selectedVisitorId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Visitor Updated!");
            loadVisitors();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete Visitor
    private void deleteVisitor() {
        if (selectedVisitorId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a visitor to delete.");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM visitor WHERE id = ?")) {
            ps.setInt(1, selectedVisitorId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Visitor Deleted!");
            loadVisitors();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load Visitors into Table
    private void loadVisitors() {
        DefaultTableModel model = (DefaultTableModel) visitorTable.getModel();
        model.setRowCount(0); // Clear existing rows
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT v.id, v.visitor_name, v.contact, v.relation, i.first_name, i.last_name FROM visitor v JOIN inmate i ON v.inmate_id = i.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int visitorId = rs.getInt("id");
                String visitorName = rs.getString("visitor_name");
                String contact = rs.getString("contact");
                String relation = rs.getString("relation");
                String inmateFullName = rs.getString("first_name") + " " + rs.getString("last_name");
                model.addRow(new Object[] { visitorId, visitorName, contact, relation, inmateFullName });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new VisitorManagement().setVisible(true);
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ActionManagement extends JFrame {
    private JTable actionTable;
    private JTextField txtActionName;
    private JComboBox<String> comboStatus;
    private JButton btnAddAction, btnEditAction, btnDeleteAction;
    private int selectedActionId = -1;

    public ActionManagement() {
        // Set up frame properties
        setTitle("Action Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        txtActionName = new JTextField(20);
        comboStatus = new JComboBox<>(new String[] { "Active", "Inactive" });
        btnAddAction = new JButton("Add Action");
        btnEditAction = new JButton("Edit Action");
        btnDeleteAction = new JButton("Delete Action");

        // Table to display action records
        actionTable = new JTable();
        actionTable.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "Action Name", "Status" }
        ));
        JScrollPane scrollPane = new JScrollPane(actionTable);

        // Set up input panel for action details
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Action Name:"));
        inputPanel.add(txtActionName);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(comboStatus);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAddAction);
        buttonPanel.add(btnEditAction);
        buttonPanel.add(btnDeleteAction);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add Action Listeners for buttons
        btnAddAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addAction();
            }
        });
        btnEditAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editAction();
            }
        });
        btnDeleteAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAction();
            }
        });

        // Table selection listener to view and edit selected action
        actionTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = actionTable.getSelectedRow();
                if (row != -1) {
                    selectedActionId = (int) actionTable.getValueAt(row, 0);
                    txtActionName.setText((String) actionTable.getValueAt(row, 1));
                    comboStatus.setSelectedItem(actionTable.getValueAt(row, 2));
                }
            }
        });
    }

    // Add Action
    private void addAction() {
        String actionName = txtActionName.getText().trim();
        String status = (String) comboStatus.getSelectedItem();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO action (action_name, status) VALUES (?, ?)")) {
            ps.setString(1, actionName);
            ps.setString(2, status);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Action Added!");
            loadActions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Edit Action
    private void editAction() {
        if (selectedActionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an action to edit.");
            return;
        }
        String actionName = txtActionName.getText().trim();
        String status = (String) comboStatus.getSelectedItem();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE action SET action_name = ?, status = ? WHERE id = ?")) {
            ps.setString(1, actionName);
            ps.setString(2, status);
            ps.setInt(3, selectedActionId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Action Updated!");
            loadActions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete Action
    private void deleteAction() {
        if (selectedActionId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an action to delete.");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM action WHERE id = ?")) {
            ps.setInt(1, selectedActionId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Action Deleted!");
            loadActions();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load Actions into Table
    private void loadActions() {
        DefaultTableModel model = (DefaultTableModel) actionTable.getModel();
        model.setRowCount(0); // Clear existing rows
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, action_name, status FROM action");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int actionId = rs.getInt("id");
                String actionName = rs.getString("action_name");
                String status = rs.getString("status");
                model.addRow(new Object[] { actionId, actionName, status });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ActionManagement().setVisible(true);
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CrimeManagement extends JFrame {

    private JTable crimeTable;
    private JTextField txtCrimeName;
    private JComboBox<String> comboStatus;
    private JButton btnAddCrime, btnEditCrime, btnDeleteCrime;
    private int selectedCrimeId = -1;

    public CrimeManagement() {
        setTitle("Crime Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color bgColor = new Color(30, 30, 30);
        Color panelColor = new Color(45, 45, 45);
        Color textColor = Color.WHITE;
        Color btnColor = new Color(70, 130, 180);
        Color hoverColor = new Color(90, 150, 200);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        add(mainPanel);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(panelColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblCrimeName = new JLabel("Crime Name:");
        lblCrimeName.setForeground(textColor);
        txtCrimeName = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.drawString("Enter Crime Name", 5, 20);
                }
            }
        };

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setForeground(textColor);
        comboStatus = new JComboBox<>(new String[]{"active", "inactive"}) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSelectedItem() == null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.drawString("Select Status", 5, 20);
                }
            }
        };

        inputPanel.add(lblCrimeName);
        inputPanel.add(txtCrimeName);
        inputPanel.add(lblStatus);
        inputPanel.add(comboStatus);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        crimeTable = new JTable();
        crimeTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Crime Name", "Status", "Created At"}));
        crimeTable.setRowHeight(25);
        crimeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        crimeTable.getTableHeader().setBackground(btnColor);
        crimeTable.getTableHeader().setForeground(Color.WHITE);
        crimeTable.setBackground(panelColor);
        crimeTable.setForeground(textColor);
        crimeTable.setSelectionBackground(btnColor);
        crimeTable.setSelectionForeground(Color.WHITE);
        crimeTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(crimeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);

        btnAddCrime = createStyledButton("Add Crime", btnColor, hoverColor);
        btnEditCrime = createStyledButton("Edit Crime", btnColor, hoverColor);
        btnDeleteCrime = createStyledButton("Delete Crime", new Color(178, 34, 34), new Color(208, 54, 54));

        buttonPanel.add(btnAddCrime);
        buttonPanel.add(btnEditCrime);
        buttonPanel.add(btnDeleteCrime);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnAddCrime.addActionListener(e -> addCrime());
        btnEditCrime.addActionListener(e -> editCrime());
        btnDeleteCrime.addActionListener(e -> deleteCrime());

        crimeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = crimeTable.getSelectedRow();
                if (row != -1) {
                    selectedCrimeId = (int) crimeTable.getValueAt(row, 0);
                    txtCrimeName.setText((String) crimeTable.getValueAt(row, 1));
                    comboStatus.setSelectedItem(crimeTable.getValueAt(row, 2));
                }
            }
        });

        loadCrimes();

        // Animation
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtCrimeName.getText().isEmpty()) {
                    txtCrimeName.setBackground(panelColor);
                } else {
                    txtCrimeName.setBackground(Color.WHITE);
                }
                if (comboStatus.getSelectedItem() == null) {
                    comboStatus.setBackground(panelColor);
                } else {
                    comboStatus.setBackground(Color.WHITE);
                }
            }
        });
        timer.start();
    }

    private JButton createStyledButton(String text, Color color, Color hoverColor) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void addCrime() {
        String crimeName = txtCrimeName.getText().trim();
        String status = (String) comboStatus.getSelectedItem();
        if (crimeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Crime Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement("SELECT COUNT(*) FROM crimes WHERE name = ?")) {
            checkPs.setString(1, crimeName);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Crime Name already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO crimes (name, status) VALUES (?, ?)")) {
            ps.setString(1, crimeName);
            ps.setString(2, status);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Crime Added!");
            loadCrimes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editCrime() {
        if (selectedCrimeId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a crime to edit.");
            return;
        }
        String crimeName = txtCrimeName.getText().trim();
        String status = (String) comboStatus.getSelectedItem();
        if (crimeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Crime Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE crimes SET name = ?, status = ? WHERE crime_id = ?")) {
            ps.setString(1, crimeName);
            ps.setString(2, status);
            ps.setInt(3, selectedCrimeId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Crime Updated!");
            loadCrimes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCrime() {
        if (selectedCrimeId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a crime to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this crime?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement checkPs = con.prepareStatement("SELECT COUNT(*) FROM inmates WHERE crime_id = ?")) {
            checkPs.setInt(1, selectedCrimeId);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Cannot delete! Crime is linked to inmates.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM crimes WHERE crime_id = ?")) {
            ps.setInt(1, selectedCrimeId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Crime Deleted!");
            loadCrimes();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCrimes() {
        DefaultTableModel model = (DefaultTableModel) crimeTable.getModel();
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT crime_id, name, status, created_at FROM crimes");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("crime_id"), rs.getString("name"), rs.getString("status"), rs.getTimestamp("created_at")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtCrimeName.setText("");
        selectedCrimeId = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrimeManagement().setVisible(true));
    }
}
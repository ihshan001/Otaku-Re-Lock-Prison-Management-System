import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import javax.imageio.ImageIO;

public class UserManagement extends JFrame {
    private JTable userTable;
    private JTextField txtFirstName, txtLastName, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboUserRole;
    private JButton btnAddUser, btnEditUser, btnDeleteUser;
    private JLabel lblImage;
    private int selectedUserId = -1;
    private byte[] userImage;

    public UserManagement() {
        setTitle("User Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ** Form Panel **
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        comboUserRole = new JComboBox<>(new String[]{"Admin", "Staff"});

        lblImage = new JLabel("Upload Image", SwingConstants.CENTER);
        lblImage.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblImage.setPreferredSize(new Dimension(120, 120));

        // ** Add components to form panel **
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtFirstName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtLastName, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(comboUserRole, gbc);
        
        // Image Upload Section
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(lblImage, gbc);

        // ** Buttons Panel **
        JPanel buttonPanel = new JPanel();
        btnAddUser = new JButton("Add User");
        btnEditUser = new JButton("Edit User");
        btnDeleteUser = new JButton("Delete User");
        buttonPanel.add(btnAddUser);
        buttonPanel.add(btnEditUser);
        buttonPanel.add(btnDeleteUser);

        // ** Table Panel **
        userTable = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"ID", "First Name", "Last Name", "Username", "Role", "Image"}) {
            public Class<?> getColumnClass(int column) {
                return (column == 5) ? ImageIcon.class : Object.class;
            }
        });
        userTable.setRowHeight(50);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // ** Add panels to frame **
        add(formPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ** Load users at startup **
        loadUsers();

        // ** Event Listeners **
        btnAddUser.addActionListener(e -> addUser());
        btnEditUser.addActionListener(e -> editUser());
        btnDeleteUser.addActionListener(e -> deleteUser());

        userTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = userTable.getSelectedRow();
                if (row != -1) {
                    selectedUserId = (int) userTable.getValueAt(row, 0);
                    txtFirstName.setText((String) userTable.getValueAt(row, 1));
                    txtLastName.setText((String) userTable.getValueAt(row, 2));
                    txtUsername.setText((String) userTable.getValueAt(row, 3));
                    comboUserRole.setSelectedItem(userTable.getValueAt(row, 4));
                    lblImage.setIcon((ImageIcon) userTable.getValueAt(row, 5));
                }
            }
        });

        lblImage.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        userImage = Files.readAllBytes(fileChooser.getSelectedFile().toPath());
                        lblImage.setIcon(resizeImage(userImage, 120, 120));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private ImageIcon resizeImage(byte[] imageBytes, int width, int height) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            Image img = ImageIO.read(bis).getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addUser() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) comboUserRole.getSelectedItem();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO users (first_name, last_name, username, password, role, image) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setBytes(6, userImage);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Added!");
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
     // Delete User
    private void deleteUser() {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, selectedUserId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Deleted!");
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Edit User
    private void editUser() {
        if (selectedUserId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = (String) comboUserRole.getSelectedItem();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE users SET first_name=?, last_name=?, username=?, password=?, role=?, image=? WHERE id=?")) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setBytes(6, userImage);
            ps.setInt(7, selectedUserId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User Updated!");
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadUsers() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, first_name, last_name, username, role, image FROM users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int userId = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String username = rs.getString("username");
                String role = rs.getString("role");
                byte[] imgBytes = rs.getBytes("image");
                ImageIcon userIcon = (imgBytes != null) ? resizeImage(imgBytes, 50, 50) : new ImageIcon();

                model.addRow(new Object[]{userId, firstName, lastName, username, role, userIcon});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
     FlatArcDarkIJTheme.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserManagement().setVisible(true);
            }
        });
    }
}

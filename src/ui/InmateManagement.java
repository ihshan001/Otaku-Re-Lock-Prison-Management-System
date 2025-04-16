import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightContrastIJTheme;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class InmateManagement extends JFrame {

    private JTextField txtCode, txtFirstName, txtLastName, txtAddress, txtComplexion, txtEyeColor;
    private JTextField txtSentence, txtEmergencyContact, txtEmergencyRelation, txtEmergencyPhone;
    private JComboBox<String> comboPrison, comboCellBlock, comboCrime, comboMaritalStatus, comboStatus;
    private JDateChooser birthDateChooser, timeServedChooser, timeEndChooser;
    private JCheckBox checkVisitorPrivilege;
    private JButton btnAdd, btnUpdate, btnDelete, btnUploadImage, btnReset;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblImage;
    private String selectedImagePath = null;
    private int selectedInmateId = -1;

    public InmateManagement() {

        // Create the UI components
        JPanel panelInput = new JPanel(new BorderLayout());
        panelInput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.decode("#333333")), "Inmate Details", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial Black", Font.BOLD, 18)));

        // Image Panel
        JPanel panelImage = new JPanel(new BorderLayout());
        panelImage.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.decode("#333333")), "Image", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial Black", Font.BOLD, 18)));

        lblImage = new JLabel("No Image", SwingConstants.CENTER);
        lblImage.setBorder(BorderFactory.createLineBorder(Color.decode("#333333"), 2));
        lblImage.setOpaque(true);
        
        lblImage.setPreferredSize(new Dimension(150, 150));

        btnUploadImage = new JButton("Upload Image");
//        addButtonHoverEffect(btnUploadImage);

        JPanel panelImageFields = new JPanel(new FlowLayout());
        panelImageFields.add(lblImage);
        panelImageFields.add(btnUploadImage);

        panelImage.add(panelImageFields, BorderLayout.CENTER);

        // Input Fields Panel
        JPanel panelInputFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input Fields
        txtCode = new JTextField();
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtAddress = new JTextField();
        txtComplexion = new JTextField();
        txtEyeColor = new JTextField();
        txtSentence = new JTextField();
        txtEmergencyContact = new JTextField();
        txtEmergencyRelation = new JTextField();
        txtEmergencyPhone = new JTextField();

        comboPrison = new JComboBox<>();
        comboCellBlock = new JComboBox<>();
        comboCrime = new JComboBox<>();
        comboMaritalStatus = new JComboBox<>(new String[]{"single", "married", "divorced", "widowed"});
        comboStatus = new JComboBox<>(new String[]{"active", "inactive", "released"});

        birthDateChooser = new JDateChooser();
        timeServedChooser = new JDateChooser();
        timeEndChooser = new JDateChooser();

        checkVisitorPrivilege = new JCheckBox("Visitor Privilege");

        // Arrange Components in GridBagLayout
        int y = 0; // Row index

        addComponent(panelInputFields, gbc, new JLabel("Code:"), 0, y);
        addComponent(panelInputFields, gbc, txtCode, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Prison:"), 2, y);
        addComponent(panelInputFields, gbc, comboPrison, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Cell Block:"), 0, y);
        addComponent(panelInputFields, gbc, comboCellBlock, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("First Name:"), 2, y);
        addComponent(panelInputFields, gbc, txtFirstName, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Last Name:"), 0, y);
        addComponent(panelInputFields, gbc, txtLastName, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Birth Date:"), 2, y);
        addComponent(panelInputFields, gbc, birthDateChooser, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Address:"), 0, y);
        addComponent(panelInputFields, gbc, txtAddress, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Marital Status:"), 2, y);
        addComponent(panelInputFields, gbc, comboMaritalStatus, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Complexion:"), 0, y);
        addComponent(panelInputFields, gbc, txtComplexion, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Eye Color:"), 2, y);
        addComponent(panelInputFields, gbc, txtEyeColor, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Crime:"), 0, y);
        addComponent(panelInputFields, gbc, comboCrime, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Sentence:"), 2, y);
        addComponent(panelInputFields, gbc, txtSentence, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Time Served:"), 0, y);
        addComponent(panelInputFields, gbc, timeServedChooser, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Time End:"), 2, y);
        addComponent(panelInputFields, gbc, timeEndChooser, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Emergency Contact:"), 0, y);
        addComponent(panelInputFields, gbc, txtEmergencyContact, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Relation:"), 2, y);
        addComponent(panelInputFields, gbc, txtEmergencyRelation, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Phone:"), 0, y);
        addComponent(panelInputFields, gbc, txtEmergencyPhone, 1, y);
        addComponent(panelInputFields, gbc, new JLabel("Status:"), 2, y);
        addComponent(panelInputFields, gbc, comboStatus, 3, y++);

        addComponent(panelInputFields, gbc, new JLabel("Visitor Privilege:"), 0, y);
        addComponent(panelInputFields, gbc, checkVisitorPrivilege, 1, y);

        // Add Panels
        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelImage, BorderLayout.WEST);
        panelMain.add(panelInputFields, BorderLayout.CENTER);

        panelInput.add(panelMain, BorderLayout.CENTER);

        // Table Panel
        model = new DefaultTableModel(new String[]{
            "ID", "Code", "Prison", "Cell Block", "Name", "Birth Date",
            "Address", "Marital Status", "Complexion", "Eye Color", "Crime",
            "Sentence", "Time Served", "Time End", "Emergency Contact",
            "Relation", "Phone", "Visitor Privilege", "Status", "Image"
        }, 0);
        table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedInmateId = (int) table.getValueAt(selectedRow, 0);
                        loadInmateDetails();
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.setBackground(Color.decode("#333333"));
        btnAdd = new JButton("Add");
//        addButtonHoverEffect(btnAdd);
        btnUpdate = new JButton("Update");
//        addButtonHoverEffect(btnUpdate);
        btnDelete = new JButton("Delete");
//        addButtonHoverEffect(btnDelete);
        btnReset = new JButton("Reset");
//        addButtonHoverEffect(btnReset);

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnReset);

       // Set Font and Color
        Font font = new Font("Source Sans 3 SemiBold", Font.BOLD, 14);
      
        

        panelInput.setFont(font);
        panelButtons.setFont(font);
        table.setFont(font);
        btnAdd.setFont(font);
        btnUpdate.setFont(font);
        btnDelete.setFont(font);
        btnReset.setFont(font);
        btnUploadImage.setFont(font);
        
        
        // Set Font and Color
        Font font2 = new Font("Source Sans 3 SemiBold", Font.BOLD, 12);
       
        

         txtCode.setFont(font2);
        txtFirstName.setFont(font2);
        txtLastName.setFont(font2);
        txtAddress.setFont(font2);
        txtComplexion.setFont(font2);
        txtEyeColor.setFont(font2);
        txtSentence.setFont(font2);
        txtEmergencyContact.setFont(font2);
        txtEmergencyRelation.setFont(font2);
        txtEmergencyPhone.setFont(font2);
        comboPrison.setFont(font2);
        comboCellBlock.setFont(font2);
        comboCrime.setFont(font2);
        comboMaritalStatus.setFont(font2);
        comboStatus.setFont(font2);
        birthDateChooser.setFont(font2);
        timeServedChooser.setFont(font2);
        
        timeEndChooser.setFont(font2);
        checkVisitorPrivilege.setFont(font2);

        // Set label font
        for (int i = 0; i < panelInputFields.getComponentCount(); i++) {
            Component component = panelInputFields.getComponent(i);
            if (component instanceof JLabel) {
                setLabelFont(component);
            }
        }

        // Set table styles
        

        // Set the frame properties
        setTitle("Inmate Management");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add the components to the frame
        add(panelInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Load the data
        loadPrisons();
        loadCellBlocks();
        loadCrimes();
        loadInmates();

        // Add button actions
        btnUploadImage.addActionListener(e -> uploadImage());
        btnAdd.addActionListener(e -> addInmate());
        btnUpdate.addActionListener(e -> updateInmate());
        btnDelete.addActionListener(e -> deleteInmate());
        btnReset.addActionListener(e -> resetForm());

        // Set the frame visible
        setVisible(true);
    }

//    // Add a hover effect to a button
//    private void addButtonHoverEffect(JButton button) {
//        button.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                button.setBackground(Color.decode("#333333"));
//                button.setForeground(Color.WHITE);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                button.setBackground(Color.WHITE);
//                button.setForeground(Color.BLACK);
//            }
//        });
//    }

    // Add a component to a panel
    private void addComponent(JPanel panel, GridBagConstraints gbc, Component comp, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }

   

    // Set the label font
    private void setLabelFont(Component component) {
        component.setFont(new Font("Source Sans 3 SemiBold", Font.PLAIN, 14));
    }

    // Upload Image Method
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png"));
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            selectedImagePath = file.getAbsolutePath();
            lblImage.setIcon(new ImageIcon(new ImageIcon(selectedImagePath).getImage()
                    .getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
        }
    }

    // Load Prisons
    private void loadPrisons() {
        comboPrison.removeAllItems();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT id, name FROM prisons WHERE status = 'active'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comboPrison.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading prisons: " + e.getMessage());
        }
    }

    // Load Cell Blocks
    private void loadCellBlocks() {
        comboCellBlock.removeAllItems();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT id, name FROM cell_blocks WHERE status = 'active'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comboCellBlock.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cell blocks: " + e.getMessage());
        }
    }

    // Load Crimes
    private void loadCrimes() {
        comboCrime.removeAllItems();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT crime_id, name FROM crimes WHERE status = 'active'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comboCrime.addItem(rs.getInt("crime_id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading crimes: " + e.getMessage());
        }
    }

    // Load Inmates
    private void loadInmates() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT i.id, i.code, p.name AS prison_name, p.id AS prison_id, c.name AS cell_block_name, c.id AS cell_block_id, "
                + "i.first_name, i.last_name, i.birth_date, i.address, i.marital_status, "
                + "i.complexion, i.eye_color, cr.name AS crime_name, cr.crime_id AS crime_id, "
                + "i.sentence, i.time_served, i.time_end, i.emergency_contact_name, "
                + "i.emergency_contact_relation, i.emergency_contact_phone, "
                + "i.visitor_privilege, i.status, i.image "
                + "FROM inmates i "
                + "JOIN prisons p ON i.prison_id = p.id "
                + "JOIN cell_blocks c ON i.cell_block_id = c.id "
                + "JOIN crimes cr ON i.crime_id = cr.crime_id")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String imagePath = rs.getString("image");

                // Convert image path to ImageIcon
                ImageIcon inmateImage = null;
                if (imagePath != null && !imagePath.isEmpty()) {
                    inmateImage = new ImageIcon(new ImageIcon(imagePath).getImage()
                            .getScaledInstance(50, 50, Image.SCALE_SMOOTH));
                }

                model.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("prison_name"),
                    rs.getString("cell_block_name"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getDate("birth_date"),
                    rs.getString("address"),
                    rs.getString("marital_status"),
                    rs.getString("complexion"),
                    rs.getString("eye_color"),
                    rs.getString("crime_name"),
                    rs.getString("sentence"),
                    rs.getDate("time_served"),
                    rs.getDate("time_end"),
                    rs.getString("emergency_contact_name"),
                    rs.getString("emergency_contact_relation"),
                    rs.getString("emergency_contact_phone"),
                    rs.getBoolean("visitor_privilege") ? "Yes" : "No",
                    rs.getString("status"),
                    inmateImage // Display image in table
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading inmates: " + e.getMessage());
        }
    }

    // Load Inmate Details
    private void loadInmateDetails() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT i.id, i.code, p.name AS prison_name, p.id AS prison_id, c.name AS cell_block_name, c.id AS cell_block_id, "
                + "i.first_name, i.last_name, i.birth_date, i.address, i.marital_status, "
                + "i.complexion, i.eye_color, cr.name AS crime_name, cr.crime_id AS crime_id, "
                + "i.sentence, i.time_served, i.time_end, i.emergency_contact_name, "
                + "i.emergency_contact_relation, i.emergency_contact_phone, "
                + "i.visitor_privilege, i.status, i.image "
                + "FROM inmates i "
                + "JOIN prisons p ON i.prison_id = p.id "
                + "JOIN cell_blocks c ON i.cell_block_id = c.id "
                + "JOIN crimes cr ON i.crime_id = cr.crime_id "
                + "WHERE i.id = ?")) {
            ps.setInt(1, selectedInmateId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtCode.setText(rs.getString("code"));
                comboPrison.setSelectedItem(rs.getInt("prison_id") + " - " + rs.getString("prison_name"));
                comboCellBlock.setSelectedItem(rs.getInt("cell_block_id") + " - " + rs.getString("cell_block_name"));
                txtFirstName.setText(rs.getString("first_name"));
                txtLastName.setText(rs.getString("last_name"));
                birthDateChooser.setDate(rs.getDate("birth_date"));
                txtAddress.setText(rs.getString("address"));
                comboMaritalStatus.setSelectedItem(rs.getString("marital_status"));
                txtComplexion.setText(rs.getString("complexion"));
                txtEyeColor.setText(rs.getString("eye_color"));
                comboCrime.setSelectedItem(rs.getInt("crime_id") + " - " + rs.getString("crime_name"));
                txtSentence.setText(rs.getString("sentence"));
                timeServedChooser.setDate(rs.getDate("time_served"));
                timeEndChooser.setDate(rs.getDate("time_end"));
                txtEmergencyContact.setText(rs.getString("emergency_contact_name"));
                txtEmergencyRelation.setText(rs.getString("emergency_contact_relation"));
                txtEmergencyPhone.setText(rs.getString("emergency_contact_phone"));
                checkVisitorPrivilege.setSelected(rs.getBoolean("visitor_privilege"));
                comboStatus.setSelectedItem(rs.getString("status"));
                selectedImagePath = rs.getString("image");
                if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                    lblImage.setIcon(new ImageIcon(new ImageIcon(selectedImagePath).getImage()
                            .getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading inmate details: " + e.getMessage());
        }
    }

    // Add Inmate
    private void addInmate() {
        if (txtCode.getText().isEmpty() || txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty()
                || birthDateChooser.getDate() == null || txtAddress.getText().isEmpty()
                || txtComplexion.getText().isEmpty() || txtEyeColor.getText().isEmpty()
                || txtSentence.getText().isEmpty() || timeServedChooser.getDate() == null
                || timeEndChooser.getDate() == null || txtEmergencyContact.getText().isEmpty()
                || txtEmergencyRelation.getText().isEmpty() || txtEmergencyPhone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO inmates "
                + "(code, prison_id, cell_block_id, first_name, last_name, birth_date, address, marital_status, "
                + "complexion, eye_color, crime_id, sentence, time_served, time_end, emergency_contact_name, "
                + "emergency_contact_relation, emergency_contact_phone, visitor_privilege, status, image) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, txtCode.getText());
            ps.setInt(2, getSelectedId(comboPrison));
            ps.setInt(3, getSelectedId(comboCellBlock));
            ps.setString(4, txtFirstName.getText());
            ps.setString(5, txtLastName.getText());
            ps.setDate(6, new java.sql.Date(birthDateChooser.getDate().getTime()));
            ps.setString(7, txtAddress.getText());
            ps.setString(8, comboMaritalStatus.getSelectedItem().toString());
            ps.setString(9, txtComplexion.getText());
            ps.setString(10, txtEyeColor.getText());
            ps.setInt(11, getSelectedId(comboCrime));
            ps.setString(12, txtSentence.getText());
            ps.setDate(13, new java.sql.Date(timeServedChooser.getDate().getTime()));
            ps.setDate(14, new java.sql.Date(timeEndChooser.getDate().getTime()));
            ps.setString(15, txtEmergencyContact.getText());
            ps.setString(16, txtEmergencyRelation.getText());
            ps.setString(17, txtEmergencyPhone.getText());
            ps.setBoolean(18, checkVisitorPrivilege.isSelected());
            ps.setString(19, comboStatus.getSelectedItem().toString());
            ps.setString(20, selectedImagePath);

            ps.executeUpdate();
            loadInmates();
            resetForm();
            JOptionPane.showMessageDialog(this, "Inmate added successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding inmate: " + e.getMessage());
        }
    }

    // Update Inmate
    private void updateInmate() {
        if (selectedInmateId == -1) {
            return;
        }

        if (txtCode.getText().isEmpty() || txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty()
                || birthDateChooser.getDate() == null || txtAddress.getText().isEmpty()
                || txtComplexion.getText().isEmpty() || txtEyeColor.getText().isEmpty()
                || txtSentence.getText().isEmpty() || timeServedChooser.getDate() == null
                || timeEndChooser.getDate() == null || txtEmergencyContact.getText().isEmpty()
                || txtEmergencyRelation.getText().isEmpty() || txtEmergencyPhone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE inmates SET "
                + "code=?, prison_id=?, cell_block_id=?, first_name=?, last_name=?, birth_date=?, address=?, "
                + "marital_status=?, complexion=?, eye_color=?, crime_id=?, sentence=?, time_served=?, time_end=?, "
                + "emergency_contact_name=?, emergency_contact_relation=?, emergency_contact_phone=?, "
                + "visitor_privilege=?, status=?, image=? WHERE id=?")) {

            ps.setString(1, txtCode.getText());
            ps.setInt(2, getSelectedId(comboPrison));
            ps.setInt(3, getSelectedId(comboCellBlock));
            ps.setString(4, txtFirstName.getText());
            ps.setString(5, txtLastName.getText());
            ps.setDate(6, new java.sql.Date(birthDateChooser.getDate().getTime()));
            ps.setString(7, txtAddress.getText());
            ps.setString(8, comboMaritalStatus.getSelectedItem().toString());
            ps.setString(9, txtComplexion.getText());
            ps.setString(10, txtEyeColor.getText());
            ps.setInt(11, getSelectedId(comboCrime));
            ps.setString(12, txtSentence.getText());
            ps.setDate(13, new java.sql.Date(timeServedChooser.getDate().getTime()));
            ps.setDate(14, new java.sql.Date(timeEndChooser.getDate().getTime()));
            ps.setString(15, txtEmergencyContact.getText());
            ps.setString(16, txtEmergencyRelation.getText());
            ps.setString(17, txtEmergencyPhone.getText());
            ps.setBoolean(18, checkVisitorPrivilege.isSelected());
            ps.setString(19, comboStatus.getSelectedItem().toString());
            ps.setString(20, selectedImagePath);
            ps.setInt(21, selectedInmateId);

            ps.executeUpdate();
            loadInmates();
            resetForm();
            JOptionPane.showMessageDialog(this, "Inmate updated successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating inmate: " + e.getMessage());
        }
    }

    // Delete Inmate
    private void deleteInmate() {
        if (selectedInmateId == -1) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this inmate?");
        if (option == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM inmates WHERE id=?")) {
                ps.setInt(1, selectedInmateId);
                ps.executeUpdate();
                loadInmates();
                resetForm();
                JOptionPane.showMessageDialog(this, "Inmate deleted successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting inmate: " + e.getMessage());
            }
        }
    }

    // Reset
    private void resetForm() {
        txtCode.setText("");
        comboPrison.setSelectedIndex(0);
        comboCellBlock.setSelectedIndex(0);
        txtFirstName.setText("");
        txtLastName.setText("");
        birthDateChooser.setDate(null);
        txtAddress.setText("");
        comboMaritalStatus.setSelectedIndex(0);
        txtComplexion.setText("");
        txtEyeColor.setText("");
        comboCrime.setSelectedIndex(0);
        txtSentence.setText("");
        timeServedChooser.setDate(null);
        timeEndChooser.setDate(null);
        txtEmergencyContact.setText("");
        txtEmergencyRelation.setText("");
        txtEmergencyPhone.setText("");
        checkVisitorPrivilege.setSelected(false);
        comboStatus.setSelectedIndex(0);
        lblImage.setIcon(null);
        selectedImagePath = null;
        selectedInmateId = -1;
    }

    // Check if cell block is already assigned
    private boolean isCellBlockAssigned(int cellBlockId) {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM inmates WHERE cell_block_id = ? AND id != ?")) {
            ps.setInt(1, cellBlockId);
            ps.setInt(2, selectedInmateId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking cell block assignment: " + e.getMessage());
            return false;
        }
    }

    private int getSelectedId(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    public static void main(String[] args) {
        FlatAtomOneLightContrastIJTheme.setup();
        
        SwingUtilities.invokeLater(() -> new InmateManagement().setVisible(true));
    }
}
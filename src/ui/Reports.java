import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.*;
import java.util.*;

public class Reports extends JFrame {
    private JTable reportsTable;
    private JTextField txtStartDate, txtEndDate;
    private JButton btnGenerateReport, btnPrintReport;

    public Reports() {
        // Set up frame properties
        setTitle("Reports");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        txtStartDate = new JTextField(10);
        txtEndDate = new JTextField(10);
        btnGenerateReport = new JButton("Generate Report");
        btnPrintReport = new JButton("Print Report");

        // Table to display the report
        reportsTable = new JTable();
        reportsTable.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "Inmate Name", "Action", "Visitor Name", "Visit Date", "Remarks" }
        ));
        JScrollPane scrollPane = new JScrollPane(reportsTable);

        // Panel for date input and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        inputPanel.add(txtStartDate);
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        inputPanel.add(txtEndDate);
        inputPanel.add(btnGenerateReport);
        inputPanel.add(btnPrintReport);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listener for "Generate Report" button
        btnGenerateReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        // Action Listener for "Print Report" button
        btnPrintReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printReport();
            }
        });
    }

    // Method to generate report
    private void generateReport() {
        String startDate = txtStartDate.getText().trim();
        String endDate = txtEndDate.getText().trim();

        // Validate date inputs
        if (startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter valid start and end dates.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT i.first_name, i.last_name, a.name AS action, v.name AS visitor, v.visit_date, v.remarks FROM inmates i " +
                                                         "JOIN actions a ON i.crime_id = a.id " +
                                                         "JOIN visitors v ON v.inmate_id = i.id WHERE v.visit_date BETWEEN ? AND ?")) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) reportsTable.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                String inmateName = rs.getString("first_name") + " " + rs.getString("last_name");
                String action = rs.getString("action");
                String visitorName = rs.getString("visitor");
                String visitDate = rs.getString("visit_date");
                String remarks = rs.getString("remarks");

                model.addRow(new Object[] { inmateName, action, visitorName, visitDate, remarks });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to print the report
    private void printReport() {
        try {
            boolean complete = reportsTable.print();
            if (complete) {
                JOptionPane.showMessageDialog(this, "Report printed successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Printing canceled.");
            }
        } catch (PrinterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to print report.");
        }
    }

    public static void main(String[] args) {
        new Reports().setVisible(true);
    }
}

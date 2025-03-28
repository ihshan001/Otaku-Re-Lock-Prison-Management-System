import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Dashboard extends JFrame {
    private JLabel lblActivePrisons, lblInactivePrisons, lblActiveCellBlocks, lblInactiveCellBlocks;
    private JLabel lblTotalCrimes, lblTotalActions, lblCurrentInmates, lblReleasedInmates, lblTodaysVisits;
    private JPanel dashboardPanel, navbarPanel;

    public Dashboard() {
        setTitle("Prison Management System - Dashboard");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create Navbar Panel
        navbarPanel = new JPanel();
        navbarPanel.setLayout(new GridLayout(1, 7, 10, 10)); // 7 buttons for all features
        navbarPanel.setBackground(Color.DARK_GRAY);

        String[] features = {"Prisons", "Cell Blocks", "Inmates", "Visitors", "Crimes", "Actions", "Users"};
        for (String feature : features) {
            JButton button = new JButton(feature);
            button.setForeground(Color.WHITE);
            button.setBackground(Color.BLACK);
            button.setFocusPainted(false);
            button.addActionListener(new NavigationListener(feature));
            navbarPanel.add(button);
        }

        // Create Dashboard Panel
        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(3, 3, 10, 10));

        // Initialize labels
        lblActivePrisons = new JLabel("Active Prisons: 0", SwingConstants.CENTER);
        lblInactivePrisons = new JLabel("Inactive Prisons: 0", SwingConstants.CENTER);
        lblActiveCellBlocks = new JLabel("Active Cell Blocks: 0", SwingConstants.CENTER);
        lblInactiveCellBlocks = new JLabel("Inactive Cell Blocks: 0", SwingConstants.CENTER);
        lblTotalCrimes = new JLabel("Total Crimes: 0", SwingConstants.CENTER);
        lblTotalActions = new JLabel("Total Actions: 0", SwingConstants.CENTER);
        lblCurrentInmates = new JLabel("Current Inmates: 0", SwingConstants.CENTER);
        lblReleasedInmates = new JLabel("Released Inmates: 0", SwingConstants.CENTER);
        lblTodaysVisits = new JLabel("Today's Visits: 0", SwingConstants.CENTER);

        // Add labels to dashboard panel
        dashboardPanel.add(lblActivePrisons);
        dashboardPanel.add(lblInactivePrisons);
        dashboardPanel.add(lblActiveCellBlocks);
        dashboardPanel.add(lblInactiveCellBlocks);
        dashboardPanel.add(lblTotalCrimes);
        dashboardPanel.add(lblTotalActions);
        dashboardPanel.add(lblCurrentInmates);
        dashboardPanel.add(lblReleasedInmates);
        dashboardPanel.add(lblTodaysVisits);

        // Add panels to frame
        add(navbarPanel, BorderLayout.NORTH);
        add(dashboardPanel, BorderLayout.CENTER);

        // Fetch data from database
        fetchData();
    }

    private void fetchData() {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            lblActivePrisons.setText("Active Prisons: " + getCount(con, "SELECT COUNT(*) FROM prison WHERE status='Active'"));
            lblInactivePrisons.setText("Inactive Prisons: " + getCount(con, "SELECT COUNT(*) FROM prison WHERE status='Inactive'"));
            lblActiveCellBlocks.setText("Active Cell Blocks: " + getCount(con, "SELECT COUNT(*) FROM cell_block WHERE status='Active'"));
            lblInactiveCellBlocks.setText("Inactive Cell Blocks: " + getCount(con, "SELECT COUNT(*) FROM cell_block WHERE status='Inactive'"));
            lblTotalCrimes.setText("Total Crimes: " + getCount(con, "SELECT COUNT(*) FROM crime"));
            lblTotalActions.setText("Total Actions: " + getCount(con, "SELECT COUNT(*) FROM action"));
            lblCurrentInmates.setText("Current Inmates: " + getCount(con, "SELECT COUNT(*) FROM inmate WHERE status='Active'"));
            lblReleasedInmates.setText("Released Inmates: " + getCount(con, "SELECT COUNT(*) FROM inmate WHERE status='Released'"));
            lblTodaysVisits.setText("Today's Visits: " + getCount(con, "SELECT COUNT(*) FROM visitor WHERE DATE(date_created) = CURDATE()"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCount(Connection con, String query) {
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Navigation Listener for buttons
    private class NavigationListener implements ActionListener {
        private String feature;

        public NavigationListener(String feature) {
            this.feature = feature;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (feature) {
                case "Prisons":
                    new PrisonManagement().setVisible(true);
                    break;
                case "Cell Blocks":
                    new CellBlockManagement().setVisible(true);
                    break;
                case "Inmates":
                    new InmateManagement().setVisible(true);
                    break;
                case "Visitors":
                    new VisitorManagement().setVisible(true);
                    break;
                case "Crimes":
                    new CrimeManagement().setVisible(true);
                    break;
                case "Actions":
                    new ActionManagement().setVisible(true);
                    break;
                case "Users":
                    new UserManagement().setVisible(true);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}

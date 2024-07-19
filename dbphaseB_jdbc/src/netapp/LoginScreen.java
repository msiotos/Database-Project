package netapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginScreen extends JFrame implements ActionListener {
	private static final String DB_URL = "jdbc:postgresql://postgres.isc.tuc.gr:5432/db2016030030";
    private static final String DB_USER = "msiotos";
    private static final String DB_PASSWORD = "010823739";
    
    private JTextField emailField;
    private JTextField passField;
    private JButton loginButton;
    
    private Connection conn;

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        emailField = new JTextField(20);
        
        passField = new JPasswordField();
        
        loginButton = new JButton("Login");

        JPanel panel = new JPanel(new GridLayout(5, 1));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(this);

        checkJDBCdriver();
        dbConnect();
    }
    
    public void showMessage(String msg) {
    	JOptionPane.showMessageDialog(null, msg);
    }
    
    private void checkJDBCdriver() {
    	//Check if a valid driver is available
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
    		showMessage("Valid driver isn't available.");
            e.printStackTrace();
        }
    }

    
    private void dbConnect() {
    	//Connect to professional network database
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            showMessage("Connection failed.");
            e.printStackTrace();
        }
    }

    private boolean authenticateUser(String email,String password) {
    	//Check the user and password
        String query = "SELECT * FROM public.member WHERE email = ? AND \"thePassword\" = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            showMessage("Authentication error.");
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String email = emailField.getText();
        String password = passField.getText();
        if (authenticateUser(email, password)) {
            showMessage("Login successful!");
            new MainScreen(conn, email).setVisible(true);
            this.dispose();
        } else {
            showMessage("Invalid email or password.");
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }


}

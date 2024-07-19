package netapp;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainScreen extends JFrame implements ActionListener{
    private Connection conn;

    private String userEmail;
    
    private JPanel personalDetailsPanel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JTextField countryField;
    private JButton updateButton;
    
    private JList<Member> networkList;
    private DefaultListModel<Member> networkListModel;
    
    

    public MainScreen(Connection conn, String userEmail) {
        this.conn = conn;
        this.userEmail = userEmail;

        setTitle("Home");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        personalDetailsPanel = new JPanel(new GridLayout(5, 2,10,10));
        personalDetailsPanel.setMaximumSize(new Dimension(600,200));
        personalDetailsPanel.setBorder(new EmptyBorder(10,10,10,10));
        personalDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        personalDetailsPanel.add(new JPanel(new FlowLayout(FlowLayout.TRAILING)).add(new JLabel("First Name:")));
        firstNameField = new JTextField();
        personalDetailsPanel.add(firstNameField);

        personalDetailsPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        personalDetailsPanel.add(lastNameField);

        personalDetailsPanel.add(new JLabel("Date of Birth:"));
        dobField = new JTextField();
        personalDetailsPanel.add(dobField);
        
        personalDetailsPanel.add(new JLabel("Country:"));
        countryField = new JTextField();
        personalDetailsPanel.add(countryField);

        personalDetailsPanel.add(new JLabel());
        updateButton = new JButton("Update");
        updateButton.addActionListener(this);
        personalDetailsPanel.add(updateButton);

        networkListModel = new DefaultListModel<>();
        networkList = new JList<>(networkListModel);
        networkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        networkList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Member selectedMember = networkList.getSelectedValue();
                    if (selectedMember != null) {
                        new MemberDetailScreen(conn, selectedMember.getEmail(), userEmail).setVisible(true);
                    }
                }
            }
        });

        
        
        add(personalDetailsPanel);
        JLabel nusers = new JLabel("My Network");
        nusers.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(nusers);
        JScrollPane jpane = new JScrollPane(networkList);
        jpane.setAlignmentX(Component.LEFT_ALIGNMENT);
        jpane.setBorder(new EmptyBorder(10,10,10,10));
        add(jpane);

        fetchPersonalDetails();
        fetchNetwork();
    }
    
    public void showMessage(String msg) {
    	JOptionPane.showMessageDialog(null, msg);
    }
    
    private void fetchPersonalDetails() {
    	// Write code to show the appropriate values in the JTextFields
        String query = "SELECT \"firstName\", \"secondName\", \"dateOfBirth\", country FROM public.member WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                firstNameField.setText(rs.getString("firstName"));
                lastNameField.setText(rs.getString("secondName"));
                dobField.setText(rs.getString("dateOfBirth"));
                countryField.setText(rs.getString("country"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void updatePersonalDetails() {
    	// Write code to update the database with new values in the JTextFields
        String query = "UPDATE member SET \"firstName\" = ?, \"secondName\" = ?, \"dateOfBirth\" = CAST(? AS date), country = ? WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, firstNameField.getText());
            pstmt.setString(2, lastNameField.getText());
            pstmt.setString(3, dobField.getText());
            pstmt.setString(4, countryField.getText());
            pstmt.setString(5, userEmail);
            pstmt.executeUpdate();
            showMessage("Personal details updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchNetwork() {
    	// Write code to fill the list with connected members
        String query = "SELECT m.\"firstName\", m.\"secondName\", m.email FROM connects c JOIN member m ON c.\"connectedWithEmail\" = m.email WHERE c.email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String firstName = rs.getString("firstName");
                String secondName = rs.getString("secondName");
                String email = rs.getString("email");
                networkListModel.addElement(new Member(firstName, secondName, email));
            }
        } catch (SQLException e) {
    		showMessage("Couldn't fetch the network");
            e.printStackTrace();
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == updateButton) {
	        updatePersonalDetails();
	    }
	}
}

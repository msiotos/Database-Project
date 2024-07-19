package netapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDetailScreen extends JFrame {
    private Connection conn;
    private String memberEmail;
    private String userEmail;
    private JTextArea detailsArea;
    private JTextArea messagesArea;
    private JTextArea educationArea;
    private JTextArea experienceArea;

    public MemberDetailScreen(Connection conn, String memberEmail, String userEmail) {
        this.conn = conn;
        this.memberEmail = memberEmail;
        this.userEmail = userEmail; 


        setTitle("Details for :"+memberEmail);
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        detailsArea = new JTextArea(5, 40);
        detailsArea.setEditable(false);
        messagesArea = new JTextArea(10, 40);
        messagesArea.setEditable(false);
        educationArea = new JTextArea(10, 40);
        educationArea.setEditable(false);
        experienceArea = new JTextArea(10, 40);
        experienceArea.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(new EmptyBorder(20,20,20,20));
        
        panel.add(new JLabel("Personal Details"));
        panel.add(new JScrollPane(detailsArea));
        panel.add(new JLabel("Messages"));
        panel.add(new JScrollPane(messagesArea));
        panel.add(new JLabel("Education"));
        panel.add(new JScrollPane(educationArea));
        panel.add(new JLabel("Experience"));
        panel.add(new JScrollPane(experienceArea));

        add(panel);

        fetchMemberDetails();
        fetchMessages();
        fetchEducation();
        fetchExperience();
    }
    public void showMessage(String msg) {
    	JOptionPane.showMessageDialog(null, msg);
    }

    private void fetchMemberDetails() {
    	// Write code to show member's details to the appropriate JTextArea
        String query = "SELECT \"firstName\", \"secondName\", TO_CHAR(\"dateOfBirth\", 'YYYY-MM-DD') AS \"dateOfBirth\", country FROM member WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberEmail);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                detailsArea.setText(
                    "First Name: " + rs.getString("firstName") + "\n" +
                    "Second Name: " + rs.getString("secondName") + "\n" +
                    "Date of Birth: " + rs.getString("dateOfBirth") + "\n" +
                    "Country: " + rs.getString("country")
                );
            }
        } catch (SQLException e) {
            showMessage("Error fetching member details.");
            e.printStackTrace();
        }
    }


    private void fetchMessages() {
    	// Write code to show messages from member to the appropriate JTextArea
        String query = "SELECT \"theText\" FROM msg WHERE \"senderEmail\" = ? AND \"receiverEmail\" = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberEmail);
            pstmt.setString(2, userEmail);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder messages = new StringBuilder();
            while (rs.next()) {
                messages.append(rs.getString("theText")).append("\n\n");
            }
            messagesArea.setText(messages.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void fetchEducation() {
    	// Write code to show member's details about education the appropriate JTextArea
        String query = "SELECT country, school, \"eduLevel\", \"categoryID\", \"fromYear\", \"toYear\" FROM education WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberEmail);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder education = new StringBuilder();
            while (rs.next()) {
                education.append(
                	"Country: " + rs.getString("country") + "\n" +
                    "School: " + rs.getString("school") + "\n" +
                    "Education Level: " + rs.getString("eduLevel") + "\n" +
                    "CategoryID: " + rs.getInt("categoryID") + "\n" +
                    "From: " + rs.getString("fromYear") + "\n" +
                    "To: " + rs.getString("toYear") + "\n\n"
                );
            }
            educationArea.setText(education.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void fetchExperience() {
    	// Write code to show member's professional experience to the appropriate JTextArea
        String query = "SELECT company, \"workStatus\", title, description, \"fromYear\", \"toYear\" FROM experience WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberEmail);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder experience = new StringBuilder();
            while (rs.next()) {
                experience.append(
                    "Company: " + rs.getString("company") + "\n" +
                    "Status: " + rs.getString("workStatus") + "\n" +                
                    "Title: " + rs.getString("title") + "\n" +
                    "Description: " + rs.getString("description") + "\n" +
                    "From: " + rs.getString("fromYear") + "\n" +
                    "To: " + rs.getString("toYear") + "\n\n"
                );
            }
            experienceArea.setText(experience.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


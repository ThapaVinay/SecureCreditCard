package CreditCardEncryptionDecryption;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CardSecurity extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String AES_SECRET_KEY = "mySecretKey12345"; // 128-bit secret key

    public CardSecurity() {
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    public static String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userEmail = request.getParameter("user_email");
        String creditCardNumber = request.getParameter("card_number");
        String cardDate = request.getParameter("card_date");
        String cvv = request.getParameter("cvv");

        // Encrypt the credit card number
        String encryptedCreditCard = encrypt(creditCardNumber);

        // Save the encrypted card number and other details into the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://admin.cjceo0oqcyqk.ca-central-1.rds.amazonaws.com:3306/credit_card_security_db", "500096288@stu.upes.ac.in", "Chetanshi@123");

            String query = "INSERT INTO credit_card_details (user_email, encrypted_card_number, real_card_number, card_date, cvv) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userEmail);
            preparedStatement.setString(2, encryptedCreditCard);
            preparedStatement.setString(3, creditCardNumber); // Real card number
            preparedStatement.setString(4, cardDate);
            preparedStatement.setString(5, cvv);
            preparedStatement.executeUpdate();

            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Card Encrypted: "+encryptedCreditCard);
        response.getWriter().println("<div class='success-message'>Credit card details saved successfully!</div><br><b>"+"Card Encrypted: </b>"+encryptedCreditCard);
    }
}
/*
-- Create database
CREATE DATABASE IF NOT EXISTS credit_card_security_db;

-- Use the database
USE credit_card_security_db;

-- Create table to store credit card details
CREATE TABLE IF NOT EXISTS credit_card_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    encrypted_card_number TEXT NOT NULL,
    real_card_number VARCHAR(255) NOT NULL,
    card_date VARCHAR(10) NOT NULL,
    cvv VARCHAR(4) NOT NULL
);





*/
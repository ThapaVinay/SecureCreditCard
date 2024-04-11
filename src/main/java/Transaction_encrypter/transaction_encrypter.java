package Transaction_encrypter;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class transaction_encrypter extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public transaction_encrypter() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    private static SecretKey generateRandomSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 128-bit key
        return keyGenerator.generateKey();
    }

    private static String encrypt(String input, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Input from HTML form
        String senderAccountNumber = request.getParameter("senderAccountNumber");
        String senderIFSC = request.getParameter("senderIFSC");
        String senderBank = request.getParameter("senderBank");
        String amount = request.getParameter("amount");
        String receiverAccountNumber = request.getParameter("receiverAccountNumber");
        String receiverIFSC = request.getParameter("receiverIFSC");
        String receiverBank = request.getParameter("receiverBank");

        try {
            // Generate random secret key
            SecretKey secretKey = generateRandomSecretKey();
            String secretKeyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println("Secret Key: " + secretKeyString);

            // Encrypt all details into a single line of text
            String encryptedDetails = encrypt(senderAccountNumber + "," + senderIFSC + "," + senderBank + "," +
                    amount + "," + receiverAccountNumber + "," + receiverIFSC + "," + receiverBank, secretKey);

            // Save secret key and encrypted text to database
            saveToDatabase(secretKeyString, encryptedDetails);
            
            // Print encrypted details and secret key
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<div class=\"result\">");
            out.println("<h3>Encrypted Details:</h3>");
            out.println("<p>" + encryptedDetails + "</p>");
            out.println("<h3>Secret Key:</h3>");
            out.println("<p>" + secretKeyString + "</p>");
            out.println("</div>");
            
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        doGet(request, response);
    }
    
    private void saveToDatabase(String secretKey, String encryptedDetails) {
        String url = "jdbc:mysql://admin.cjceo0oqcyqk.ca-central-1.rds.amazonaws.com:3306/credit_card_security_db";
        String username = "500096288@stu.upes.ac.in";
        String password = "Chetanshi@123";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = "INSERT INTO transaction_details (secret_key, encrypted_text) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, secretKey);
                statement.setString(2, encryptedDetails);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

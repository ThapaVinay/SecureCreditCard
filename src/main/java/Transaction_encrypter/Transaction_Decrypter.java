package Transaction_encrypter;

import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Transaction_Decrypter
 */

public class Transaction_Decrypter extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Transaction_Decrypter() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    public static void decryptAndPrint(HttpServletRequest request, String encryptedDetails, String secretKey) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        SecretKeySpec key = new SecretKeySpec(decodedKey, "AES");

        String decryptedDetails = decrypt(encryptedDetails, key);
        String[] details = decryptedDetails.split(",");

        // Set decrypted details as request attributes
        request.setAttribute("senderAccountNumber", details[0]);
        request.setAttribute("senderIFSC", details[1]);
        request.setAttribute("senderBank", details[2]);
        request.setAttribute("amount", details[3]);
        request.setAttribute("receiverAccountNumber", details[4]);
        request.setAttribute("receiverIFSC", details[5]);
        request.setAttribute("receiverBank", details[6]);
    }


    private static String decrypt(String input, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(input));
        return new String(decryptedBytes);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve parameters from the request
        String secretKey = request.getParameter("secretKey");
        String encryptedDetails = request.getParameter("encryptedDetails");

        try {
            decryptAndPrint(request, encryptedDetails, secretKey);
            // Forward the request to a JSP for rendering
            request.getRequestDispatcher("/decrypt_result.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doGet(request, response);
    }
}


/*
 use credit_card_security_db;
CREATE TABLE transaction_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    secret_key VARCHAR(255) NOT NULL,
    encrypted_text TEXT NOT NULL
);

 */

 
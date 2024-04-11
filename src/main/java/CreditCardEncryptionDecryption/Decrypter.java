package CreditCardEncryptionDecryption;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Decrypter extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String AES_SECRET_KEY = "mySecretKey12345";

    public Decrypter() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    public static String decrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            SecretKeySpec secretKey = new SecretKeySpec(AES_SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(input);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String encryptedCreditCard = request.getParameter("decrypt_text");
        String decryptedCreditCard = decrypt(encryptedCreditCard);
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body><br><br><br>");
        out.println("<h2>Decrypted Credit Card Number</h2>");
        out.println("<p>" + decryptedCreditCard + "</p>");
        out.println("</body></html>");
    }
}

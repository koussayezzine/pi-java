package tn.esprit.sirine.utils;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {
    // Note: For Gmail, you need to use an App Password, not your regular password
    // To generate an App Password:
    // 1. Go to your Google Account > Security
    // 2. Enable 2-Step Verification if not already enabled
    // 3. Go to App passwords
    // 4. Select "Mail" and "Other (Custom name)" and enter a name for your app
    // 5. Click "Generate" and use the generated password below
    private static final String AUTH_EMAIL = "killjoyxxxx0000@gmail.com";
    private static final String AUTH_PASS = "unpwiifxtbhgausx"; // This should be an App Password

    public static void sendResetEmail(String toEmail, String newPassword) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true"); // Enable debug mode for troubleshooting

        System.out.println("Attempting to send email to: " + toEmail);

        try {
            Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(AUTH_EMAIL, AUTH_PASS);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(AUTH_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Réinitialisation de votre mot de passe");
            message.setText("Bonjour,\n\nVotre nouveau mot de passe est : " + newPassword +
                    "\n\nVeuillez le modifier après connexion.\n\nCordialement,\nL'équipe Support");

            Transport.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to be handled by the caller
        }
    }
}

package app.util;

import java.io.IOException;
import java.util.Map;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import app.exceptions.EmailException;

public class EmailSender {

    private static final String FROM_EMAIL_ADDRESS = System.getenv("FROM_EMAIL_ADDRESS");
    private static final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");

    public static final String TEMPLATE_ID_QUOTE_CONFIRMATION = System.getenv("TEMPLATE_ID_QUOTE_CONFIRMATION");
    public static final String TEMPLATE_ID_QUOTE_READY = System.getenv("TEMPLATE_ID_QUOTE_READY");
    public static final String TEMPLATE_ID_FORGOT_PASSWORD = System.getenv("TEMPLATE_ID_FORGOT_PASSWORD");

    public static void sendEmail(String toEmailAddress, String sendgridTemplateId, Map<String, Object> emailParams) throws EmailException {
        Email from = new Email(FROM_EMAIL_ADDRESS);
        from.setName("Byggemarked");

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(toEmailAddress));
        for (String key : emailParams.keySet()) {
            personalization.addDynamicTemplateData(key, emailParams.get(key));
        }

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.templateId = sendgridTemplateId;
        mail.addPersonalization(personalization);
        mail.addCategory("FogCarport");

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            if (response.getStatusCode() != 202) {
                throw new EmailException("Error when sending email: RESPONSE STATUS CODE: " + response.getStatusCode()
                        + " RESPONSE HEADER: " + response.getHeaders() + " RESPONSE BODY: " + response.getBody());
            }

        } catch (IOException e) {
            throw new EmailException("Error when sending mail: " + e.getMessage());
        }
    }

}
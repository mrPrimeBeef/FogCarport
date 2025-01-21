package app.util;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import java.io.IOException;
import java.util.HashMap;

public class EmailSender {

    private static final String FROM_EMAIL_ADDRESS = System.getenv("FROM_EMAIL_ADDRESS");
    private static final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");
    public static final String TEMPLATE_ID_QUOTE_READY_MAIL = System.getenv("TEMPLATE_ID_QUOTE_READY_MAIL");

    public static void sendEmail(String toEmailAddress, String sendgridTemplateId, HashMap<String, String> map) {
        Email from = new Email(FROM_EMAIL_ADDRESS);
        from.setName("Byggemarked");

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(toEmailAddress));
        for (String key : map.keySet()) {
            personalization.addDynamicTemplateData(key, map.get(key));
        }

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.templateId = sendgridTemplateId;
        mail.addPersonalization(personalization);
        mail.addCategory("carportapp");

        System.out.println(mail.templateId);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e) {
            System.out.println("Error sending mail");
        }
    }


    public void sendEmail(String name, String email, String password) throws IOException {
        Email from = new Email(FROM_EMAIL_ADDRESS);
        from.setName("Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();

        /* Erstat kunde@gmail.com, name, email og zip med egne værdier ****/
        /* I test-fasen - brug din egen email, så du kan modtage beskeden */
        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", name);
        personalization.addDynamicTemplateData("email", email);
        personalization.addDynamicTemplateData("zip", password);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-2ffc20e92ae94388b6b25a2277ec6f06";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e) {
            System.out.println("Error sending mail");
            throw e;
        }
    }
}
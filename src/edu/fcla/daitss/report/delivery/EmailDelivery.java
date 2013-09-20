/*
 * DAITSS Copyright (C) 2007 University of Florida
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package edu.fcla.daitss.report.delivery;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.MimeMediaType;
import edu.fcla.daitss.report.Report;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;

/**
 * @author franco
 *
 */
public class EmailDelivery extends Delivery {

        private Address[] recipients;
    private Report report;

    /**
     * @param addresses
     * @param report
     * @throws DeliveryException
     */
    public EmailDelivery(Report report, String[] addresses) throws DeliveryException {

        this.report = report;

        this.recipients = new Address[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            try {
                this.recipients[i] = new InternetAddress(addresses[i]);
            } catch (AddressException e) {
                throw new DeliveryException(e.getMessage());
            }
        }
    }

    /* (non-Javadoc)
     * @see edu.fcla.daitss.report.delivery.Delivery#deliver()
     */
    public void deliver() throws DeliveryException, FatalException {

        /* Archive Properties */
        ArchiveProperties ap = ArchiveProperties.getInstance();

        /* lookup mail properties */
        String mailUser = ap.getArchProperty("REPORT_MAIL_USER");
        String mailHost = ap.getArchProperty("REPORT_MAIL_HOST");
        String mailReplyTo = ap.getArchProperty("REPORT_MAIL_REPLY_TO");
        
        boolean keepMailLog = ap.getArchProperty("REPORT_MAIL_KEEP_LOG").equals("true");
        long mailTimeOut = Long.parseLong(ap.getArchProperty("REPORT_MAIL_TIMEOUT")) * 1000;
        String mailFrom = ap.getArchProperty("REPORT_MAIL_FROM");

        String mailBCC = ap.getArchProperty("REPORT_MAIL_BCC");
        boolean hasBCC = mailBCC != null && !mailBCC.equals("");

        // interpolate the filename in the subject
        String mailSubject = ap.getArchProperty("REPORT_MAIL_SUBJECT_DEFAULT").replace("${FILE_NAME}", report.getFileName());


        /* Create the mail session */
        Properties p = new Properties();
        p.put("mail.host", mailHost);
        p.put("mail.user", mailUser);
        p.put("mail.smtp.timeout", Long.toString(mailTimeOut * 1000));
        p.put("mail.smtp.from", mailFrom);

        if (System.getenv("SMTP_PORT") != null && System.getenv("SMTP_PORT").trim() != "") {
        	p.put("mail.smtp.port", System.getenv("SMTP_PORT"));	
        }

        Session session = Session.getInstance(p);

        /* Turn on mail loggin */
        session.setDebug(keepMailLog);
        session.setDebugOut(System.out);

        /* Create the message */
        MimeMessage message = new MimeMessage(session);

        try {

            /* build the content of the message */
            Multipart content = makeContent();

            message.setContent(content);

            /* set the sender's address */
            message.setFrom(new InternetAddress(mailFrom));

            /* set the reply to */
            message.setReplyTo(new InternetAddress[] { new InternetAddress(mailReplyTo) });
            
            /* set the subject */ 
            message.setSubject(mailSubject);

            /* add recipients */
            message.addRecipients(Message.RecipientType.TO, this.recipients);

            /* send a copy to the archive for record keeping */
            List<Address> bccList = new Vector<Address>();
            for (String address : mailBCC.split("\\s*,\\s*")){
                bccList.add(new InternetAddress(address));
            }

            if (hasBCC) {
                message.addRecipients(Message.RecipientType.BCC, bccList.toArray(new Address[bccList.size()]));
            }

            /* send the message */
            Transport.send(message);

            /* inform that message has been e-mailed */
            Informer.getInstance().info(
                                        this,
                                        "deliver()",
                                        "Message delivered TO: " + Arrays.asList(this.recipients).toString(),
                                        "Email Delivery",
                                        false);
        } catch (MessagingException e) {
            throw new DeliveryException(e.getMessage());
        }
    }

        private Multipart makeContent() throws MessagingException, FatalException {

                /* message content */
                MimeMultipart content = new MimeMultipart();

                /* text message body */
                BodyPart textPart = new MimeBodyPart();
                textPart.setText(report.description());
                textPart.setDescription("text summary");
                content.addBodyPart(textPart);

        /* attached xml part */
        String xmlText = report.serializeXML();
        BodyPart xmlPart = new MimeBodyPart();

        xmlPart.setContent(xmlText, MimeMediaType.MIME_TXT_XML);
        xmlPart.setFileName(report.getFileName());
        xmlPart.setDescription("xml report");
        content.addBodyPart(xmlPart);

                return content;
        }
}

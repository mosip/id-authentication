package io.mosip.kernel.core.notification.spi;


/**
 * Service implementation class for {@link MailNotifier}.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 */

public interface EmailNotificationService<T,D> {

	/**
	 * SendEmail
	 * 
	 * @param mailTo
	 * @param mailCc
	 * @param mailSubject
	 * @param mailContent
	 * @param attachments
	 * @return
	 */
	public D sendEmail(String[] mailTo, String[] mailCc, String mailSubject,
			String mailContent, T attachments);
}

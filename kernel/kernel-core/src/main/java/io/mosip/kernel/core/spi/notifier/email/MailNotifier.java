package io.mosip.kernel.core.spi.notifier.email;

import java.util.concurrent.CompletableFuture;

/**
 * This interface provides method(s) for handling mail notification service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public interface MailNotifier<T, D> {
	/**
	 * @param mailTo
	 *            array of email id's, to which mail should be sent.
	 * @param mailCc
	 *            array of email id's, to which the email should be sent as carbon
	 *            copy.
	 * @param mailSubject
	 *            the subject.
	 * @param mailContent
	 *            the content.
	 * @param attachment
	 *            the attachments.
	 * @return the response.
	 */
	public CompletableFuture<D> sendEmail(String[] mailTo, String[] mailCc, String mailSubject,
			String mailContent, T[] attachment);
}

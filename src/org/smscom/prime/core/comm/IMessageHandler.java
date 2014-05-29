package org.smscom.prime.core.comm;

import org.smscom.prime.core.comm.protocol.PrimeMessage;

public interface IMessageHandler {

	/**
	 * Message handlers must implement this method for call back. It defines how to manage the incoming notification.
	 * @param message the incoming message to process
	 */
	public void handleMessage(PrimeMessage message);
	
	
}

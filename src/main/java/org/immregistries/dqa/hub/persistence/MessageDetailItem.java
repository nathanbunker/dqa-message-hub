package org.immregistries.dqa.hub.persistence;

import java.util.List;

public class MessageDetailItem {
	private MessageListItem messageMetaData;
	private List<HL7LocationValue> vxuParts;
	private String providerKey;
	private String messageReceived;
	private String messageResponse;
	/**
	 * @return the messageMetaData
	 */
	public MessageListItem getMessageMetaData() {
		return messageMetaData;
	}
	/**
	 * @param messageMetaData the messageMetaData to set
	 */
	public void setMessageMetaData(MessageListItem messageMetaData) {
		this.messageMetaData = messageMetaData;
	}
	/**
	 * @return the vxuParts
	 */
	public List<HL7LocationValue> getVxuParts() {
		return vxuParts;
	}
	/**
	 * @param vxuParts the vxuParts to set
	 */
	public void setVxuParts(List<HL7LocationValue> vxuParts) {
		this.vxuParts = vxuParts;
	}
	/**
	 * @return the messageReceived
	 */
	public String getMessageReceived() {
		return messageReceived;
	}
	/**
	 * @param messageReceived the messageReceived to set
	 */
	public void setMessageReceived(String messageReceived) {
		this.messageReceived = messageReceived;
	}
	/**
	 * @return the messageResponse
	 */
	public String getMessageResponse() {
		return messageResponse;
	}
	/**
	 * @param messageResponse the messageResponse to set
	 */
	public void setMessageResponse(String messageResponse) {
		this.messageResponse = messageResponse;
	}
	public String getProviderKey() {
		return providerKey;
	}
	public void setProviderKey(String providerKey) {
		this.providerKey = providerKey;
	}
	
	
		
}

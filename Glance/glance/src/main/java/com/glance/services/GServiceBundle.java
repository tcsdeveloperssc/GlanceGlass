/*
Copyright (c) 2013, TATA Consultancy Services Limited (TCSL)
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
+ Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
+ Neither the name of TCSL nor the names of its contributors may be 
  used to endorse or promote products derived from this software without 
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */

package com.glance.services;


/**
 * This class is used for Creating a service bundle with all relevant info to 
 * execute a web service request.
 * @author 351860auon
 *
 */
public class GServiceBundle {

	public static final int POST = 1;
	public static final int GET = 2;
	public static final int DELETE = 3;
	public static final int PUT = 4;
	
	private String serviceUrl;
	private Object requestObject;
	private Class<?> responseType;
	private int connectionType;
	private boolean signatureRequired;
	private String queryParams;
	private int opertationType;
	private String accountId;
	
	private boolean encryptionRequired;
	private boolean decryptionRequired;
	
	private int serviceTimeOutInMillis;
	private boolean isUserCredentialsRequired;
	
	
	public GServiceBundle(){
		this.encryptionRequired = true;
		this.decryptionRequired = true;
		this.isUserCredentialsRequired = true;
		this.serviceTimeOutInMillis = HttpConnector.TIMEOUT_2;
	}
	
	
	/**
	 * @return the serviceTimeOutInMillis
	 */
	public int getServiceTimeOutInMillis() {
		return serviceTimeOutInMillis;
	}

	/**
	 * @param serviceTimeOutInMillis the serviceTimeOutInMillis to set
	 */
	public void setServiceTimeOutInMillis(int serviceTimeOutInMillis) {
		this.serviceTimeOutInMillis = serviceTimeOutInMillis;
	}



	/**
	 * @return the decryptionRequired
	 */
	public boolean isDecryptionRequired() {
		return decryptionRequired;
	}
	/**
	 * @param decryptionRequired the decryptionRequired to set
	 */
	public void setDecryptionRequired(boolean decryptionRequired) {
		this.decryptionRequired = decryptionRequired;
	}
	/**
	 * @return the encryptionRequired
	 */
	public boolean isEncryptionRequired() {
		return encryptionRequired;
	}
	/**
	 * @param encryptionRequired the encryptionRequired to set
	 */
	public void setEncryptionRequired(boolean encryptionRequired) {
		this.encryptionRequired = encryptionRequired;
	}
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	/**
	 * @return the queryParams
	 */
	public String getQueryParams() {
		return queryParams;
	}
	/**
	 * @param queryParams the queryParams to set
	 */
	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}
	/**
	 * @return the isSignatureRequired
	 */
	public boolean isSignatureRequired() {
		return signatureRequired;
	}
	/**
	 * @param isSignatureRequired the isSignatureRequired to set
	 */
	public void setSignatureRequired(boolean isSignatureRequired) {
		this.signatureRequired = isSignatureRequired;
	}
	public String getServiceUrl() {
		return serviceUrl;
	}
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	public Object getRequestObject() {
		return requestObject;
	}
	public void setRequestObject(Object requestObject) {
		this.requestObject = requestObject;
	}
	public Class<?> getResponseType() {
		return responseType;
	}
	public void setResponseType(Class<?> responseType) {
		this.responseType = responseType;
	}
	public int getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(int connectionType) {
		this.connectionType = connectionType;
	}
	public int getOpertationType() {
		return opertationType;
	}
	public void setOpertationType(int opertationType) {
		this.opertationType = opertationType;
	}


	public boolean isUserCredentialsRequired() {
		return isUserCredentialsRequired;
	}


	public void setUserCredentialsRequired(boolean isUserCredentialsRequired) {
		this.isUserCredentialsRequired = isUserCredentialsRequired;
	}

}

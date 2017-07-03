package uk.co.globalinput.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MYSSLContext {
	static final char[] JKS_PASSWORD = "39".toCharArray();
	static final char[] KEY_PASSWORD = "39".toCharArray();
	private static String fullPathOfKeyStore(){
		return "/app/config/keystore.jks";		
	}
	public static SSLContext getSSLContext(){
		try {
			/* Get the JKS contents */
			final KeyStore keyStore = KeyStore.getInstance("JKS");
			try (final InputStream is = new FileInputStream(fullPathOfKeyStore())) {
				keyStore.load(is, JKS_PASSWORD);
			}
			final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
			kmf.init(keyStore, KEY_PASSWORD);
			final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
			tmf.init(keyStore);

			/*
			 * Creates a socket factory for HttpsURLConnection using JKS
			 * contents
			 */
			final SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
			return sc;

		} catch (final GeneralSecurityException | IOException exc) {
			throw new RuntimeException(exc);
		}
	}
	/**
	 * Makes {@link HttpsURLConnection} accepts our certificate.
	 */
	private static void acceptMyCertificate() {
			final SSLContext sc = getSSLContext();
			final SSLSocketFactory socketFactory = sc.getSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);	
	}
	
	public static HostnameVerifier createHostnameVerifier(){
		  HostnameVerifier hostnameVerifier=new HostnameVerifier() {			
			@Override
			public boolean verify(String hostname, SSLSession session) {
				// TODO Auto-generated method stub
				return true;
			}
		  };		    
		  return hostnameVerifier;
		}
		 
	 
}

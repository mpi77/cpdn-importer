package cz.sd2.cpdn.importer.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Connection {
	public static final String IDP_GW_URL = "https://idp.cpdn.sd2.cz/";
	public static final String API_V1_GW_URL = "https://api.cpdn.sd2.cz/v1/";

	private static CloseableHttpClient httpCLient = null;
	private static String accessToken = "";

	public static CloseableHttpClient getHttpClient()
			throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		if (Connection.httpCLient == null) {
			SSLContextBuilder context_b = SSLContextBuilder.create();
			context_b.loadTrustMaterial(new org.apache.http.conn.ssl.TrustSelfSignedStrategy());
			SSLContext ssl_context = context_b.build();
			org.apache.http.conn.ssl.SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
					ssl_context, new org.apache.http.conn.ssl.DefaultHostnameVerifier());
			Connection.httpCLient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
		}
		return httpCLient;
	}

	public static void authenticate(String clientId, String username, String password) throws IOException,
			NoSuchAlgorithmException, KeyStoreException, KeyManagementException, AuthenticationException {
		CloseableHttpClient client = Connection.getHttpClient();
		HttpPost post = new HttpPost(Connection.IDP_GW_URL + "token");
		post.addHeader("Accept", "application/json");

		StringEntity se = new StringEntity(
				String.format("grant_type=password&client_id=%s&username=%s&password=%s", clientId, username, password),
				ContentType.APPLICATION_FORM_URLENCODED);
		post.setEntity(se);

		HttpResponse response = client.execute(post);

		if (response.getStatusLine().getStatusCode() == 200) {
			JSONObject obj = new JSONObject(EntityUtils.toString(response.getEntity()));
			Connection.accessToken = obj.getString("access_token");
		} else {
			throw new AuthenticationException("Could not obtain a valid access token.");
		}
	}

	public static HttpResponse get(String url) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, AuthenticationException {
		CloseableHttpClient client = Connection.getHttpClient();

		if (Connection.accessToken.isEmpty()) {
			throw new AuthenticationException("Unauthorized access to remote resource.");
		}

		HttpGet get = new HttpGet(url);
		get.addHeader("Accept", "application/json");
		get.addHeader("Authorization", String.format("Bearer %s", Connection.accessToken));

		return client.execute(get);
	}

	public static HttpResponse post(String url, HttpEntity body) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException, KeyStoreException, AuthenticationException {
		CloseableHttpClient client = Connection.getHttpClient();

		if (Connection.accessToken.isEmpty()) {
			throw new AuthenticationException("Unauthorized access to remote resource.");
		}

		HttpPost post = new HttpPost(url);
		post.addHeader("Accept", "application/json");
		post.addHeader("Authorization", String.format("Bearer %s", Connection.accessToken));
		post.setEntity(body);

		return client.execute(post);
	}

	public static HttpResponse delete(String url) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException, KeyStoreException, AuthenticationException {
		CloseableHttpClient client = Connection.getHttpClient();

		if (Connection.accessToken.isEmpty()) {
			throw new AuthenticationException("Unauthorized access to remote resource.");
		}

		HttpDelete delete = new HttpDelete(url);
		delete.addHeader("Accept", "application/json");
		delete.addHeader("Authorization", String.format("Bearer %s", Connection.accessToken));

		return client.execute(delete);
	}

	public static void main(String[] args) {
		try {
			Connection.authenticate("importerV1", "imp", "fgr9");

		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException
				| AuthenticationException e) {
			e.printStackTrace();
		}
	}
}

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Crawler {
	private DefaultHttpClient client = null;
	private String base = null;
	private String username = null;
	private String password = null;
	public boolean login(String u, String p, String wbset) throws ClientProtocolException,
			IOException {
		username = u;
		password = p;
		client = new DefaultHttpClient();
		base = wbset;
		HttpPost postmethod = new HttpPost(base
				+ "/index.php?Controller=Users&action=FLogin");
		client.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", u));
			nvps.add(new BasicNameValuePair("password", p));
			postmethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = client.execute(postmethod);
			System.out.println("Post Username And Password");
			String entity = EntityUtils.toString(response.getEntity());
			if (!entity.equals("<script>self.location.href='index.php?Controller=Signin&action=Agree';</script>"))
				throw new Exception("Invalid Username or Password");

			HttpGet getmethod = new HttpGet(base
					+ "/index.php?Controller=Signin&action=Agree");
			response = client.execute(getmethod);
			getmethod.releaseConnection();

			getmethod = new HttpGet(base + "/index.php");
			response = client.execute(getmethod);
			getmethod.releaseConnection();

			getmethod = new HttpGet(base
					+ "/index.php?Controller=Show&bet_type=R&global_type=F");
			response = client.execute(getmethod);
			getmethod.releaseConnection();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

	}
	public void reLogin() throws ClientProtocolException, IOException{
		System.out.println("ReLogin");
		login(username,password,base);
	}
	public String crawRJson() {
		System.out.println("Start Getting Json(F/R)...");
		String x = null;
		try {
			HttpGet getmethod = new HttpGet(
					base + "/index.php?Controller=Show&action=GetData&bet_type=R&global_type=F");
			HttpResponse response = client.execute(getmethod);
			if (response.getStatusLine().getStatusCode() != 200){
				System.out.println("ReLogin");
				reLogin();
			}
				
			x = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			System.out.println("Error When Getting Json");
			System.out.println(e);
		}
		System.out.println("Finish Getting Json(F/R)...");
		return x;
	}

	public String crawREJson() {
		String x = null;
		System.out.println("Start Getting Json(F/RE)...");
		try {
			HttpGet getmethod = new HttpGet(
					base + "/index.php?Controller=Show&action=GetData&bet_type=RE&global_type=F");
			HttpResponse response = client.execute(getmethod);
			x = EntityUtils.toString(response.getEntity());
			if (response.getStatusLine().getStatusCode() != 200){
				System.out.println("ReLogin");
				reLogin();
			}
		} catch (Exception e) {
			System.out.println("Error When Getting Json");
			System.out.println(e);
		}
		System.out.println("Finish Getting Json(F/RE)...");
		return x;
	}
}

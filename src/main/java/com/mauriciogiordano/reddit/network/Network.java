package com.mauriciogiordano.reddit.network;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.List;

/**
 * This class is responsible to communicate with Reddit API.
 */
public class Network
{
    public static final String TAG = "NETWORK";

    private Delegate delegateReceiver;

    public final static String HOST = "www.reddit.com";
    public final static int GET 	= 0;
    public final static int POST 	= 1;

    public static class Status
    {
        public boolean hasInternet = false;
        public HttpClientHelper httpClientHelper = null;
        public HttpResponse response = null;
        public JSONObject result = null;
        public String value = null;
    }

    /**
     * Creates a new request to the API.
     * @param client The object that stores all the required information for the request.
     * @param method Whether is GET or POST. Use the static values provided on this class.
     * @param delegate The callback function.
     */
    public static final void newRequest(HttpClientHelper client, int method, Delegate delegate)
    {
        new APIRequest(client, method, delegate).execute("");
    }

    public static class APIRequest extends AsyncTask<String, String, String>
    {
        private HttpClientHelper client = null;
        private int	method = 0;

        /*
         * 0: GET
         * 1: POST
         */
        private Delegate delegateReceiver		= null;
        private HttpResponse response			= null;
        private JSONObject result				= null;
        private String value					= null;
        private boolean hasInternet				= true;

        public APIRequest(HttpClientHelper client, int method)
        {
            this.client 			= client;
            this.method 			= method;
        }

        public APIRequest(HttpClientHelper client, int method, Delegate delegateReceiver)
        {
            this.client 			= client;
            this.method 			= method;
            this.delegateReceiver 	= delegateReceiver;
        }

        public APIRequest(HttpClientHelper client, int method, Delegate delegateReceiver, String withValue)
        {
            this.client 			= client;
            this.method 			= method;
            this.delegateReceiver 	= delegateReceiver;
            this.value 				= withValue;
        }

        @Override
        protected String doInBackground(String... data)
        {
            try
            {
                switch(this.method)
                {
                    case GET:
                        this.response = client.executeGet();
                        break;
                    case POST:
                        this.response = client.executePost();
                        break;
                    default:
                        this.response = client.executeGet();
                }

                HttpEntity entity = response.getEntity();

                this.result = new JSONObject(EntityUtils.toString(entity));

                if(entity != null)
                {
                    entity.consumeContent();
                }
            }
            catch (UnknownHostException e) { e.printStackTrace(); }
            catch(Exception e) { e.printStackTrace(); }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(this.response == null)
            {
                this.hasInternet = false;
            }

            try
            {
                Log.d(TAG, "URI: " + client.getURI() + "\nRESPONSE: " + response.getStatusLine().toString());
                Log.d(TAG, "POST: "+ client.postParams().toString());
            } catch(NullPointerException e) {
                Log.d(TAG, "NO INTERNET CONNECTION!");
            }

            List<Cookie> cookies = this.client.getCookieStore().getCookies();
            for (int i = 0; i < cookies.size(); i++) {
                Log.d("Local cookie: ", cookies.get(i).toString());
            }

            Network.Status status = new Network.Status();

            status.hasInternet = this.hasInternet;
            status.httpClientHelper = this.client;
            status.response = this.response;
            status.result = this.result;
            status.value = this.value;

            if(delegateReceiver != null)
            {
                delegateReceiver.requestResults(status);
            }
        }
    }
}


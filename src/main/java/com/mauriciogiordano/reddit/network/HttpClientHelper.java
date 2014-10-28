package com.mauriciogiordano.reddit.network;

import android.app.Activity;
import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientHelper
{
    public static final Integer PORT 	= 80;
    public static final String METHOD	= "http";

    private URI uri = null;
    private String url;
    private String path = "";

    private static DefaultHttpClient httpClient = null;

    private static String cookie = null;

    private List<NameValuePair> dataPost = new ArrayList<NameValuePair>();
    private List<NameValuePair> dataGet  = new ArrayList<NameValuePair>();

    public HttpClientHelper(String path, Context context)
    {
        this.url = Network.HOST;
        this.path = path;

        if(httpClient == null)
        {
            httpClient = new DefaultHttpClient();

            cookie = context
                        .getSharedPreferences(
                            this.getClass().getPackage().getName(), Activity.MODE_PRIVATE)
                        .getString(".cookie", null);
        }
    }

    public void addParamForGet(String key, String value)
    {
        dataGet.add(new BasicNameValuePair(key, value));
    }

    public void addParamForPost(String key, String value)
    {
        dataPost.add(new BasicNameValuePair(key, value));
    }

    public HttpResponse executePost(boolean withCookie)
    {
        HttpResponse response = null;

        try {
            uri = URIUtils.createURI(METHOD, url, PORT, path,
                    dataGet == null ? null : URLEncodedUtils.format(dataGet, "UTF-8"), null);

            HttpPost httpPost = new HttpPost(uri);

            if(cookie != null && withCookie)
            {
                httpPost.addHeader("cookie", "reddit_session=" + cookie);
            }

            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            httpPost.setEntity(new UrlEncodedFormEntity(dataPost, HTTP.UTF_8));

            response = httpClient.execute(httpPost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return response;
    }

    public HttpResponse executeGet(boolean withCookie)
    {
        try {
            uri = URIUtils.createURI(METHOD, url, PORT, path,
                    dataGet == null ? null : URLEncodedUtils.format(dataGet, "UTF-8"), null);
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        HttpGet httpget = new HttpGet(uri);

        if(cookie != null && withCookie)
        {
            httpget.addHeader(new BasicHeader("cookie", "reddit_session=" + cookie));
        }

        HttpResponse response = null;

        try {
            response = httpClient.execute(httpget);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    public void setCookie(String cookie, Context context)
    {
        context.getSharedPreferences(
                this.getClass().getPackage().getName(), Activity.MODE_PRIVATE)
            .edit()
            .putString(".cookie", cookie)
            .commit();

        this.cookie = cookie;
    }

    public DefaultHttpClient getHttpClient() { return httpClient; }

    public String getURI()
    {
        return uri.toString();
    }

    public List<NameValuePair> getParams()
    {
        return dataGet;
    }

    public List<NameValuePair> postParams()
    {
        return dataPost;
    }
}
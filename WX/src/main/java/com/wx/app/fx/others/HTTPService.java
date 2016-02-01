package com.wx.app.fx.others;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by darren foung on 2016/1/20.
 */
public class HTTPService {

    private static final int CONNECTION_TIMEOUT = 30000;

    private static HTTPService instance;

    private HTTPService() {
    }

    public static HTTPService getInstance() {
        if (instance == null) {
            instance = new HTTPService();
        }
        return instance;
    }

    /**
     * 是否有可用的网络
     *
     * @param context
     * @return
     */
    public static boolean hasActiveNet(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 测试是否有wifi网络
     *
     * @param context
     * @return
     */
    public static boolean hasWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    public InputStream getStream(String url) {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == status) {
                return response.getEntity().getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get 请求
     *
     * @param url
     * @param cookie
     * @return
     */
    public String[] getRequest(String url, String cookie) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams,
                CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams,
                CONNECTION_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpGet get = new HttpGet(url);
        if (cookie != null) {
            get.addHeader("Cookie", cookie);
        }
        try {
            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");

            String message = "";
            if (HttpStatus.SC_OK == statusCode) {
                message = "ok";
            } else if (HttpStatus.SC_INTERNAL_SERVER_ERROR == statusCode) {
                message = "服务器内部错误";
            } else {
                if (result != null) {
                    JSONObject obj = JSON.parseObject(result);
                    if (obj != null && obj.containsKey("error")) {
                        message = obj.getString("error");
                    } else {
                        message = "发生未知错误";
                    }
                } else {
                    message = "发生未知错误";
                }
            }
            return new String[]{statusCode + "", result, message};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Post 请求
     *
     * @param url
     * @param headers
     * @param params
     * @param cookie
     * @return
     */
    public String[] postRequest(String url, Map<String, String> headers,
                                Map<String, String> params, String cookie) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(url);

        if (headers != null) {
            for (Map.Entry<String, String> item : headers.entrySet()) {
                String key = item.getKey();
                String value = item.getValue();
                post.addHeader(key, value);
            }
        }

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Map.Entry<String, String> item : params.entrySet()) {
                String key = item.getKey();
                String value = item.getValue();
                BasicNameValuePair basic = new BasicNameValuePair(key, value);
                nvps.add(basic);
            }
        }

        if (cookie != null) {
            post.addHeader("Cookie", cookie);
        }

        try {
            HttpEntity entity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
            String message = "";
            if (HttpStatus.SC_OK == statusCode) {
                message = "ok";
            } else if (HttpStatus.SC_INTERNAL_SERVER_ERROR == statusCode) {
                message = "服务器内部错误";
            } else {
                if (result != null) {
                    JSONObject obj = JSON.parseObject(result);
                    if (obj != null && obj.containsKey("error")) {
                        message = obj.getString("error");
                    } else {
                        message = "发生未知错误";
                    }
                } else {
                    message = "发生未知错误";
                }
            }
            return new String[]{statusCode + "", result, message};
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}

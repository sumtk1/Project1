package com.zyd.wlwsdk.server.network;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;



/**
 * Created by JinzLin on 2016/8/9.
 * 网络请求管理
 */
public class HttpManager {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static String URL;

    private static HttpManager httpManager;

    public static void init(String url) {
        URL = url;
    }

    public static HttpManager getInstance(){
//        if (httpManager == null){
        httpManager = new HttpManager();
//        }
        return httpManager;
    }


    public static RequestHandle doPost(final int requestTag, RequestParams params, final OnDataListener onDataListener, final int showLoad) {
        // 重连次数,超时时间
        client.setMaxRetriesAndTimeout(0, 5000);
        return client.post(URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                onDataListener.onStart(requestTag, showLoad);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onDataListener.onSuccess(requestTag, response, showLoad);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onDataListener.onFailure(requestTag, errorResponse, showLoad);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onDataListener.onCancel(requestTag, showLoad);
            }
        });
    }

    public static RequestHandle doGet(String url, final int requestTag, final OnDataListener onDataListener, final int showLoad) {
        // 重连次数,超时时间
        client.setMaxRetriesAndTimeout(0, 5000);
        return client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                onDataListener.onStart(requestTag, showLoad);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onDataListener.onSuccess(requestTag, response, showLoad);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onDataListener.onFailure(requestTag, errorResponse, showLoad);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onDataListener.onCancel(requestTag, showLoad);
            }
        });
    }

    /**
     *
     * @param requestTag
     * @param params
     * @param onDataListener
     * @param showLoad 加载框状态， 0表示弹出收起，1表示只弹出，2表示只收起
     * @param pullToRefreshLayout
     * @param requsetType  当数据位0时表示普通请求，为1时表示刷新请求，为2时表示加载请求
     * @return
     */
    public static RequestHandle doPost(final int requestTag, RequestParams params, final OnDataListener onDataListener, final int showLoad,
                                       final PullToRefreshLayout pullToRefreshLayout, final int requsetType) {
        // 重连次数,超时时间
        client.setMaxRetriesAndTimeout(0, 5000);
        return client.post(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                onDataListener.onStart(requestTag, showLoad);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (requsetType == 1) {
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
//                else if (requsetType == 2){
//                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                }
                onDataListener.onSuccess(requestTag, response, showLoad);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (requsetType == 1) {
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                } else if (requsetType == 2) {
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
                onDataListener.onFailure(requestTag, errorResponse, showLoad);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onDataListener.onCancel(requestTag, showLoad);
            }
        });
    }

    //TODO
    //商城URL请求地址
    private static String new_url = "http://192.168.1.176:9626//api.post";
    public static RequestHandle doPostWithShangCheng(final int requestTag, RequestParams params, final OnDataListener onDataListener, final int showLoad) {
        // 重连次数,超时时间
        client.setMaxRetriesAndTimeout(0, 5000);
        return client.post(new_url, params, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                onDataListener.onStart(requestTag, showLoad);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onDataListener.onSuccess(requestTag, response, showLoad);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onDataListener.onFailure(requestTag, errorResponse, showLoad);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onDataListener.onCancel(requestTag, showLoad);
            }
        });
    }

    //商城URL请求地址
    private static String old_url = "http://192.168.1.176:1781//interface";
    public static RequestHandle doPostWithOldShangCheng(final int requestTag, RequestParams params, final OnDataListener onDataListener, final int showLoad) {
        // 重连次数,超时时间
        client.setMaxRetriesAndTimeout(0, 5000);
        return client.post(old_url, params, new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
                onDataListener.onStart(requestTag, showLoad);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onDataListener.onSuccess(requestTag, response, showLoad);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onDataListener.onFailure(requestTag, errorResponse, showLoad);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onDataListener.onCancel(requestTag, showLoad);
            }
        });
    }

    /**
     * 有分页刷新加载的请求方式（不用pullToRefreshLayout）
     *
     * @param requestTag
     * @param params
     * @param onDataListener
     * @param showLoad       加载框状态， 0表示弹出收起，1表示只弹出，2表示只收起
     * @param requsetType    当数据位0时表示普通请求，为1时表示刷新请求，为2时表示加载请求
     * @return
     */
    public static RequestHandle doPost(final int requestTag, RequestParams params,  final OnDataListener onDataListener, final int showLoad,
                                       final int requsetType) {
        // 重连次数,超时时间
        client.setMaxRetriesAndTimeout(0, 5000);
        return client.post(URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                onDataListener.onStart(requestTag, showLoad);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onDataListener.onSuccess(requestTag, response, showLoad);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onDataListener.onFailure(requestTag, errorResponse, showLoad);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                onDataListener.onCancel(requestTag, showLoad);
            }
        });
    }

}

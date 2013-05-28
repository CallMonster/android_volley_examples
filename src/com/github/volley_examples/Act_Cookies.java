/**
 * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.volley_examples;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.volley_examples.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class Act_Cookies extends Activity {
    private TextView mTvCookie;
    private RequestQueue mQueue;
    private AbstractHttpClient mHttpClient;
    private Button mBtnSetCookie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__cookie);

        // we hold a reference to the HttpClient in order to be able to get/set cookies
        mHttpClient = new DefaultHttpClient();

        mQueue = Volley.newRequestQueue(Act_Cookies.this, new HttpClientStack(mHttpClient));

        mTvCookie = (TextView) findViewById(R.id.tv_cookie);
        setTvCookieText("n/a");

        Button btnRequest = (Button) findViewById(R.id.btn_execute_request);
        btnRequest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue.add(createRequest());
            }
        });

        mBtnSetCookie = (Button) findViewById(R.id.btn_set_cookie);
        mBtnSetCookie.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CookieStore cs = mHttpClient.getCookieStore();
                BasicClientCookie c = (BasicClientCookie) getCookie(cs, "my_cookie");
                c.setValue("41");
                cs.addCookie(c);

                mQueue.add(createRequest());
            }
        });
    }


    private StringRequest createRequest() {
        StringRequest myReq = new StringRequest(Method.GET,
                                                "http://khs.bolyartech.com/http_cookie.php",
                                                createMyReqSuccessListener(),
                                                createMyReqErrorListener());

        return myReq;
    }


    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CookieStore cs = mHttpClient.getCookieStore();
                BasicClientCookie c = (BasicClientCookie) getCookie(cs, "my_cookie");

                if (c != null) {
                    setTvCookieText(c.getValue());
                }
                mBtnSetCookie.setEnabled(true);
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setTvCookieText(error.getMessage());
            }
        };
    }


    private void setTvCookieText(String str) {
        mTvCookie.setText(String.format(getString(R.string.act_cookie__tv_cookie), str));
    }


    public Cookie getCookie(CookieStore cs, String cookieName) {
        Cookie ret = null;

        List<Cookie> l = cs.getCookies();
        for (Cookie c : l) {
            if (c.getName().equals(cookieName)) {
                ret = c;
                break;
            }
        }

        return ret;
    }
}

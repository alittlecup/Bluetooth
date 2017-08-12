package com.example.hbl.bluetooth.network;

import android.net.Uri;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by hbl on 2017/8/12.
 */

public class AddParamsInterceptor implements Interceptor {

    @Override

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String s = Uri.decode(bodyToString(request));
        Request.Builder builder = chain.request().newBuilder();
        builder.post(RequestBody.create(request.body().contentType(), s+"&u=xwjy&v=616888"));
        return chain.proceed(builder.build());
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            String s = buffer.readUtf8();
//            if (copy.body().contentType().toString().contains("form")) {
//                return formToJson(s);
//            } else {
                return s;
//            }

        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }

    private String formToJson(String body) {
        Map<String, String> map = new HashMap<>();
        if (body.contains("&")) {
            String[] split = body.split("&");
            for (String str : split) {
                String[] split1 = str.split("=");
                if (split1.length < 2) {
                    map.put(split1[0], "");
                } else {
                    map.put(split1[0], split1[1]);
                }
            }
        } else {
            String[] split = body.split("=");
            if (split.length < 2) {
                map.put(split[0], "");
            } else {
                map.put(split[0], split[1]);
            }
        }
        return new JSONObject(map).toString();
    }

}

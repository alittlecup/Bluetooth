package com.example.hbl.bluetooth.network;


import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * com.gomejr.myfangagent.core.network.interceptor.LoggingIntercepter
 *
 * @author Just.T
 * @since 17/1/12
 */
public class LoggingIntercepter implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static String TAG = "GomeNetwork";

    public static void init(String tag) {
        if (tag != null && tag.length() > 0)
            TAG = tag;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        log("================================================Request================================================");
        log("url : " + request.url());
        log("method : " + request.method());
        log("time :" + tookMs + "ms");
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Headers requestHeaders = request.headers();
        for (int i = 0, count = requestHeaders.size(); i < count; i++) {
            String name = requestHeaders.name(i);
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                log(name + ": " + requestHeaders.value(i));
            }
        }
        if (hasRequestBody) {
            if (requestBody.contentType() != null) {
                log("Content-Type: " + requestBody.contentType());
            }
            if (requestBody.contentLength() != -1) {
                log("Content-Length: " + requestBody.contentLength());
            }
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            //编码设为UTF-8
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (isPlaintext(buffer)) {
                log(buffer.readString(charset));
            }

        }
        log("================Response================");
        Headers headers = response.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            log(headers.name(i) + ": " + headers.value(i));
        }
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();


        if (bodyEncoded(response.headers())) {
            log("==================================================End==================================================");
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    log("==================================================End==================================================");
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                log("==================================================End==================================================");
                return response;
            }

            if (contentLength != 0) {
                //获取Response的body的字符串 并打印
                log(buffer.clone().readString(charset));
            }
            log("==================================================End==================================================");
        }

        return response;
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private void log(String log) {
        BLog.e(log);
    }
}

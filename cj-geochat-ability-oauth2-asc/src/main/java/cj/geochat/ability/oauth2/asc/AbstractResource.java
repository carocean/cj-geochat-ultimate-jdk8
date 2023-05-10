package cj.geochat.ability.oauth2.asc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

@Slf4j
public abstract class AbstractResource {
    protected void doResponse(feign.Response in, HttpServletRequest req, HttpServletResponse out) throws IOException {
        //TODO: 头的拷贝还有问题，一些头可能不合法。postman有时报无效字符,vue根本就无法调用。
        out.setStatus(in.status());
        Map<String, Collection<String>> srcHeaders = in.headers();
        for (String key : srcHeaders.keySet()) {
            String v = srcHeaders.get(key).stream().findFirst().get();
            out.setHeader(key, v);
        }
        InputStream inputStream = in.body().asInputStream();
        OutputStream outputStream = out.getOutputStream();
        byte[] buffer = new byte[80960];
        while (true) {
            int len = inputStream.read(buffer);
            if (len < 0) {
                break;
            }
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
    }

    protected byte[] readFully(feign.Response in) throws IOException {
        InputStream inputStream = in.body().asInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[80960];
        int len = 0;
        while ((len = inputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }
}

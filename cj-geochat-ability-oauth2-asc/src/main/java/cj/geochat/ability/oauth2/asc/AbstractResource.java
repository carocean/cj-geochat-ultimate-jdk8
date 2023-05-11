package cj.geochat.ability.oauth2.asc;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

@Slf4j
public abstract class AbstractResource {
    public static final String ENCODING = "ISO-8859-1";
    public static final String DELIMITER = "\r\n";

    protected void doResponse(feign.Response in, HttpServletRequest req, HttpServletResponse out) throws IOException {
        //TODO: 头的拷贝还有问题，一些头可能不合法。postman有时报无效字符,vue根本就无法调用。
        //vue报：HPE_INVALID_CHUNK_SIZE，如果outputStream.close()则报： HPE_UNEXPECTED_CONTENT_LENGTH
        out.setStatus(in.status());
        Map<String, Collection<String>> srcHeaders = in.headers();
        for (String key : srcHeaders.keySet()) {
            String v = srcHeaders.get(key).stream().findFirst().get();
            out.setHeader(key, v);
        }
        InputStream inputStream = in.body().asInputStream();
        OutputStream outputStream = out.getOutputStream();
        byte[] buffer = new byte[8096];
        while (true) {
            int len = inputStream.read(buffer);
            if (len < 0) {
                break;
            }
            //虽然这样能用，但vue要在本地代理中开启header：Connection: 'keep-alive'
            outputStream.write(buffer, 0, len);
            outputStream.flush();
        }
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

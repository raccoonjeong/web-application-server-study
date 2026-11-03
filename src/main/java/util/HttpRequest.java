package util;

import java.io.InputStream;

public class HttpRequest {

    private InputStream in;

    public HttpRequest(InputStream in) {
        this.in = in;
    }

    public String getMethod() {
        return "";
    }

    public String getPath() {
        return "";
    }

    public String getHeader(String connection) {
        return "";
    }

    public String getParameter(String userid) {
        return "";
    }
}

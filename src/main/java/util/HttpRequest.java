package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private InputStream in;
    private String method;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> parameters;

    public HttpRequest(InputStream in) throws IOException {
        this.in = in;
        readInput();
    }

    private void readInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line = br.readLine();

        String[] arr = line.split(" ");
        this.method = arr[0];

        if (arr.length == 1) {
            return;
        }

        String[] split = arr[1].split("\\?");
        this.path = split[0];
        if (split.length > 1) {
            System.out.println(split[1]);
            this.parameters = HttpRequestUtils.parseQueryString(split[1]);
        }
        System.out.println(this.parameters.get("userId"));

        this.headers = new HashMap<>();

        while ((line = br.readLine()) != null) {
            this.headers.put(line.split(":")[0], line.split(":")[1].trim()) ;

            if (line.isEmpty()) {
                break;
            }
        }

    }

    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }
}

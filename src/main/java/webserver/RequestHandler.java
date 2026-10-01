package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private static final List<User> users = new ArrayList<>();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        System.out.println("리스트 상태:" + users);

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = null;

            String line = br.readLine();
            String path = "";
            String queryString = "";

            String[] arr = line.split(" ");
            String method = arr[0];
            String paths = "";

            if (arr.length > 1) {
                paths = arr[1];
                String[] split = paths.split("\\?");
                path = split[0];
                if (split.length > 1) {
                    queryString = split[1];
                }
            }

            if ("GET".equals(method)) {

                Map<String, String> stringStringMap = HttpRequestUtils.parseQueryString(
                    queryString);
                body = Files.readAllBytes(new File("./webapp" + path).toPath());

                while ((line = br.readLine()) != null) {
                    System.out.println("GET:::::" + line);
                    if (line.isEmpty()) {
                        break;
                    }
                }
            }

            if ("POST".equals(method)) {

                int contentLength = 0;

                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    if ("Content-Length".equals(line.split(":")[0])) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                    if (line.isEmpty()) {
                        break;
                    }
                }

                String contentBody = IOUtils.readData(br, contentLength);
                Map<String, String> stringStringMap = HttpRequestUtils.parseQueryString(
                    contentBody);

                if ("/user/create".equals(path)) {
                    User user = new User(stringStringMap);
                    users.add(user);
                    System.out.println("리스트에추가:"+users);
                    response302Header(dos);
                    return;
                }

                if ("/user/login".equals(path)) {
                    User logined = new User(stringStringMap);
                    User user = users.stream().filter(x -> x.getUserId().equals(logined.getUserId())
                        && x.getPassword().equals(logined.getPassword())).findAny().orElse(null);
                    if (user == null) {
                        response200HeaderWithCookie(dos, "logined=false");
                        return;
                    }
                    response200HeaderWithCookie(dos, "logined=true");
                    return;
                }

                response302Header(dos);
                return;
            }

            if (body == null) {
                body = "Hello World".getBytes();
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderWithCookie(DataOutputStream dos, String cookieContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: " + cookieContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("Content-Length: " + 0 + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

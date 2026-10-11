package webserver;

import db.DataBase;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
//    private static final List<User> users = new ArrayList<>();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

//        System.out.println("리스트 상태:" + users);

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
                if ("".equals(path) || "/".equals(path)) {
                    path = "/index.html";
                }

                String cookieValue = "";
                while ((line = br.readLine()) != null) {
                    System.out.println("GET:::::" + line);
                    if ("Cookie".equals(line.split(":")[0])) {
                        cookieValue = line.split(":")[1].trim();
                    }
                    if (line.isEmpty()) {
                        break;
                    }
                }
                Map<String, String> cookieMap = HttpRequestUtils.parseCookies(
                    cookieValue);

                if ("/user/list".equals(path)) {
                    if ("true".equals(cookieMap.get("logined"))) {
                        StringBuilder sb = new StringBuilder();
                        Collection<User> all = DataBase.findAll();
                        Map<String, User> userMap = all.stream()
                            .collect(Collectors.toMap(
                                User::getUserId,
                                user -> user
                            ));

                        for (User user : userMap.values()) {
                            sb.append(
                                "<div><p>아이디: " + user.getUserId() + "</p><p>이름: " + user.getName()
                                    + "</p></div>");
                        }
                        body = sb.toString().getBytes();
                    } else {
                        response302Header(dos, "/user/login.html");
                        return;
                    }

                } else {
                    body = Files.readAllBytes(new File("./webapp" + path).toPath());
                    if ("/css/bootstrap.min.css".equals(path) || "/css/styles.css".equals(path)) {
                        response200HeaderWithCSS(dos);
                        responseBody(dos, body);
                        return;
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
                    DataBase.addUser(user);
                    response302Header(dos, "/index.html");
                    return;
                }

                if ("/user/login".equals(path)) {
                    User logined = new User(stringStringMap);
                    User userById = DataBase.findUserById(logined.getUserId());

                    boolean isMatched =
                        userById != null && userById.getPassword().equals(logined.getPassword());

                    if (isMatched) {
                        response302HeaderWithCookie(dos, "/index.html", "logined=true");
                        return;
                    }

                    response302HeaderWithCookie(dos, "/user/login.html","logined=false");
                    return;
                }

                response302Header(dos, "/index.html");
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

    private void response200HeaderWithCSS(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
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

    private void response302HeaderWithCookie(DataOutputStream dos, String path, String cookieContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Content-Length: " + 0 + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookieContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
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

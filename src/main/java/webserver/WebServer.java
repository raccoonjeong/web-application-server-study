package webserver;

import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            log.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                RequestHandler requestHandler = new RequestHandler(connection);
                requestHandler.start();
                /* LEARN:
                Thread 클래스 내  private native void start0 메소드
                native: "이 메소드의 구현은 자바 코드가 아니라 JVM 네이티브(C/C++) 코드에 있다"는 뜻. 하위 클래스가 구현하는 게 아니라, JNI를 통해 JVM 자체
                requestHandler.start()        // Thread.start() (상속)
                └─ start0()                   // native, JVM 내부 C++ 코드
                     └─ OS 스레드 생성 (pthread_create / CreateThread)
                          └─ JVM 내부에서 this.run() 콜백
                               └─ RequestHandler.run()  ← 실제 비즈니스 로직 (RequestHandler.java:21) */
            }
        }
    }
}

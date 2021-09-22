package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.StringTokenizer;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private Socket connection;

  public RequestHandler(Socket connectionSocket) {
    this.connection = connectionSocket;
  }

  public void run() {
    log.debug(
        "New Client Connect! Connected IP : {}, Port : {}",
        connection.getInetAddress(),
        connection.getPort());

    try (InputStream in = connection.getInputStream();
        OutputStream out = connection.getOutputStream()) {
      // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

      DataOutputStream dos = new DataOutputStream(out);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      String line = bufferedReader.readLine();

      StringTokenizer st = new StringTokenizer(line);
      if (line != null) {
        String[] tokens = line.split(" ");
        if (tokens[1].equals("/index")) {
          //          byte[] body = Files.readAllBytes(new File("./webapp" +
          // "/index.html").toPath());
          byte[] body = Files.readAllBytes(Paths.get("./webapp/index.html"));
          System.out.println(st.nextToken() + " 1");
          System.out.println(st.nextToken() + " 2");

          response200Header(dos, body.length);
          responseBody(dos, body);
        }

        // 회원가입 버튼
        if (tokens[1].equals("/user/form.html")) {
          byte[] body = Files.readAllBytes(Paths.get("./webapp/user/form.html"));
          response200Header(dos, body.length);
          responseBody(dos, body);
        }

        int index = tokens[1].indexOf("?");

        // 회원가입 제출
        if (index > 0) {
          String requestPath = tokens[1].substring(0, index);
          if (requestPath.equals("/user/create")) {
            Map<String, String> lineMap =
                HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
            String userId = lineMap.get("userId");
            String password = lineMap.get("password");
            String name = lineMap.get("name");
            String email = lineMap.get("email");
            User user = new User(userId, password, name, email);
            DataBase.addUser(user);
            System.out.println(DataBase.findUserById(userId));
          }
        }
      }
      byte[] body = "Hello Ae jeong".getBytes();
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

  private void responseBody(DataOutputStream dos, byte[] body) {
    try {
      dos.write(body, 0, body.length);
      dos.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}

package webserver;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.StringTokenizer;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
        // tokens[1] -> 요청 url
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
        // 로그인 버튼
        if (tokens[1].equals("/user/login.html")) {
          byte[] body = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
          response200Header(dos, body.length);
          responseBody(dos, body);
        }
        if (tokens[1].equals("/user/login")) {
          String dataLength = "";
          String checkUser = "";
          while (!line.equals("")) {
            if (line.contains("Content-Length")) {
              String[] lineItem = line.split(" ");
              dataLength = lineItem[1];
            }
            line = bufferedReader.readLine();
          }

          if (!dataLength.equals("")) {
            String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(dataLength));
            Map<String, String> lineMap = HttpRequestUtils.parseQueryString(requestBody);
            checkUser = findUser(lineMap);
          }
          cookie200Header(dos, checkUser);
        }

        // get 방식 회원가입
        int index = tokens[1].indexOf("?");
        if (index > 0) {
          String requestPath = tokens[1].substring(0, index);
          if (requestPath.equals("/user/create")) {
            Map<String, String> lineMap =
                HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
            saveUser(lineMap);
          }
        }

        // post 방식 회원가입
        if (tokens[1].equals("/user/create")) {
          String dataLength = "";
          // InputStream 라인이 없을 때 까지
          while (!line.equals("")) {
            if (line.contains("Content-Length")) {
              String[] lineItem = line.split(" ");
              dataLength = lineItem[1];
            }
            // 계속해서 다음 라인 확인
            line = bufferedReader.readLine();
          }

          if (!dataLength.equals("")) {

            String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(dataLength));
            log.debug(dataLength);
            log.debug(requestBody);

            Map<String, String> lineMap = HttpRequestUtils.parseQueryString(requestBody);
            saveUser(lineMap);
          }
          response302Header(dos, "/index");
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

  private void cookie200Header(DataOutputStream dos, String cookie) {
    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
      //      dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
      dos.writeBytes("Set-Cookie: " + cookie);
      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response302Header(DataOutputStream dos, String url) {
    try {

      dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
      dos.writeBytes("Location: " + url + "\r\n");
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

  private void saveUser(Map<String, String> userParameter) {
    String userId = userParameter.get("userId");
    String password = userParameter.get("password");
    String name = userParameter.get("name");
    String email = userParameter.get("email");
    User user = new User(userId, password, name, email);
    DataBase.addUser(user);
    System.out.println(DataBase.findUserById(userId));
  }

  private String findUser(Map<String, String> userParameter) {
    String checkLogin = "logined=false";
    String userId = userParameter.get("userId");
    String password = userParameter.get("password");
    System.out.println(userId);
    System.out.println(password);
    User user = DataBase.findUserById(userId);
    if (user.getPassword().equals(password)) {
      checkLogin = "logined=true";
    }
    return checkLogin;
  }
}

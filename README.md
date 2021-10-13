# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* BufferedReader 를 배울 수 있었다. 
  - 장점
    입력된 데이터가 바로 전달되지 않고 버퍼를 거쳐 전달되므로 데이터 처리 효율성이 높다.
    많은 양의 데이터를 처리할 때 유리하다.
  - 사용법
    BufferedReader buffer = new BufferedReader(new InputStreamReader());
    String line = buffer.readLine();  -> 라인 단위로 나눠진다.
  - 데이터 가공법
    1. StringTokenizer st = new StringTokenizer(line);
       st.nextToken()
    2. String[] tk = line.split(" ");
    
* Files.readAllByres 를 배울 수 있었다.
  - 프로젝트 내의 파일을 읽을 때 사용했는데 
    1. byte[] body = Files.readAllBytes(new File("./webapp"+"/index.html").toPath());
    2. byte[] body = Files.readAllBytes(Paths.get("./webapp/index.html")); 
    를 사용하여 index.html 의 내용을 response 해주었다.
    1번과 같이 파일 객체를 새로 선언하는 것보다 Paths 를 사용하는게 더 효율적일까?   

### 요구사항 2 - get 방식으로 회원가입
* request url 로 받은 요청을 substring 하여 url ,parameter 을 분리하여 DB에 insert 

### 요구사항 3 - post 방식으로 회원가입
*  // 대상 버퍼, 문자 저장할 시작점, 읽을 최대 문자 수
   br.read(body, 0, contentLength);
   header 뒤에 한줄 띄고 나서 body의 값을 읽을 수 있기 때문에 
   header에서 Content-Length 를 읽어와 IOUtils 에 작성해주신 메서드를 통해 body를 읽어낼 수 있었다.
   처음에는 header 포함 글자 수 인줄 알았는데 기본이 부족한 탓이다! 오늘도 배워간다!!

### 요구사항 4 - redirect 방식으로 이동
* 이 부분은 뒷부분을 조금 봐서 공부했다.
  Http 경우는 강의로 공부를 했는데 복습을 안해서인지 잘 꺼먹는것 같다 실제 구현할 때 많이 써봐야할 것 같다.
  우선 redirect 를 하기 위해 302 status code 를 사용해야 했다.
  무작정 이동시켜보니 주소가 변경되지 않았다. response302Header 메서드를 통해 Location : url 을 변경해주니 정상적으로 반영이 된다.
  작동 방식을 검색해 보니 post 요청 -> 요청 처리 -> response 로 location 포함 -> 해당 location 으로 재요청 함 -> 해당 요청 처리
  

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
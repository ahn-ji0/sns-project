### 미션 요구사항 분석 & 체크리스트

---

- [X] 회원가입, 로그인 기능 구현
- [X] 포스트 조회, 상세 조회 기능 구현
- [X] 포스트 작성, 수정, 삭제 기능 구현
- [X] ADMIN은 모든 포스트를 수정, 삭제할 수 있는 권한 부여
- [X] 인증/인가 필터 구현, 예외 처리
- [X] ExceptionManager 구현하여 컨트롤러에서 발생하는 예외 처리
- [X] ADMIN 회원은 다른 회원을 ADMIN 혹은 USER로 변경할 수 있는 권한 부여
- [X] AWS EC2 인스턴스에 애플리케이션 배포

### 1주차 미션 요약

---

http://ec2-13-124-140-201.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui.html

**[접근 방법]**

### 회원가입, 로그인 기능

_**회원가입**_

`POST /api/v1/users/join`

- 중복되는 이름이 있는지 확인 후, 없다면 비밀번호를 인코딩하여 DB에 저장한다. 
- BCryptPasswordEncoder 사용을 위해 EncrypterConfiguration에서 빈으로 등록하였다.

_**로그인**_

`POST /api/v1/users/login`

- 존재하는 이름인지 확인, 패스워드의 일치여부 확인 후 조건 만족시 토큰을 발급한다.

---
### 인증/인가 필터 구현, 예외 처리

Spring Security를 사용하기 위해서 의존성을 추가하고 SecurityConfig을 구성하였다. 

_**인증/인가 필터 구현**_

- 커스텀 필터 JwtFilter를 UsernamePasswordAuthenticationFilter 앞에 추가하였다. 
- JwtFilter에서는 헤더로 들어온 jwt 토큰이 적합한지, 유효한지를 확인하고 권한을 부여한다.


_**예외처리**_

- 기존의 RestControllerAdvice 어노테이션을 사용한 ExceptionManager는 컨트롤러에서 발생하는 예외를 처리하기 때문에 그 이전에 있는 서블릿 필터에서 발생하는 예외를 잡지 못한다. 
- 따라서 AuthenticationEntryPoint를 구현하는 CustomAuthenticationEntryPoint를 생성하여 필터에서 오류가 발생하면 이를 처리할 수 있도록 해주었다.

---
### 포스트 조회, 상세 조회 기능 구현

_**포스트 조회**_

`GET /api/v1/posts`

- size=20, createdAt 기준으로 내림차순으로 정렬하는 pageable 객체를 주입받는다.

_**포스트 상세 조회**_

`GET /api/v1/posts/{postId}`

- postId에 해당하는 포스트 존재여부 확인 후 반환한다.

---
### 포스트 작성 기능 구현

_**포스트 작성**_

`POST /api/v1/posts`
- 인증된 회원만 포스트 작성을 할 수 있다.
- 인증된 회원이 존재하는 유저인지 확인하고, 맞다면 request를 Entity로 변환하여 저장한다.

---
### 포스트 수정, 삭제 기능 구현

포스트 수정, 삭제 권한은 작성자 혹은 ADMIN 회원에게만 부여된다. JwtFilter에서는 유효한 토큰이 존재하면 url에 접근할 수 있는 권한을 주도록 하였고, 구체적인 사항(작성자 유저 일치 여부 등)은 service에서 구현하였다.

_**포스트 수정**_

`PUT /api/v1/posts/{postId}`

- 인증된 회원이 존재하는 유저인지 확인, postId에 해당하는 포스트의 존재 유무 확인, 작성자와 유저의 일치 여부 확인 후 entity를 수정하여 저장(update)한다. 
- 이때 authorities 정보를 통해서 회원의 role이 ADMIN이라면 작성자 일치 여부에 무관하게 수정이 가능하도록 하였다.

_**포스트 삭제**_

`DELETE /api/v1/posts/{postId}`

- 인증된 회원이 존재하는 유저인지 확인, postId에 해당하는 포스트의 존재 유무 확인, 작성자와 유저의 일치 여부 확인 후 entity를 삭제한다.
- 이때 authorities 정보를 통해서 회원의 role이 ADMIN이라면 작성자 일치 여부에 무관하게 삭제가 가능하도록 하였다.

---
### 컨트롤러 예외 처리

`@RestControllerAdvice`, `@ExceptionHandler`로 구현하였다.

---
### ADMIN 회원 - 다른 회원의 role 변경 권한

`POST /api/v1/users/{id}/role/change`

- ADMIN 회원은 다른 회원의 role을 ADMIN/USER로 변경할 수 있다. 
- SecurityConfiguration에 hasAuthority("ADMIN") 설정으로 해당 url에는 ADMIN 회원만 접근할 수 있도록 하였다.



---
**[특이사항]**

**1. 포스트 수정, 삭제 권한에 관하여**

포스트 수정, 삭제는 포스트를 작성한 본인만 할 수 있는데, 이를 어느 단에서 처리해야 할 지 고민이 되었다. 처음에는 인증 단계(특히 JwtFilter)에서 이를 처리하여 접근 자체를 못하게 막아야 한다고 생각을 했다. 하지만 결국 유저와 포스트 작성자가 일치함을 판단하기 위해서는 DB에 접근해야 한다. 따라서 Spring Security로는 회원의 유무만 판단하여 인가하고, service단에서 작성자와 유저가 일치하는 지를 확인하도록 로직을 작성하였다. 

**2. 컨트롤러 테스트에 관하여**

포스트 작성 컨트롤러 테스트의 JWT가 잘못되었거나 유효하지 않은 경우 실패하는 테스트를 작성하는 과정에서 어떻게 접근을 해야할 지 고민이 되었다. MockMvc는 테스트용 DispatcherServlet에 요청을 보내고 DispatcherServlet은 매핑 정보를 토대로 적절한 핸들러를 호출하는 작업을 하는데, JWT가 유효한지 아닌지를 판단하는 JwtFilter는 서블릿 필터에 존재한다. 여기서 문제점은 서블릿 필터가 DispatcherServlet 앞에 배치가 되어있다는 점이다. 그렇다면 WebMvcTest로는 서블릿 필터에 대한 접근이 가능하지 않을텐데, 이를 어떻게 주입해야 할지에 대한 고민을 하였다. 


### 2주차 미션 요약

---
http://ec2-13-124-140-201.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui.html

**[접근 방법]**
### 인증 예외 처리
- 인증단계에서 발생할 예외를 종류에 따라 WRONG_TOKEN, EXPIRED_TOKEN, USER_NOTFOUND, INVALID_TOKEN으로 구분하여 발생시켰다.
- HttpServletRequest 객체의 setAttribute, getAttribute을 통해 예외 정보를 전달하였다.
---
### Soft Delete 구현
- `@SQLDelete` 어노테이션으로 해당 엔티티에 대한 delete 메소드 쿼리 호출 시 사용자가 지정한 쿼리를 보낼 수 있도록 구현하였다.
  - delete 쿼리 대신 deleted_at 필드를 현재 시간으로 update 하는 쿼리를 보내도록 설정하였다.
- `@Where` 어노테이션으로 deleted_at이 null인 데이터만 조회될 수 있도록 하였다.

---
### 마이피드 기능 구현
_**마이피드**_

`Get /api/v1/posts/my`

- 로그인된 유저의 피드 목록(모든 포스트)을 제공하는 기능
- size=20이고 createdAt을 기준으로 내림차순으로 정렬하는 pageable 객체를 주입받는다.

---
### 댓글 작성 기능 구현
_**댓글 작성**_

`POST /api/v1/posts/{postId}/comments`

- 특정 포스트에 댓글을 작성하는 기능.
- 로그인된 유저만이 댓글을 작성할 수 있다. 
---
### 댓글 수정, 삭제 기능 구현
_**댓글 수정**_

`PUT /api/v1/posts/{postId}/comments/{commentId}`

- 자신이 작성한 댓글만 수정할 수 있다. 이때 예외적으로 userRole이 ROLE_ADMIN인 회원은 모든 댓글을 수정할 수 있다.
- 유저 존재 여부, 포스트 존재 여부, 커맨트 존재 여부, 포스트 id와 커맨트 id의 대응 여부, 로그인한 유저와 작성자의 일치여부를 확인한다.
- postService의 editComment 메소드에 트랜젝션 처리하여 save 메소드 쿼리를 호출하지 않고 DB에 반영되도록 구현하였다.
  - https://velog.io/@ji0ahn/Spring-Data-JPA-save-쿼리-메소드를-호출하지-않고-데이터베이스에-엔티티-변경-내용을-반영하기
  
_**댓글 삭제**_

`DELETE /api/v1/posts/{postId}/comments/{commentId}`

- 자신이 작성한 댓글만 삭제할 수 있다. 이때 예외적으로 userRole이 ROLE_ADMIN인 회원은 모든 댓글을 삭제할 수 있다.
- 유저 존재 여부, 포스트 존재 여부, 커맨트 존재 여부, 포스트 id와 커맨트 id의 대응 여부, 로그인한 유저와 작성자의 일치여부를 확인한다.


- _**포스트 삭제 수정사항**_
  - 포스트 삭제 시 포스트에 작성된 comment, likes도 삭제 처리될 수 있도록 수정하였다.

---
### 댓글 조회 기능 구현
_**댓글 조회**_

`GET /api/v1/posts/{postId}/comments`

- 모든 사용자가 접근할 수 있다.
- size=10이고 createdAt을 기준으로 내림차순으로 정렬하는 pageable 객체를 주입받는다.

---
### 좋아요 누르기 기능 구현
_**좋아요 누르기**_

`POST /posts/{postId}/likes`

- 로그인한 사용자만 좋아요 누르기 가능.
- 좋아요는 유저마다 하나의 포스트당 한 번씩만 누를 수 있으며, 중복으로 누를 시 예외가 발생한다.

---
### 좋아요 개수 조회 기능 구현
_**좋아요 개수 조회**_

`POST api/v1/posts/{postId}/likes`

- postId에 해당하는 포스트의 좋아요 개수를 조회하는 기능.
- size=20이고 createdAt을 기준으로 내림차순으로 정렬하는 pageable 객체를 주입받는다.
---
### 알람 기능 구현
_**알람 발생시키기**_
- 유저가 작성한 포스트에 댓글, 좋아요가 눌리는 즉시 알람을 발생시키도록 구현
- 리스너 클래스 AlarmListener를 작성하여 `PostPersist`시 postService의 alarm 메소드가 호출될 수 있도록 구현하였고 해당 클래스를 Comment, Likes 엔티티의 EntityListener로 등록하였다.
  - postService의 alarm 메소드에서는 Alarm 엔티티를 구성하여 데이터베이스에 저장될 수 있도록 하였다.

_**알람 조회**_

`GET api/v1/posts/alarms`

- 로그인 한 유저의 알람 조회하는 기능.
- size=20이고 createdAt을 기준으로 내림차순으로 정렬하는 pageable 객체를 주입받는다.
---

**[특이사항]**

_**리팩토링 계획**_

_1. post controller에서 comment controller, likes controller 분리_

_2. 서비스 단의 getUserByUserName, getPostById 등 여러 서비스단에서 공통으로 사용되는 메소드 클래스로 추출_

_**테스트 코드 추가 계획**_

_1. soft delete DB 테스트_

_2. 인증 filter단 테스트_
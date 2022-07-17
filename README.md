테스트 전 메모를 제외한 아래 설명을 읽어봐 주세요.

감사합니다.

# 구현 목록
1. TODO CRUD
2. Authorization
    
    Jwt 를 활용한 인증 구현   
3. Image Upload

    Todo 생성시 파일 첨부 구현
4. Dev / Prod Environment
    
    application.yml 설정 분리
5. Unit Test, Integration Test
6. ~~CI / CD~~ 미구현

# 변경 점
### 1. Todo Schema

[Todo Model](https://todos.dietfriends.kr/)

[Todo Full](https://dietfriends.stoplight.io/docs/todo-api/3ffde70245266-todo-full)

[Todo Partial](https://dietfriends.stoplight.io/docs/todo-api/c2NoOjQ4MTc5MDgy-todo-partial)

Todo Partial 은 Todo Full 의 일부분으로 schema 를 따로 생성해야하나 의문이 들던 와중 Todo Model 과 Todo Full 의 key 값도 일치하지 않는 것 같아 Todo Schema 단일로 처리하였습니다.
추가로 파일업로드 구현에 따른 File Schema 도 추가 되었습니다.

```java
public class Todo {
    private int id;
    private String name;
    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;
    private List<FileInfo> fileInfos;
}
```

### 2. List Todos 파라미터 누락

Query Parameters 중 `limit` `skip` 두가지가 요구되었는데, `skip`이 무엇인지 잘 모르겠습니다. 문의 결과 Page 구현을 요구하시는 것 같지만 skip 에 대한 정보는 찾을 수 없어 `limit` `page` 로 구현하였습니다.

### 3. Authorization

JWT 인증 구현에 따라 `signin` `signup` 제외 요청시 Header 에 토큰 정보 필수입니다.
`Authorization : accessToken` 

요구된 `apikey` 와 별개이며, `refreshToken`은 발급은 하나 관련 로직은 구현하지 않았습니다.

# APIKEY = seonghamoon

# url

시간관계상 swagger 및 docs 는 작성하지 못했습니다. root 경로의 postman.zip 혹은 `*Controller.java` 를 참고해 주세요.

---
---
---
---

# 메모
- 다하고 보니까 `FileInfo` 에 type 을 빼먹음.
- filter 동작 시에는 yml 을 읽어 올 수 없음 Environment 를 통해 해결하긴 했으나 lifecycle 확인 하도록.
- application.yml 그룹 분리 시 Test 에 어떤 설정으로 할지 명시해야함. `@ActiveProfiles("local")`
- nginx 서브 도메인이 안먹는 이유는?

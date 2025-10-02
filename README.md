해당 레지스트리는 집컴으로 연습하는 용도 
메인 파일은 Myplace 레지스트리에 있음


홈 화면(할 일 알림) 중간지점 탐색 내 플레이스 캘린더 통계



1. 하단 네비게이션 메뉴는 5개로 고정
2. 지도 api는 중간지점 탐색, 내 플레이스 화면에서만 사용(불필요한 api호출 x)
3. 통계에는 일일 이동거리, 예상 교통비, 월일 이동거리, 예상 교통비를 표시함 , 약속 일 수를 표시함
4. MVVM 패턴으로 구현을 해야함.
5. MODEL : 데이터 구조 정의 ViewModel : Live data활용 및 api관련 비즈니스 로직(데이터 수정,제어등등)  View : ViewModel에서 수정하거나 제어된 데이터를 data binding으로 가져와 화면에 표시
6. Interface를 활용하여 가독성과 더 쉬운 코드 관리를 할 수 있게 하도록 생각중(아직 어떤 식으로 사용해야 할지 감이 안잡힘)
7. Api Service관련 로직을 어떻게 분리하여 모듈화 시킬지를 고민해야함.(모듈화 시켜 불필요한 api호출 이나 자원낭비를 막아야 함.)



기능 구현

1.홈 화면
  - 오늘의 스케줄을 표시(캘린더 화면내용 연동)
  - 오늘의 스케줄이 존재한다면 broadcast로 알림메시지를 보냄
  - 리스트 뷰로 구성 되있으며 하단에 일일 메모 추가가능( 하루가 지나면 메모 자동 삭제)
  - (SQLite 또는 다른화면에 있는 정보를 Live Data 옵저버를 활용해 databinding으로 불러와서 정렬할까 고민중)




    

2.중간 지점 탐색
  - 위치 a, b를 입력하면 중간 지점을 찾아준다.(구글 중간경로 탐색 api가 존재함, 다른데도 있는지 확인 해야 함.)
  - 하단에 예상 시간등을 확인 할 수 있게 표시된다.
    
<img width="237" height="530" alt="image" src="https://github.com/user-attachments/assets/a86ae4bc-2bd8-46ec-a57f-ca0b44ba6d7b" />


  - 장소를 입력하면 연관검색어가 뜨도록..
    
<img width="238" height="527" alt="image" src="https://github.com/user-attachments/assets/89690547-4e12-4e93-888b-12a2e1407323" />



3.내플레이스 탐색

  - 나만의 장소를 찾아 추가하고 저장 해놓을 수 있음

![image](https://github.com/user-attachments/assets/75e81e2b-3318-4e94-9830-be50be1ecd51)


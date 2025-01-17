## 프로젝트 설명
- ai 모델과 통신해서 이미지 조정 기능을 제공하는 서버

## 프로젝트 진행 상황
- 현재 프로토타입 구현 중이며, 프론트엔드 없이 간단한 html 템플릿에서 이미지 가공 결과를 확인할 수 있도록 함
![img](./img/presize-proto.jpg)

## 접근 가능 url
- http://ec2-43-200-97-22.ap-northeast-2.compute.amazonaws.com:8080/test/upload
- 윗쪽 폼은 입력받은 이미지를 같이 받은 너비, 높이 값으로 업스케일한 결과를 보여줌
- 아랫쪽 폼은 입력받은 이미지를 정사각형 형태로 수정한 결과를 보여줌
![form](./img/presize-form.png)

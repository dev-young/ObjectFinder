package io.ymsoft.objectfinder

interface TodoList {

    // TODO: 2020-05-17 AddStorageFragment mvvm 다시 설계하기
    // TODO: 2020-05-26 검색화면에서 아이템 삭제시 이벤트 처리

    // TODO: 2020-05-18 오브젝트 복수선택하여 이동 기능 추가
    // TODO: 2020-05-18 저장소 삭제시 사진도 함께 삭제
    // TODO: 2020-05-25 이미지 저장시 파일 경로를 저장해야하나 파일 명을 저장해야하나?

    // 디자인
    // TODO: 2020-05-17 포인터 애니메이션 효과 넣기
    // TODO: 2020-05-25 회면 회전시 바텀 앱바 보이는 현상



    /**참고사항
     * 뷰모델 사용: https://developer.android.com/topic/libraries/architecture/viewmodel#implement
     *
     * 필요한 기능들을 쭉 나열하고 한 화면마다 어떤 기능들을 구성할지 정리해보면 좋을 듯 하다.
     *
     * 데이터 추가, 삭제, 변경시 프로그레스 표시는 각 작업마다 뷰모델에 LiveData 변수를 만들어 관리하면 좋을 듯 하다. (내 생각임)
     * */

}
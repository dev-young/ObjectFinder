package io.ymsoft.objectfinder

interface TodoList {

    // TODO: 2020-05-17 StorageDetailFragment 에서 사용할 뷰모델 따로 생성하기 (현재 StorageViewModel 사용중)
    // TODO: 2020-05-19 StorageDetailFragment 에 Storage 수정, 삭제 메뉴 추가
    // TODO: 2020-05-17 AddStorageFragment mvvm 적용
    // TODO: 2020-05-20 사진 추가시 특정 사진들은 회전되는 현상 수정하기
    // TODO: 2020-05-17 사진 추가 로직 최적화
    // TODO: 2020-05-17 이미지 크기 최적화 ( +정사각형으로? )
    // TODO: 2020-05-18 오브젝트 복수선택하여 이동 기능 추가
    // TODO: 2020-05-18 한번 찍은 사진 다른 장소로도 사용할 수 있게 하기
    // TODO: 2020-05-18 저장소 삭제시 사진도 함께 삭제

    // 디자인
    // TODO: 2020-05-17 포인터 애니메이션 효과 넣기



    /**참고사항
     * 뷰모델 사용: https://developer.android.com/topic/libraries/architecture/viewmodel#implement
     *
     * 필요한 기능들을 쭉 나열하고 한 화면마다 어떤 기능들을 구성할지 정리해보면 좋을 듯 하다.
     *
     * 데이터 추가, 삭제, 변경시 프로그레스 표시는 각 작업마다 뷰모델에 LiveData 변수를 만들어 관리하면 좋을 듯 하다. (내 생각임)
     * */

}
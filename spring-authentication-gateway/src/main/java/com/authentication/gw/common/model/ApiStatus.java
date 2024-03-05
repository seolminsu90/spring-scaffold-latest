package com.authentication.gw.common.model;

import lombok.Getter;

@Getter
public enum ApiStatus {
    SUCCESS(2000, "성공"),
    FAIL(1000, "실패"),
    UNAUTHORIZED(4001, "인증이 실패하였습니다."),
    BAD_REQUEST(4002, "잘못된 요청입니다."),
    BAD_REQUEST_NOT_VALID(4003, "잘못된 요청입니다. - 필수 값이 누락되었거나 형식에 맞지 않습니다."),
    BAD_REQUEST_SERVICE(4004, "등록되지 않은 서비스를 호출하려 하고 있습니다."),
    UNAUTHORIZED_TOKEN_VERIFY_FAIL(4005, "인증이 실패하였습니다. - 토큰 검증에 실패했습니다."),
    FORBIDDEN(4006, "기능을 사용할 수 있는 권한이 없습니다."),
    BAD_REQUEST_ROUTE_ID(4007, "라우트 아이디 (권한)은 시스템 어드민 ROLE 이름과 같을 수 없습니다."),
    BAD_REQUEST_ROLE_UPDATE_FAIL(4008, "잘못된 요청입니다. - 권한 설정에 실패했습니다."),
    INTERNAL_SERVER_ERROR(5000, "내부 서버 오류");

    private final int code;
    private final String desc;

    ApiStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}

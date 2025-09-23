package com.teamalgo.algo.global.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 사용자 이름입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않거나 만료되었습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    OAUTH_FAILED(HttpStatus.UNAUTHORIZED, "OAuth 인증에 실패했습니다."),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "기록을 찾을 수 없습니다."),
    ALREADY_REVIEWED(HttpStatus.BAD_REQUEST, "이미 복습 완료 처리된 문제입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 이미지 형식입니다."),

    MISSING_TITLE(HttpStatus.BAD_REQUEST, "제목을 입력해주세요."),
    // 레코드 발행 시 에러
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "Status 값은 success 또는 fail 이어야 합니다."),
    INVALID_DIFFICULTY(HttpStatus.BAD_REQUEST, "난이도는 1~5 사이여야 합니다."),
    INVALID_CODES(HttpStatus.BAD_REQUEST, "코드는 최소 1개 이상 필요합니다."),
    INVALID_STEPS(HttpStatus.BAD_REQUEST, "풀이 단계는 최소 1개 이상 필요합니다."),
    INVALID_CATEGORIES(HttpStatus.BAD_REQUEST, "카테고리는 최소 1개 이상 필요합니다."),
    INVALID_DETAIL(HttpStatus.BAD_REQUEST, "상세 설명은 비워둘 수 없습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않은 이미지 형식입니다."),

    INVALID_REDIRECT_URI(HttpStatus.BAD_REQUEST, "유효하지 않은 Redirect URI 입니다.");

    private final HttpStatus status;
    private final String message;
}


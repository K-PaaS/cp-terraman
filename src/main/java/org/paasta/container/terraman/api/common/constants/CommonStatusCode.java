package org.paasta.container.terraman.api.common.constants;

public enum CommonStatusCode {
    OK(200, "정상적으로 처리 되었습니다.", "Processed successfully."),
    BAD_REQUEST(400, "잘못된 요청을 처리할 수 없습니다.", "Incorrect request. Could not be processed."),
    UNAUTHORIZED(401, "인증 오류입니다.", "Authentication error."),
    FORBIDDEN(403, "페이지 접근 허용이 거부 되었습니다.", "Page access denied."),
    NOT_FOUND(404, "찾을 수 없습니다.", "Could not be found."),
    CONFLICT(409, "요청을 수행하는 중에 충돌이 발생하였습니다.", "A crash occurred while performing the request."),
    UNPROCESSABLE_ENTITY(422, "문법 오류로 인하여 요청을 처리할 수 없습니다.", "The request could not be processed due to a grammatical error."),
    INTERNAL_SERVER_ERROR(500, "요청 사항을 수행 할 수 없습니다.", "The request could not be processed."),
    SERVICE_UNAVAILABLE(503, "서버가 요청을 처리할 준비가 되지 않았습니다.", "The server is not ready to process the request."),
    MANDATORY(1000, "Required value.", "Required value."),
    INVALID_FORMAT(1001, "Invalid YAML Format.", "Invalid YAML Format.");

    private int code;
    private String ko_msg;
    private String eng_msg;

    CommonStatusCode(int code, String ko_msg, String eng_msg) {
        this.code = code;
        this.ko_msg = ko_msg;
        this.eng_msg = eng_msg;
    }

    public int getCode() {
        return code;
    }

    public String getKo_msg() {
        return ko_msg;
    }

    public String getEng_msg() {
        return eng_msg;
    }

    public String getMsg() {
        String u_lang = "";
        try {
            u_lang = Constants.U_LANG_KO;
        } catch (Exception e) {
            return getEng_msg();
        }
        if (u_lang.equals(Constants.U_LANG_KO)) {
            return getKo_msg();
        }
        return getEng_msg();
    }

}

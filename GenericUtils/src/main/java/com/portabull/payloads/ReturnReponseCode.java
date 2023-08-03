package com.portabull.payloads;

import com.portabull.constants.PortableConstants;
import com.portabull.response.EmailResponse;
import com.portabull.response.PortableResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;

public class ReturnReponseCode {

    private ReturnReponseCode() {
    }

    /**
     * In the given response based on statusCode,status,statuscode it will take the status code in given response
     *
     * @param response
     * @return
     * @throws IllegalAccessException
     */

    public static <T> ResponseEntity<T> generateResponseEntity(T response) throws IllegalAccessException {
        if (response == null) {
            return new ResponseEntity(new PortableResponse(
                    "Api Failed", 500L,
                    PortableConstants.FAILED, null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Object data = null;
        try {
            try {
                data = accessPrivateField(response, "statusCode");
            } catch (NoSuchFieldException e) {
                try {
                    data = accessPrivateField(response, "status");
                } catch (NoSuchFieldException ex) {
                    try {
                        data = accessPrivateField(response, "statuscode");
                    } catch (NoSuchFieldException exe) {
                        data = "500";
                    }
                }
            }
        } catch (IllegalAccessException e) {
            data = "500";
        }
        if (data != null) {
            return new ResponseEntity(response, getHttpStatusCode(Long.valueOf(data.toString()).intValue()));
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    public static <T> T accessPrivateField(Object data, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field privateField = data.getClass().getDeclaredField(fieldName);
        privateField.setAccessible(true);
        return (T) privateField.get(data);
    }

    public static ResponseEntity<EmailResponse> generateResponseEntity(EmailResponse emailResponse) {
        if (emailResponse == null) {
            emailResponse = new EmailResponse();
            emailResponse.setStatusCode(500L);
            emailResponse.setMessage("Api Failed");
            emailResponse.setStatus(PortableConstants.FAILED);
            return new ResponseEntity<>(emailResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(emailResponse,
                ReturnReponseCode.getHttpStatusCode(
                        emailResponse.getStatusCode() != null ? emailResponse.getStatusCode().intValue() : 200));
    }

    public static HttpStatus getHttpStatusCode(int statusCode) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.value() == statusCode) {
                return status;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }


}

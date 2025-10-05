package org.coderun.compiler.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean success;
    private String error;
    private T data;

    public ApiResponse(boolean success, String error, T data) {
        this.success = success;
        this.error = error;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true, null, data);
    }

    public static ApiResponse<Void> success(){
        return new ApiResponse<>(true, null, null);
    }

    public static <T> ApiResponse<T> error(String error){
        return new ApiResponse<>(false, null,null);
    }

}

package org.team14.webty.common.dto;

import lombok.Getter;
import org.team14.webty.user.dto.UserDto;

@Getter
public class ApiResponse<T> {
    private final UserDto user;
    private final T data;
    
    public ApiResponse(UserDto user, T data) {
        this.user = user;
        this.data = data;
    }
} 
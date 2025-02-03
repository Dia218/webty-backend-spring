package org.team14.webty.common.response;

import lombok.Getter;
import org.team14.webty.user.dto.UserDto;

@Getter
public class StandardResponse<T> {
    private final UserDto user;
    private final T data;
    
    public StandardResponse(UserDto user, T data) {
        this.user = user;
        this.data = data;
    }
} 
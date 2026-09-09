package com.nadila.training_management_system_api.response;

import com.nadila.training_management_system_api.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private ResponseStatus responseStatus;
    private String message;
    private Object data;
}

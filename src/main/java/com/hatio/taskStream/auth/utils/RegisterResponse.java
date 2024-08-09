package com.hatio.taskStream.auth.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {



        private Integer id;
        private String name;
        private String email;
        private String username;
        private String message;

    }

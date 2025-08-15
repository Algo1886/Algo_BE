package com.teamalgo.algo.global.common.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    HttpStatus getStatus();
    String getMessage();
}
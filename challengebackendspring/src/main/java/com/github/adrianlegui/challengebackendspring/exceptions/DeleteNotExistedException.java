package com.github.adrianlegui.challengebackendspring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.experimental.StandardException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@StandardException
public class DeleteNotExistedException extends RuntimeException {private static final long serialVersionUID = 1L;

}

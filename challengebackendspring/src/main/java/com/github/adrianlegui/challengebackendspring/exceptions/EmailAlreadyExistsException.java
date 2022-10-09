package com.github.adrianlegui.challengebackendspring.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.experimental.StandardException;

@StandardException
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class EmailAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}

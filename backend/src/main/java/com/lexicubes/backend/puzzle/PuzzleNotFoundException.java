package com.lexicubes.backend.puzzle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such puzzle found")
public class PuzzleNotFoundException extends RuntimeException {}

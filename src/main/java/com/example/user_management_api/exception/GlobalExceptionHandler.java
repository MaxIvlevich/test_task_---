package com.example.user_management_api.exception;

import com.example.user_management_api.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Обрабатывает {@link MethodArgumentNotValidException}, возникающее при
     * провале валидации DTO, помеченного аннотацией {@code @Valid}.
     *
     * @param ex      исключение, содержащее детали ошибок валидации полей.
     * @param request объект запроса.
     * @return {@link ResponseEntity} со статусом 400 (Bad Request) и списком ошибок.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                errors,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает кастомное исключение {@link UserNotFoundException},
     * когда запрашиваемый пользователь не найден в базе данных.
     *
     * @param ex      перехваченное исключение {@code UserNotFoundException}.
     * @param request объект запроса.
     * @return {@link ResponseEntity} со статусом 404 (Not Found).
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает {@link BadCredentialsException} при неверном логине/пароле
     * или при некорректном старом пароле во время его смены.
     *
     * @param ex      перехваченное исключение {@code BadCredentialsException}.
     * @param request объект запроса.
     * @return {@link ResponseEntity} со статусом 400 (Bad Request).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает {@link TokenRefreshException} при ошибках с refresh-токеном
     * (например, если он истек или не найден).
     *
     * @param ex      перехваченное исключение {@code TokenRefreshException}.
     * @param request объект запроса.
     * @return {@link ResponseEntity} со статусом 403 (Forbidden).
     */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenRefreshException(TokenRefreshException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Обрабатывает все непредвиденные исключения как последняя линия защиты.
     * Гарантирует возврат стандартизированного JSON-ответа со статусом 500.
     *
     * @param ex      перехваченное исключение.
     * @param request объект запроса.
     * @return {@link ResponseEntity} со статусом 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("An unexpected error occurred for request: {}", request.getRequestURI(), ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: ",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает ошибки, связанные с нарушением целостности данных в БД,
     * например, при попытке вставить запись с уже существующим уникальным значением.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = "A user with the given username or email already exists.";
        log.warn("Data integrity violation for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Обрабатывает ошибки, когда тип параметра в URL не соответствует ожидаемому
     * (например, передана строка вместо UUID).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        log.warn("Method argument type mismatch for request to {}: {}", request.getRequestURI(), message);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Обрабатывает исключение {@link HttpMessageNotReadableException}.
     * <p>
     * Это исключение возникает, когда тело HTTP-запроса не может быть прочитано или
     * преобразовано в ожидаемый объект. Наиболее частые причины:
     *
     * @param ex      перехваченное исключение {@code HttpMessageNotReadableException}.
     * @param request объект запроса, содержащий информацию о запросе, который вызвал ошибку.
     * @return {@link ResponseEntity} со статусом 400 (Bad Request) и телом,
     *         сообщающим о неверном формате запроса.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Failed to read HTTP message for request to {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON request or invalid data format.",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Обрабатывает исключение {@link HttpRequestMethodNotSupportedException}.
     * <p>
     *
     * @param ex      перехваченное исключение {@code HttpRequestMethodNotSupportedException}.
     * @param request объект запроса, содержащий информацию о запросе, который вызвал ошибку.
     * @return {@link ResponseEntity} со статусом 405 (Method Not Allowed) и телом,
     *         информирующим клиента о недопустимом методе запроса.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("HTTP method not supported for request to {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                String.format("Request method '%s' not supported for this endpoint.", ex.getMethod()),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
    /**
     * Обрабатывает исключение {@link IllegalArgumentException}.
     * <p>
     *
     * @param ex      перехваченное исключение {@code IllegalArgumentException}.
     * @param request объект запроса, содержащий информацию о запросе, который вызвал ошибку.
     * @return {@link ResponseEntity} со статусом 400 (Bad Request) и телом,
     *         содержащим детализированную информацию об ошибке.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument for request to {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    /**
     * Обрабатывает исключение, которое возникает при попытке загрузить файл,
     * превышающий настроенные лимиты.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("Max upload size exceeded for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "File size exceeds the allowable limit.",
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение {@link MissingServletRequestParameterException}.
     * <p>
     * Это исключение возникает, когда в запросе отсутствует обязательный параметр.
     * В нашем случае, это наиболее вероятно при попытке загрузить аватар без предоставления
     * файла в поле с именем "file".
     *
     * @param ex      перехваченное исключение {@code MissingServletRequestParameterException}.
     * @param request объект запроса, содержащий информацию о запросе, который вызвал ошибку.
     * @return {@link ResponseEntity} со статусом 400 (Bad Request) и телом,
     *         информирующим клиента о недостающем параметре.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("Missing request parameter for request to {}: {}", request.getRequestURI(), ex.getMessage());

        String message = String.format("Required parameter '%s' is not present.", ex.getParameterName());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}

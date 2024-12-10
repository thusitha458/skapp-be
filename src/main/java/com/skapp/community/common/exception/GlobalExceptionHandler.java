package com.skapp.community.common.exception;

import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.payload.response.ErrorResponse;
import com.skapp.community.common.payload.response.ResponseEntityDto;
import com.skapp.community.common.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final MessageUtil messageUtil;

	private final HttpServletRequest request;

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ResponseEntityDto> handleValidationErrors(BindException e) {
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ErrorResponse errorResponse = new ErrorResponse(status,
				messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_ERROR),
				CommonMessageConstant.COMMON_ERROR_VALIDATION_ERROR);

		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
			log.debug("Validation error on field '{}': {}", fieldError.getField(), fieldError.getDefaultMessage());
		}

		String message = messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_VALIDATION_ERROR);
		logDetailedException(e, CommonMessageConstant.COMMON_ERROR_VALIDATION_ERROR.name(), message, status);

		return new ResponseEntity<>(new ResponseEntityDto(true, errorResponse), status);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseEntityDto> handleAccessDeniedException(AccessDeniedException e) {
		HttpStatus status = HttpStatus.FORBIDDEN;
		String message = messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_ACCESS_DENIED);
		logDetailedException(e, CommonMessageConstant.COMMON_ERROR_ACCESS_DENIED.name(), message, status);

		return new ResponseEntity<>(new ResponseEntityDto(true,
				new ErrorResponse(status, message, CommonMessageConstant.COMMON_ERROR_ACCESS_DENIED)), status);
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	public ResponseEntity<ResponseEntityDto> handleInsufficientAuthenticationException(
			InsufficientAuthenticationException e) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_UNAUTHORIZED_ACCESS);
		logDetailedException(e, CommonMessageConstant.COMMON_ERROR_UNAUTHORIZED_ACCESS.name(), message, status);

		return new ResponseEntity<>(
				new ResponseEntityDto(true,
						new ErrorResponse(status, message, CommonMessageConstant.COMMON_ERROR_UNAUTHORIZED_ACCESS)),
				status);
	}

	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<ResponseEntityDto> handleInternalAuthenticationServiceException(
			InternalAuthenticationServiceException e) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND);
		logDetailedException(e, CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND.name(), message, status);

		return new ResponseEntity<>(new ResponseEntityDto(true,
				new ErrorResponse(status, message, CommonMessageConstant.COMMON_ERROR_USER_NOT_FOUND)), status);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ResponseEntityDto> handleBadCredentialsException(BadCredentialsException e) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		String message = messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_INVALID_CREDENTIALS);
		logDetailedException(e, CommonMessageConstant.COMMON_ERROR_INVALID_CREDENTIALS.name(), message, status);

		return new ResponseEntity<>(
				new ResponseEntityDto(true,
						new ErrorResponse(status, message, CommonMessageConstant.COMMON_ERROR_INVALID_CREDENTIALS)),
				status);
	}

	@ExceptionHandler(ModuleException.class)
	public ResponseEntity<ResponseEntityDto> handleModuleExceptions(ModuleException e) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		logDetailedException(e, e.getMessageKey().name(), messageUtil.getMessage(e.getMessageKey()), status);

		return new ResponseEntity<>(
				new ResponseEntityDto(true, new ErrorResponse(status, e.getMessage(), e.getMessageKey())), status);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ResponseEntityDto> handleEntityNotFoundExceptions(EntityNotFoundException e) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		logDetailedException(e, e.getMessageKey().name(), messageUtil.getMessage(e.getMessageKey()), status);

		return new ResponseEntity<>(
				new ResponseEntityDto(true, new ErrorResponse(status, e.getMessage(), e.getMessageKey())), status);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ResponseEntityDto> handleValidationExceptions(ValidationException e) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ErrorResponse errorResponse = new ErrorResponse(status, e.getMessage(), e.getMessageKey());

		if (e.getValidationErrors() != null && !e.getValidationErrors().isEmpty()) {
			for (String error : e.getValidationErrors()) {
				errorResponse.addValidationError("validation", error);
			}
		}

		logDetailedException(e, e.getMessageKey().name(), messageUtil.getMessage(e.getMessageKey()), status);

		return new ResponseEntity<>(new ResponseEntityDto(true, errorResponse), status);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ResponseEntityDto> handleNoHandlerFoundException(NoHandlerFoundException e) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		String message = messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_ENTITY_NOT_FOUND);
		logDetailedException(e, CommonMessageConstant.COMMON_ERROR_ENTITY_NOT_FOUND.name(), message, status);

		return new ResponseEntity<>(
				new ResponseEntityDto(true,
						new ErrorResponse(status, message, CommonMessageConstant.COMMON_ERROR_ENTITY_NOT_FOUND)),
				status);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseEntityDto> handleExceptions(Exception e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = messageUtil.getMessage(CommonMessageConstant.COMMON_ERROR_MODULE_EXCEPTION);
		logDetailedException(e, CommonMessageConstant.COMMON_ERROR_MODULE_EXCEPTION.name(), message, status);

		return new ResponseEntity<>(
				new ResponseEntityDto(true,
						new ErrorResponse(status, e.getMessage(), CommonMessageConstant.COMMON_ERROR_MODULE_EXCEPTION)),
				status);
	}

	private void logDetailedException(Exception e, String messageKey, String message, HttpStatus status) {
		String apiPath = request.getRequestURI();
		String redColor = "\u001B[31m";
		String resetColor = "\u001B[0m";

		String errorLog = "\n" + redColor + "==================== Exception Occurred ====================\n"
				+ String.format("API Path:            %s%n", apiPath)
				+ String.format("Exception Type:      %s%n", e.getClass().getSimpleName())
				+ String.format("Status Code:         %d%n", status.value())
				+ String.format("Key:                 %s%n", messageKey)
				+ String.format("Message:             %s%n", message)
				+ String.format("Additional Info:     %s%n", e.getMessage())
				+ "============================================================" + resetColor;

		log.error(errorLog);
	}

}
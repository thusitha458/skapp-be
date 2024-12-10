package com.skapp.community.common.util;

import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.exception.ValidationException;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

import static com.skapp.community.common.constant.ValidationConstant.HEXA_DECIMAL_VALIDATION_PATTERN;
import static com.skapp.community.common.constant.ValidationConstant.TIME_ZONE_VALIDATION_PATTERN;

@UtilityClass
public class Validation {

	private static final int MAX_NAME_LENGTH = 50;

	private static final String SPECIAL_CHAR_REGEX = "^[a-zA-Z]*$";

	public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

	public static final String NAME_REGEX = "[\\p{L}\\p{M}\\s\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u00FF"
			+ "\\u0100-\\u017F\\u010C\\u010D\\u0106\\u0107\\u0160\\u0161"
			+ "\\u017D\\u017E\\u0143\\u0144\\u0027\\u002D\\u005E\\u0060\\u007E"
			+ "\\u00E7\\u00C7\\u00A8\\u02DA\\u00D8\\u00F8\\u0142\\u00AF]*";

	public static final String ADDRESS_REGEX = "[\\p{L}\\p{M}\\s\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u00FF"
			+ "\\u0100-\\u017F\\u010C\\u010D\\u0106\\u0107\\u0160\\u0161"
			+ "\\u017D\\u017E\\u0143\\u0144\\u0027\\u002D\\u005E\\u0060\\u007E"
			+ "\\u00E7\\u00C7\\u00A8\\u02DA\\u00D8\\u00F8\\u0142\\u00AF" + "0-9]*";

	public static final String COUNTRY_CODE_PATTERN = "^[0-9]{1,4}$";

	public static final String PHONE_NUMBER_PATTERN = "^.{4,15}$";

	public static final String VALID_IDENTIFICATION_NUMBER_REGEXP = "^[a-zA-Z0-9\\-_]*$";

	public static final String VALID_NIN_NUMBER_REGEXP = "^[a-zA-Z0-9]*$";

	public static void isValidFirstName(String firstName) throws ValidationException {
		if (!StringUtils.hasText(firstName)) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ENTER_FIRST_NAME);
		}
		if (firstName.length() > MAX_NAME_LENGTH) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_FIRST_NAME_LENGTH,
					List.of(String.valueOf(MAX_NAME_LENGTH)));
		}
		if (!Pattern.matches(SPECIAL_CHAR_REGEX, firstName)) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_FIRST_NAME_SPECIAL_CHAR);
		}
	}

	public static void isValidLastName(String lastName) throws ValidationException {
		if (!StringUtils.hasText(lastName)) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_ENTER_LAST_NAME);
		}
		if (lastName.length() > MAX_NAME_LENGTH) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_LAST_NAME_LENGTH,
					List.of(String.valueOf(MAX_NAME_LENGTH)));
		}
		if (!Pattern.matches(SPECIAL_CHAR_REGEX, lastName)) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_LAST_NAME_SPECIAL_CHAR);
		}
	}

	public static void isValidEmail(String email) throws ValidationException {
		if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_EMAIL);
		}
	}

	public static void isValidPassword(String password) throws ValidationException {
		if (!StringUtils.hasText(password)) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_PASSWORD);
		}
		if (!Pattern.matches(PASSWORD_REGEX, password)) {
			throw new ValidationException(CommonMessageConstant.COMMON_ERROR_VALIDATION_PASSWORD_REQUIREMENTS);
		}
	}

	public static boolean isInvalidStartAndEndDate(LocalDate startDate, LocalDate endDate) {
		if (endDate == null) {
			return false;
		}
		else {
			return startDate == null || !startDate.isBefore(endDate);
		}
	}

	public static boolean isValidOrganizationTimeZone(String organizationTimeZone) {
		if (organizationTimeZone == null) {
			return false;
		}
		else {
			return Pattern.compile(TIME_ZONE_VALIDATION_PATTERN).matcher(organizationTimeZone).matches();
		}
	}

	public static boolean isValidThemeColor(String themeColor) {
		if (themeColor == null) {
			return false;
		}
		else {
			return Pattern.compile(HEXA_DECIMAL_VALIDATION_PATTERN).matcher(themeColor).matches();
		}
	}

}
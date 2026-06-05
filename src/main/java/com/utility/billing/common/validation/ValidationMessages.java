package com.utility.billing.common.validation;

public final class ValidationMessages {

    private ValidationMessages() {}

    public static final String REQUIRED          = "This field is required";
    public static final String INVALID           = "This field contains an invalid value";
    public static final String NOT_BLANK         = "This field must not be blank";
    public static final String NO_WHITESPACE     = "This field must not contain leading or trailing whitespace";
    public static final String TOO_SHORT         = "This field is too short";
    public static final String TOO_LONG          = "This field is too long";

    public static final String EMAIL_REQUIRED    = "Email address is required";
    public static final String EMAIL_INVALID     = "Must be a valid email address";
    public static final String EMAIL_TOO_LONG    = "Email address must not exceed 254 characters";

    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_INVALID  =
            "Password must be 8–72 characters and contain at least one uppercase letter, "
            + "one lowercase letter, one digit, and one special character";

    public static final String FULL_NAME_REQUIRED = "Full name is required";
    public static final String FULL_NAME_TOO_LONG = "Full name must not exceed 100 characters";

    public static final String PHONE_INVALID       = "Must be a valid phone number (e.g. +250780000000)";
    
    public static final String CUSTOMER_CODE_REQUIRED = "Customer code is required";
    public static final String NATIONAL_ID_REQUIRED    = "National ID is required";
    public static final String NATIONAL_ID_INVALID     = "National ID must be valid";
    
    public static final String METER_NUMBER_REQUIRED = "Meter number is required";
    public static final String METER_TYPE_REQUIRED   = "Meter type is required";
    
    public static final String READING_REQUIRED = "Reading value is required";
    public static final String POSITIVE            = "Value must be positive";
    public static final String POSITIVE_OR_ZERO    = "Value must be zero or positive";
    
    public static final String ENUM_INVALID        = "Invalid value";
}

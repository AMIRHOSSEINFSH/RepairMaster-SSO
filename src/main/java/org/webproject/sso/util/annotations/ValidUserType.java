package org.webproject.sso.util.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserTypeValidator.class)
public @interface ValidUserType {
    String message() default "Invalid user type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
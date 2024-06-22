package org.webproject.sso.util.annotations;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.webproject.sso.model.enumModel.USERTYPE;

public class UserTypeValidator implements ConstraintValidator<ValidUserType, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return USERTYPE.Companion.returnUserType(value) != null;
    }
}

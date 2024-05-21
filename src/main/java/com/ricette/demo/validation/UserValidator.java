package com.ricette.demo.validation;

import com.ricette.demo.model.User;
import com.ricette.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    final Integer MAX_NAME_LENGTH = 100;
    final Integer MIN_NAME_LENGTH = 2;

    @Autowired
    private UserService userService;

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
        //rimuove gli spazi
        String nome = user.getName().trim();
        String cognome = user.getSurname().trim();

        if (nome.isEmpty())
            errors.rejectValue("name", "required");
        else if (nome.length() < MIN_NAME_LENGTH || nome.length() > MAX_NAME_LENGTH)
            errors.rejectValue("name", "size");

        if (cognome.isEmpty())
            errors.rejectValue("surname", "required");
        else if (cognome.length() < MIN_NAME_LENGTH || cognome.length() > MAX_NAME_LENGTH)
            errors.rejectValue("surname", "size");


        if (this.userService.alreadyExists(user)) {
            errors.reject("duplicate.user");
        }

    }
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }
}

package com.ytpor.api.service;

import com.ytpor.api.model.Password;
import com.ytpor.api.model.PasswordEncodeDTO;
import com.ytpor.api.model.PasswordMatchDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Password encode(PasswordEncodeDTO encodeDTO) {
        Password password = new Password();
        password.setPlainText(encodeDTO.getPlainText());
        password.setHash(passwordEncoder.encode(encodeDTO.getPlainText()));
        password.setMatch(true);

        return password;
    }

    public Password match(PasswordMatchDTO matchDTO) {
        Password password = new Password();
        password.setPlainText(matchDTO.getPlainText());
        password.setHash(matchDTO.getHash());
        password.setMatch(passwordEncoder.matches(matchDTO.getPlainText(), matchDTO.getHash()));

        return password;
    }
}

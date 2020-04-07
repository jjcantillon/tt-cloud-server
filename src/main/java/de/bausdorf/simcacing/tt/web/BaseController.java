package de.bausdorf.simcacing.tt.web;

import de.bausdorf.simcacing.tt.web.security.TtClientRegistrationRepository;
import de.bausdorf.simcacing.tt.web.security.TtUser;
import de.bausdorf.simcacing.tt.web.security.TtUserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

public class BaseController {

    @Autowired
    TtClientRegistrationRepository userService;

    @ModelAttribute("user")
    TtUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<TtUser> details = userService.findById(auth.getName());
        return details.isPresent() ? details.get() : TtUser.builder()
                .name("Unknown")
                .userType(TtUserType.TT_NEW)
                .build();
    }
}

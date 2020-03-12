package org.immregistries.mqe.hub.authentication;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequestMapping(value = "/api/logged")
@RestController
public class AccountController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Principal urlEncodedHttpFormFilePost(Principal principal) throws Exception {
        System.out.println(principal);
        return principal;
    }
}

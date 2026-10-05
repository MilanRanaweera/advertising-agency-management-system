package com.axiom.users;

import com.axiom.core.Rules;
import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Access {

  private final AppUserRepository users;

  public Access(AppUserRepository users) {
    this.users = users;
  }

  public AppUser current() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    return Rules.found(users.findByEmail(auth.getName()));
  }

  public boolean is(String... roles) {
    return java.util.Arrays.asList(roles).contains(current().role);
  }

  public void role(String... roles) {
    Rules.access(is(roles));
  }

  public void owner(Long id) {
    Rules.access(current().id.equals(id));
  }
}

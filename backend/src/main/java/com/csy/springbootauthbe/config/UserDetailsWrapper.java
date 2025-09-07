package com.csy.springbootauthbe.config;

import com.csy.springbootauthbe.user.entity.AccountStatus;
import com.csy.springbootauthbe.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserDetailsWrapper implements UserDetails {

    private final User user;

    public UserDetailsWrapper(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities(); // or map roles to GrantedAuthority
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // assuming login is via email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // implement if you need expiry logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return !AccountStatus.SUSPENDED.equals(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return AccountStatus.ACTIVE.equals(user.getStatus());
    }

}

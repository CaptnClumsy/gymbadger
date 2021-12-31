package com.clumsy.gymbadger.security;

import java.util.Collection;
import java.util.Map;
 
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
 
public class AppUser implements OAuth2User {
 
    private OAuth2User oauth2User;
     
    public AppUser(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
    }
 
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }
 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }
 
    @Override
    public String getName() {  
        return oauth2User.getAttribute("id");
    }

    public String getDisplayName() {  
        return oauth2User.getAttribute("name");
    }

}
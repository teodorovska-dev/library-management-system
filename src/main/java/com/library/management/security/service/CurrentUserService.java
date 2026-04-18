package com.library.management.security.service;

import com.library.management.entity.User;

public interface CurrentUserService {
    User getCurrentAuthenticatedUser();
}
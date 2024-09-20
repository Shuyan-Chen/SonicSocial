package com.shuyan.sonicsocial.service;

import com.shuyan.sonicsocial.entity.User;

import java.util.List;

public interface MatchService {

    List<User> findMatchingUsers(double latitude,double longitude,double radius);
}

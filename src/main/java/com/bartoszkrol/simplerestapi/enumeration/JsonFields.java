package com.bartoszkrol.simplerestapi.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JsonFields {
    ID("id"),
    LOGIN("login"),
    NAME("name"),
    TYPE("type"),
    AVATAR_URL("avatarUrl"),
    CREATED_AT("createdAt"),
    GITHUB_CREATED_AT("created_at"),
    GITHUB_AVATAR_URL("avatar_url"),
    CALCULATIONS("calculations"),
    FOLLOWERS("followers"),
    PUBLIC_REPOS("public_repos");

    private String name;
}

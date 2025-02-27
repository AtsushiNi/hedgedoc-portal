package com.atsushini.hedgedocportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    // keycloakのユーザー名
    private String userName;
    // hedgedoc内でのユーザーID
    private String hedgedocId;
    // HedgeDocのAPIに使うcookie.
    private String hedgedocCookies;
}

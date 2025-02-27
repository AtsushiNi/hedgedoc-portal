package com.atsushini.hedgedocportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String userName;
    // userNameとは別？
    private String hedgedocId;
    // HedgeDocのAPIに使うcookie.
    private String hedgedocCookies;
}

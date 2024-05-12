package com.atsushini.hedgedocportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrentUserDto {
    private Long id;
    private String hedgedocId;
    // HedgeDocのAPIに使うcookie.
    private String cookie;
}

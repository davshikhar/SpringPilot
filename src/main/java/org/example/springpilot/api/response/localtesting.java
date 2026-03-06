package org.example.springpilot.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class localtesting{
    private String city;
    private String country;
    private int temperature;
    private String description;
}
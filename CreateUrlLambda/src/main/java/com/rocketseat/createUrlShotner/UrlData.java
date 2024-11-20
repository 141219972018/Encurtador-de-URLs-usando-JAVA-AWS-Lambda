package com.rocketseat.createUrlShotner;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UrlData {
    @Getter  String originalUrl;
    @Getter  long expirationTime;

}

package com.rocketseat.createUrlShotner;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

// Press ⇧ twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main implements RequestHandler<Map<String, Object>,Map<String,String>>{

  private final ObjectMapper objectMapper= new ObjectMapper();
  private final S3Client s3Client = S3Client.builder().build();
  @Override
  public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
    String body = input.get("body").toString();

    Map<String,String>bodyMap;
    try{
      bodyMap = objectMapper.readValue(body,Map.class);
    }catch (Exception exception){
      throw new RuntimeException("Error parsing Json BODY:"+ exception.getMessage(),exception);

    }
    String originalUrl= bodyMap.get("originalUrl");
    String expirationTime= bodyMap.get("expirationTime");
    long expirationTimeInSeconds = Long.parseLong(expirationTime);

    String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);
    UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

    try {
      String urlDataJson = objectMapper.writeValueAsString(urlData);

      PutObjectRequest request = PutObjectRequest.builder()
              .bucket("url-shortener-storage-lambdas")
              .key(shortUrlCode + ".json")
              .build();

      s3Client.putObject(request, RequestBody.fromString(urlDataJson));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error saving URL data to S3: " + e.getMessage(), e);
    }


    Map<String, String> response = new HashMap<>();
    response.put ("code", shortUrlCode);
    return null;
  }
}
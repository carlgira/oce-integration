package com.oce.files;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Class to upload files to OCE
 */
public class FileManager {

    /**
     * Create a HttpHeaders object with credentials
     * @param username
     * @param password
     * @return
     */
    private static HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(
                    auth.getBytes(StandardCharsets.US_ASCII) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    /**
     * Method to upload files to OCE with the content file in base64
     * @param oceUrl
     * @param user
     * @param password
     * @param parentID
     * @param fileName
     * @param fileContentType
     * @param base64Content
     */
    public static void uploadFile(String oceUrl, String user, String password, String parentID, String fileName ,String fileContentType  ,String base64Content){
            oceUrl = oceUrl + "/api/1.2/files/data";
            RestTemplate restTemplate = new RestTemplate();
            HttpMethod requestMethod = HttpMethod.POST;

            HttpHeaders headers = createHeaders(user, password);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
            ContentDisposition contentDisposition = ContentDisposition
                    .builder("form-data")
                    .name("primaryFile")
                    .filename(fileName)
                    .build();

            byte[] doc = Base64.getDecoder().decode(base64Content);

            fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            fileMap.add(HttpHeaders.CONTENT_TYPE, fileContentType);
            HttpEntity<byte[]> fileEntity = new HttpEntity<>(doc, fileMap);

            MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
            ContentDisposition paramContentDisposition = ContentDisposition
                    .builder("form-data")
                    .name("jsonInputParameters")
                    .build();

            paramsMap.add(HttpHeaders.CONTENT_DISPOSITION, paramContentDisposition.toString());
            paramsMap.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> requestEntity = new HttpEntity<>("{ \"parentID\" : \""  +  parentID + "\" }", headers);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("jsonInputParameters", requestEntity);
            body.add("primaryFile", fileEntity);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(oceUrl, requestMethod, request, String.class);
    }

    /**
     * Method to download file of OCE with the content file in base64
     * @param oceUrl
     * @param user
     * @param password
     * @param fileId
     * @return
     */
    public static String downloadFile(String oceUrl, String user, String password, String fileId){
        oceUrl = oceUrl + "/api/1.2/files/" +  fileId + "/data";

        RestTemplate restTemplate = new RestTemplate();
        String base64 = restTemplate.execute(oceUrl, HttpMethod.GET,
                request -> {
                    request.getHeaders().setBasicAuth(user, password);
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
                },
                response -> {
                    byte[] bytes = IOUtils.toByteArray(response.getBody());
                    return Base64.getEncoder().encodeToString(bytes);
                });

        return base64;
    }
}

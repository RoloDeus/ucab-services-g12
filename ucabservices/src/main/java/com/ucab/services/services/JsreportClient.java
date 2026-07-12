package com.ucab.services.services;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import java.util.List;
import java.util.Map;
 
@Service
public class JsreportClient {
 
    private final RestTemplate restTemplate = new RestTemplate();
 
    // Viene de application.properties: jsreport.url=http://localhost:5488
    @Value("${jsreport.url}")
    private String jsreportUrl;
 
    public byte[] generarPdf(String nombrePlantilla, List<Map<String, Object>> filas) {
        Map<String, Object> template = Map.of("name", nombrePlantilla);
        Map<String, Object> data = Map.of("rows", filas);
        Map<String, Object> body = Map.of("template", template, "data", data);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
 
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(jsreportUrl + "/api/report", request, byte[].class);
    }
}
 
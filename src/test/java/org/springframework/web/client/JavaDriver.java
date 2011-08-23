package org.springframework.web.client;

/** @author Arjen Poutsma */
public class JavaDriver {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        Object[] vars = new Object[0];
        restTemplate.delete("http://example.com", vars);
        
    }

}

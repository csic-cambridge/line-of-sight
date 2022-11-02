/*
 * Copyright © 2022 Costain Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.costain.cdbb.core.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;



@Component
public class TestApiManager {
    private static HttpHeaders headers = null;


    @Autowired
    private TestRestTemplate restTemplate;


    public ResponseEntity<String> doApiRequest(String body,
                                               String url,
                                               HttpMethod method,
                                               HttpStatus expectedResultStatus) {
        if (headers == null) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        if (body != null) {
            System.out.println("Body for " + url + " = " + body);
        }
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            url, method, request, String.class);
        if (expectedResultStatus != HttpStatus.NO_CONTENT) {
            System.out.println("Response to " + url + " = " + response.getBody());
        }
        assertEquals(expectedResultStatus, response.getStatusCode());
        return response;
    }

    public ResponseEntity<String> doSuccessfulGetApiRequest(String url) {
        return doApiRequest(null, url, HttpMethod.GET, HttpStatus.OK);
    }

    public ResponseEntity<String> doSuccessfulPostApiRequest(String body, String url) {
        return doApiRequest(body, url, HttpMethod.POST, HttpStatus.OK);
    }

    public ResponseEntity<String> doSuccessfulPutApiRequest(String body, String url) {
        return doApiRequest(body, url, HttpMethod.PUT, HttpStatus.OK);
    }

    public void doSuccessfulDeleteApiRequest(String url) {
        doApiRequest(null, url, HttpMethod.DELETE, HttpStatus.NO_CONTENT);
    }
}

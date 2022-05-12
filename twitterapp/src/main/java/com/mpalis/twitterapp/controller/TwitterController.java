package com.mpalis.twitterapp.controller;

import com.mpalis.twitterapp.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TwitterController {
    private static  final Logger LOGGER = LoggerFactory.getLogger(TwitterController.class);

    private final TwitterService twitterService;
    public TwitterController(TwitterService twitterService) {
        this.twitterService = twitterService;
    }

    @GetMapping("/{location}")
    public ResponseEntity<?> getTrends(@PathVariable(value = "location") String location) throws Exception{
        if((null == location) || "".equals(location)){
            String message = "Location not provided";
            LOGGER.error(message);
            throw new Exception(message);
        }
        List<String> trends = twitterService.getTrendsForLocation(location);
        if(trends.isEmpty()){
            return(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        return(new ResponseEntity<>(trends, HttpStatus.OK));
    }
}

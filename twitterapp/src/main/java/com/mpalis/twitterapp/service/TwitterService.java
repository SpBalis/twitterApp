package com.mpalis.twitterapp.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.PostConstruct;

@Service
@ApplicationScope
public class TwitterService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TwitterService.class);

    @Value("${twitter_consumer_key}")
    private String twitter_consumer_key;

    @Value("${twitter_consumer_key_secret}")
    private String twitter_consumer_key_secret;

    @Value("${twitter_access_token}")
    private String access_token;

    @Value("${twitter_access_token_secret}")
    private String access_token_secret;
    @Value("${twitter.api.access.bearer}")
    private String apiAccessBearer;

    private Twitter twitter;

    @PostConstruct
    private void getTwitterInstance() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuth2AccessToken(apiAccessBearer)
                .setOAuthConsumerKey(twitter_consumer_key)
                .setOAuthConsumerSecret(twitter_consumer_key_secret)
                .setOAuthAccessToken(access_token)
                .setOAuthAccessTokenSecret(access_token_secret);

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public List<String> getTrendsForLocation(String location) {
        List<String> twitterTrends = new ArrayList<String>();
        try {
            int idTrendLocation = getTrendLocation(location);
            if (idTrendLocation <= 0) {
                String message = "Trend Location Not Found";
                LOGGER.error(message);
                throw new TwitterException(message);
            }
            Trends trends = twitter.getPlaceTrends(idTrendLocation);
            for (int i = 0; i < trends.getTrends().length; i++) {
                twitterTrends.add(trends.getTrends()[i].getName());
            }
        } catch (TwitterException te) {
            twitterTrends.add(te.getMessage());
        }
        return (twitterTrends);
    }

    private int getTrendLocation(String locationName) {
        int idTrendLocation = 0;
        try {
            ResponseList<Location> locations;
            locations = twitter.getAvailableTrends();

            for (Location location : locations) {
                if (location.getName().equalsIgnoreCase(locationName)) {
                    idTrendLocation = location.getWoeid();
                    break;
                }
            }
            return idTrendLocation;
        } catch (TwitterException te) {
            LOGGER.error("Failed to get trends :  " + te.getMessage());
            return (0);
        }
    }
}


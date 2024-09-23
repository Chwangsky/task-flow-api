package com.taskflow.api.todostack.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.taskflow.api.global.entity.UserGoogleTokenEntity;
import com.taskflow.api.global.repository.UserGoogleTokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarController {

    private static final String APPLICATION_NAME = "TaskFlow Calendar App";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private UserGoogleTokenRepository userGoogleTokensRepository;

    @GetMapping("/today-events/{userId}")
    public List<Event> getTodayEvents(@PathVariable Long userId) throws GeneralSecurityException, IOException {
        // Get Google access token from the repository for the given userId
        Optional<UserGoogleTokenEntity> optionalTokens = userGoogleTokensRepository.findById(userId);

        if (optionalTokens.isEmpty()) {
            throw new IllegalArgumentException("Access token for the user not found.");
        }

        UserGoogleTokenEntity userGoogleToken = optionalTokens.get();
        String accessToken = userGoogleToken.getAccessToken();

        // Initialize Calendar API
        Calendar service = getCalendarService(accessToken);

        // Define the time range for today
        DateTime startOfDay = new DateTime(Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        DateTime endOfDay = new DateTime(Date.from(LocalDate.now()
                .plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Fetch events for today
        Events events = service.events().list("primary")
                .setTimeMin(startOfDay)
                .setTimeMax(endOfDay)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }

    private Calendar getCalendarService(String accessToken) throws GeneralSecurityException, IOException {
        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JSON_FACTORY)
                .build()
                .setAccessToken(accessToken);

        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
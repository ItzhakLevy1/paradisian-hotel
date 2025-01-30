package com.paradisian.paradisianHotelMongo.service.interfac;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

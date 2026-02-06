package com.dansmultipro.ops.pojo;

public class MailPoJo {

    private String emailAddress;
    private String emailBody;
    private String username;

    public MailPoJo(String emailAddress, String emailBody, String username) {
        this.emailAddress = emailAddress;
        this.emailBody = emailBody;
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getUsername() {
        return username;
    }

}

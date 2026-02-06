package com.dansmultipro.ops.pojo;

public class MailUpdateStatusPoJo {

    private String emailAddress;
    private String emailBody;
    private String username;
    private String status;

    public MailUpdateStatusPoJo(String emailAddress, String emailBody, String username, String status) {
        this.emailAddress = emailAddress;
        this.emailBody = emailBody;
        this.username = username;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

}

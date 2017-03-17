package com.avinash.calendarservice;

public class Attendee {

    private String name;

    private String mail;

    // None(0), Optional(1),
    // Required(2), Resource(3)
    private String type;

    // Attendee(1),
    // None(0),
    // Organizer(2),
    // Performer(3),
    // Speaker(4)
    private String relationShip;

    // None(0), Accepted(1),
    // Decline(2),
    // Invited(3),
    // Tentative(4)
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(String relationShip) {
        this.relationShip = relationShip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

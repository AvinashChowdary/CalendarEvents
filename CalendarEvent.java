package com.avinash.calendarservice;

import java.util.Date;
import java.util.List;

public class CalendarEvent implements Comparable<CalendarEvent> {

    private static final int THIRTY_ONE = 31;

    private String title;

    private Date beginDate;

    private Date endDate;

    private boolean allDay;

    private long id;

    private String location;

    private String desc;

    private List<Attendee> attendee;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date begin) {
        this.beginDate = begin;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date end) {
        this.endDate = end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    @Override
    public String toString() {
        return getTitle() + "\n" + getBeginDate();
    }

    @Override
    public int compareTo(CalendarEvent other) {
        // -1 = less, 0 = equal, 1 = greater
        return getBeginDate().compareTo(other.beginDate);
    }

    @Override
    public boolean equals(Object obj) {
        return beginDate.equals(((CalendarEvent) obj).getBeginDate());
    }

    @Override
    public int hashCode() {
        final int prime = THIRTY_ONE;
        int result = 1;
        result = prime * result
                + ((beginDate == null) ? 0 : beginDate.hashCode());
        return result;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Attendee> getAttendee() {
        return attendee;
    }

    public void setAttendee(List<Attendee> attendees) {
        this.attendee = attendees;
    }

}

Component : Calendar Service
This document explains how to use the calendar service component

Classes to be Copied :
Copy the following classes from component folder

 *         Attendee
 *         CalendarEvent
 *         CalendarService

 Note : Current classes are in the package com.pcs.calendarservice, these can either be copied to specified package or
 change them as per requirement.

Attendee : is the POJO for Attendee of an Event

CalendarEvent : is the POJO for CalendarEvent

CalendarService : is used for obtaining the below by passing
context as parameter to reasCalendar method

 *         Add a new Calendar Event
 *         Delete a particular Event
 *         Edit a particular Event
 *         Getting list of Events from all the available Calendars
 *         Getting list of Attendees for a particular Event
 *         Adding list of Attendees for a particular Event

 First we need to create an object for the class CalendarService as

  CalendarService calService = new CalendarService(this);

  and then...

1) Add a new Calendar Event by passing title, start time,
end time and description to addEvent(...)

 Syntax  : addEvent(String title, long startTime, long endTime, String desc)
 returns : eventId of type long

2) Delete a particular Event by passing eventId to deleteEvent(...)

 Syntax  : deleteEvent(long eventId)
 returns : No.of events deleted of type int

3) Edit a particular Event by passing eventId, title, start time,
end time and description to editEvent(...)

 Syntax  : editCompleteEvent(long eventId, String title, long startTime, long endTime, String desc)
 returns : No.of events edited of type int

4) Edit a particular Event by passing eventId, title editEventTitle(...)

 Syntax  : editEventTitle(long eventId, String title)
 returns : No.of events edited of type int

5) Edit a particular Event time by passing eventId, start time,
end time and description to editEventTime(...)

 Syntax  : editEventTime(long eventId, long startTime, long endTime)
 returns : No.of events edited of type int

6) Edit a particular Event by passing eventId, description to editEventDescription(...)

  Syntax  : editEventDescription(long eventId, String desc)
  returns : No.of events edited of type int

7) Getting list of Events from all the available Calendars by passing
(days & hours)optional to readCalendar(...), will return List of CalendarEvents.

 Syntax  : readCalendar() / readCalendar(Context context, int days, int hours)
 returns : List<CalendarEvent>

8) Getting list of Attendees for a particular Event by passing
eventId to getAttendees(...), will return the List of Attendees

 Syntax  : getAttendees(long eventId)
 returns : List<Attendee>

9) Adding list of Attendees for a particular Event by passing eventId and
List of Attendees to addAttendees(...)

 Syntax  : addAttendees(long eventID, List<Attendee> attendees)
 returns : void

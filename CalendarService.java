package com.avinash.calendarservice;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Events;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author Avinash
 *         <p/>
 *         CalendarService class is used for obtaining the below by passing
 *         context as parameter to readCalendar method
 *         <p/>
 *         Add a new Calendar Event
 *         <p/>
 *         Delete a particular Event
 *         <p/>
 *         Edit a particular Event
 *         <p/>
 *         <p/>
 *         Edit a particular Event title
 *         <p/>
 *         <p/>
 *         Edit a particular Event time
 *         <p/>
 *         <p/>
 *         Edit a particular Event description
 *         <p/>
 *         Adding list of Attendees for a particular Event
 *         <p/>
 *         Getting list of Events from all the available Calendars
 *         <p/>
 *         Getting list of Attendees for a particular Event
 */
public class CalendarService {

    private static final String TAG = CalendarService.class.getSimpleName();

    // Modify DAYS and HOURS in order to get events
    // range from from today-DAYS-HOURS to today+DAYS+HOURS
    // default 365 days to get events past 1year to future 1year
    private static final int DAYS = 365;

    private static final int HOURS = 0;

    private static final int TWO = 2;

    private static final int THREE = 3;

    private static final int FOUR = 4;

    private static final int FIVE = 5;

    private static final int SIX = 6;

    private static final String CALENDAR_URI = "content://com.android.calendar/events";

    private String[] calendarParams = new String[]{"calendar_id", "title", "description",
            "dtstart", "dtend", "eventLocation"};

    private String[] eventsParams = new String[]{"event_id", "title", "begin", "end",
            "allDay", "description", "eventLocation"};

    private Context context;

    private static final String CONTEXT_NULL_LOG = "Context is null";

    private static final String EDITED = "Edited";

    public CalendarService(Context context) {
        this.context = context;
    }

    /**
     * @return List of calendar events
     * <p/>
     * Default method called when passing context
     * Returns Events from today-DAYS-HOURS to today+DAYS+HOURS Default DAYS = 365 and HOURS = 0
     */
    public List<CalendarEvent> readCalendar() {
        return readCalendar(DAYS, HOURS);
    }

    /**
     * @param days
     * @param hours
     * @return List of calendar events
     * <p/>
     * Use to specify specific the time span DAYS and HOURS
     */
    public List<CalendarEvent> readCalendar(int days,
                                            int hours) {
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();

            Cursor cursor = null;

            // Create a hash map of calendar ids and the events of each id
            Map<String, List<CalendarEvent>> eventMap = new HashMap<>();
            Map<String, List<CalendarEvent>> eventsMap = new HashMap<>();

            try {

                // Obtaining cursor based on OS Version as there are two different
                // kinds One before ICS and another after ICS
                cursor = contentResolver.query(
                        Uri.parse(CALENDAR_URI), calendarParams,
                        null, null, null);

                // Create a set containing all of the calendar IDs available on the
                // phone
                Set<String> calendarIds = getCalenderIds(cursor);

                // Loop over all of the calendars
                for (String id : calendarIds) {

                    // Create a builder to define the time span
                    Uri.Builder builder = Uri.parse(
                            "content://com.android.calendar/instances/when")
                            .buildUpon();
                    long now = new Date().getTime();

                    // create the time span based on the inputs
                    ContentUris.appendId(builder, now
                            - (DateUtils.DAY_IN_MILLIS * days)
                            - (DateUtils.HOUR_IN_MILLIS * hours));
                    ContentUris.appendId(builder, now
                            + (DateUtils.DAY_IN_MILLIS * days)
                            + (DateUtils.HOUR_IN_MILLIS * hours));
                    eventMap = getEventsMap(contentResolver, builder, id);
                    eventsMap.putAll(eventMap);
                }
            } catch (Exception e) {
                Log.e(TAG, " " + e);
            } finally {
                cursor.close();
            }
            return getListOfEvents(eventsMap);
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return new ArrayList<>();
        }
    }

    /**
     * @param contentResolver
     * @param builder
     * @param id
     * @return Map of calendar events
     * <p/>
     * To get map of events of a particular calendar
     */
    private Map<String, List<CalendarEvent>> getEventsMap(ContentResolver contentResolver, Uri.Builder builder, String id) {
        Map<String, List<CalendarEvent>> eventMap = new HashMap<>();
        Cursor eventCursor = null;
        try {
            // Create an event cursor to find all events in the calendar
            eventCursor = contentResolver.query(builder.build(),
                    eventsParams, "Calendar_id=" + id, null,
                    "startDay ASC, startMinute ASC");

            Log.i(TAG, "eventCursor count=" + eventCursor.getCount());

            // If there are actual events in the current calendar, the
            // count will exceed zero
            if (eventCursor.getCount() > 0) {

                // Create a list of calendar events for the specific
                // calendar
                List<CalendarEvent> eventList = new ArrayList<>();

                // Move to the first object
                eventCursor.moveToFirst();

                CalendarEvent ce;

                do {
                    // Adds the object to the list of events
                    ce = loadEvent(eventCursor);
                    eventList.add(ce);
                } while (eventCursor.moveToNext());

                eventCursor.close();

                Collections.sort(eventList);
                eventMap.put(id, eventList);

                Log.i(TAG, eventMap.keySet().size() + " " + eventMap.values());
            }
        } catch (Exception e) {
            Log.e(TAG, " " + e);
        } finally {
            eventCursor.close();
        }
        return eventMap;
    }

    /**
     * @param cursor
     * @return CalendarEvent object
     * <p/>
     * Returns a new instance of the calendar object
     */
    private CalendarEvent loadEvent(Cursor cursor) {

        CalendarEvent calendarEvent = new CalendarEvent();

        calendarEvent.setId(cursor.getLong(0));
        calendarEvent.setTitle(cursor.getString(1));
        calendarEvent.setBeginDate(new Date(
                cursor.getLong(TWO)));
        calendarEvent.setEndDate(new Date(
                cursor.getLong(THREE)));
        calendarEvent.setAllDay(!"0"
                .equals(cursor.getString(FOUR)));
        calendarEvent.setDesc(cursor.getString(FIVE));
        calendarEvent.setLocation(cursor.getString(SIX));
        calendarEvent.setAttendee(getAttendees(cursor.getLong(0)));

        return calendarEvent;
    }

    /**
     * @param cursor
     * @return a set of available calendar Id's
     * <p/>
     * Creates the list of calendar ids and returns it in a set
     */
    private Set<String> getCalenderIds(Cursor cursor) {

        Set<String> calendarIds = new HashSet<>();

        try {
            cursor.moveToFirst();

            // If there are more than 0 calendars, continue
            if (cursor.getCount() > 0) {

                // Loop to set the id for all of the calendars
                do {

                    String id = cursor.getString(0);
                    String displayName = cursor.getString(1);

                    Log.i(TAG, "Id: " + id + " Display Name: " + displayName);
                    calendarIds.add(id);

                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (AssertionError ex) {
            Log.e(TAG, " " + ex);
        } catch (Exception e) {
            Log.e(TAG, " " + e);
        } finally {
            cursor.close();
        }
        return calendarIds;
    }

    /**
     * @param eventsMap
     * @return List of events
     * <p/>
     * Obtains List of Events from the HashMap of Events
     */
    private List<CalendarEvent> getListOfEvents(
            Map<String, List<CalendarEvent>> eventsMap) {

        List<CalendarEvent> eventsList = new ArrayList<>();

        Iterator<String> mapIterator = eventsMap.keySet().iterator();

        while (mapIterator.hasNext()) {

            String mapEntry = mapIterator.next();

            List<CalendarEvent> value = eventsMap.get(mapEntry);

            for (int i = 0; i < value.size(); i++) {
                eventsList.add(value.get(i));
            }
        }
        return eventsList;
    }

    /**
     * @param eventId
     * @return List of Attendees
     * <p/>
     * Fetches the list of Attendees for the given Event ID
     */
    public List<Attendee> getAttendees(long eventId) {
        if (context != null) {
            List<Attendee> attendees = new ArrayList<>();

            final String[] attendeeProjection = new String[]{
                    Attendees._ID,
                    Attendees.EVENT_ID,
                    Attendees.ATTENDEE_NAME,
                    Attendees.ATTENDEE_EMAIL,
                    Attendees.ATTENDEE_TYPE,
                    Attendees.ATTENDEE_RELATIONSHIP,
                    Attendees.ATTENDEE_STATUS};

            final String query = "(" + Attendees.EVENT_ID
                    + " = ?)";

            final String[] args = new String[]{String.valueOf(eventId)};

            final Cursor cursor = context.getContentResolver().query(
                    Attendees.CONTENT_URI, attendeeProjection,
                    query, args, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                Attendee attendee;
                do {
                    attendee = new Attendee();
                    attendee.setName(cursor.getString(TWO));
                    attendee.setMail(cursor.getString(THREE));
                    attendee.setType(cursor.getString(FOUR));
                    attendee.setRelationShip(cursor.getString(FIVE));
                    attendee.setStatus(cursor.getString(SIX));
                    attendees.add(attendee);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return attendees;
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return new ArrayList<>();
        }
    }

    /**
     * @param eventId
     * @return no.of rows deleted
     * <p/>
     * Delete event based on Event ID
     */
    public int deleteEvent(long eventId) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
            int rows = cr.delete(deleteUri, null, null);
            Log.i(TAG, rows + " Deleted");
            return rows;
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return 0;
        }
    }

    /**
     * @param eventId
     * @param title
     * @param startTime
     * @param endTime
     * @param desc
     * @return no.of rows edited
     * <p/>
     * Edit event with the given parameters
     */
    public int editCompleteEvent(long eventId, String title, long startTime,
                                 long endTime, String desc) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(Events.TITLE, title);
            values.put(Events.DTSTART, startTime);
            values.put(Events.DTEND, endTime);
            values.put(Events.DESCRIPTION, desc);
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));

            Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
            int rows = cr.update(updateUri, values, null, null);
            Log.i(TAG, rows + EDITED);
            return rows;
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return 0;
        }
    }

    /**
     * @param eventId
     * @param title
     * @return no.of rows edited
     * <p/>
     * Edit event title
     */
    public int editEventTitle(long eventId, String title) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(Events.TITLE, title);
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));

            Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
            int rows = cr.update(updateUri, values, null, null);
            Log.i(TAG, rows + EDITED);
            return rows;
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return 0;
        }
    }

    /**
     * @param eventId
     * @param startTime
     * @param endTime
     * @return no.of rows edited
     * <p/>
     * Edit event time
     */
    public int editEventTime(long eventId, long startTime,
                             long endTime) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(Events.DTSTART, startTime);
            values.put(Events.DTEND, endTime);
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));

            Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
            int rows = cr.update(updateUri, values, null, null);
            Log.i(TAG, rows + EDITED);
            return rows;
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return 0;
        }
    }

    /**
     * @param eventId
     * @param desc
     * @return no.of rows edited
     * <p/>
     * Edit event description
     */
    public int editEventDescription(long eventId, String desc) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(Events.DESCRIPTION, desc);
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));

            Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
            int rows = cr.update(updateUri, values, null, null);
            Log.i(TAG, rows + EDITED);
            return rows;
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return 0;
        }
    }

    /**
     * @param title
     * @param startTime
     * @param endTime
     * @param desc
     * @return eventId
     * <p/>
     * Add a new Event with the given parameters
     */
    public long addEvent(String title, long startTime, long endTime,
                         String desc) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();

            Cursor cursor = context.getContentResolver().query(
                    Uri.parse(CALENDAR_URI),
                    calendarParams, null,
                    null, null);

            // Create a list from a set containing all of the calendar IDs available on the
            // phone
            List<String> ids = new ArrayList<>(getCalenderIds(cursor));
            // Calendar ID can be replaced with any ID available on the device as you wish
            values.put(Events.CALENDAR_ID, Integer.parseInt(ids.get(0)));
            values.put(Events.TITLE, title);
            values.put(Events.DTSTART, startTime);
            values.put(Events.DTEND, endTime);
            values.put(Events.DESCRIPTION, desc);
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));

            Uri uri = cr.insert(Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());
            Log.i(TAG, eventID + " Added");
            return eventID;
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
            return 0;
        }
    }

    /**
     * @param attendees
     * @param eventID   <p/>
     *                  Adds the attendee list to the specific event
     */
    public void addAttendees(long eventID, List<Attendee> attendees) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            Uri uri = null;
            values.put(Attendees.EVENT_ID, eventID);

            for (int i = 0; i < attendees.size(); i++) {
                values.put(Attendees.ATTENDEE_NAME, attendees.get(i).getName());
                values.put(Attendees.ATTENDEE_EMAIL, attendees.get(i).getMail());
                values.put(Attendees.ATTENDEE_RELATIONSHIP, attendees.get(i).getRelationShip());
                values.put(Attendees.ATTENDEE_TYPE, attendees.get(i).getType());
                values.put(Attendees.ATTENDEE_STATUS, attendees.get(i).getStatus());
                uri = cr.insert(Attendees.CONTENT_URI, values);
            }

            long id = Long.parseLong(uri.getLastPathSegment());
            Log.i(TAG, "Added attendees to event " + id);
        } else {
            Log.i(TAG, CONTEXT_NULL_LOG);
        }
    }

}

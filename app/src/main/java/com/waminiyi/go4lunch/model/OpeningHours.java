package com.waminiyi.go4lunch.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * Opening hours for a Place Details result. Please see <a
 * href="https://developers.google.com/places/web-service/details#PlaceDetailsResults">Place Details
 * Results</a> for more details.
 */
public class OpeningHours implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Whether the place is open at the current time.
     *
     * <p>Note: this field will be null if it isn't present in the response.
     */
    @SerializedName("open_now")
    public Boolean openNow;

    /**
     * The opening hours for a Place for a single day.
     */
    public static class Period implements Serializable {

        private static final long serialVersionUID = 1L;

        public static class OpenClose implements Serializable {

            private static final long serialVersionUID = 1L;

            public enum DayOfWeek {
                SUNDAY("Sunday"),
                MONDAY("Monday"),
                TUESDAY("Tuesday"),
                WEDNESDAY("Wednesday"),
                THURSDAY("Thursday"),
                FRIDAY("Friday"),
                SATURDAY("Saturday"),

                /**
                 * Indicates an unknown day of week type returned by the server. The Java Client for Google
                 * Maps Services should be updated to support the new value.
                 */
                UNKNOWN("Unknown");

                private DayOfWeek(String name) {
                    this.name = name;
                }

                private final String name;

                public String getName() {
                    return name;
                }
            }

            /**
             * Day that this Open/Close pair is for.
             */
            public com.google.maps.model.OpeningHours.Period.OpenClose.DayOfWeek day;

            /**
             * Time that this Open or Close happens at.
             */
            public LocalTime time;

            @Override
            public String toString() {
                return String.format("%s %s", day, time);
            }
        }

        /**
         * When the Place opens.
         */
        public com.google.maps.model.OpeningHours.Period.OpenClose open;

        /**
         * When the Place closes.
         */
        public com.google.maps.model.OpeningHours.Period.OpenClose close;

        @Override
        public String toString() {
            return String.format("%s - %s", open, close);
        }
    }

    /**
     * Opening periods covering seven days, starting from Sunday, in chronological order.
     */
    public com.google.maps.model.OpeningHours.Period[] periods;

    /**
     * The formatted opening hours for each day of the week, as an array of seven strings; for
     * example, {@code "Monday: 8:30 am – 5:30 pm"}.
     */
    public String[] weekdayText;

    /**
     * Indicates that the place has permanently shut down.
     *
     * <p>Note: this field will be null if it isn't present in the response.
     */
    public Boolean permanentlyClosed;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" [OpeningHours:");
        if (permanentlyClosed != null && permanentlyClosed) {
            sb.append(" permanentlyClosed");
        }
        if (openNow != null && openNow) {
            sb.append(" openNow : true");
        }else{
            sb.append(" openNow : false");
        }
        sb.append(" ").append(Arrays.toString(periods));
        return sb.toString();
    }
}

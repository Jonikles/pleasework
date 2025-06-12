package com.tutoringplatform.observer;

import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;

public class TutorUpdateObserver implements BookingObserver {
    private ITutorRepository tutorRepository;

    public TutorUpdateObserver(ITutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Override
    public void update(BookingEvent event) {
        Tutor tutor = event.getTutor();
        String day = event.getBooking().getDateTime().getDayOfWeek().toString().substring(0, 1)
                + event.getBooking().getDateTime().getDayOfWeek().toString().substring(1).toLowerCase();
        int startHour = event.getBooking().getDateTime().getHour();

        if (event.getEventType() == BookingEvent.EventType.CONFIRMED) {
            for (int i = 0; i < event.getBooking().getDurationHours(); i++) {
                tutor.removeAvailability(day, startHour + i);
            }
            System.out.println("TutorUpdateObserver: Updated tutor availability after booking confirmation for " + day
                    + " at " + startHour + "h.");
            tutorRepository.update(tutor);

        } else if (event.getEventType() == BookingEvent.EventType.CANCELLED) {
            for (int i = 0; i < event.getBooking().getDurationHours(); i++) {
                tutor.addAvailability(day, startHour + i);
            }
            System.out.println("TutorUpdateObserver: Restored tutor availability after booking cancellation for " + day
                    + " at " + startHour + "h.");
            tutorRepository.update(tutor);
        }
    }
}
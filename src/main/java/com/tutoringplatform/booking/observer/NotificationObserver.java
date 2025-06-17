package com.tutoringplatform.booking.observer;

import com.tutoringplatform.notification.NotificationType;
import com.tutoringplatform.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationObserver {

    private static final Logger logger = LoggerFactory.getLogger(NotificationObserver.class);
    private final NotificationService notificationService;

    @Autowired
    public NotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void onBookingEvent(BookingEvent event) {
        try {
            switch (event.getEventType()) {
                case CREATED:
                    notificationService.notifyBookingCreated(
                            event.getStudent().getId(),
                            event.getTutor().getId(),
                            event.getBooking().getId());
                    break;

                case CONFIRMED:
                    notificationService.notifyBookingConfirmed(
                            event.getStudent().getId(),
                            event.getTutor().getId(),
                            event.getBooking().getId());
                    break;

                case CANCELLED:
                    notificationService.notifyBookingCancelled(
                            event.getStudent().getId(),
                            event.getTutor().getId(),
                            event.getBooking().getId());
                    break;

                case COMPLETED:
                    // Request feedback from student
                    notificationService.createNotification(
                            event.getStudent().getId(),
                            NotificationType.SESSION_FEEDBACK_REQUEST,
                            "Session Completed",
                            "How was your session with " + event.getTutor().getName() + "? Leave a review!",
                            "/reviews/create/" + event.getTutor().getId(),
                            event.getBooking().getId());
                    break;
            }

            logger.debug("Processed notifications for booking event: {} - {}",
                    event.getEventType(), event.getBooking().getId());

        } catch (Exception e) {
            logger.error("Error processing notifications for booking event", e);
        }
    }
}
package com.tutoringplatform.notification;

public enum NotificationType {
    // Booking related
    BOOKING_CREATED,
    BOOKING_CONFIRMED,
    BOOKING_CANCELLED,
    BOOKING_COMPLETED,
    BOOKING_REMINDER,

    // Payment related
    PAYMENT_RECEIVED,
    PAYMENT_REFUNDED,
    LOW_BALANCE_WARNING,

    // Review related
    NEW_REVIEW,

    // System notifications
    PROFILE_UPDATE,
    NEW_MESSAGE,
    SYSTEM_ANNOUNCEMENT,

    // Tutor specific
    NEW_STUDENT_REQUEST,
    EARNINGS_DEPOSITED,

    // Student specific
    TUTOR_AVAILABLE,
    SESSION_FEEDBACK_REQUEST
}
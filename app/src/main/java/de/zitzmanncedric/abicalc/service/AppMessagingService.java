package de.zitzmanncedric.abicalc.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Hintergrund-Service zum Empfangen von Nachrichten
 */
public class AppMessagingService extends FirebaseMessagingService {

    /**
     * Funktion zum Empfangen von Nachrichten. (Aktuell nicht funktionsfähig, da für spätere Entwicklung gedacht)
     * @param remoteMessage Empfanges Nachricht-Objekt
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}

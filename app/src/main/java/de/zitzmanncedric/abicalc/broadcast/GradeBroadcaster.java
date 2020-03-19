package de.zitzmanncedric.abicalc.broadcast;

import java.util.ArrayList;

import lombok.Getter;

public class GradeBroadcaster {

    private static GradeBroadcaster instance;

    @Getter private ArrayList<OnBroadcastActionListener> actionListeners = new ArrayList<>();

    public GradeBroadcaster() {
        instance = this;
    }

    public static GradeBroadcaster getInstance() {
        if(instance == null) instance = new GradeBroadcaster();
        return instance;
    }

    public void register(OnBroadcastActionListener actionListener) {
        if(!actionListeners.contains(actionListener)) {
            actionListeners.add(actionListener);
        }
    }
    public void unregister(OnBroadcastActionListener actionListener) {
        actionListeners.remove(actionListener);
    }

    public void broadcastUpdate(int broadcastCode, String payload) {
        for(OnBroadcastActionListener listener : actionListeners) {
            listener.onBroadcast(broadcastCode, payload);
        }
    }

}

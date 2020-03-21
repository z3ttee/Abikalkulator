package de.zitzmanncedric.abicalc.broadcast;

import java.util.ArrayList;

import de.zitzmanncedric.abicalc.api.Subject;
import lombok.Getter;

public class AverageUpdatedBroadcaster {

    private static AverageUpdatedBroadcaster instance;

    @Getter
    private ArrayList<Callback> actionListeners = new ArrayList<>();

    public AverageUpdatedBroadcaster() {
        instance = this;
    }

    public static AverageUpdatedBroadcaster getInstance() {
        if(instance == null) instance = new AverageUpdatedBroadcaster();
        return instance;
    }

    public void register(Callback actionListener) {
        if(!actionListeners.contains(actionListener)) {
            actionListeners.add(actionListener);
        }
    }
    public void unregister(Callback actionListener) {
        actionListeners.remove(actionListener);
    }

    public void broadcast(Subject old, Subject updated, int termID) {
        for(Callback listener : actionListeners) {
            listener.onAverageUpdated(old, updated, termID);
        }
    }

    public interface Callback {
        void onAverageUpdated(Subject old, Subject updated, int termID);
    }
}

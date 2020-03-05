package de.zitzmanncedric.abicalc;

import android.view.View;

import androidx.annotation.AnimRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Klasse verwaltet Fragment-Übergänge mit Animationen
 * @author Cedric Zitzmann
 */
public class AppFragments {

    /**
     * Startet einen Fragment-Übergang
     * @param manager Benötigt, um ein Fragment durch ein anderes zu ersetzen
     * @param container Benötigt, legt den Container fest, wo das neue Fragment eingesetzt werden soll
     * @param newFragment Benötigt, übergibt das neue Fragment, welches eingefügt werden soll
     * @param popBackstack Legt fest, ob Fragments im Cache gelöscht werden sollen
     * @param name Legt den Name eines Fragments im Cache fest, um es zu identifzieren
     * @param animations Legt Eingangs und Ausgangsanimationen fest
     */
    public static void replaceFragment(FragmentManager manager, View container, Fragment newFragment, boolean popBackstack, @Nullable String name, @AnimRes int... animations) {
        if(popBackstack) manager.popBackStack();
        FragmentTransaction transaction = manager.beginTransaction();

        if(animations.length == 2) {
            transaction.setCustomAnimations(animations[0], animations[1]);
        } else if(animations.length == 4) {
            transaction.setCustomAnimations(animations[0], animations[1],animations[2],animations[3]);
        }

        if(name != null) {
            transaction.addToBackStack(name);
        }

        transaction.replace(container.getId(), newFragment).commit();
    }

}

package com.Softy.Launcher2;

import android.transition.Transition;

/**
 * Created by mcom on 1/22/17.
 */

public interface Loader{
    void setTransition(Transition t);
    Transition getTransition();
}
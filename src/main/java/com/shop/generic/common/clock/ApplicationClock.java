package com.shop.generic.common.clock;

import java.time.Clock;

public class ApplicationClock implements GsClock {

    /**
     * @return a clock that returns the current instant using the best available system clock,
     * converting to date and time using the default timezone
     */
    @Override
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

}

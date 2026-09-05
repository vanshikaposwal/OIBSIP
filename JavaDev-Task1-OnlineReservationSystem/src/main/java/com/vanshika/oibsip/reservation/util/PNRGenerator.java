package com.vanshika.oibsip.reservation.util;

public class PNRGenerator {

    public static String getPNR() {

        long pnrNumber = 1000000000L +
                        (long) (Math.random() * 9000000000L);

        return "PNR" + pnrNumber;
    }
}

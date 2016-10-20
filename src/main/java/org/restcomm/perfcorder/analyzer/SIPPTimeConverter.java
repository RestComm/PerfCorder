package org.restcomm.perfcorder.analyzer;

public class SIPPTimeConverter implements ColumnConverter {

    public SIPPTimeConverter() {
    }

    /**
     *
     * @param value
     * @return parsed millis assuming "HH:mm:ss:SSSSSS"
     */
    @Override
    public double convert(String value) {

        double millis = 0;
        try {
            millis = millis + Integer.parseInt(value.substring(0, 2)) * 3600000;
            millis = millis + Integer.parseInt(value.substring(3, 5)) * 60000;
            millis = millis + Integer.parseInt(value.substring(6, 8)) * 1000;
            millis = millis + Integer.parseInt(value.substring(9, 12));
        } catch (Exception e) {

        }
        return millis;
    }

}

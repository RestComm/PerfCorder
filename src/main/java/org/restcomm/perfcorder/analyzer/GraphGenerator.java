package org.restcomm.perfcorder.analyzer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class GraphGenerator {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PerfCorderAnalyzeApp.class.getName());

    public static String generateGraph(AnalysisMeasTarget target, List<String[]> readAll, long linesToStrip) throws IOException {
        TimeSeries tSeries = new TimeSeries(target.getLabel());
        Second current = new Second();
        for (int i = 0; i < readAll.size() - linesToStrip; i++) {
            String[] readNext = readAll.get(i);
            int column = target.getColumn();
            if (column < readNext.length) {
                String nextCol = readNext[column];
                double nexValue = target.transformIntoDouble(nextCol);
                tSeries.add(current, nexValue);
                current = (Second) current.next();
            } else {
                LOGGER.warn("Attempted invalid column:" + target.getLabel());
            }
        }
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(tSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(target.getLabel(), "time", target.getLabel(), timeSeriesCollection, false, false, false);
        BufferedImage createBufferedImage = chart.createBufferedImage(320, 240);
        byte[] graph = ChartUtilities.encodeAsPNG(createBufferedImage, false, 9);
        String graphStr = encode(graph);
        return graphStr;
    }
    private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public static String encode(byte[] buf) {
        int size = buf.length;
        char[] ar = new char[((size + 2) / 3) * 4];
        int a = 0;
        int i = 0;
        while (i < size) {
            byte b0 = buf[i++];
            byte b1 = (i < size) ? buf[i++] : 0;
            byte b2 = (i < size) ? buf[i++] : 0;

            int mask = 0x3F;
            ar[a++] = ALPHABET[(b0 >> 2) & mask];
            ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
            ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
            ar[a++] = ALPHABET[b2 & mask];
        }
        switch (size % 3) {
            case 1:
                ar[--a] = '=';
            case 2:
                ar[--a] = '=';
        }
        return new String(ar);
    }
}

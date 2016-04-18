package org.restcomm.perfcorder.analyzer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class GraphGenerator {
    public static String generateGraph(AnalysisMeasTarget target, List<String[]> readAll, long linesToStrip) throws IOException {
        TimeSeries tSeries = new TimeSeries(target.getLabel());
        Second current = new Second();
        for (int i = 0; i < readAll.size() - linesToStrip; i++) {
            String[] readNext = readAll.get(i);
            int column = target.getColumn();
            String nextCol = readNext[column];
            double nexValue = target.transformIntoDouble(nextCol);
            tSeries.add(current, nexValue);
            current = (Second) current.next();
        }
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(tSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(target.getLabel(), "time", target.getLabel(), timeSeriesCollection, false, false, false);
        BufferedImage createBufferedImage = chart.createBufferedImage(320, 240);
        byte[] graph = ChartUtilities.encodeAsPNG(createBufferedImage, false, 9);
        String graphStr = Base64.getEncoder().encodeToString(graph);
        return graphStr;
    }    
}

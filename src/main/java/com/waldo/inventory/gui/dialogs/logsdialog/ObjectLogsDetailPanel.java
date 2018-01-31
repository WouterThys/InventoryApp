package com.waldo.inventory.gui.dialogs.logsdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.ObjectLog;
import com.waldo.utils.DateUtils;
import com.waldo.utils.icomponents.ILabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.TreeMap;

import static com.waldo.inventory.gui.Application.imageResource;

public class ObjectLogsDetailPanel extends JPanel implements GuiUtils.GuiInterface {

    private final ImageIcon objectLogIcon = imageResource.readImage("Log.Object");

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel nameLbl;
    private ILabel timeNeededLbl;
    private ILabel fetchTimeLbl;

    private JPanel panel;
    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ObjectLog selectedLog;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public ObjectLogsDetailPanel() {
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    /*
     *                  METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JFreeChart createChart(String title, XYDataset dataSet) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                "Date",
                "Amount",
                dataSet,
                false,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(false);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangePannable(true);

//        XYItemRenderer r = plot.getRenderer();
//        if (r instanceof XYLineAndShapeRenderer) {
//            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
//            renderer.setDefaultSha
//        }
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(DateUtils.shortDate);
        Date d = DateUtils.now();
        d = DateUtils.addDays(d, -20);
        axis.setMinimumDate(d);

        return chart;
    }

    private XYDataset createDataset(TreeMap<Date, Integer> map) {
        TimeSeries series = new TimeSeries("Values");

        for (Date date : map.keySet()) {
            int day = DateUtils.getDay(date);
            int month = DateUtils.getMonth(date);
            int year = DateUtils.getYear(date);
            series.add(new Day(day, month, year), map.get(date));
        }

        TimeSeriesCollection dataSet = new TimeSeriesCollection();
        dataSet.addSeries(series);

        return dataSet;
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(new ILabel(objectLogIcon), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(nameLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(fetchTimeLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(timeNeededLbl, gbc);

        headerPanel.setBorder(BorderFactory.createEmptyBorder(5,10,2,10));
        return headerPanel;
    }

    private void createChartPanel() {
        XYDataset set = createDataset(selectedLog.getStatisticsMap());
        JFreeChart chart = createChart(selectedLog.getListName(), set);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(200, 100));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(2,10,5,10));

        if (panel != null) {
            panel.removeAll();
            panel.add(chartPanel, BorderLayout.CENTER);
        }
    }

    private void setChartPanelVisible(boolean visible) {
        if (panel != null) {
            panel.setVisible(visible);
        }
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        nameLbl = new ILabel("", ILabel.LEFT);
        timeNeededLbl = new ILabel("", ILabel.LEFT);
        fetchTimeLbl = new ILabel("", ILabel.LEFT);

        panel = new JPanel(new BorderLayout());
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void updateComponents(Object... args) {
        if (args.length == 0) {
            setVisible(false);
            selectedLog = null;
        } else {
            if (args[0] instanceof ObjectLog) {
                selectedLog = (ObjectLog) args[0];

                nameLbl.setText(selectedLog.getListName());
                timeNeededLbl.setText(String.valueOf(selectedLog.getCacheList().getFetchTimeInNanos()) + "ns");
                fetchTimeLbl.setText(DateUtils.formatDateTime(selectedLog.getCacheList().getInitialisationTime()));

                if (selectedLog.hasStatistics()) {
                    createChartPanel();
                    setChartPanelVisible(true);
                } else {
                    setChartPanelVisible(false);
                }

                setVisible(true);
            }
        }
    }
}
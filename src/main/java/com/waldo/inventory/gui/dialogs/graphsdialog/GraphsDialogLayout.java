package com.waldo.inventory.gui.dialogs.graphsdialog;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.dbclasses.DbHistory;
import com.waldo.inventory.database.DatabaseAccess;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;

abstract class GraphsDialogLayout extends IDialog {

    private static final String[] CHARTS = {"Db history"};

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private JComboBox<String> chartComboBox;

    private ChartPanel dbHistoryChart;

    /*
    *                  VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    GraphsDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void createDbHistoryPanel() {
        if (dbHistoryChart != null) {
            return;
        }
        DefaultCategoryDataset barDataSet = new DefaultCategoryDataset();
        DefaultCategoryDataset lineDataSet = new DefaultCategoryDataset();

        TreeMap<Date, List<DbHistory>> historyMap = createHistoryMap();
        for (Date date : historyMap.keySet()) {
            List<DbHistory> historyList = historyMap.get(date);

            int inserts = 0;
            int updates = 0;
            int deletes = 0;
            int total;

            for (DbHistory history : historyList) {
                switch (history.getDbAction()) {
                    case DatabaseAccess.OBJECT_INSERT: inserts++; break;
                    case DatabaseAccess.OBJECT_UPDATE: updates++; break;
                    case DatabaseAccess.OBJECT_DELETE: deletes++; break;
                }
            }

            total = inserts + updates + deletes;

            barDataSet.addValue(inserts, "Inserts", date);
            barDataSet.addValue(updates, "Updates", date);
            barDataSet.addValue(deletes, "Deletes", date);
            lineDataSet.addValue(total, "Total", date);
        }


        JFreeChart chart = ChartFactory.createBarChart(
                "Database history",
                "",
                "Actions",
                barDataSet,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        plot.setDataset(1, lineDataSet);
        plot.mapDatasetToRangeAxis(1,0);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.blue);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
        renderer2.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        plot.setRenderer(1, renderer2);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0);
        renderer.setShadowVisible(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
                0.0f, 0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
                0.0f, 0.0f, new Color(64, 0, 0));
        GradientPaint gp3 = new GradientPaint(0.0f, 0.0f, Color.yellow,
                0.0f, 0.0f, Color.orange);
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        renderer2.setSeriesPaint(0, gp3);

        renderer.setLegendItemToolTipGenerator(
                new StandardCategorySeriesLabelGenerator("Tooltip: {0}"));
        renderer2.setLegendItemToolTipGenerator(
                new StandardCategorySeriesLabelGenerator("Tooltip: {0}"));

        dbHistoryChart = new ChartPanel(chart, false);
        dbHistoryChart.setPreferredSize(new Dimension(500,300));
        dbHistoryChart.setMouseWheelEnabled(true);
        dbHistoryChart.setMouseZoomable(true, true);
        dbHistoryChart.setRangeZoomable(true);
        dbHistoryChart.setDomainZoomable(true);
    }

    private TreeMap<Date, List<DbHistory>> createHistoryMap() {
        TreeMap<Date, List<DbHistory>> historyMap = new TreeMap<>();
        List<DbHistory> historyList = cache().getDbHistory();

        for (DbHistory dbh : historyList) {
            Date date = DateUtils.stripTime(dbh.getDate());

            if (!historyMap.containsKey(date)) {
                historyMap.put(date, new ArrayList<>());
            }

            historyMap.get(date).add(dbh);
        }

        return historyMap;
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        setTitleIcon(imageResource.readImage("Statistics.Title"));
        setTitleName(getTitle());

        // Combo box
        chartComboBox = new JComboBox<>(CHARTS);
        chartComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String value = CHARTS[chartComboBox.getSelectedIndex()];
            }
        });

        // Charts
        createDbHistoryPanel();

    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BorderLayout());

        // Extra
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(chartComboBox, BorderLayout.WEST);

        getContentPanel().add(northPanel, BorderLayout.NORTH);
        getContentPanel().add(dbHistoryChart, BorderLayout.CENTER);

        pack();
    }

    @Override
    public void updateComponents(Object... object) {

    }
}
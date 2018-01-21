package com.waldo.inventory.classes;

import com.waldo.inventory.Utils.DateUtils;
import com.waldo.inventory.classes.cache.CacheList;
import com.waldo.inventory.classes.dbclasses.Statistics;
import com.waldo.inventory.managers.CacheManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.List;
import java.util.TreeMap;

public class ObjectLog {

    private String listName;
    private CacheList cacheList;
    private String staticsName;

    private TreeMap<Date, Integer> statisticsMap = null;

    public ObjectLog(String listName, CacheList cacheList) {
        this(listName, cacheList, "");
    }

    public ObjectLog(String listName, CacheList cacheList, String staticsName) {
        this.listName = listName;
        this.cacheList = cacheList;
        if (staticsName != null && !staticsName.isEmpty()) {
            setFieldName(staticsName);
        }
    }

    private void setFieldName(String fieldName) {
        String firstChar = String.valueOf(fieldName.charAt(0));
        if (firstChar.equals(firstChar.toLowerCase())) {
            fieldName = firstChar.toUpperCase() + fieldName.substring(1, fieldName.length());
        }
        this.staticsName = fieldName;
    }

    public String getListName() {
        if (listName == null) {
            listName = "";
        }
        return listName;
    }

    public CacheList getCacheList() {
        return cacheList;
    }

    public int getCacheListSize() {
        if (cacheList.isFetched()) {
            return cacheList.size();
        }
        return -1;
    }

    public TreeMap<Date, Integer> getStatisticsMap() {
        if (statisticsMap == null && staticsName != null && !staticsName.isEmpty()) {
            statisticsMap = new TreeMap<>();
            List<Statistics> statisticsList = CacheManager.cache().getStatistics();
            int avgCount = 1;
            for (Statistics s : statisticsList) {
                try {
                    Method getMethod = s.getClass().getMethod("get" + staticsName);
                    String intString = String.valueOf(getMethod.invoke(s));

                    Date date = DateUtils.stripTime(s.getCreationTime());
                    int intVal = Integer.valueOf(intString);

                    if (statisticsMap.containsKey(date)) {
                        avgCount++;
                        int val = statisticsMap.get(date);
                        int avg = (val + intVal) / avgCount;
                        statisticsMap.put(date, avg);
                    } else {
                        avgCount = 1;
                        statisticsMap.put(date, intVal);
                    }

                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return statisticsMap;
    }

    public boolean hasStatistics() {
        return getStatisticsMap() != null && getStatisticsMap().size() > 0;
    }
}

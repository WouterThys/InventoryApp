package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.classes.SetItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CapacitorParser extends SetItemParser{

    CapacitorParser() {}

    @Override
    public List<SetItem> parse(String series) throws ParseException, IOException {
        setItems = new ArrayList<>();
        String fileName = "setvalues/" + R + series + ".txt";
        parserFile = new File(getClass().getClassLoader().getResource(fileName).getFile());
        if (parserFile.exists()) {

            String line;
            List<String> stringValues = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(parserFile))) {
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("\\s+");
                    stringValues.addAll(Arrays.asList(split));
                }
            }

            System.out.println("Min value: " + getMinValue());
            System.out.println("Max value: " + getMaxValue());

            setItems = createSetItemsFromValues(stringValues);

        } else {
            setItems = null;
            throw new ParseException("File " + fileName + " does not exist..", 0);
        }
        return setItems;
    }

    @Override
    public List<SetItem> crop(int value) {
        List<SetItem> cropped = new ArrayList<>();
        if (value <= 1) {
            return setItems;
        } else {
            for (int i = 0; i < setItems.size(); i++) {
                if (i % value == 0) {
                    cropped.add(setItems.get(i));
                }
            }
        }
        return cropped;
    }

    @Override
    public String getUnit() {
        return C_UNIT;
    }

    private List<SetItem> createSetItemsFromValues(List<String> stringValues) {
        List<SetItem> setitems = new ArrayList<>();
        List<Double> decades = decadeValues(getMinValue(), getMaxValue());
        List<Double> values = new ArrayList<>();

        for (double d : decades) {
            for (String value : stringValues) {
                try {
                    double eVal = Double.valueOf(value);
                    double rVal = d * eVal;
                    if (rVal >= getMinValue() && rVal <= getMaxValue()) {
                        values.add(rVal);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (double val : values) {
            SetItem si = new SetItem();
            si.setName(C);
            si.setValue(convertToPrettyString(val));
            setitems.add(si);
        }

        return setitems;
    }
}

package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.Utils.ValueUtils;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.classes.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.waldo.inventory.Utils.Statics.Units;

public class ResistorParser extends SetItemParser {

    ResistorParser() {}

    @Override
    public List<SetItem> parse(String series) throws ParseException, IOException {
        setItems = new ArrayList<>();
        String fileName = "setvalues/" + R + series + ".txt";

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            String line;
            List<String> stringValues = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("\\s+");
                    stringValues.addAll(Arrays.asList(split));
                }
            }

            setItems = createSetItemsFromValues(stringValues);
        } catch (Exception e) {
            setItems = null;
            throw e;
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
        return Units.R_UNIT;
    }

    private List<SetItem> createSetItemsFromValues(List<String> stringValues) {
        List<SetItem> setitems = new ArrayList<>();
        List<Double> decades = decadeValues(getMinValue(), getMaxValue());
        List<Value> values = new ArrayList<>();

        for (double d : decades) {
            for (String value : stringValues) {
                try {
                    double eVal = Double.valueOf(value);
                    double rVal = d * eVal;
                    if (rVal >= getMinValue() && rVal <= getMaxValue()) {
                        int multiplier = 0;
                        double newD = d;
                        if (d / 1000000 > 1) {
                            multiplier = 6;
                            newD /= 1000000;
                        }
                        else if (d / 1000 > 1) {
                            multiplier = 3;
                            newD /= 1000;
                        }
                        else if (d < 1 && d * 1000 > 1) {
                            multiplier = -3;
                            newD *= 1000;
                        }
                        else if (d < 1 && d * 1000000 > 1) {
                            multiplier = 6;
                            newD *= 1000000;
                        }

                        Value v = new Value(ValueUtils.round(eVal * newD, 2), multiplier, Units.R_UNIT);
                        values.add(v);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (Value val : values) {
            SetItem si = new SetItem();
            si.setName(R);
            si.setValue(val);
            setitems.add(si);
        }

        return setitems;
    }
}

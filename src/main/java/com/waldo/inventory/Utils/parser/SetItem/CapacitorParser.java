package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.ValueUtils;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.classes.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.waldo.inventory.Utils.Statics.Units.C_UNIT;

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

        List<BigDecimal> bigDecimals = toDecimalList(stringValues);
        List<Integer> exponents = ValueUtils.exponents(getMinValue(), getMaxValue());

        for (int exp : exponents) {
            for (BigDecimal bd : bigDecimals) {
                BigDecimal decimal = bd.scaleByPowerOfTen(exp);
                if (decimal.compareTo(getMinValue()) >= 0 && decimal.compareTo(getMaxValue()) <= 0) {

                    Value value = new Value(decimal);
                    value.setUnit(Statics.Units.C_UNIT);
                    SetItem setItem = new SetItem("C");
                    setItem.setValue(value);

                    setitems.add(setItem);
                }
            }
        }

        return setitems;
    }
}

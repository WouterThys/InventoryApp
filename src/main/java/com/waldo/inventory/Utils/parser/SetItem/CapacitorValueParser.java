package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.ValueUtils;
import com.waldo.inventory.classes.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.Utils.Statics.Units.C_UNIT;

public class CapacitorValueParser extends SetItemValueParser {

    CapacitorValueParser() {}

    @Override
    public List<Value> parse(String series) throws ParseException, IOException {
        valueList = new ArrayList<>();

            List<String> stringValues = getParseValues(series);

            valueList = createValues(stringValues);


        return valueList;
    }

    @Override
    public String getFileName(String series) {
        String result = "";
        if (series != null && !series.isEmpty()) {
            result = "setvalues/" + R + series + ".txt";
        }
        return result;
    }

    @Override
    public List<Value> crop(int value) {
        List<Value> cropped = new ArrayList<>();
        if (value <= 1) {
            return valueList;
        } else {
            for (int i = 0; i < valueList.size(); i++) {
                if (i % value == 0) {
                    cropped.add(valueList.get(i));
                }
            }
        }
        return cropped;
    }

    @Override
    public String getUnit() {
        return C_UNIT;
    }

    private List<Value> createValues(List<String> stringValues) {
        List<Value> values = new ArrayList<>();

        List<BigDecimal> bigDecimals = toDecimalList(stringValues);
        List<Integer> exponents = ValueUtils.exponents(getMinValue(), getMaxValue());

        for (int exp : exponents) {
            for (BigDecimal bd : bigDecimals) {
                BigDecimal decimal = bd.scaleByPowerOfTen(exp);
                if (decimal.compareTo(getMinValue()) >= 0 && decimal.compareTo(getMaxValue()) <= 0) {

                    Value value = new Value(decimal);
                    value.setUnit(Statics.Units.C_UNIT);

                    values.add(value);
                }
            }
        }

        return values;
    }
}

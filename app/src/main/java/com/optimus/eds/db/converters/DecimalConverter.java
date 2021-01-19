package com.optimus.eds.db.converters;

import java.math.BigDecimal;

import androidx.room.TypeConverter;

public class DecimalConverter {

        @TypeConverter
        public BigDecimal fromString(String value) {
            return value == null ? null : new BigDecimal(value);
        }

        @TypeConverter
        public String amountToString(BigDecimal bigDecimal) {
            if (bigDecimal == null) {
                return null;
            } else {
                return String.valueOf(bigDecimal.doubleValue());
            }
        }
}

package com.optimus.eds;

public class Enums {


    public enum LimitBy {;
        public static final int Quantity=1;
        public static final int Amount=2;

    }

   public enum AccessSequenceCode {
        DISTRIBUTION_PRODUCT,
        OUTLET_PRODUCT,
        ROUTE_PRODUCT,
        REGION_PRODUCT, PRODUCT, OUTLET, ROUTE, DISTRIBUTION;
    }

   public enum CalculationType {
        ;
        public static final int Fix=1;
        public static final int Percentage=2;
    }

    public enum OperationType {
        ;
        public static final int Plus=1;
        public static final int Minus=2;
    }

    public enum PriceTypeLevel {
        ;
        static final int Product = 1;
        static final int Invoice = 2;

    }

    public enum PricingType {
        ;
        public static int Sales=1;
        public static int Purchase = 2;
    }

    public enum RoundingRule {
        ;
        public static final int Zero_Decimal_Precision=1;
        public static final int  Two_Decimal_Precision=2;
        public static final int  Floor=3;
        public static final int  Ceiling=4;

    }

   public enum ScaleBasis {
        ;
        public static Integer Quantity=1;
        public static Integer Value=2;
        public static Integer Total_Quantity=3;
    }

    public enum MessageSeverityLevel
    {;
        public static Integer    ERROR = 1;
        public static Integer   WARNING = 2;
        public static Integer   MESSAGE = 3;
    }

}

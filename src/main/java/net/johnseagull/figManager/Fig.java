package net.johnseagull.figManager;



import java.util.*;

/**
 * Standard Fig object type. <br>
 * Includes basic details such: <br>as a human-readable name<br>
 * a short description<br>
 * an rendered flag to determine how its rendered<br>
 * an identifier that will automatically be set to the Fig's field name. Used for internal saving and data handling<br><br>
 * all Fig types must extend this class in order to be detected/processed correctly
 * */
public class Fig {
    //generic properties for other figs
    public String name = "";
    public String widgetType = "";
    public String description = "";
    public boolean inGroup = false;
    public boolean rendered = false;
    public String id = "";
    public String dataType = "";
    //can be used to quickly store custom data so i dont have to rewrite the classes
    public Map<String, Object> customData = new HashMap<>();

    public static class NumFig extends Fig {}
    public static class CollectionFig extends Fig {
        public String itemType;
        public int maxLength;
        public int dispLength;

    }
    /**
     * Fig-type object for storing an Integer
     */
    public static class IntFig extends NumFig {
            public int value;
            public int min;
            public int max;

        /**
         * Stores an integer in the Fig format
         * @param value Default value
         * @param min Minimum value
         * @param max Maximum value
         * */
            public IntFig(String name, String description, int value, int min, int max) {
                this.widgetType = "box";
                this.dataType = "int_";
                this.name = name;
                this.description = description;
                this.value = value;
                this.min = min;
                this.max = max;
            }
        }

    /**
     * Fig-type object for storing a String
     */
    public static class StringFig extends Fig{
        public String value;
        public int max;
        /**
         * Stores a string in the Fig format
         * @param value Default value
         * @param max Maximum amount of characters
         * */
        public StringFig(String name, String description, String value, int max) {
            this.value = value;
            this.name = name;
            this.description = description;
            this.max = max;
            this.widgetType = "box";
            this.dataType = "string_";
        }
    }

    /**
     * Fig-type object for storing a Boolean
     */
    public static class BooleanFig extends Fig {
        public boolean value;
        /**
         * Stores a boolean in the Fig format
         * @param value Default value
         * */
        public BooleanFig(String name, String description, Boolean value) {
            this.value = value;
            this.name = name;
            this.description = description;
            this.widgetType = "check";
            this.dataType = "boolean_";

        }
    }

    /**
     * Fig-Type object for storing a Float
     */
    public static class FloatFig extends NumFig {
            public float value;
            public float min;
            public float max;
        /**
         * Stores a float in the Fig format
         * @param value Default value
         * @param min Minimum value
         * @param max Maximum value
         * */
            public FloatFig(String name, String description, float value, float min, float max) {
                this.name = name;
                this.description = description;
                this.value = value;
                this.min = min;
                this.max = max;
                this.widgetType = "box";
                this.dataType = "float_";
            }
        }



    /**
     * Fig-type object for storing a map of a String and an Object
     */
    public static class MapFig extends CollectionFig {
        public HashMap<String, String> value;
       public String key;
        public String keyDesc;
        public String valueString;
        public String valueDesc;

        /**
         * Creates a MapFig of a String and a data type.
         *
         * @param maxLength Maximum length of the Map
         * @param dispLength Number of entries to be displayed before scrolling is required
         * @param dataType What data the map will hold. Acceptable types include {@code string}. {@code int}, {@code boolean}, {@code float}
         * @param keyDesc Name of the value
         * @param valueDesc Short description of what the value is
         */
        public MapFig(String name, String desc, int maxLength, int dispLength,String dataType, String keyDesc, String valueDesc) {
            this.name = name;
            this.description = desc;
            this.maxLength = maxLength;
            this.value = new HashMap<>(maxLength);
            this.itemType = dataType;
            this.keyDesc = keyDesc;
            this.valueDesc = valueDesc;
            this.dispLength = dispLength;
            this.dataType = "map_";
            this.widgetType = "list";
        }
        public MapFig(String name, String desc, int maxLength, int dispLength,String dataType, String keyDesc, String valueDesc, HashMap<String,String>value) {
            this.name = name;
            this.value = value;
            this.description = desc;
            this.maxLength = maxLength;
            this.value = new HashMap<>(maxLength);
            this.itemType = dataType;
            this.keyDesc = keyDesc;
            this.valueDesc = valueDesc;
            this.dispLength = dispLength;
            this.dataType = "map_";
            this.widgetType = "list";
        }
        public void add(String key, String value) {
            if (this.value.size()<maxLength) {
                this.value.put(key,value);
            }
        }
    }

    public static class ListFig extends CollectionFig {
        public List<String> value;
      public String valueDesc;

        /**
         * Creates a MapFig of a String and a data type.
         *
         * @param maxLength Maximum length of the Map
         * @param dispLength Number of entries to be displayed before scrolling is required
         * @param valueDesc Short description of what the value is
         */
        public ListFig(String name, String desc, int maxLength, int dispLength, String valueDesc) {
            this.name = name;
            this.description = desc;
            this.maxLength = maxLength;
            this.value = new ArrayList<>(maxLength);
            this.valueDesc = valueDesc;
            this.dispLength = dispLength;
            this.dataType = "list_";
            this.widgetType = "list";
        }
        public void add(String key) {
            if (this.value.size()<maxLength) {
                this.value.add(key);
            }
        }
    }
}

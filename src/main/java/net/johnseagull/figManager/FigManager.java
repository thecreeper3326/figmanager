package net.johnseagull.figManager;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.Files.readAllBytes;

/**
 * Main server-side manager for interpreting incoming config packets, saving/loading, conversion, and validation.
 */
public class FigManager {

    public static final Logger LOGGER = LoggerFactory.getLogger("JohnSeagull FigManager");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static  Object FIGS;
    public static  Class<?> FIGCLASS;
    public static  String name = "";
    public static  String version = "";
 
    public static  boolean exExists = false;
    public static  String exName = "";
    /**
     * Saves the current configuration for the given project
     * @param projectName
     */
    public static void save(String projectName) {
        File file = new File("config/" + projectName + "/figs.json");
        try {
            file.getParentFile().mkdirs();
            String json = toString(FIGS);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }
            LOGGER.info("Figs saved successfully.");
        } catch (Exception e) {
            
            LOGGER.error("Failed to save figs :(");
            LOGGER.error(e.getMessage());
        }
    }
    public static void genInfo() {
        Object defaults;
        try {
            defaults = FIGCLASS.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.error("uhuh... smth bad happen :(");
            LOGGER.error(e.getMessage());
            return;
        }
        File file = new File("config/" + name + "/info.txt");
        StringBuilder temp = new StringBuilder();
        temp.append(name+" version: "+version+"\n\n");
        temp.append("CONFIGURATION GUIDE");
        temp.append("\n");
        temp.append("This text file is a reference for editing the mod's configuration outside the game.\nThis is NOT the configuration file, use config/"+name+"/figs.json instead.");
        temp.append("\n\n");
        for (Field field : defaults.getClass().getFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())) continue;

                Object f = field.get(defaults);
                if (f instanceof Fig.IntFig t) {
                    temp.append(field.getName()+"\n");
                    temp.append(t.name+" - Integer\n");
                    temp.append(t.description+"\n");
                    temp.append("Default value: "+t.value+"\n");
                    temp.append("Range: "+t.min+" to "+t.max+"\n");
                    temp.append("\n");
                }
                if (f instanceof Fig.FloatFig t) {
                    temp.append(field.getName()+"\n");
                    temp.append(t.name+" - Float\n");
                    temp.append(t.description+"\n");
                    temp.append("Default value: "+t.value+"\n");
                    temp.append("Range: "+t.min+" to "+t.max+"\n");
                    temp.append("\n");
                }
                if (f instanceof Fig.StringFig t) {
                    temp.append(field.getName()+"\n");
                    temp.append(t.name+" - String\n");
                    temp.append(t.description+"\n");
                    temp.append("Default value: "+t.value+"\n");
                    temp.append("Max length: "+t.max+"\n");
                    temp.append("\n");
                }
                if (f instanceof Fig.BooleanFig t) {
                    temp.append(field.getName()+"\n");
                    temp.append(t.name+" - Boolean\n");
                    temp.append(t.description+"\n");
                    temp.append("Default value: "+t.value+"\n");
                    temp.append("\n");
                }


            } catch (Exception ignored) {}
        }
        try {
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(temp.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
    /**
     * Loads the configuration for the given project
     * @param projectName
     */
    public static void load(String projectName) {
        File file = new File("config/" + projectName + "/figs.json");
        try {
            if (!file.exists()) {
                save(projectName);
                return;
            }
            String json = new String(readAllBytes(file.toPath()));
            Object temp = fromString(json, FIGCLASS);
            assert temp != null;
            FIGS = temp;
            LOGGER.info("Figs loaded successfully!");
        } catch (Exception e) {
            LOGGER.error("Failed to load figs :(");
            LOGGER.error(e.getMessage());
        }
    }

    public static void rebuildIDs() {
        for (Field field : FIGS.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object o = field.get(FIGS);
                if (o instanceof Fig f) {
                    f.id = field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();

            }

        }

    }
    /**
     * Initializes an instance of FigManager for any mod
     * @param projectName name of the mod that is being loaded; will be used for config directory and config screen
     * @param projectVersion version of the mod, shown in the config screen
     * @param figs instance of class type {@code Figs}, used to store data while running
     * */
    public void init(String projectName,String projectVersion, Object figs) {

        LOGGER.info("Initializing FigManager for project: " + projectName + " v"+ projectVersion);
        name = projectName;
        version = projectVersion;
        FIGS = figs;
        FIGCLASS = figs.getClass();
        rebuildIDs();
        try {
            Method ex = this.getClass().getMethod("extension");
            if (!ex.getDeclaringClass().equals(FigManager.class)) {
                exExists =true;
                LOGGER.info("Found extension method. Attempting to load...");
            }
        } catch (NoSuchMethodException ignored) {}
        if (exExists) {
            extension();
            if(exName.equals("")) {
                LOGGER.info("Extension loaded successfully!");
            } else  {
                LOGGER.info("Extension \""+exName+"\" loaded successfully!");
            }
        }
        File infoFile = new File("config/" + projectName + "/info.txt");
        if (!infoFile.exists()) {
            genInfo();
        }
        load(name);

        List<Object> temp = validate(FIGS);
        FIGS = temp.get(0);
        for (String e : (List<String>)temp.get(2)) {
            LOGGER.error(e);
        }


        LOGGER.info("Initialized for "+projectName);
    }
    public void extension() {

    }
    /**
     * Creates a JSON string from a Figs instance. It only includes values, all other metadata such as names and descriptions is redundant, so they are discarded and re-added when loadign from the String.
     * @param instance {@code Figs} instance to be converted
     * @return String of converted JSON
     */
    public static String toString(Object instance) {
        Map<String, Object> data = new LinkedHashMap<>();
        for (Field field : instance.getClass().getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if ( field.getType()!= FigGroup.class) {

                    Object figObj = field.get(instance);

                        if (figObj != null) {
                            Field valueField = figObj.getClass().getField("value");
                            data.put(field.getName(), valueField.get(figObj));
                        }


                }
            } catch (Exception ignored) {}
        }

        return GSON.toJson(data);
    }

    /**
     * Creates a Figs instance from a JSON string and a template class. Reconstructs Fig metadata using provided FigClass
     * @param figs JSON string to be converted
     * @param figClass Template Fig Class
     * @return <code>Figs</code> instance
     */

    public static Object fromString(String figs, Class<?> figClass) {
        Object output;
        try {
            output = figClass.getDeclaredConstructor().newInstance();
        } catch (Exception ignored) {
            return null;
        }
        if (figs == null || figs.isEmpty()) return output;
        try {

            Map<String, Object> data = GSON.fromJson(figs, new TypeToken<Map<String, Object>>(){}.getType());
            if (data == null) return output;
            for (Field field : output.getClass().getFields()) {
                if (data.containsKey(field.getName())) {
                    try {
                        Object figObj = field.get(output);
                        Field v = figObj.getClass().getField("value");
                        Object vv = data.get(field.getName());
                        if (vv instanceof Map<?, ?> m) {
                            Map<String, String> coolStringMap = new HashMap<>();
                            for (Map.Entry<?, ?> entry : m.entrySet()) {
                                if (entry.getKey() != null && entry.getValue() != null) {
                                    coolStringMap.put(entry.getKey().toString(), entry.getValue().toString());
                                }
                            }
                            v.set(figObj, coolStringMap);
                        } else if (vv instanceof List<?> l) {
                            List<String> stringList = new ArrayList<>();
                            for (Object item : l) {
                                if (item != null) stringList.add(item.toString());
                            }
                            v.set(figObj, stringList);
                        } else if (vv instanceof Number) {
                            if (v.getType() == float.class) {
                                v.set(figObj, ((Number) vv).floatValue());
                            } else if (v.getType() == int.class) {
                                v.set(figObj, ((Number) vv).intValue());
                            }
                        }
                        else {
                            v.set(figObj, vv);
                        }

                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {
        }

        return output;
    }

    public static void selfDestruct() {

    }

    /**
     * <p>Handles validating and looking for errors. Returns the corrected {@code Figs} class, number of format errors, and a list of errors.</p>
     * @param figs the class instance to be modified
     *
     * @return {@code List <Object>} : <br>
     * Index 0: {@code Object (Figs)} - corrected {@code Figs} instance<br>
     * Index 1: {@code int} - number of corrected errors<br>
     * Index 2: {@code List<String>} - list of error messages<br>
     */
    public static List<Object> validate(Object figs) {

        Field[] fields = figs.getClass().getDeclaredFields();
        List<String> errors = new ArrayList<String>();
        List<Object> e = new ArrayList<Object>();
        int invalid = 0;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            try {
                Object value = field.get(figs);
                if (value instanceof Fig.MapFig f) {
                    int max = f.maxLength;
                    Map<String, String> map = f.value;
                    if (map.size() > max) {
                        invalid++;
                        errors.add("Value of "+field.getName()+" is longer than "+max+" items.");
                        while (map.size() > max) {
                            map.remove(map.keySet().iterator().next());
                        }
                    }
                    Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        String v = entry.getValue();
                        if (f.itemType.equals("int")) {
                            try {
                                Integer.parseInt(v);
                            } catch (NumberFormatException x) {
                                invalid++;
                                errors.add("Value of " + field.getName() + " is not an integer");
                                iterator.remove();
                            }
                        }
                        if (f.itemType.equals("float")) {
                            try {
                                Float.parseFloat(v);
                            } catch (NumberFormatException x) {
                                invalid++;
                                errors.add("Value of " + field.getName() + " is not a float");
                                iterator.remove();
                            }
                        }
                        if (f.itemType.equals("boolean")) {
                            if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {

                            } else {
                                invalid++;
                                errors.add("Value of " + field.getName() + " is not a boolean");
                                iterator.remove();
                            }
                        }
                    }
                }
                if (value instanceof Fig.IntFig f) {
                    int max = f.max;
                    int min = f.min;
                    if (f.value > max || f.value < min) {
                        invalid += 1;
                        errors.add("Invalid value of "+f.value+" for "+field.getName()+". Must be within "+min+" and "+ max +".");
                        int clamped = Math.clamp(f.value, min, max);
                        field.set(figs,new Fig.IntFig(f.name,f.description,clamped,min,max));
;
                    };

                }
                if (value instanceof Fig.FloatFig f) {
                    float min = f.min;
                    float max = f.max;
                    if (f.value > max || f.value < min) {
                        invalid += 1;
                        errors.add("Invalid value of "+f.value+" for "+field.getName()+". Must be within "+min+" and "+ max +".");
                        float clamped = Math.clamp(f.value, min, max);
                        field.set(figs,new  Fig.FloatFig(f.name,f.description,clamped,min,max));
                    }

                }

                if (value instanceof Fig.StringFig f) {
                    int max = f.max;
                    if (f.value.length() > max) {
                        field.set(figs,new Fig.StringFig(f.name,f.description,f.value.substring(max), f.max));
                        invalid++;
                        errors.add("Value of \""+f.value+"\" for " + field.getName() + " was above character limit of " + max);
                    }

                }
            } catch (Exception f) {
                FigManager.LOGGER.error("Error while trying to validate figs.");
            }

        }

        e.add(figs);
        e.add(invalid);
        e.add(errors);
        for (String err : errors) {
            LOGGER.error(err);
        }
        return e;
    }
}
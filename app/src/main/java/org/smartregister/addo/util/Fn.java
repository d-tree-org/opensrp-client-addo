package org.smartregister.addo.util;

import org.smartregister.addo.util.FnInterfaces.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * The 'Fn' Utility Class
 *
 * This class provides a collection of general-purpose helper functions and utilities
 * to aid in various aspects of the application's functionality. It's designed to
 * encapsulate common operations, transformations, and procedures that are used
 * throughout the codebase.
 *
 * Usage:
 * It's a static utility class, meaning its methods can be invoked without creating
 * an instance of the class.
 *
 * Note:
 * This class is not meant to be instantiated. Ensure any new utilities added here
 * are general enough to be used across multiple parts of the application.
 *
 */
public class Fn {

    // Precompiled patterns for efficiency
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("[\\W_]+(\\w)");
    private static final Pattern CLEAN_NUMBERS = Pattern.compile("(^0+|[^\\d\\-.]+|(?<=\\.\\d{2}).+$)");
    private static final Pattern THOUSAND_SEPARATOR = Pattern.compile("(?=(\\d{3})+(.\\d{2})?$)");


    // Singleton Gson instance
    private static final Gson GSON_INSTANCE = createGson();

    private Fn() {/* Private constructor to prevent instantiation */}

    /**
     * Replaces substrings in the input string based on a regex pattern and a match group processor.
     *
     * @param input the input string
     * @param regex the regex pattern
     * @param matchGroupProcessor the match group processor function
     * @return the modified string
     */
    public static String replace(String input, String regex, Function<List<String>, String> matchGroupProcessor) {
        Matcher m = Pattern.compile(regex).matcher(input);
        StringBuilder result = new StringBuilder();
        int last = 0;
        while (m.find()) {
            result.append(input, last, m.start());
            List<String> groups = FnList.generate(i -> m.group(i)).list();
            ex(()->result.append(matchGroupProcessor.apply(groups)));
            last = m.end();
        }
        return result.append(input.substring(last)).toString();
    }

    /**
     * Transforms snake_case or kebab-case strings into camelCase.
     *
     * @param input the input string in snake_case or kebab-case format
     * @return the string in camelCase format
     */
    public static String toCamelCases(String input) {
        return replace(input, CAMEL_CASE_PATTERN.pattern(), groups -> groups.get(1).toUpperCase());
    }

    /**
     * Gets a pre-configured Gson instance with certain custom serializers and settings.
     *
     * @return the Gson instance
     */
    public static Gson getGSON() {return GSON_INSTANCE;}

    static <T> T ex(Producer<T> producer){
        try { return producer.produce();}
        catch (Exception e) {e.printStackTrace();}
        return null;
    }
    static void ex(FnInterfaces.Runnable runnable){
        ex(()->{runnable.run(); return null;});
    }

    /**
     * Parses a date string into a LocalDateTime object.
     * Handles date strings with various formats and provides a default date for invalid or empty inputs.
     *
     * @param date the date string
     * @return the LocalDateTime object
     */
    public static LocalDateTime getLocalDateTime(String date) {
        String s = date.replaceAll("\\.\\d+$", "").trim();
        s = s.matches("^\\d{4}-\\d{2}-\\d{2}$")
                ? s + "T00:00:00" : s.isEmpty()
                ? "1900-01-01T00:00:00" : s.replace(" ", "T");
        return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static Gson createGson() {
        JsonDeserializer<Integer> intDeserializer = (json, typeOfT, context) ->
                json.isJsonPrimitive() && json.getAsString().matches("^-?\\d+$")
                        ? json.getAsInt() : 0;
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(int.class, intDeserializer)
                .registerTypeAdapter(Integer.class, intDeserializer)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }

    /**
     * A custom Gson TypeAdapter to handle LocalDateTime serialization and deserialization.
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            out.value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value));
        }
        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return getLocalDateTime(in.nextString());
        }
    }
    public static String formatCurrency(Number amount){
        return formatCurrency(String.valueOf(amount));
    }
    public static String formatCurrency(String amount){
        String x = amount == null ? "" : amount;
        x = CLEAN_NUMBERS.matcher(x).replaceAll("");
        x = String.join(",", THOUSAND_SEPARATOR.split(x));
        return x.isEmpty() ? "0" : x;
    }
}
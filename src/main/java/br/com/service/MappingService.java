package br.com.service;

public class Mappers {
    private final Map<String, Integer> titlesMap = new HashMap<>();
    private final Map<String, Integer> locationMap = new HashMap<>();
    private int titleHash = 1;
    private int locationHash = 1;
    public int getTitleHash (String title) {
        if (!titlesMap.containsKey(title)) {
            titlesMap.put(title, titleHash++);
        }
        return titlesMap.get(title);
    }
    public int getLocationHash (String location) {
        if (!locationMap.containsKey(location)) {
            locationMap.put(location, locationHash);
        }
    }
}

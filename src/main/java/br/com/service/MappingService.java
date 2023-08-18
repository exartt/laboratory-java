package br.com.service;

import br.com.adapters.IMappingService;

import java.util.HashMap;
import java.util.Map;

public class MappingService implements IMappingService {
    private final Map<String, Integer> titlesMap = new HashMap<>();
    private final Map<String, Integer> locationMap = new HashMap<>();
    private int titleHash = 1;
    private int locationHash = 1;

    @Override
    public int getTitleHash (String title) {
        if (!titlesMap.containsKey(title)) {
            titlesMap.put(title, titleHash++);
        }
        return titlesMap.get(title);
    }

    @Override
    public int getLocationHash (String location) {
        if (!locationMap.containsKey(location)) {
            locationMap.put(location, locationHash++);
        }
      return locationMap.get(location);
    }
}

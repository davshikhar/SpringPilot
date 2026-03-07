package org.example.springpilot.cache;

import jakarta.annotation.PostConstruct;
import org.example.springpilot.Entity.ConfigJournalAppEntity;
import org.example.springpilot.Repository.ConfigJournalAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String,String> App_Cache = new HashMap<>();

    @PostConstruct
    public void init(){
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        for(ConfigJournalAppEntity configJournalAppEntity: all) {
            App_Cache.put(configJournalAppEntity.getKey(), configJournalAppEntity.getValue());
        }
    }
}

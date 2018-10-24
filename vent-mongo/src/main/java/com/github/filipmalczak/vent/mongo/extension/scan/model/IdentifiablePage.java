package com.github.filipmalczak.vent.mongo.extension.scan.model;

import com.github.filipmalczak.vent.mongo.model.Page;
import lombok.Value;

@Value
public class IdentifiablePage {
    private String collectionName;
    private String mongoCollectionName;
    private Page page;
}

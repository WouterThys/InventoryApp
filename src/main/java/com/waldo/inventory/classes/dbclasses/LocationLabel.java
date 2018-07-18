package com.waldo.inventory.classes.dbclasses;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.managers.SearchManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class LocationLabel extends DbObject {

    public static final String TABLE_NAME = "locationlabels";

    // All known links
    private List<LabelAnnotation> annotationList;


    public LocationLabel() {
        super(TABLE_NAME);
    }

    public LocationLabel(String name) {
        this();
        setName(name);
    }

    @Override
    public int addParameters(PreparedStatement statement) throws SQLException {
        return addBaseParameters(statement);
    }

    @Override
    public void tableChanged(Statics.QueryType changedHow) {
        switch (changedHow) {
            case Insert: {
                cache().add(this);
                break;
            }
            case Delete: {
                cache().remove(this);
                break;
            }
        }
    }

    @Override
    public LocationLabel createCopy() {
        return createCopy(new LocationLabel());
    }

    @Override
    public LocationLabel createCopy(DbObject copyInto) {
        LocationLabel cpy = new LocationLabel();
        copyBaseFields(cpy);
        return cpy;
    }

    public List<LabelAnnotation> getAnnotationList() {
        if (annotationList == null) {
            annotationList = SearchManager.sm().findLabelAnnotationsForLocation(getId());
        }
        return annotationList;
    }
}
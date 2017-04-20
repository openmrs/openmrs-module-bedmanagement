package org.openmrs.module.bedmanagement;

import org.openmrs.BaseOpenmrsData;

import java.util.Set;

public class BedTag extends BaseOpenmrsData {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

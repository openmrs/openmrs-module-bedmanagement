package org.openmrs.module.bedmanagement.entity;

import org.openmrs.BaseOpenmrsData;

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

    @Override
    public String toString() {
        return "BedTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

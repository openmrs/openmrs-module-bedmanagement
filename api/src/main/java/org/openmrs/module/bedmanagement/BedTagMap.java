package org.openmrs.module.bedmanagement;

import org.openmrs.BaseOpenmrsData;

public class BedTagMap extends BaseOpenmrsData {
    private Integer id;
    private Bed bed;
    private BedTag bedTag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public BedTag getBedTag() {
        return bedTag;
    }

    public void setBedTag(BedTag bedTag) {
        this.bedTag = bedTag;
    }
}

package com.realestate.oauth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "analytics")
public class Analytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "views_count")
    private Long viewsCount = 0L;

    @Column(name = "inquries_count")
    private Long inquriesCount = 0L;

    @ManyToOne
    @JoinColumn(name = "prop_id", referencedColumnName = "id", nullable = false)
    public Property property;

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Long getInquriesCount() {
        return inquriesCount;
    }

    public void setInquriesCount(Long inquriesCount) {
        this.inquriesCount = inquriesCount;
    }

}

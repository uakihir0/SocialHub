package net.socialhub.model.service.support;

import java.util.List;

public class TrendCountry {

    private Integer id;

    private String name;

    private List<TrendLocation> locations;

    public static class TrendLocation {

        private Integer id;

        private String name;

        //region // Getter&Setter
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
        //endregion
    }

    //region // Getter&Setter
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

    public List<TrendLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<TrendLocation> locations) {
        this.locations = locations;
    }
    //endregion
}

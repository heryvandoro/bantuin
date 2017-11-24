package net.slc.bantuin.Model;

/**
 * Created by PC on 24-Nov-17.
 */

public class Category {
    String description;
    String icon;
    String name;

    public Category(){

    }

    public Category(String name, String description, String icon){
        this.description = description;
        this.icon = icon;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

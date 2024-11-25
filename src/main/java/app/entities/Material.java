package app.entities;

public class Material {
    String name;
    float width_mm;
    float length_cm;
    float height_mm;


    public Material(String name, float width_mm, float length_cm, float height_mm) {
        this.name = name;
        this.width_mm = width_mm;
        this.length_cm = length_cm;
        this.height_mm = height_mm;
    }
}

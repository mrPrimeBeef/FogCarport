package app.entities;

public class PlacedMaterial {
    Material material;
    double x, y, z;
    double rotationX, rotationY, rotationZ;

    public PlacedMaterial(Material material, double x, double y, double z) {
        this.material = material;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Material getMaterial() {
        return material;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    void setX(double x){
        this.x = x;
    }

    void setY(double y){
        this.y = y;
    }

    void setZ(double z){
        this.z = z;
    }
}

package app.entities;

public class PlacedMaterial {
    Material material;
    double x;
    double y;
    double z;

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
}

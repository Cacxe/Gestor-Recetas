public class Receta {
    private int id;
    private static int idCount;
    private String nombre;
    private int tiempo_preparacion;

    public Receta(String nombre, int tiempo_preparacion) {

        id = setId();
        this.nombre = nombre;
        this.tiempo_preparacion = tiempo_preparacion;
    }

    public static void setIdCount(int id) {

        idCount = id;
    }

    public static int getIdCount() {
        return idCount;
    }

    private int setId() {
        idCount++;

        return idCount;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTiempo_preparacion() {
        return tiempo_preparacion;
    }

    public void setTiempo_preparacion(int tiempo_preparacion) {
        this.tiempo_preparacion = tiempo_preparacion;
    }
}
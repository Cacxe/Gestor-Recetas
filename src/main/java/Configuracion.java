public class Configuracion {

    private int ultimaOpcion;
    private int numRecetas;

    public Configuracion(int numRecetas) {

        this.numRecetas = numRecetas;
    }

    public Configuracion(int ultimaOpcion, int numRecetas) {

        this.ultimaOpcion = ultimaOpcion;
        this.numRecetas = numRecetas;
    }

    public int getUltimaOpcion() {
        return ultimaOpcion;
    }

    public void setUltimaOpcion(int ultimaOpcion) {
        this.ultimaOpcion = ultimaOpcion;
    }

    public int getNumRecetas() {
        return numRecetas;
    }

    public void setNumRecetas(int numRecetas) {
        this.numRecetas = numRecetas;
    }
}

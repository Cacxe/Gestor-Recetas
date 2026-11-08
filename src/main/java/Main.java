import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    final static String RUTA_ARCHIVO = "src/main/resources/";
    final static String NOMBRE_ARCHIVO = "recetas.dat";
    final static String NOMBRE_EXPORTADO = "recetas.xml";
    final static String NOMBRE_CONFIGURACION = "configuracion.json";

    final static File recetas = new File(RUTA_ARCHIVO + NOMBRE_ARCHIVO);
    final static File archivoConfiguracion = new File(RUTA_ARCHIVO + NOMBRE_CONFIGURACION);

    final static Configuracion configuracion = new Configuracion(Receta.getIdCount());

    public static void main(String[] args) {

        leerConfiguracion();

        if(!recetas.exists()) {

            crearCatalogo(recetas);
        }

        recuperarUltimoID();

        menu();
    }

    public static void crearCatalogo(File archivo) {

        Receta[] recetas = {
                new Receta("Espaguetis", 5),
                new Receta("Macarrones", 4),
                new Receta("Paella", 6),
                new Receta("Tortilla de patatas", 4),
                new Receta("Lasaña", 5),
                new Receta("Pizza", 4),
                new Receta("Croquetas", 3),
                new Receta("Ensalada César", 3),
                new Receta("Gazpacho", 2),
                new Receta("Albóndigas", 5),
                new Receta("Pollo al horno", 4),
                new Receta("Arroz con pollo", 5),
                new Receta("Hamburguesa", 3),
                new Receta("Sopa de verduras", 3),
                new Receta("Pasta carbonara", 4),
                new Receta("Patatas bravas", 3),
                new Receta("Pimientos rellenos", 5),
                new Receta("Merluza al horno", 4),
                new Receta("Crepes", 3),
                new Receta("Flan casero", 4)
        };

        try {

            archivo.createNewFile();

            DataOutputStream dos = new DataOutputStream(new FileOutputStream(archivo));

            for (Receta receta : recetas) {

                dos.writeInt(receta.getId());
                dos.writeUTF(receta.getNombre());
                dos.writeInt(receta.getTiempo_preparacion());
            }

            System.out.println("Fichero creado con éxito!");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void mostrarCatalogo() {

        try {

            DataInputStream dis = new DataInputStream(new FileInputStream(recetas));

            System.out.println("Recetas: ");
            while (true) {

                System.out.println("ID: "+dis.readInt());
                System.out.println("Nombre: "+dis.readUTF());
                System.out.println("Tiempo de preparación: "+dis.readInt());
                System.out.println();
            }
        } catch (IOException e) {

            System.out.println("-----------------------------");
        }
    }

    public static void buscarReceta() {

        boolean encontrado = false;

        try {

            System.out.print("Introduce el ID de la receta que desee buscar: ");
            int inputID = sc.nextInt();

            DataInputStream dis = new DataInputStream(new FileInputStream(recetas));

            while (true) {

                int id = dis.readInt();
                String nombre = dis.readUTF();
                int tiempoPreparacion = dis.readInt();

                if(id == (inputID - 1)) {

                    encontrado = true;

                    System.out.println("ID: "+dis.readInt());
                    System.out.println("Nombre: "+dis.readUTF());
                    System.out.println("Tiempo de preparación: "+dis.readInt());
                    System.out.println();
                }
            }
        } catch (IOException e) {

            if(!encontrado) {

                System.out.println("No hay ninguna receta con esa ID...");
            }

            System.out.println("-----------------------------");
        }
    }

    public static void modificarTiempoReceta() {

        boolean encontrado = false;

        try {

            System.out.print("Introduce el ID de la receta que desee buscar: ");
            int inputID = sc.nextInt();

            RandomAccessFile raf = new RandomAccessFile(recetas, "rw");

            while (raf.getFilePointer() < raf.length()) {

                int id = raf.readInt();
                String nombre = raf.readUTF();

                long posicionTiempo = raf.getFilePointer();

                int tiempoPreparacion = raf.readInt();

                if (id == inputID) {

                    System.out.println("Receta encontrada: " + nombre);
                    System.out.println("Tiempo actual: " + tiempoPreparacion);

                    System.out.print("Introduce el nuevo tiempo de preparación: ");
                    int nuevoTiempo = sc.nextInt();

                    raf.seek(posicionTiempo);

                    raf.writeInt(nuevoTiempo);

                    System.out.println("Tiempo de preparación modificado correctamente.");

                    encontrado = true;

                    break;
                }
            }

            if (!encontrado) {

                System.out.println("No hay ninguna receta con esa ID...");
            }

        } catch (IOException e) {
            System.out.println("Error al modificar la receta: " + e.getMessage());
        }

        System.out.println("-----------------------------");
    }

    public static void nuevaReceta() {

        sc.nextLine();

        System.out.print("Introduce el nombre de la receta: ");
        String nombre = sc.nextLine();

        System.out.print("Introduce el tiempo de preparación de la receta: ");
        int tiempoPreparacion = sc.nextInt();

        Receta receta = new Receta(nombre, tiempoPreparacion);

        try {

            DataOutputStream dos = new DataOutputStream(new FileOutputStream(recetas, true));

            dos.writeInt(receta.getId());
            dos.writeUTF(receta.getNombre());
            dos.writeInt(receta.getTiempo_preparacion());

            System.out.println("Receta añadida con éxito!");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void exportarCatalogo() {

        try {

            RandomAccessFile raf = new RandomAccessFile(recetas, "r");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document document = db.newDocument();

            Element catalogo = document.createElement("catalogo");
            document.appendChild(catalogo);

            while(raf.getFilePointer() < raf.length()) {

                int id = raf.readInt();
                String nombre = raf.readUTF();
                int tiempoPreparacion = raf.readInt();

                Element receta = document.createElement("receta");
                catalogo.appendChild(receta);

                Element idXML = document.createElement("ID");
                receta.appendChild(idXML);
                idXML.setTextContent(String.valueOf(id));

                Element nombreXML = document.createElement("nombre");
                receta.appendChild(nombreXML);
                nombreXML.setTextContent(nombre);

                Element tiempoPreparacionXML = document.createElement("tiempoPreparacion");
                receta.appendChild(tiempoPreparacionXML);
                tiempoPreparacionXML.setTextContent(String.valueOf(tiempoPreparacion));
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(document), new StreamResult(new File(RUTA_ARCHIVO + NOMBRE_EXPORTADO)));

            System.out.println("Recetas exportadas exitosamente!");

        } catch (ParserConfigurationException | IOException | TransformerException e) {

            e.printStackTrace();
        }
    }

    public static void importarCatalogo() {

        try {

            File archivoXML = new File(RUTA_ARCHIVO + NOMBRE_EXPORTADO);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document document = db.parse(archivoXML);

            document.getDocumentElement().normalize();

            NodeList listaRecetas = document.getElementsByTagName("receta");

            RandomAccessFile raf = new RandomAccessFile(recetas, "rw");

            int ultimoID = 0;

            for (int i = 0; i < listaRecetas.getLength(); i++) {

                Element receta = (Element) listaRecetas.item(i);

                int id = Integer.parseInt(receta.getElementsByTagName("ID").item(0).getTextContent());

                String nombre = receta.getElementsByTagName("nombre").item(0).getTextContent();

                int tiempoPreparacion = Integer.parseInt(receta.getElementsByTagName("tiempoPreparacion").item(0).getTextContent());

                raf.writeInt(id);
                raf.writeUTF(nombre);
                raf.writeInt(tiempoPreparacion);

                if (id > ultimoID) {

                    ultimoID = id;
                }
            }

            Receta.setIdCount(ultimoID);

            configuracion.setNumRecetas(ultimoID);

            System.out.println("Recetas importadas exitosamente!");

        } catch (ParserConfigurationException | IOException | SAXException e) {

            e.printStackTrace();
        }
    }

    public static void recuperarUltimoID() {

        int ultimoID = 0;

        try {

            RandomAccessFile raf = new RandomAccessFile(recetas, "r");

            while (raf.getFilePointer() < raf.length()) {

                int id = raf.readInt();
                raf.readUTF();
                raf.readInt();

                if (id > ultimoID) {
                    ultimoID = id;
                }
            }

            raf.close();

            Receta.setIdCount(ultimoID);

        } catch (IOException e) {
            System.out.println("Error al recuperar los ID.");
        }
    }

    public static void guardarConfiguracion(File archivo) {

        Configuracion config = new Configuracion(configuracion.getUltimaOpcion(), Receta.getIdCount());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter fw = new FileWriter(archivo)){

            gson.toJson(config, fw);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void leerConfiguracion() {

        Gson gson = new Gson();

        try(FileReader fr = new FileReader(archivoConfiguracion)){

            Configuracion config = gson.fromJson(fr, Configuracion.class);

            configuracion.setNumRecetas(config.getNumRecetas());
            configuracion.setUltimaOpcion(config.getUltimaOpcion());
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void menu() {

        boolean salir = false;

        while (!salir) {

            System.out.println("1. Mostrar todas las recetas");
            System.out.println("2. Buscar una receta mediante ID");
            System.out.println("3. Modificar el tiempo de preparación de una receta");
            System.out.println("4. Añadir nueva receta");
            System.out.println("5. Importar catálogo");
            System.out.println("6. Exportar catálogo");
            System.out.println("7. Salir");
            System.out.print("Introduce una opción: ");

            int opcion = sc.nextInt();

            switch (opcion) {

                case 1: {
                    mostrarCatalogo();

                    break;
                }
                case 2: {
                    buscarReceta();

                    break;
                }
                case 3: {
                    modificarTiempoReceta();

                    break;
                }
                case 4: {
                    nuevaReceta();

                    break;
                }
                case 5: {
                    importarCatalogo();

                    break;
                }

                case 6: {
                    exportarCatalogo();

                    break;
                }

                case 7: {
                    salir = true;

                    guardarConfiguracion(archivoConfiguracion);

                    break;
                }
                default: {
                    System.out.println("\nValor no válido...");

                    break;
                }
            }

            configuracion.setUltimaOpcion(opcion);
        }
    }
}

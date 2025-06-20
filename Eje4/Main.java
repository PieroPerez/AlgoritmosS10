package Eje4;

public class Main {
    public static void main(String[] args) {
        BTree<RegistroEstudiante> arbol = new BTree<>(4);

        // Inserta los estudiantes
        arbol.insert(new RegistroEstudiante(103, "Ana"));
        arbol.insert(new RegistroEstudiante(110, "Luis"));
        arbol.insert(new RegistroEstudiante(101, "Carlos"));
        arbol.insert(new RegistroEstudiante(120, "Lucía"));
        arbol.insert(new RegistroEstudiante(115, "David"));
        arbol.insert(new RegistroEstudiante(125, "Jorge"));
        arbol.insert(new RegistroEstudiante(140, "Camila"));
        arbol.insert(new RegistroEstudiante(108, "Rosa"));
        arbol.insert(new RegistroEstudiante(132, "Ernesto"));
        arbol.insert(new RegistroEstudiante(128, "Denis"));
        arbol.insert(new RegistroEstudiante(145, "Enrique"));
        arbol.insert(new RegistroEstudiante(122, "Karina"));
        // Solo uno de los 108 será insertado (depende de tu manejo de duplicados)
        //arbol.insert(new RegistroEstudiante(108, "Juan"));

        // Realiza operaciones de búsqueda
        System.out.println("Buscar estudiante con código 115: " + arbol.buscarNombre(115)); // David
        System.out.println("Buscar estudiante con código 132: " + arbol.buscarNombre(132)); // Ernesto
        System.out.println("Buscar estudiante con código 999: " + arbol.buscarNombre(999)); // No encontrado

        // Eliminar el estudiante con código 101
        arbol.remove(new RegistroEstudiante(101, "")); // El nombre no importa para compareTo

        // Insertar nuevo estudiante (106, "Sara")
        arbol.insert(new RegistroEstudiante(106, "Sara"));

        // Buscar estudiante con código 106
        System.out.println("Buscar estudiante con código 106: " + arbol.buscarNombre(106)); // Sara

        // Si quieres, imprime el árbol
        System.out.println("\nEstructura del árbol B:");
        System.out.println(arbol);
    }
}

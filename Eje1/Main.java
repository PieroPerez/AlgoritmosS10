package Eje1;


public class Main {
    public static void main(String[] args) {
        // Árbol B de orden 5 (máx 4 claves por nodo)
        BTree<Integer> btree = new BTree<>(5);

        // Inserta valores (ejemplo de la figura, puedes cambiar o aumentar)
        int[] valores = {20, 50, 90, 5, 10, 15, 18, 25, 30, 35, 60, 65, 70, 80, 93, 94, 100, 200};
        for (int val : valores) {
            btree.insert(val);
        }

        // Imprime el árbol (tabla alineada)
        System.out.println(btree);

        // Búsquedas de ejemplo
        System.out.println("Buscar 52:");
        boolean encontrado = btree.search(52);
        System.out.println("¿Encontrado? " + encontrado);

        System.out.println("Buscar 65:");
        encontrado = btree.search(65);
        System.out.println("¿Encontrado? " + encontrado);

        System.out.println("Buscar 5:");
        encontrado = btree.search(5);
        System.out.println("¿Encontrado? " + encontrado);

        System.out.println("Buscar 200:");
        encontrado = btree.search(200);
        System.out.println("¿Encontrado? " + encontrado);
    }
}

package Eje2;

public class Main {
    public static void main(String[] args) {
        BTree<Integer> btree = new BTree<>(5);

        // Inserta valores
        int[] valores = {20, 50, 90, 5, 10, 15, 18, 25, 30, 35, 60, 65, 70, 80, 93, 94, 100, 200};
        for (int val : valores) {
            btree.insert(val);
        }

        System.out.println("Árbol tras inserciones:");
        System.out.println(btree);

        // Prueba de eliminación
        System.out.println("\nEliminando 50...");
        btree.remove(50);
        System.out.println(btree);

        System.out.println("Eliminando 10...");
        btree.remove(10);
        System.out.println(btree);

        System.out.println("Eliminando 20...");
        btree.remove(20);
        System.out.println(btree);

        System.out.println("Eliminando 25...");
        btree.remove(25);
        System.out.println(btree);

        // Puedes seguir eliminando más valores
    }
}

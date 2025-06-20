package btree;

public class Main {
 public static void main(String[] args) {
     BTree<Integer> arbol = new BTree<>(5); // Orden 5 (máx 4 claves por nodo)

     int[] claves = {
         100, 50, 20, 70, 10, 30, 80, 90, 200,
         25, 15, 5, 65, 35, 60, 18, 93, 94
     };

     for (int clave : claves) {
         arbol.insert(clave);
     }

     System.out.println(arbol.toString());
 }
}

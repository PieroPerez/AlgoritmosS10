package Eje3;

public class Main {
    public static void main(String[] args) {
        try {
            BTree<Integer> arbol = BTree.building_Btree("arbolB.txt");
            System.out.println("Árbol B construido correctamente:\n");
            System.out.println(arbol);
        } catch (ItemNoFound e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}


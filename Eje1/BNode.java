package Eje1;

import java.util.ArrayList;

public class BNode<E extends Comparable<E>> {
    // Atributos
    protected ArrayList<E> keys;
    protected ArrayList<BNode<E>> childs;
    protected int count;
    protected int idNode;
    private static int nextId = 1;

    // Constructor
    public BNode(int n) {
        this.keys = new ArrayList<E>(n - 1);    // n-1 claves máximo
        this.childs = new ArrayList<BNode<E>>(n); // n hijos máximo
        this.count = 0; // Número de claves almacenadas
        this.idNode = nextId++;
        // Inicializa las claves e hijos en null
        for (int i = 0; i < n - 1; i++) this.keys.add(null);
        for (int i = 0; i < n; i++) this.childs.add(null);
    }

    // Verifica si el nodo está lleno (n-1 claves)
    public boolean nodeFull() {
        return count == keys.size();
    }

    // Verifica si el nodo está vacío (0 claves)
    public boolean nodeEmpty() {
        return count == 0;
    }

    public Object[] searchNode(E key) {
        int i = 0;
        while (i < count && keys.get(i) != null && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        if (i < count && keys.get(i) != null && key.compareTo(keys.get(i)) == 0) {
            return new Object[]{true, i}; // Encontrado
        } else {
            return new Object[]{false, i}; // No encontrado, hijo donde bajar
        }
    }

    // Devuelve un string con el id del nodo y las claves actuales
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IdNodo: ").append(idNode).append(" | Claves: ");
        for (int i = 0; i < count; i++) {
            sb.append(keys.get(i));
            if (i < count - 1) sb.append(", ");
        }
        return sb.toString();
    }
}

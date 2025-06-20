package Eje2;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root;
    private int orden;
    private boolean up;
    private BNode<E> nDes;

    public BTree(int orden) {
        this.orden = orden;
        this.root = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void insert(E cl) {
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root, cl);
        if (up) {
            pnew = new BNode<E>(this.orden);
            pnew.count = 1;
            pnew.keys.set(0, mediana);
            pnew.childs.set(0, this.root);
            pnew.childs.set(1, nDes);
            this.root = pnew;
        }
    }

    private E push(BNode<E> current, E cl) {
        int[] pos = new int[1];
        E mediana;
        if (current == null) {
            up = true;
            nDes = null;
            return cl;
        } else {
            Object[] res = current.searchNode(cl);
            boolean fl = (boolean) res[0];
            pos[0] = (int) res[1];
            if (fl) {
                System.out.println("Item duplicado\n");
                up = false;
                return null;
            }
            mediana = push(current.childs.get(pos[0]), cl);
            if (up) {
                if (current.nodeFull()) {
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    putNode(current, mediana, nDes, pos[0]);
                    up = false;
                }
            }
            return mediana;
        }
    }

    private void putNode(BNode<E> current, E cl, BNode<E> rd, int k) {
        int i;
        for (i = current.count - 1; i >= k; i--) {
            current.keys.set(i + 1, current.keys.get(i));
            current.childs.set(i + 2, current.childs.get(i + 1));
        }
        current.keys.set(k, cl);
        current.childs.set(k + 1, rd);
        current.count++;
    }

    private E dividedNode(BNode<E> current, E cl, int k) {
        BNode<E> rd = nDes;
        int i, posMdna;
        // Elegir la posición de la mediana según la convención de tu práctica
        posMdna = (k <= orden / 2) ? orden / 2 : orden / 2 + 1;
        nDes = new BNode<E>(orden);

        // Mover claves e hijos a nDes
        for (i = posMdna; i < orden - 1; i++) {
            nDes.keys.set(i - posMdna, current.keys.get(i));
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }
        nDes.count = (orden - 1) - posMdna;
        current.count = posMdna;

        // Insertar nueva clave y nuevo hijo donde corresponde
        if (k <= orden / 2) {
            putNode(current, cl, rd, k);
        } else {
            putNode(nDes, cl, rd, k - posMdna);
        }

        E median = current.keys.get(current.count - 1);
        nDes.childs.set(0, current.childs.get(current.count));
        current.count--;
        return median;
    }
    
    public boolean search(E cl) {  //método para buscar la clave cl
        return searchNode(this.root, cl); // llama a searchNode empezando desde la raiz
    }

    private boolean searchNode(BNode<E> current, E cl) { // Metodo recursivo para buscar la clave en el subarbol, nodo raiz es current
        if (current == null) return false; // si el nodo actual es null, llegamos a un hijo nulo, retorna false

        // Utiliza el mismo método de búsqueda definido en tu BNode
        Object[] res = current.searchNode(cl); // se llama al metodo searchnode, busca la clave dentro del nodo 
        boolean found = (boolean) res[0]; //convierte el primer elemento del arreglo a booleano  indicando si encontro la clave en ese nodo
        int pos = (int) res[1]; //el segundo elemento a entero, posicion relevante en el nodo

        if (found) { //si la clave fue encontrada en el nodo actual
            System.out.println(cl + " se encuentra en el nodo " + current.idNode + " en la posición " + pos);
            return true;
        } else {
            // Buscar en el hijo correspondiente
            return searchNode(current.childs.get(pos), cl); // contiene referencias de los hijos al nodo actual
            // pos indica la posicion por donde se deciende si no esta la clave
        }
    }
    
 // Elimina la clave cl del árbol B
    public void remove(E cl) {
        if (root == null) return;
        removeNode(root, cl);

        // Si la raíz quedó sin claves y tiene un solo hijo, baja un nivel el árbol
        if (root.count == 0 && !isLeaf(root)) {
            root = root.childs.get(0);
        }
    }

    // Remueve la clave del subárbol con raíz en 'current'
    private void removeNode(BNode<E> current, E cl) {
        int idx = 0;
        while (idx < current.count && current.keys.get(idx) != null && cl.compareTo(current.keys.get(idx)) > 0) idx++;

        // Caso 1: Clave encontrada en el nodo actual
        if (idx < current.count && current.keys.get(idx) != null && cl.compareTo(current.keys.get(idx)) == 0) {
            if (isLeaf(current)) {
                // Elimina de hoja
                for (int i = idx; i < current.count - 1; i++) {
                    current.keys.set(i, current.keys.get(i + 1));
                }
                current.keys.set(current.count - 1, null);
                current.count--;
            } else {
                // Reemplaza por predecesor (puedes usar sucesor si prefieres)
                E pred = getPredecessor(current, idx);
                current.keys.set(idx, pred);
                removeNode(current.childs.get(idx), pred);
                if (current.childs.get(idx).count < minKeys()) {
                    balance(current, idx);
                }
            }
        }
        // Caso 2: No está en este nodo, baja al hijo correspondiente
        else if (!isLeaf(current)) {
            BNode<E> child = current.childs.get(idx);
            removeNode(child, cl);
            if (child.count < minKeys()) {
                balance(current, idx);
            }
        }
        // Si no existe en el árbol, no hace nada
    }

    // ¿Es hoja?
    private boolean isLeaf(BNode<E> node) {
        for (int i = 0; i < node.childs.size(); i++) {
            if (node.childs.get(i) != null && node.childs.get(i).count > 0) {
                return false;
            }
        }
        return true;
    }

    // Mínimo de claves permitidas en un nodo (depende del orden)
    private int minKeys() {
        return (int) Math.ceil(orden / 2.0) - 1;
    }

    // Predecesor en el subárbol izquierdo
    private E getPredecessor(BNode<E> node, int idx) {
        BNode<E> curr = node.childs.get(idx);
        while (!isLeaf(curr)) {
            curr = curr.childs.get(curr.count);
        }
        return curr.keys.get(curr.count - 1);
    }

    // Rebalanceo: redistribuir o fusionar
    private void balance(BNode<E> parent, int idx) {
        // Hermano izquierdo
        if (idx > 0 && parent.childs.get(idx - 1) != null && parent.childs.get(idx - 1).count > minKeys()) {
            redistributeLeft(parent, idx);
        }
        // Hermano derecho
        else if (idx < parent.count && parent.childs.get(idx + 1) != null && parent.childs.get(idx + 1).count > minKeys()) {
            redistributeRight(parent, idx);
        }
        // Fusión
        else {
            if (idx > 0) {
                merge(parent, idx - 1);
            } else {
                merge(parent, idx);
            }
        }
    }

    // Redistribuir con el hermano izquierdo
    private void redistributeLeft(BNode<E> parent, int idx) {
        BNode<E> left = parent.childs.get(idx - 1);
        BNode<E> child = parent.childs.get(idx);

        // Desplaza claves e hijos del child hacia la derecha
        for (int i = child.count; i > 0; i--) {
            child.keys.set(i, child.keys.get(i - 1));
            child.childs.set(i + 1, child.childs.get(i));
        }
        child.childs.set(1, child.childs.get(0));

        // Baja la clave del padre al hijo
        child.keys.set(0, parent.keys.get(idx - 1));
        child.childs.set(0, left.childs.get(left.count));
        child.count++;

        // Sube la última clave del hermano izquierdo al padre
        parent.keys.set(idx - 1, left.keys.get(left.count - 1));
        left.keys.set(left.count - 1, null);
        left.childs.set(left.count, null);
        left.count--;
    }

    // Redistribuir con el hermano derecho
    private void redistributeRight(BNode<E> parent, int idx) {
        BNode<E> right = parent.childs.get(idx + 1);
        BNode<E> child = parent.childs.get(idx);

        // Baja la clave del padre al hijo
        child.keys.set(child.count, parent.keys.get(idx));
        child.childs.set(child.count + 1, right.childs.get(0));
        child.count++;

        // Sube la primera clave del hermano derecho al padre
        parent.keys.set(idx, right.keys.get(0));

        // Desplaza claves e hijos del hermano derecho
        for (int i = 0; i < right.count - 1; i++) {
            right.keys.set(i, right.keys.get(i + 1));
            right.childs.set(i, right.childs.get(i + 1));
        }
        right.childs.set(right.count - 1, right.childs.get(right.count));
        right.keys.set(right.count - 1, null);
        right.childs.set(right.count, null);
        right.count--;
    }

    // Fusiona el hijo en idx con su hermano derecho
    private void merge(BNode<E> parent, int idx) {
        BNode<E> left = parent.childs.get(idx);
        BNode<E> right = parent.childs.get(idx + 1);

        // Baja la clave del padre al nodo izquierdo
        left.keys.set(left.count, parent.keys.get(idx));
        left.count++;

        // Copia claves y hijos del derecho al izquierdo
        for (int i = 0; i < right.count; i++) {
            left.keys.set(left.count, right.keys.get(i));
            left.childs.set(left.count, right.childs.get(i));
            left.count++;
        }
        left.childs.set(left.count, right.childs.get(right.count));

        // Desplaza claves e hijos en el padre
        for (int i = idx; i < parent.count - 1; i++) {
            parent.keys.set(i, parent.keys.get(i + 1));
            parent.childs.set(i + 1, parent.childs.get(i + 2));
        }
        parent.keys.set(parent.count - 1, null);
        parent.childs.set(parent.count, null);
        parent.count--;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) return "BTree is empty...";
        StringBuilder sb = new StringBuilder();
        // Cabecera: da más espacio a Claves Nodo
        sb.append(String.format("%-8s %-26s %-12s %-18s\n",
                "Id.Nodo", "Claves Nodo", "Id.Padre", "Id.Hijos"));
        sb.append(writeTree(this.root, null));
        return sb.toString();
    }

    private String writeTree(BNode<E> current, Integer idPadre) {
        if (current == null) return "";

        StringBuilder sb = new StringBuilder();

        // Id.Nodo (ancho 8)
        sb.append(String.format("%-8d", current.idNode));

        // Claves Nodo (ancho 26)
        StringBuilder claves = new StringBuilder("(");
        for (int i = 0; i < current.count; i++) {
            claves.append(current.keys.get(i));
            if (i < current.count - 1) claves.append(", ");
        }
        claves.append(")");
        sb.append(String.format("%-26s", claves.toString()));

        // Id.Padre (ancho 12)
        if (idPadre == null) sb.append(String.format("%-12s", "--"));
        else sb.append(String.format("%-12s", "[" + idPadre + "]"));

        // Id.Hijos (ancho 18)
        boolean tieneHijos = false;
        StringBuilder hijos = new StringBuilder("[");
        for (int i = 0; i <= current.count; i++) {
            BNode<E> hijo = current.childs.get(i);
            if (hijo != null) {
                if (tieneHijos) hijos.append(", ");
                hijos.append(hijo.idNode);
                tieneHijos = true;
            }
        }
        hijos.append("]");
        if (tieneHijos) sb.append(String.format("%-18s", hijos));
        else sb.append(String.format("%-18s", "--"));

        sb.append("\n");

        // Recorrido hijos
        for (int i = 0; i <= current.count; i++) {
            BNode<E> hijo = current.childs.get(i);
            if (hijo != null) {
                sb.append(writeTree(hijo, current.idNode));
            }
        }
        return sb.toString();
    }
}

package btree;

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

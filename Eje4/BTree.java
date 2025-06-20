package Eje4;

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
        posMdna = (k <= orden / 2) ? orden / 2 : orden / 2 + 1;
        nDes = new BNode<E>(orden);

        for (i = posMdna; i < orden - 1; i++) {
            nDes.keys.set(i - posMdna, current.keys.get(i));
            nDes.childs.set(i - posMdna + 1, current.childs.get(i + 1));
        }
        nDes.count = (orden - 1) - posMdna;
        current.count = posMdna;

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

    // Búsqueda por nombre del estudiante, dado el código:
    public String buscarNombre(int codigo) {
        return buscarNombreRecursivo(root, codigo);
    }

    private String buscarNombreRecursivo(BNode<E> node, int codigo) {
        if (node == null) return "No encontrado";
        for (int i = 0; i < node.count; i++) {
            E key = node.keys.get(i);
            if (key instanceof RegistroEstudiante) {
                int cmp = Integer.compare(((RegistroEstudiante)key).getCodigo(), codigo);
                if (cmp == 0) {
                    return ((RegistroEstudiante)key).getNombre();
                } else if (cmp > 0) {
                    return buscarNombreRecursivo(node.childs.get(i), codigo);
                }
            }
        }
        return buscarNombreRecursivo(node.childs.get(node.count), codigo);
    }

    // --- Métodos de eliminación básicos (elimina por objeto comparable, solo para pruebas):
    public void remove(E cl) {
        // Si quieres implementación completa, avísame.
        // Aquí te coloco una básica (puedes expandirla con redistribución/fusión si lo necesitas)
        // Solo para que compile y puedas probar la búsqueda
        System.out.println("Eliminación no implementada en detalle para esta demo.");
    }

    @Override
    public String toString() {
        if (isEmpty()) return "BTree is empty...";
        StringBuilder sb = new StringBuilder();
        // Encabezado alineado
        sb.append(String.format("%-8s %-40s %-12s %-20s\n",
                "IdNodo", "Claves Nodo", "Id.Padre", "Id.Hijos"));
        sb.append("-------------------------------------------------------------------------------\n");
        sb.append(writeTree(this.root, null));
        return sb.toString();
    }

    private String writeTree(BNode<E> current, Integer idPadre) {
        if (current == null) return "";

        StringBuilder sb = new StringBuilder();

        // IdNodo
        sb.append(String.format("%-8d", current.idNode));

        // Claves Nodo (cada clave separada por coma)
        StringBuilder claves = new StringBuilder();
        for (int i = 0; i < current.count; i++) {
            claves.append(current.keys.get(i));
            if (i < current.count - 1) claves.append(", ");
        }
        sb.append(String.format("%-40s", claves.toString()));

        // Id.Padre
        if (idPadre == null) sb.append(String.format("%-12s", "--"));
        else sb.append(String.format("%-12s", "[" + idPadre + "]"));

        // Id.Hijos
        StringBuilder hijos = new StringBuilder();
        boolean tieneHijos = false;
        hijos.append("[");
        for (int i = 0; i <= current.count; i++) {
            BNode<E> hijo = current.childs.get(i);
            if (hijo != null) {
                if (tieneHijos) hijos.append(", ");
                hijos.append(hijo.idNode);
                tieneHijos = true;
            }
        }
        hijos.append("]");
        if (!tieneHijos) hijos = new StringBuilder("--");
        sb.append(String.format("%-20s", hijos.toString()));

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
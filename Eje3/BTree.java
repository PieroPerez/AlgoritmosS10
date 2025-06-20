package Eje3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

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

        if (found) {
            System.out.println(cl + " se encuentra en el nodo " + current.idNode + " en la posición " + pos);
            return true;
        } else {
            // Buscar en el hijo correspondiente
            return searchNode(current.childs.get(pos), cl); // contiene referencias de los hijos al nodo actual
            // pos indica la posicion por donde se deciende si no esta la clave
        }
    }
    
    public void remove(E cl) {
        if (root == null) return;
        removeNode(root, cl);

        // Si la raíz queda vacía y tiene un solo hijo, el hijo se convierte en la nueva raíz
        if (root.count == 0 && !root.childs.isEmpty() && root.childs.get(0) != null) {
            root = root.childs.get(0);
        } else if (root.count == 0) {
            root = null; // Árbol vacío
        }
    }

    private void removeNode(BNode<E> current, E cl) {
        int idx = findKeyIndex(current, cl);

        // Caso 1: Clave encontrada en el nodo actual
        if (idx < current.count && current.keys.get(idx).compareTo(cl) == 0) {
            if (isLeaf(current)) {
                // Caso 1a: Es hoja, eliminar directamente
                current.keys.remove(idx);
                current.keys.add(null); // Mantener tamaño de array
                current.count--;
            } else {
                // Caso 1b: Nodo interno
                // Reemplaza por predecesor o sucesor
                BNode<E> predNode = current.childs.get(idx);
                if (predNode.count >= minKeys() + 1) {
                    E pred = getPredecessor(predNode);
                    current.keys.set(idx, pred);
                    removeNode(predNode, pred);
                } else {
                    BNode<E> succNode = current.childs.get(idx + 1);
                    if (succNode.count >= minKeys() + 1) {
                        E succ = getSuccessor(succNode);
                        current.keys.set(idx, succ);
                        removeNode(succNode, succ);
                    } else {
                        // Fusiona predNode, clave y succNode
                        merge(current, idx);
                        removeNode(predNode, cl);
                    }
                }
            }
        } else {
            // Caso 2: Clave no está en el nodo actual
            if (isLeaf(current)) {
                System.out.println("Clave no encontrada.");
                return;
            }
            boolean lastChild = (idx == current.count);
            BNode<E> child = current.childs.get(idx);
            if (child.count == minKeys()) {
                // Intentar redistribución o fusión
                if (idx > 0 && current.childs.get(idx - 1).count > minKeys()) {
                    borrowFromPrev(current, idx);
                } else if (idx < current.count && current.childs.get(idx + 1).count > minKeys()) {
                    borrowFromNext(current, idx);
                } else {
                    // Fusión
                    if (idx < current.count) {
                        merge(current, idx);
                    } else {
                        merge(current, idx - 1);
                        child = current.childs.get(idx - 1);
                    }
                }
            }
            removeNode(child, cl);
        }
    }

    // Métodos auxiliares que necesitas implementar:
    private int minKeys() {
        return (int) Math.ceil((double)orden / 2) - 1;
    }
    private boolean isLeaf(BNode<E> node) {
        for (int i = 0; i < node.childs.size(); i++) {
            if (node.childs.get(i) != null) return false;
        }
        return true;
    }
    private int findKeyIndex(BNode<E> node, E cl) {
        int idx = 0;
        while (idx < node.count && node.keys.get(idx).compareTo(cl) < 0) idx++;
        return idx;
    }
    private E getPredecessor(BNode<E> node) {
        // Baja siempre por el hijo más derecho
        while (!isLeaf(node)) node = node.childs.get(node.count);
        return node.keys.get(node.count - 1);
    }
    private E getSuccessor(BNode<E> node) {
        // Baja siempre por el hijo más izquierdo
        while (!isLeaf(node)) node = node.childs.get(0);
        return node.keys.get(0);
    }
    private void borrowFromPrev(BNode<E> parent, int idx) {
        // Implementa redistribución desde el hermano izquierdo
        // Mueve clave del hermano izquierdo al hijo actual y sube la clave del padre
    }
    private void borrowFromNext(BNode<E> parent, int idx) {
        // Implementa redistribución desde el hermano derecho
        // Mueve clave del hermano derecho al hijo actual y sube la clave del padre
    }
    private void merge(BNode<E> parent, int idx) {
        // Fusiona el hijo en idx con el hijo idx+1 y la clave del padre que los separa
    }
    
    public static BTree<Integer> building_Btree(String filename) throws ItemNoFound {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int orden = Integer.parseInt(br.readLine().trim());
            BTree<Integer> tree = new BTree<>(orden);

            // Mapas para nodos y sus relaciones
            Map<Integer, BNode<Integer>> nodos = new HashMap<>();
            Map<Integer, List<Integer>> hijosPorPadre = new LinkedHashMap<>();
            Map<Integer, Integer> nivelPorNodo = new HashMap<>();
            Map<Integer, List<Integer>> nodosPorNivel = new TreeMap<>();
            List<Integer> ordenLectura = new ArrayList<>();

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",", 3);
                int nivel = Integer.parseInt(partes[0].trim());
                int idNodo = Integer.parseInt(partes[1].trim());
                nivelPorNodo.put(idNodo, nivel);
                nodosPorNivel.computeIfAbsent(nivel, k -> new ArrayList<>()).add(idNodo);
                ordenLectura.add(idNodo);

                // Claves del nodo
                List<Integer> claves = new ArrayList<>();
                if (partes.length > 2) {
                    String[] clavesStr = partes[2].split(",");
                    for (String s : clavesStr) claves.add(Integer.parseInt(s.trim()));
                }

                // Crea el nodo con claves
                BNode<Integer> nodo = new BNode<>(orden);
                nodo.count = claves.size();
                for (int i = 0; i < claves.size(); i++) nodo.keys.set(i, claves.get(i));
                nodo.idNode = idNodo;
                nodos.put(idNodo, nodo);
            }

            // Enlazar hijos a sus padres según la estructura del archivo
            // (Por niveles, asociando cada padre con la cantidad adecuada de hijos)
            for (int nivel = 0; nodosPorNivel.containsKey(nivel) && nodosPorNivel.containsKey(nivel+1); nivel++) {
                List<Integer> padres = nodosPorNivel.get(nivel);
                List<Integer> hijos = new ArrayList<>(nodosPorNivel.get(nivel+1));
                int index = 0;
                for (int idPadre : padres) {
                    BNode<Integer> padre = nodos.get(idPadre);
                    int hijosEsperados = padre.count + 1;
                    for (int i = 0; i < hijosEsperados; i++) {
                        if (index < hijos.size()) {
                            int idHijo = hijos.get(index++);
                            padre.childs.set(i, nodos.get(idHijo));
                        }
                    }
                }
            }

            // Buscar la raíz (nivel 0, único nodo)
            Integer rootId = nodosPorNivel.get(0).get(0);
            tree.root = nodos.get(rootId);

            // Validación básica de árbol B
            if (!validateBTree(tree.root, orden)) {
                throw new ItemNoFound("El archivo no cumple las propiedades de un BTree válido.");
            }

            return tree;
        } catch (Exception e) {
            throw new ItemNoFound("Error construyendo el árbol: " + e.getMessage());
        }
    }

    // Valida que ningún nodo tenga más claves de las permitidas y recursivamente todos los hijos
    private static boolean validateBTree(BNode<Integer> node, int orden) {
        if (node == null) return true;
        if (node.count > orden - 1) return false;
        for (int i = 0; i <= node.count; i++) {
            BNode<Integer> hijo = node.childs.get(i);
            if (hijo != null && !validateBTree(hijo, orden)) return false;
        }
        return true;
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

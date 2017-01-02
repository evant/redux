import groovy.transform.CompileStatic

/**
 * Xml merging utility. For each node in custom xml searches existing path in original xml.
 * If full node path exists, but with different value then value simply overridden. If path is partially exists,
 * then required nodes will be added. Generally its a merge process in most common sense.
 * <p>
 * Current downside is semantic isn't tracked. For example, if pom contains developer definition and user closure
 * also contains developer then values of existing developer would be simply overridden and not updated tags will
 * remain (for example, id, name email were in pom and closure sets only name and email.. id will remain in merged
 * pom, which is ofc not correct. Assuming pretty similar merge situations to happen.
 * <p>
 * In order to prevent same path blocks override (e.g. multiple developers), each node in user closure has
 * assigned unique id. When existing node in original tree gets updated, then child node id assigned to original node.
 * Other nodes with the same path will not reuse the same tag because of assigned id.
 * <p>
 * In cases when tag name in pom closure clash with gradle project method name, '_' prefix could be used.
 * For example, 'relativePath' can't be used as method with the same name present in project, so use '_relativePath'
 * instead.
 *
 * @author Vyacheslav Rusakov
 * @since 28.07.2016
 */
@CompileStatic
final class XmlMerger {

    /**
     * Attribute name used to store node id (to reference nodes in old and original trees).
     */
    private static final String NID_ATTR = 'nid'

    /**
     * Complex merge logic is required to avoid tag duplicates.
     * For example, if scm.url tag specified manually  and in pom closure then
     * after using simply '+' to append closure scm section will be duplicated.
     * Correct behaviour is to override existing value and reuse section for other sub nodes.
     *
     * @param pomXml pom xml
     * @param userPom user pom closure
     */
    static void mergePom(Node pomXml, Closure userPom) {
        // required for proper properties tag rendering
        userPom.resolveStrategy = Closure.DELEGATE_FIRST
        Node newNode = (Node) new NodeBuilder().invokeMethod('dummyNode', userPom)
        prepareTree(newNode, '1')
        merge(pomXml, newNode.children())
        cleanIds(pomXml)
    }

    /**
     * Assign unique id for each provided child node to be able to track insertions in original tree.
     * Method called recursively.
     *
     * @param root current root node in original (modifying) xml
     * @param merge child nodes to merge into original xml
     */
    private static void merge(Node root, List<Node> merge) {
        merge.each { Node node ->
            mergeChild(root, node)
        }
    }

    /**
     * Searching for the longest existing path in original xml to correctly merge xmls without tags duplication.
     * Method called recursively (indirectly).
     *
     * @param root original xml node
     * @param child child to search path for
     */
    private static void mergeChild(Node root, Node child) {
        // check if parent xml contains child tag
        Node target = findPath(root.get(child.name() as String) as NodeList, child)
        if (target) {
            if (isLeaf(child)) {
                // replacing current node value
                target.replaceNode(child)
                associate(root, child)
            } else {
                // recursive merge
                merge(target, child.children())
            }
        } else {
            // no tag with child name exist appending entire subtree
            root.append(child)
            associate(root, child)
        }
    }

    /**
     * @param node node to check id
     * @return assigned node id or null
     */
    private static String nid(Node node) {
        node.attributes().get(NID_ATTR)
    }

    /**
     * Associates node in original tree with target tree node to avoid overriding already inserted content.
     *
     * @param root current source node
     * @param child current child (which parent's id will be associated with root node)
     */
    private static void associate(Node root, Node child) {
        root.attributes().put(NID_ATTR, nid(child.parent()))
    }

    /**
     * Selects correct path for multiple variants. If possible path node has assigned id, different with current
     * child id, then node can't be used. Only not used node selected (without id at all or with same id).
     *
     * @param list possible source paths
     * @param child child node to find path for
     * @return resolved path or null (in null case child should be simply appended to source xml)
     */
    private static Node findPath(NodeList list, Node child) {
        String childID = nid(child)
        // look for matching id or any node without id
        (list.find { childID == nid(it as Node) } ?: list.find { !nid(it as Node) }) as Node
    }

    /**
     * Apply unique ids for all nodes.
     * Remove '_' prefix from nodes (workaround for clashes with method names).
     *
     * @param root current node
     * @param topId id to apply
     */
    private static void prepareTree(Node root, String topId) {
        root.attributes().put(NID_ATTR, topId)
        String name = root.name()
        if (name.startsWith('_')) {
            Node replace = new Node(null, name[1..-1])
            replace.setValue(root.value())
            root.replaceNode(replace)
        }
        int pos = 0
        if (!isLeaf(root)) {
            root.children().each { prepareTree(it as Node, "${topId}_${++pos}") }
        }
    }

    /**
     * Removes all assigned ids from tree (to remove them from target pom).
     *
     * @param node root node to start cleanup from
     */
    private static void cleanIds(Node node) {
        node.attributes().remove(NID_ATTR)
        if (!isLeaf(node)) {
            node.children().each {
                cleanIds(it as Node)
            }
        }
    }

    /**
     * @param node node to check
     * @return true if node has no children (except text value), false otherwise
     */
    @SuppressWarnings('Instanceof')
    private static boolean isLeaf(Node node) {
        return node.children().empty || !(node.children()[0] instanceof Node)
    }
}

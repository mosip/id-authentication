package io.mosip.kernel.masterdata.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Unbalanced tree implementation to manage hierarchy data
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 * 
 */
public interface UBtree<T> {

	/**
	 * Method to create an unbalanced tree using the input list
	 * 
	 * @param list
	 *            input list
	 * @return list of {@link Node}
	 */
	public default List<Node<T>> createTree(List<T> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		Map<String, Node<T>> mapTmp = new HashMap<>();

		// convert into node
		List<Node<T>> nodes = list.stream().map(this::convertToNode).collect(Collectors.toList());
		// Save all nodes to a map
		for (Node<T> current : nodes) {
			mapTmp.put(current.getId(), current);
		}
		// loop and assign parent/child relationships
		for (Node<T> current : nodes) {
			String parentId = current.getParentId();
			if (parentId != null) {
				Node<T> parent = mapTmp.get(parentId);
				if (parent != null) {
					current.setParent(parent);
					parent.addChild(current);
					mapTmp.put(parentId, parent);
					mapTmp.put(current.getId(), current);
				}
			}
		}
		return nodes;
	}

	/**
	 * Method to find the leaf nodes using the passed node
	 * 
	 * @param node
	 *            input node
	 * @return {@link List} of leaf {@link Node}
	 */
	public default List<Node<T>> findLeafs(Node<T> node) {
		if (node == null) {
			return Collections.emptyList();
		}
		List<Node<T>> flatList = new ArrayList<>();
		Deque<Node<T>> q = new ArrayDeque<>();
		q.addLast(node);

		while (!q.isEmpty()) {
			Node<T> n = q.removeLast();
			List<Node<T>> children = n.getChilds();
			if (children != null) {
				for (Node<T> child : children) {
					q.addLast(child);
				}
			} else {
				flatList.add(n);
			}
		}

		return flatList;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public default List<T> findLeafsValue(Node<T> node) {
		if (node == null) {
			return Collections.emptyList();
		}
		List<Node<T>> nodes = findLeafs(node);
		return nodes.stream().map(Node::getValue).collect(Collectors.toList());
	}

	/**
	 * Method to find the root node for the input node
	 * 
	 * @param node
	 *            input node
	 * @return root {@link Node}
	 */
	public default Node<T> findRootNode(Node<T> node) {
		if (node == null) {
			return node;
		}
		// get the root
		Node<T> root = node;
		boolean flag = true;
		while (flag) {
			if (root.getParent() != null) {
				root = root.getParent();
			} else {
				flag = false;
			}
		}
		return root;
	}

	/**
	 * Method to find the root node and their value
	 * 
	 * @param node
	 *            input node
	 * @return node value
	 */
	public default T findRootNodeValue(Node<T> node) {
		if (node == null) {
			return null;
		}
		return findRootNode(node).getValue();
	}

	/**
	 * Method to fetch the hierarchy for the input node
	 * 
	 * @param node
	 *            input node
	 * @return {@link List} of node value
	 */
	public default List<T> getChildHierarchy(Node<T> node) {
		if (node == null) {
			return Collections.emptyList();
		}
		List<T> flatList = new ArrayList<>();
		Deque<Node<T>> q = new ArrayDeque<>();
		q.addLast(node);
		flatList.add(node.getValue());
		while (!q.isEmpty()) {
			Node<T> n = q.removeLast();
			List<Node<T>> children = n.getChilds();
			if (children != null) {
				for (Node<T> child : children) {
					q.addLast(child);
					flatList.add(child.getValue());
				}
			}
		}

		return flatList;

	}

	public default List<T> getParentHierarchy(Node<T> node) {
		Objects.requireNonNull(node);
		// get the root
		List<T> data = new ArrayList<>();
		Node<T> root = node;
		data.add(node.getValue());
		boolean flag = true;
		while (flag) {
			if (root.getParent() != null) {
				root = root.getParent();
				data.add(root.getValue());
			} else {
				flag = false;
			}
		}
		return data;
	}

	/**
	 * Method to Search Node the specified node
	 * 
	 * @param root
	 *            input root node
	 * @param id
	 *            id of the node
	 * @return {@link Node}
	 */
	public default Node<T> searchNode(Node<T> root, String id) {
		if (root == null) {
			return null;
		}
		Deque<Node<T>> q = new ArrayDeque<>();
		q.addLast(root);
		while (!q.isEmpty()) {
			Node<T> n = q.removeLast();
			if (n.getId().equals(id)) {
				return n;
			} else {
				List<Node<T>> children = n.getChilds();
				if (children != null) {
					for (Node<T> child : children) {
						q.addLast(child);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Method to find the {@link Node} from the list of {@link Node}
	 * 
	 * @param list
	 *            input list of {@link Node}
	 * @param id
	 *            input to search the Node
	 * @return {@link Node}
	 */
	public default Node<T> findNode(List<Node<T>> list, String id) {
		Optional<Node<T>> node = list.stream().filter(i -> i.getId().equals(id)).findAny();
		return node.isPresent() ? node.get() : null;
	}

	public Node<T> convertToNode(T node);
}

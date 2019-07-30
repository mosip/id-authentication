package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Unbalanced tree Node to hold hierarchy data
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 * 
 */
@Getter
public class Node<T> {
	private T value;
	private List<Node<T>> childs;
	private String parentId;
	private String id;
	private Node<T> parent;

	public Node(String id, T value, String parentId) {
		this.id = id;
		this.parentId = parentId;
		this.value = value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean addChild(Node<T> child) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		return childs.add(child);
	}

	public boolean addChilds(List<Node<T>> list) {
		if (childs == null) {
			childs = new ArrayList<>();
		}
		return childs.addAll(list);
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}
}

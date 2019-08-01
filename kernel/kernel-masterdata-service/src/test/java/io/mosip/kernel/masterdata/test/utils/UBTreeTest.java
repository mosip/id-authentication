package io.mosip.kernel.masterdata.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.utils.Node;
import io.mosip.kernel.masterdata.utils.UBtree;

@RunWith(JUnit4.class)
public class UBTreeTest {

	public UBtree<Zone> zoneTree;

	private List<Zone> zones;

	@Before
	public void setup() {
		zoneTree = (zone) -> new Node<>(zone.getCode(), zone, zone.getParentZoneCode());
		zones = new ArrayList<>();
		zones.add(new Zone("AAA", "ENG", "AAA", (short) 0, "AAA", null, "AAA"));
		zones.add(new Zone("BBB", "ENG", "AAA", (short) 1, "BBB", "AAA", "AAA/BBB"));
		zones.add(new Zone("CCC", "ENG", "AAA", (short) 1, "CCC", "AAA", "AAA/CCC"));
		zones.add(new Zone("DDD", "ENG", "AAA", (short) 1, "DDD", "AAA", "AAA/DDD"));
		zones.add(new Zone("AAA1", "ENG", "AAA", (short) 2, "AAA1", "BBB", "AAA/BBB/AAA1"));
		zones.add(new Zone("AAA2", "ENG", "AAA", (short) 2, "AAA2", "CCC", "AAA/CCC/AAA2"));
		zones.add(new Zone("AAA3", "ENG", "AAA", (short) 2, "AAA3", "DDD", "AAA/DDD/AAA3"));
		zones.add(new Zone("AAA4", "ENG", "AAA", (short) 4, "AAA4", "AAA3", "AAA/DDD/AAA3/AAA4"));
	}

	@Test
	public void testCreateTreeSuccess() {
		zoneTree.createTree(zones);
	}

	@Test
	public void testFindNodeSuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA");
		assertNotNull(node);
	}

	@Test
	public void testLeafsNodeSuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA");
		List<Node<Zone>> nodes = zoneTree.findLeafs(node);
		assertNotNull(node);
		assertNotNull(nodes);
	}

	@Test
	public void testLeafNodeValuesSuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA");
		List<Zone> nodes = zoneTree.findLeafsValue(node);
		assertNotNull(node);
		assertNotEquals(0, nodes.size());
	}

	@Test
	public void testRootNodeSuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA4");
		Node<Zone> root = zoneTree.findRootNode(node);
		assertNotNull(node);
		assertNotNull(root);
	}

	@Test
	public void testRootNodeValueSuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA4");
		Zone root = zoneTree.findRootNodeValue(node);
		assertNotNull(node);
		assertNotNull(root);
	}

	@Test
	public void testSearchNodeSuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA");
		Node<Zone> root = zoneTree.searchNode(node, "AAA4");
		assertNotNull(node);
		assertNotNull(root);
	}

	@Test
	public void testChildHierarchySuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA");
		List<Zone> childs = zoneTree.getChildHierarchy(node);
		assertNotNull(node);
		assertNotEquals(0, childs.size());
	}

	@Test
	public void testChildHierarchyNullValue() {
		List<Zone> childs = zoneTree.getChildHierarchy(null);
		assertEquals(0, childs.size());
	}

	@Test
	public void testRootNodeNullValue() {
		Node<Zone> root = zoneTree.findRootNode(null);
		assertNull(root);
	}

	@Test
	public void testLeafNodeNull() {
		List<Node<Zone>> leafs = zoneTree.findLeafs(null);
		assertEquals(0, leafs.size());
	}

	@Test
	public void testLeafNodeValueNull() {
		List<Zone> leafs = zoneTree.findLeafsValue(null);
		assertEquals(0, leafs.size());
	}

	@Test
	public void testParentHierarchySuccess() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA4");
		List<Zone> childs = zoneTree.getParentHierarchy(node);
		assertNotNull(node);
		assertNotEquals(0, childs.size());
	}

	@Test
	public void testFindNodeNull() {
		List<Node<Zone>> tree = zoneTree.createTree(zones);
		Node<Zone> node = zoneTree.findNode(tree, "AAA5");
		assertNull(node);
	}

	@Test
	public void testSearchNodeNull() {
		Node<Zone> node = zoneTree.searchNode(null, "AAA5");
		assertNull(node);
	}

	@Test
	public void testFindRootNodeValueNull() {
		Zone zone = zoneTree.findRootNodeValue(null);
		assertNull(zone);
	}

	@Test
	public void testCreateTreeNull() {
		List<Node<Zone>> tree = zoneTree.createTree(null);
		assertEquals(0, tree.size());
	}

	@Test
	public void testNode() {
		Zone zone = zones.get(0);
		Node<Zone> node = new Node<>(zone.getCode(), zone, zone.getParentZoneCode());
		List<Node<Zone>> nodes = zoneTree.createTree(zones);
		node.addChilds(nodes);
		node.setParent(node);
		node.setValue(zone);
		node.setParentId(zone.getParentZoneCode());
	}

}

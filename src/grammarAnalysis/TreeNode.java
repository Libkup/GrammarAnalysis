package grammarAnalysis;

import java.util.ArrayList;

public class TreeNode {

	private int MaxChildren;			//最大儿子节点数
	private ArrayList<TreeNode> child;	//所有儿子节点
	private TreeNode sibling;			//兄弟节点
	private int val;					//节点值
	private String nodeKind;			//节点类型
	
	
	public TreeNode(String nodeKind) {
		this.MaxChildren = 10;
		this.child = new ArrayList<TreeNode>();
		this.sibling = null;
		this.val = 0;
		this.nodeKind = nodeKind;
	}


	public int getMaxChildren() {
		return MaxChildren;
	}


	public void setMaxChildren(int maxChildren) {
		MaxChildren = maxChildren;
	}


	public ArrayList<TreeNode> getChild() {
		return child;
	}


	public void setChild(TreeNode child) {
		this.child.add(child);
	}


	public TreeNode getSibling() {
		return sibling;
	}


	public void setSibling(TreeNode sibling) {
		this.sibling = sibling;
	}


	public int getVal() {
		return val;
	}


	public void setVal(int val) {
		this.val = val;
	}


	public String getNodeKind() {
		return nodeKind;
	}


	public void setNodeKind(String nodeKind) {
		this.nodeKind = nodeKind;
	}
	
	
}

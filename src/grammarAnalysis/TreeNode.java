package grammarAnalysis;

import java.util.ArrayList;

public class TreeNode {

	private int MaxChildren;			//�����ӽڵ���
	private ArrayList<TreeNode> child;	//���ж��ӽڵ�
	private TreeNode sibling;			//�ֵܽڵ�
	private int val;					//�ڵ�ֵ
	private String nodeKind;			//�ڵ�����
	
	
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

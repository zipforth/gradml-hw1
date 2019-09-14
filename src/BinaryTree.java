
public class BinaryTree
{
	Node root;
	public BinaryTree(int n)
	{
		add(n);
	}
	public void add(int n)
	{
		root = addR(root, n);
	}
	public Node addR(Node node, int n)
	{
		if(node == null)
		{
			node = new Node(n);
		}
		
		if(n<node.n)
			node.left=addR(node.left,n);
		if(n>node.n)
			node.right=addR(node.right,n);
		else
			return node;
		return node;
		
	}
	
	public boolean find(int n)
	{
		return findR(root,n);
	}
	public boolean findR(Node node,int n)
	{
		if(node==null)
			return false;
		if(node.n==n)
			return true;
		if(n<node.n)
			return findR(node.left,n);
		if(n>node.n)
			return findR(node.right,n);
		return false;
		
		
	}
}

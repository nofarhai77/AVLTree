/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

/**
312495328, Nofar Haim, nofarhaim
206962912, Nizan Shami, nizans
*/

public class AVLTree {
	private IAVLNode root;
	private IAVLNode min;
	private IAVLNode max;

	//use in split
	private AVLTree(IAVLNode root , AVLTree t) {
		root.setParent(null);
		this.root = root;
		this.min = t.min;
		this.max = t.max;
	}


	public AVLTree() {
		this.root = null;
		this.min = null;
		this.max = null;
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {

		return (root == null) || (!root.isRealNode());
	}

	/**
	 * private IAVLNode searchRec(IAVLNode root, int k)
	 * receive root of AVLTree and key k,
	 * search recursively if key k exists in the tree,
	 * if so, returns the node of with key k, 
	 * otherwise, returns virtualNode
	 */
	private IAVLNode searchRec(IAVLNode root, int k) {
		if (root.getKey() == k || !root.isRealNode()) {
			return root;
		}
		if (root.getKey() > k) {
			return searchRec(root.getLeft(), k);
		}
		if (root.getKey() < k) {
			return searchRec(root.getRight(), k);
		}
		return null; // never gets here
	}	

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k)
	{
		if(this.getRoot() == null) {
			return null;
		}
		IAVLNode node = searchRec(this.getRoot(), k);
		if(node.isRealNode()) {
			return node.getValue();
		}
		return null;
	}
	/**
	 * private void rotateRight(IAVLNode root)
	 * receive current node
	 * rotate tree to right side, according to algorithm learned in class
	 * return nothing
	 */
	private void rotateRight(IAVLNode root) {
		IAVLNode tmpRoot = root;
		IAVLNode tmpLeft = root.getLeft();
		tmpLeft.setParent(tmpRoot.getParent());
		if(!this.getRoot().equals(tmpRoot)){ // if input node isn't root update his parent
			IAVLNode parent = tmpRoot.getParent();
			if(tmpRoot.equals(parent.getLeft())) {
				parent.setLeft(tmpLeft);
			}else {
				parent.setRight(tmpLeft);
			}
			tmpRoot.setParent(tmpLeft);
		}else {this.root = tmpLeft;} // if input node is root update the tree root
		tmpRoot.setLeft(tmpLeft.getRight());
		tmpLeft.getRight().setParent(root);
		tmpLeft.setRight(tmpRoot);
		tmpRoot.setParent(tmpLeft);
		demote(root);
		root.setSize(root.getLeft().getSize() + root.getRight().getSize() + 1);
	}

	/**
	 * private void rotateLeft(IAVLNode root)
	 * receive current node
	 * rotate tree to left side, according to algorithm learned in class
	 * return nothing
	 */
	private void rotateLeft(IAVLNode root) {
		IAVLNode right = root.getRight(); // input's right son
		IAVLNode tmpParent = root.getParent(); // input's parent
		root.setRight(right.getLeft());
		right.getLeft().setParent(root);
		right.setLeft(root);
		right.setParent(tmpParent);
		root.setParent(right);
		if (root.equals(this.getRoot())) { // if input is Tree's root change right son to Tree's root
			this.root = right;
		}
		else { // otherwise, change input's parent's sons accordingly
			if (tmpParent.getLeft().equals(root)) { // input is left son of his parent
				tmpParent.setLeft(right);
			}
			else { // input is right son of his parent
				tmpParent.setRight(right);
			}
		}
		demote(root);
		root.setSize(root.getLeft().getSize() + root.getRight().getSize() + 1);
	}

	private void virtualToReal(IAVLNode virtual, IAVLNode real) {
		IAVLNode parent = virtual.getParent();
		if (parent.getKey() < real.getKey()) {
			parent.setRight(real);
		}
		else {
			parent.setLeft(real);
		}
		real.setParent(parent);
	}

	private void promote(IAVLNode node) {
		node.setRank(node.getRank() + 1);
		node.setHeight(node.getHeight() + 1);
	}

	private void demote(IAVLNode node) {
		node.setRank(node.getRank() - 1);
		node.setHeight(node.getHeight() - 1);
	}

	private int rebalance(IAVLNode parent, int cnt) {
		if (parent == null) {
			return cnt;
		}
		int rightRankDiffer = parent.getRank() - parent.getRight().getRank();
		int leftRankDiffer = parent.getRank() - parent.getLeft().getRank();

		// insert case: 0-1 or 1-0
		if ((rightRankDiffer == 0 && leftRankDiffer == 1) || 
				((rightRankDiffer == 1 && leftRankDiffer == 0))) {
			promote(parent);
			cnt++;
			if (!parent.equals(this.getRoot())) {
				return rebalance(parent.getParent(), cnt);
			}
		}

		// insert case: 2-0 or 0-2
		if (rightRankDiffer == 2 && leftRankDiffer == 0) {
			int isDouble = parent.getLeft().getRank() - parent.getLeft().getLeft().getRank();
			if (isDouble == 2) {
				promote(parent.getLeft().getRight()); // promote b as signed in lecture
				this.rotateLeft(parent.getLeft());
				this.rotateRight(parent);
				cnt += 5;
			} else { 
				this.rotateRight(parent);
				cnt += 2;
			}
			return cnt;
		}
		if (rightRankDiffer == 0 && leftRankDiffer == 2) {
			int isDouble = parent.getRight().getRank() - parent.getRight().getRight().getRank();
			if (isDouble == 2) {
				promote(parent.getRight().getLeft()); // Symmetrically to b
				this.rotateRight(parent.getRight());
				this.rotateLeft(parent);
				cnt += 5;
			} else {
				this.rotateLeft(parent);
				cnt += 2;
			}
			return cnt;
		}

		// delete case: 2-2
		if (rightRankDiffer == 2 && leftRankDiffer == 2) {
			demote(parent);
			cnt++;
			if (!parent.equals(this.getRoot())) {
				return rebalance(parent.getParent(), cnt);
			}
		}

		// delete case: 1-3 or 3-1
		if (rightRankDiffer == 1 && leftRankDiffer == 3) {
			int isDouble = parent.getLeft().getRank() - parent.getLeft().getLeft().getRank();
			if (isDouble == 2) {
				demote(parent);
				promote(parent.getLeft().getRight());
				this.rotateLeft(parent.getLeft());
				this.rotateRight(parent);
				cnt += 6;
				if (!parent.getParent().equals(this.getRoot())) {
					return rebalance(parent.getParent().getParent(), cnt);
				}
			} else {
				int isCase3 = parent.getLeft().getRank() - parent.getLeft().getRight().getRank(); // case 3 according to lecture
				if (isCase3 == 2) {
					demote(parent);
					this.rotateRight(parent);
					cnt += 3;
					if (!parent.getParent().equals(this.getRoot())) {
						return rebalance(parent.getParent().getParent(), cnt);
					}
				}
				else { // case 2 according to lecture
					promote(parent.getLeft());
					this.rotateRight(parent);
					cnt += 3;
				}
			}
		}
		if (rightRankDiffer == 3 && leftRankDiffer == 1) {
			int isDouble = parent.getRight().getRank() - parent.getRight().getRight().getRank();
			if (isDouble == 2) {
				promote(parent.getRight().getLeft());
				demote(parent);
				this.rotateRight(parent.getRight());
				this.rotateLeft(parent);
				cnt += 6;
				if (!parent.getParent().equals(this.getRoot())) {
					return rebalance(parent.getParent().getParent(), cnt);
				}
			} else {
				int isCase3 = parent.getRight().getRank() - parent.getRight().getLeft().getRank(); // case 3 according to lecture
				if (isCase3 == 2) {
					demote(parent);
					this.rotateLeft(parent);
					cnt += 3;
					if (!parent.getParent().equals(this.getRoot())) {
						return rebalance(parent.getParent().getParent(), cnt);
					}
				}
				else {
					promote(parent.getRight());
					this.rotateLeft(parent);
					cnt += 3;
				}
			}
			return cnt;
		}
		return cnt;
	}

	/**
	 * private void updateSize(IAVLNode node)
	 *
	 * receive AVLTree
	 * update tree's size recursively and return nothing
	 */
	private void updateSize(IAVLNode node) {
		if (node == null) {
			return;
		}
		if (node.equals(this.getRoot())) {
			node.setSize(node.getLeft().getSize() + node.getRight().getSize() + 1);
			return;
		}
		IAVLNode parent = node.getParent();
		node.setSize(node.getLeft().getSize() + node.getRight().getSize() + 1);
		updateSize(parent);
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	 * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
	 * returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		int cnt = 0;
		IAVLNode newNode = new AVLNode(k, i);
		if (this.empty()) {
			this.root = newNode;
			// when the tree is empty node == min,max
			this.max = newNode;
			this.min = newNode;
			return cnt;
		}
		if(k < this.min.getKey()) {//update min
			this.min = newNode;
		}
		if(k > this.max.getKey()) {//update max
			this.max = newNode;
		}
		IAVLNode searchK = searchRec(this.getRoot(), k);
		if (searchK.isRealNode()) {
			return -1;
		}
		virtualToReal(searchK, newNode); // switch virtual node to new node
		cnt = rebalance(newNode.getParent(), cnt);
		this.updateSize(newNode);
		return cnt;
	}
	/**
	 * private IAVLNode predecessor(IAVLNode node)
	 *
	 * receive IAVLNode node and return node's predecessor
	 * in case node has no successor return null
	 */
	private IAVLNode predecessor(IAVLNode node) {
		IAVLNode leftSon = node.getLeft();
		if(leftSon.isRealNode()) {
			IAVLNode prev = leftSon;
			IAVLNode cur = leftSon.getRight();
			while(cur.isRealNode()) {
				prev = cur;
				cur = cur.getRight();
			}
			return prev;
		}
		else {
			IAVLNode parent = node.getParent();
			IAVLNode parentChild = node;
			while (parent != null  && parentChild.equals(parent.getRight())) {
				parentChild = parent;
				parent = parent.getParent();
			}
			return parent;
		}

	}
	/**
	 * private IAVLNode successor(IAVLNode node)
	 *
	 * receive IAVLNode node and return node's successor
	 * in case node has no successor return null
	 */
	private IAVLNode successor(IAVLNode node)
	{
		IAVLNode rightSon = node.getRight();
		if (rightSon.isRealNode()) {
			IAVLNode leftPrev = rightSon;
			IAVLNode leftCurr = rightSon.getLeft();
			while (leftCurr.isRealNode()) {
				leftPrev = leftCurr;
				leftCurr = leftCurr.getLeft();
			}
			return leftPrev;
		}
		else {
			IAVLNode parent = node.getParent();
			IAVLNode parentChild = node;
			while (parent != null  && parentChild.equals(parent.getRight())) {
				parentChild = parent;
				parent = parent.getParent();
			}
			return parent;
		}
	}

	/**
	 * private void switchNodes(IAVLNode x, IAVLNode y)
	 * 
	 * replace x with y
	 */
	private void switchNodes(IAVLNode x, IAVLNode y) {

		if (x.equals(this.getRoot())) {
			this.root = y;
		}

		// sets y's heights & y's rank
		y.setHeight(x.getHeight());
		y.setRank(x.getRank());

		// sets y's parent & y's right son & y's left son
		y.setParent(x.getParent());
		y.setRight(x.getRight());
		y.setLeft(x.getLeft());

		// sets x's parent to y
		if (x.getParent() != null) {
			if (x.getParent().getRight().equals(x)) {
				x.getParent().setRight(y);
			}
			else {
				x.getParent().setLeft(y);
			}
		}

		// sets x's sons parent to y
		x.getRight().setParent(y);
		x.getLeft().setParent(y);
		this.updateSize(y);

	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k)
	{
		if (this.empty()) {
			return -1;
		}
		int cnt = 0;
		IAVLNode node = searchRec(this.getRoot(), k);
		if (!node.isRealNode()) {
			return -1;
		}
		if (this.size() == 1) {
			this.root = null;
			this.min = null;
			this.max = null;
			return cnt;
		}
		else if(node == this.min) {//update min 
			this.min = this.successor(this.min);
		}
		else if(node == this.max) {//update max
			this.max = this.predecessor(max);
		}

		IAVLNode parent = node.getParent();
		boolean isRight = node.getRight().isRealNode();
		boolean isLeft = node.getLeft().isRealNode();

		// delete a leaf
		if (!isRight && !isLeft) {
			if (k < parent.getKey()) {
				parent.setLeft(node.getLeft());
				node.getLeft().setParent(parent);
			}
			else {
				parent.setRight(node.getRight());
				node.getRight().setParent(parent);
			}
			cnt = this.rebalance(parent, cnt);
			this.updateSize(node.getParent());

			return cnt;
		}

		// delete an unary node
		if (!isRight && isLeft) {
			if (this.getRoot().getKey() == k) {
				this.root = node.getLeft();
				node.getLeft().setParent(parent);
			} else {
				if (k < parent.getKey()) {
					parent.setLeft(node.getLeft());
					node.getLeft().setParent(parent);
					this.updateSize(node.getLeft());
				}
				else {
					parent.setRight(node.getLeft());
					node.getLeft().setParent(parent);
				}
			}
		} else if (isRight && !isLeft) {
			if (this.getRoot().getKey() == k) {
				this.root = node.getRight();
				node.getRight().setParent(parent);
			} else {
				if (k < parent.getKey()) {
					parent.setLeft(node.getRight());
					node.getRight().setParent(parent);
				}
				else {
					parent.setRight(node.getRight());
					node.getRight().setParent(parent);
				}
			}
			cnt = this.rebalance(parent, cnt);
			this.updateSize(parent);
			return cnt;
		}

		// delete node with 2 sons
		if (isRight && isLeft) {
			IAVLNode successor = this.successor(node);
			IAVLNode sucParent = successor.getParent();
			// remove successor from tree
			if (sucParent.getRight().equals(successor)) {
				sucParent.setRight(successor.getRight());
				successor.getRight().setParent(sucParent);
			} else {
				sucParent.setLeft(successor.getRight());
				successor.getRight().setParent(sucParent);
			}
			// replace node with successor
			switchNodes(node, successor);
			// checking whether deleted node is successor's parent
			if (sucParent.getKey() == k) {
				cnt = this.rebalance(successor, cnt);
			} else {
				cnt = this.rebalance(sucParent, cnt);
				this.updateSize(sucParent);	
			}
			return cnt;
		}
		return cnt; // never gets here
	}


	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 */
	public String min()
	{
		if (this.empty()) {
			return null;
		}
		return this.min.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */
	public String max()
	{
		if (this.empty()) {
			return null;
		}
		return this.max.getValue();
	}

	/**
	 * private void inOrder()
	 * Receive root of AVLTree and empty array in tree's size
	 * changed the array itself to tree's IAVLNode by in-order walking
	 */
	private void inOrder(IAVLNode root, IAVLNode[] nodes) {
		if (!root.isRealNode()) {
			return;
		}
		inOrder(root.getLeft(), nodes);
		add_last(nodes, root);
		inOrder(root.getRight(), nodes);

	}
	// add root after the last element != null in nodes
	private void add_last(IAVLNode[] nodes, IAVLNode root) {
		for(int i = 0;i < nodes.length;i++) {
			if(nodes[i] == null) {
				nodes[i] = root;
				return;
			}
		}

	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray()
	{
		int[] arr = new int[this.size()];
		if (this.empty()) {
			return arr;
		}
		IAVLNode[] nodes = new IAVLNode[this.size()];
		inOrder(this.getRoot(), nodes);
		for (int i = 0; i < arr.length; i++) {
			arr[i] = nodes[i].getKey();
		}

		return arr;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] infoToArray()
	{
		String[] arr = new String[this.size()];
		if (this.empty()) {
			return arr;
		}
		IAVLNode[] nodes = new IAVLNode[this.size()];
		inOrder(this.getRoot(), nodes);
		for (int i = 0; i < arr.length; i++) {
			arr[i] = nodes[i].getValue();
		}

		return arr;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public int size()
	{
		return this.getRoot().getSize();
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public IAVLNode getRoot()
	{
		return this.root;
	}
	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. 
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
	 * postcondition: none
	 */   
	public AVLTree[] split(int x)
	{
		IAVLNode nodex = this.searchRec(this.root, x);		
		AVLTree smaller = new AVLTree(nodex.getLeft(), this);
		AVLTree bigger = new AVLTree(nodex.getRight(), this);
		// finding min,max of the trees
		bigger.max = this.max;
		bigger.min = this.successor(nodex);
		smaller.max = this.predecessor(nodex);
		smaller.min = this.min;
		AVLTree[] trees = {smaller, bigger};
		split_rec(x,nodex, smaller, bigger);//building the trees
		return trees;
	}

	private void split_rec(int x, IAVLNode root , AVLTree t1, AVLTree t2) {
		if(root.getKey() == this.getRoot().getKey()) {
			if(x < root.getKey()) {
				t2.join(root, new AVLTree(root.getRight(), t2));
			}
			if(x > root.getKey()) {
				t1.join(root, new AVLTree(root.getLeft(), t1));
			}
			return;
		}
		else if(x < root.getKey()) {
			IAVLNode parent = root.getParent();
			t2.join(root, new AVLTree(root.getRight(), t2));
			split_rec(x, parent, t1, t2);
		}
		else if(x > root.getKey()){
			IAVLNode parent = root.getParent();
			t1.join(root, new AVLTree(root.getLeft(), t1));
			split_rec(x, parent, t1, t2);
		}else {
			split_rec(x, root.getParent(), t1, t2);
		}
		return;
	}

	/**
	 * private IAVLNode travelToRank(AVLTree tree, int rank)
	 *
	 * receive AVLTree and rank
	 * and return node in left spine of AVLTree,
	 * with rank that smaller / equal to given rank
	 */   
	private IAVLNode travelToRank(AVLTree tree, int rank, int right) {
		IAVLNode node = tree.getRoot();
		if(right == 0) {
			while (node.getRank() > rank) {
				node = node.getLeft();
			}
		}else {
			while (node.getRank() > rank) {
				node = node.getRight();
			}
		}
		return node;
	}

	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. 	
	 * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	 * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
	 * postcondition: none
	 */
	public int join(IAVLNode x, AVLTree t)
	{
		int result = -1;
		if (this.empty() || t.empty()) { // join when t/tree is empty
			if (this.empty()) {
				if(!t.empty()) {
					result = t.getRoot().getRank();
				}
				t.insert(x.getKey(), x.getValue());
				this.root = t.getRoot();
				this.min = t.min;
				this.max = t.max;
			}
			else {
				result = this.getRoot().getRank();
				this.insert(x.getKey(), x.getValue());
			}
			return Math.abs(result -(-1)) + 1;
		}

		int thisRank = this.getRoot().getRank();
		int tRank = t.getRoot().getRank();
		int complexity = thisRank - tRank;
		if(complexity < 0) { // rank of tree < rank of t
			if(this.getRoot().getKey() < x.getKey()) {
				IAVLNode node = this.travelToRank(t, thisRank, 0);
				x.setRank(thisRank + 1);
				x.setHeight(thisRank + 1);
				x.setParent(node.getParent());
				node.getParent().setLeft(x);
				node.setParent(x);
				x.setRight(node);
				this.getRoot().setParent(x);
				x.setLeft(this.getRoot());
				this.root = t.getRoot();
				this.rebalance(x.getParent(), 0);
				this.updateSize(x);
				this.max = t.max;//update max
			} else{
				IAVLNode node = this.travelToRank(t, thisRank, 1);
				x.setRank(thisRank + 1);
				x.setHeight(thisRank + 1);
				x.setParent(node.getParent());
				node.getParent().setRight(x);
				node.setParent(x);
				x.setRight(this.getRoot());
				this.getRoot().setParent(x);
				x.setLeft(node);
				this.root = t.getRoot();
				this.rebalance(x.getParent(), 0);
				this.updateSize(x);
				this.min = t.min; // update min
			}
		} else if(complexity > 0) {
			if(this.getRoot().getKey() < x.getKey()) {
				IAVLNode node = this.travelToRank(this, tRank, 1);
				x.setRank(tRank + 1);
				x.setHeight(tRank + 1);
				x.setParent(node.getParent());
				node.getParent().setRight(x);
				node.setParent(x);
				x.setRight(t.getRoot());
				t.getRoot().setParent(x);
				x.setLeft(node);
				this.rebalance(x.getParent(), 0);
				this.updateSize(x);
				this.max = t.max;
			} else {
				IAVLNode node = this.travelToRank(this, tRank, 0);
				x.setRank(tRank + 1);
				x.setHeight(tRank + 1);
				x.setParent(node.getParent());
				node.getParent().setLeft(x);
				node.setParent(x);
				x.setRight(node);
				t.getRoot().setParent(x);
				x.setLeft(t.getRoot());
				this.rebalance(x.getParent(), 0);
				this.updateSize(x);
				this.min = t.min;
			}	
		}else {
			if(x.getKey() > this.getRoot().getKey()) {
				x.setLeft(this.getRoot());
				this.getRoot().setParent(x);
				x.setRight(t.getRoot());
				t.getRoot().setParent(x);
			}else {
				x.setLeft(t.getRoot());
				t.getRoot().setParent(x);
				x.setRight(this.getRoot());
				this.getRoot().setParent(x);
			}
			this.root = x;
			x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
			x.setRank(x.getHeight());
			this.updateSize(x);
			x.setParent(null);
		}
		System.out.println(Math.abs(complexity) + 1);
		return Math.abs(complexity) + 1;
	}

	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public int getRank(); //returns node's rank
		public void setRank(int i);//sets node's rank
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
		public int getSize(); //returns the size of the tree that node is his root
		public void setSize(int size);// sets size
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in 
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode{
		private int rank = 0;
		private int key;
		private String value;
		private IAVLNode right;
		private IAVLNode left;
		private IAVLNode parent;
		private int height;
		private int size;//the size of the sub-tree that this == root

		public AVLNode(int key, String value) {
			this.key = key;
			this.value = value;
			this.height = 0;
			this.left = virtualNode(this);
			this.right = virtualNode(this);
			this.rank = 0;
			this.size = 1;
		}

		private AVLNode(IAVLNode parent) { //constructor for virtualNode
			this.key = -1;
			this.height = -1;
			this.parent = parent;
			this.rank = -1;
			this.size = 0;
		}

		private IAVLNode virtualNode(IAVLNode parent) { //create new virtualNode
			IAVLNode virtual = new AVLNode(parent);
			return virtual;
		}

		public int getKey()
		{
			return this.key;
		}
		public String getValue()
		{
			return this.value;
		}
		public void setLeft(IAVLNode node)
		{
			this.left = node;
		}
		public IAVLNode getLeft()
		{
			return this.left;
		}
		public void setRight(IAVLNode node)
		{
			this.right = node;
		}
		public IAVLNode getRight()
		{
			return this.right;
		}
		public void setParent(IAVLNode node)
		{
			this.parent = node;
		}
		public IAVLNode getParent()
		{
			return this.parent;
		}
		public boolean isRealNode() // returns True if this is a non-virtual AVL node
		{
			return this.getKey() != -1;
		}
		public void setHeight(int height)
		{
			this.height = height;
		}
		public int getHeight()
		{
			return this.height;
		}
		public void setRank(int rank)
		{
			this.rank = rank;
		}
		public int getRank()
		{
			return this.rank;
		}
		public int getSize() 
		{
			return this.size;
		}
		public void setSize(int size) 
		{
			this.size = size;
		}
	}

}



import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.Callable;

import static java.util.Collections.reverse;

public class LeetCode {
//    public static void main(String[] args) {
//        System.out.println("hello world");
//        System.out.println(getSomeThing());
//        System.out.println(getMultiName(3));
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println(timestamp);
//    }

    public static String getSomeThing() {
        return "someThing";
    }

    public static String getMultiName(int times) {
        StringBuilder sb = new StringBuilder();
        sb.append("🐔哥，".repeat(Math.max(0, times)));
        return sb.toString();
    }


    public int minIncrements(int n, int[] cost) {
        int ans = 0;
        for (int i = n - 2; i > 0; i -= 2) {
            ans += Math.abs(cost[i] - cost[i + 1]);
            cost[i / 2] += Math.max(cost[i], cost[i + 1]);
        }
        return ans;
    }

    int cnt = 0, res = 0;
    int k;
    List<Integer>[] g;
    Set<Long> set;

    public int rootCount(int[][] edges, int[][] guesses, int k) {
        // 初始化
        this.k = k;
        int n = edges.length + 1;
        // g是List<List<Integer>>, g[i]表示节点i的邻接节点
        g = new List[n];
        for (int i = 0; i < n; i++) {
            g[i] = new ArrayList<Integer>();
        }
        // set是HashSet<Long>, set存储所有的猜测，自己实现了一个hash函数，简化了猜测判断
        set = new HashSet<Long>();
        for (int[] v : edges) {
            g[v[0]].add(v[1]);
            g[v[1]].add(v[0]);
        }
        for (int[] v : guesses) {
            set.add(h(v[0], v[1]));
        }

        treeDfs(0, -1);
        reDfs(0, -1, cnt);
        return res;
    }

    public long h(int x, int y) {
        return (long) x << 20 | y;
    }

    public void treeDfs(int x, int father) {
        // x节点的邻接节点 作为子节点，看看统计猜测正确的次数
        for (int y : g[x]) {
            if (y == father) {
                // 父节点跳过
                continue;
            }
            // 猜测里存在(x,y)则猜测正确计数+1
            cnt += set.contains(h(x, y)) ? 1 : 0;
            // 以y为根继续深搜
            treeDfs(y, x);
        }
    }

    // 换根，如果猜测正确次数>k,则当前节点为可能的树根，res++
    // 这个换根以及换根的状态转换公式，使得不需要再对每个节点为根进行dfs统计猜测正确次数
    //（x,y）->(y,x) cnt = cnt - (set.contains(h(x, y)) ? 1 : 0) + (set.contains(h(y, x)) ? 1 : 0)
    public void reDfs(int x, int father, int cnt) {
        if (cnt >= k) {
            res++;
        }
        for (int y : g[x]) {
            if (y == father) {
                continue;
            }
            // 猜测存在(x,y)则 cnt - 1，存在(y,x)则 cnt + 1
            reDfs(y, x, cnt - (set.contains(h(x, y)) ? 1 : 0) + (set.contains(h(y, x)) ? 1 : 0));
        }
    }

    public boolean validPartition(int[] nums) {
        int n = nums.length + 1;
        boolean[] dp = new boolean[n];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            if (i >= 2) {
                dp[i] = dp[i - 2] && nums[i - 2] == nums[i - 1];
            }
            if (i >= 3) {
                dp[i] = dp[i] || dp[i - 3] && ((nums[i - 3] == nums[i - 1] && nums[i - 2] == nums[i - 1]) || (nums[i - 3] + 1 == nums[i - 2] && nums[i - 2] + 1 == nums[i - 1]));
            }
        }
        return dp[n];
    }


    int ans = 0;

    public int reachableNodes(int n, int[][] edges, int[] restricted) {
        boolean[] isRestrict = new boolean[n];
        for (int restrictedNode : restricted) {
            isRestrict[restrictedNode] = true;
        }
        // g是List<Integer>[], g[i]表示节点i的邻接节点
        List<Integer>[] g = new List[n];
        for (int i = 0; i < n; i++) {
            g[i] = new ArrayList<Integer>();
        }
        for (int[] v : edges) {
            g[v[0]].add(v[1]);
            g[v[1]].add(v[0]);
        }
        // 初始化0没有父亲节点，dfs(0,-1)表示从0节点开始深搜
        dfs(0, -1, isRestrict, g);
        return ans;
    }

    public void dfs(int currentNode, int father, boolean[] isRestrict, List<Integer>[] g) {
        ans++;
        for (int son : g[currentNode]) {
            if (son == father || isRestrict[son]) {
                continue;
            }
            dfs(son, currentNode, isRestrict, g);
        }
    }

    public int romanToInt(String s) {
        Map<Character, Integer> valMap = new HashMap<>();
        int ans = 0;
        int n = s.length();
        valMap.put('I', 1);
        valMap.put('V', 5);
        valMap.put('X', 10);
        valMap.put('L', 50);
        valMap.put('C', 100);
        valMap.put('D', 500);
        valMap.put('M', 1000);
        for (int i = 0; i < n; i++) {
            if (i < n - 1 && valMap.get(s.charAt(i)) < valMap.get(s.charAt(i + 1))) {
                ans -= valMap.get(s.charAt(i));
            } else {
                ans += valMap.get(s.charAt(i));
            }
        }
        return ans;
    }

    public int countPaths1(int n, int[][] roads) {
        int mod = 1000000007;
        // e是邻接表，e[i][0]表示节点i相邻的节点，e[i][1]表示节点i相邻的节点的距离
        List<int[]>[] e = new List[n];
        // 初始化
        for (int i = 0; i < n; i++) {
            e[i] = new ArrayList<int[]>();
        }
        for (int[] road : roads) {
            int x = road[0], y = road[1], t = road[2];
            e[x].add(new int[]{y, t});
            e[y].add(new int[]{x, t});
        }
        // dis[i] 表示节点i到0节点的最短距离
        long[] dis = new long[n];
        //  初始化，默认距离为无限大
        Arrays.fill(dis, Long.MAX_VALUE);
        // ways[i] 表示节点0到节点i的最短路径数
        int[] ways = new int[n];

        // 队列的元素表示到达节点a[1] 的最短路径为 a[0]，每次距离最短的元素出列
        PriorityQueue<long[]> pq = new PriorityQueue<long[]>((a, b) -> Long.compare(a[0], b[0]));
        pq.offer(new long[]{0, 0});

        dis[0] = 0;
        ways[0] = 1;

        while (!pq.isEmpty()) {
            long[] arr = pq.poll();
            long t = arr[0];
            int u = (int) arr[1];
            // 距离大于当前到u节点的最短距离，不更新，不处理
            if (t > dis[u]) {
                continue;
            }
            // 相等或者小于当前最短距离
            for (int[] next : e[u]) {
                int v = next[0], w = next[1];
                // 如果经过节点u到节点v的距离小于当前到到节点v的最短路径，则更新dis[v], ways[v] = ways[u]
                if (t + w < dis[v]) {
                    dis[v] = t + w;
                    ways[v] = ways[u];
                    pq.offer(new long[]{t + w, v});
                    // 如果经过节点u到节点v的距离等于当前到到节点v的最短路径，则不更新dis[v], ways[v] += ways[u]
                } else if (t + w == dis[v]) {
                    ways[v] = (ways[u] + ways[v]) % mod;
                }
            }
        }
        // 表示到达节点n的最短路径有多少种路径
        return ways[n - 1];
    }

    public int countPaths(int n, int[][] roads) {
        int mod = 1000000007;
        // 初始化邻接表
        List<int[]>[] e = new List[n];
        for (int i = 0; i < n; i++) {
            e[i] = new ArrayList<int[]>();
        }
        for (int[] road : roads) {
            int u = road[0];
            int v = road[1];
            int d = road[2];
            e[u].add(new int[]{v, d});
            e[v].add(new int[]{u, d});
        }
        // 初始化距离
        long[] dis = new long[n];
        Arrays.fill(dis, Long.MAX_VALUE);
        // 初始化路径数
        int[] ways = new int[n];
        //定义到达节点i的距离d 的优先级队列，每次出队列距离最短的节点
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        // 从节点0出发，到达节点0的距离为0，路径数为1，入队
        pq.offer(new long[]{0, 0});
        dis[0] = 0;
        ways[0] = 1;
        while (!pq.isEmpty()) {
            long[] node = pq.poll();
            long distance = node[0];
            int nodeu = (int) node[1];
            if (distance > dis[nodeu]) {
                continue;
            }
            for (int[] next : e[nodeu]) {
                int nodev = next[0];
                long distancetov = next[1];
                // 经过节点u到达节点v 的距离小于当前到达节点v的最短路径，则更新最短路径,ways[nodev] = ways[nodeu]
                if (distance + distancetov < dis[nodev]) {
                    dis[nodev] = distance + distancetov;
                    //当前节点入队列
                    ways[nodev] = ways[nodeu];
                    pq.offer(new long[]{dis[nodev], nodev});
                } else if (distance + distancetov == dis[nodev]) {
                    ways[nodev] = (ways[nodeu] + ways[nodev]) % mod;
                }
            }
        }

        return ways[n - 1];
    }

    public int findKOr(int[] nums, int k) {
        int ans = 0;
        for (int i = 0; i < 31; i++) {
            int cnt = 0;
            for (int num : nums) {
                // 统计有多少个数的第i位是1
                cnt += num >> i & 1;
            }
            if (cnt >= k) {
                ans += 1 << i;
            }
        }
        return ans;
    }

    public int[] divisibilityArray(String word, int m) {

        int[] res = new int[word.length()];
        long cur = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            cur = (cur % m * 10 + c - '0') % m;
            res[i] = cur == 0 ? 1 : 0;
        }
        return res;
    }

//    static class Treenode{
//        int val;
//        int dep;
//        Treenode left;
//        Treenode mid;
//        Treenode right;
//        Treenode(int val,int dep){
//            this.val = val;
//            this.dep = dep;
//        }
//    }
//
//
//    public static void main(String[] args) {
//
//        Scanner in = new Scanner(System.in);
//        // 注意 hasNext 和 hasNextLine 的区别
//        int n = 0;
//        int maxDep = 0;
//        n = in.nextInt();
//        int node [] = new int[n];
//        for(int i = 0; i < n; i++){
//            node[i] = in.nextInt();
//        }
//        Treenode root  = new Treenode(node[0],1);
//
//        for (int j = 1; j < n; j++) {
//            Treenode treenode = new Treenode(node[j],0);
//            int dep = dfs(node[j],root,treenode);
//            maxDep = maxDep > dep ? maxDep : dep;
//        }
//        System.out.println(maxDep);
//    }
//
//    public static int dfs(int val, Treenode root,Treenode father){
//        if (root == null){
//            int curDep = father.dep+1;
//            Treenode node = new Treenode(val,curDep);
//            if (val < father.val-500){
//                father.left = node;
//            }else if (val > father.val+500){
//                father.right = node;
//            }else {
//                father.mid = node;
//            }
//            return curDep;
//        }else {
//            if (val < root.val-500){
//                return dfs(val,root.left,root);
//            }else if (val > root.val+500){
//                return dfs(val,root.right,root);
//            }else {
//                return dfs(val,root.mid,root);
//            }
//        }
//    }

//    public static void main(String[] args) {
//        System.out.println(fastPow(2, 10));
//    }

    public static boolean ifOk(String str) {
        String[] split = str.split(",");
        int abCount = 0;
        boolean flag1 = true;
        boolean flag2 = true;
        for (int i = 0; i < split.length; i++) {

            if (split[i].equals("absent")) {
                abCount++;
            }

            if (split[i].equals("late") || split[i].equals("leaveearly")) {
                if (i < split.length - 1 && (split[i + 1].equals("late") || split[i + 1].equals("leaveearly"))) {
                    flag1 = false;
                }
            }
            if (i < split.length - 7) {
                int count = 0;
                for (int m = i; m < i + 7; m++) {
                    if (!split[m].equals("present")) {
                        count++;
                    }
                }
                if (count > 3) {
                    flag2 = false;
                }
            }
        }
        return abCount < 2 || flag1 || flag2;
    }

    public String capitalizeTitle(String title) {
        StringBuilder sb = new StringBuilder();
        String[] words = title.split(" ");

        for (String word : words) {
            if (word.length() <= 2) {
                sb.append(word.toLowerCase());

            } else {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1).toLowerCase());
            }
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String capitalizeTitle1(String title) {
        StringBuilder sb = new StringBuilder();
        String[] words = title.split(" ");

        for (int i = 0; i < words.length; i++) {
            if (words[i].length() <= 2) {
                sb.append(words[i].toLowerCase());
            } else {
                sb.append(Character.toUpperCase(words[i].charAt(0)));
                sb.append(words[i].substring(1).toLowerCase());
            }
            if (i < words.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }


    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    class FindElements {

        Set<Integer> set = new HashSet<>();

        public FindElements(TreeNode root) {
            buildTree(root, 0);
        }

        public boolean find(int target) {
            return set.contains(target);
        }

        public void buildTree(TreeNode root, int value) {
            if (root == null) {
                return;
            }
            root.val = value;
            set.add(root.val);
            buildTree(root.left, value * 2 + 1);
            buildTree(root.right, value * 2 + 2);
        }
    }

    public String maximumOddBinaryNumber(String s) {
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        int count1 = 0;
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == '1') {
                count1++;
            }
        }
        for (int i = 0; i < n - 1; i++) {
            if (i < count1 - 1) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        sb.append('1');
        return sb.toString();
    }

    public long maxArrayValue(int[] nums) {
        long sum = nums[nums.length - 1];
        for (int i = nums.length - 2; i >= 0; i--) {
            sum = sum > nums[i] ? sum + nums[i] : nums[i];
        }
        return sum;
    }


    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        List<Integer> ans = new ArrayList<>();
        if (n == 1) {
            ans.add(0);
            return ans;
        }
        int[] degree = new int[n];
        List<Integer>[] adj = new List[n];
        // 初始化邻接矩阵
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        // 根据边初始化
        for (int[] edge : edges) {
            adj[edge[0]].add(edge[1]);
            adj[edge[1]].add(edge[0]);
            degree[edge[0]]++;
            degree[edge[1]]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (degree[i] == 1) {
                queue.offer(i);
            }
        }

        int remainSize = n; // 剩余节点数ize
        while (remainSize > 2) {
            int qSize = queue.size();
            remainSize -= qSize;
            for (int i = 0; i < qSize; i++) {
                Integer curr = queue.poll();
                // 将相邻节点的入度减1
                for (int next : adj[curr]) {
                    degree[next]--;
                    if (degree[next] == 1) {
                        queue.offer(next);
                    }
                }

            }
        }
        while (queue.size() > 0) {
            ans.add(queue.poll());
        }
        return ans;
    }


    public int numIslands(char[][] grid) {
        int ans = 0;
        int n = grid.length;
        int m = grid[0].length;

        int[][] status = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (grid[i][j] == '1' && status[i][j] == 0) {
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});
                    while (!queue.isEmpty()) {
                        int[] poll = queue.poll();
                        int x = poll[0];
                        int y = poll[1];
                        if (x - 1 >= 0) {
                            if (grid[x - 1][y] == '1' && status[x - 1][y] == 0) {
                                queue.offer(new int[]{x - 1, y});
                                status[x - 1][y] = 1;
                            }
                        }
                        if (x + 1 < n) {
                            if (grid[x + 1][y] == '1' && status[x + 1][y] == 0) {
                                queue.offer(new int[]{x + 1, y});
                                status[x + 1][y] = 1;
                            }
                        }
                        if (y - 1 >= 0) {
                            if (grid[x][y - 1] == '1' && status[x][y - 1] == 0) {
                                queue.offer(new int[]{x, y - 1});
                                status[x][y - 1] = 1;
                            }
                        }
                        if (y + 1 < m) {
                            if (grid[x][y + 1] == '1' && status[x][y + 1] == 0) {
                                queue.offer(new int[]{x, y + 1});
                                status[x][y + 1] = 1;
                            }
                        }

                    }
                    ans++;
                }
            }
        }
        return ans;
    }


    public int maximumScore(int[] nums, int k) {
        int ans = 0;
        int left = k - 1;
        int right = k + 1;
        for (int i = nums[k]; ; i--) {
            while (left >= 0 && nums[left] >= i) {
                left--;
            }
            while (right < nums.length && nums[right] >= i) {
                right++;
            }
            ans = Math.max(ans, i * (right - left - 1));
            if (left < 0 && right == nums.length) {
                break;
            }
        }
        return ans;
    }


    public int minNonZeroProduct(int p) {
        long mod = 1000000007;
        long x = fastPow(2, p, mod) - 1;
        long y = fastPow(2, p - 1, mod);
        return (int) ((x * fastPow(x - 1, y - 1, mod)) % mod);
    }

    // 快速幂,计算a的b次方，结果对mod取余
    public long fastPow(long a, long b, long mod) {
        long ans = 1;
        while (b > 0) {
            if ((b & 1) == 1) {
                ans = ans * a % mod;
            }
            a = a * a % mod;
            b >>= 1;
        }
        return ans;
    }

    // 快速幂
    public static long fastPow(long a, long b) {
        long ans = 1;
        while (b > 0) {
            if ((b & 1) == 1) {
                ans *= a;
            }
            a *= a;
            b >>= 1;
        }
        return ans;
    }

    HashMap<Integer, Integer> kTimes;
    HashMap<Integer, Set<Integer>> times;
//    public FrequencyTracker() {
//        kTimes = new HashMap<>();
//        times = new HashMap<>();
//
//    }

    public void add(int number) {
        int time = kTimes.getOrDefault(number, 0) + 1;
        kTimes.put(number, time);
        if (times.get(time) == null) {
            times.put(time, new HashSet<>());
        }
        times.get(time).add(number);
        if (times.get(time - 1) != null) {
            times.get(time - 1).remove(number);
        }
    }

    public void deleteOne(int number) {
        int time = kTimes.getOrDefault(number, 0) - 1;
        if (time < 0) {
            return;
        }
        if (time == 0) {
            kTimes.remove(number);
            times.get(1).remove(number);
            return;
        }
        kTimes.put(number, time);
        if (times.get(time) == null) {
            times.put(time, new HashSet<>());
        }
        times.get(time).add(number);
        if (times.get(time + 1) != null) {
            times.get(time + 1).remove(number);
        }
    }

    public boolean hasFrequency(int frequency) {
        return times.get(frequency) != null && !times.get(frequency).isEmpty();
    }

    public int coinChange(int[] coins, int amount) {
        // 最多需要acount个数值为1的硬币
        int maxCount = amount + 1;
        //初始化
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, maxCount);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                // 数值大于当前所求总数的跳过
                if (coins[j] <= i) {
                    // 状态转移方程
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    public int change(int amount, int[] coins) {
        //  初始化
        int[] dp = new int[amount + 1];
        // 0元只有一种组合
        dp[0] = 1;
        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                // 状态转移方程
                dp[i] += dp[i - coin];
            }
        }
        return dp[amount];
    }

    public boolean isValidSerialization1(String preorder) {
        int n = preorder.length();
        int i = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        // 初始化上只能入栈根节点
        stack.push(1);
        while (i < n) {
            if (stack.isEmpty()) {
                return false;
            }
            if (preorder.charAt(i) == '#') {
                Integer top = stack.pop() - 1;
                if (top > 0) {
                    stack.push(top);
                }
                i++;
            } else if (preorder.charAt(i) == ',') {
                i++;
            } else {
                // 读下一个数字
                while (i < n && preorder.charAt(i) != ',') {
                    i++;
                }
                Integer top = stack.pop() - 1;
                if (top > 0) {
                    stack.push(top);
                }
                stack.push(2);
            }

        }
        return stack.isEmpty();
    }

    public boolean isValidSerialization(String preorder) {
        int n = preorder.length();
        int i = 0;
        int count = 1;
        while (i < n) {
            if (count == 0) {
                return false;
            }
            if (preorder.charAt(i) == '#') {
                count--;
                i++;
            } else if (preorder.charAt(i) == ',') {
                i++;
            } else {
                // 读下一个数字, 数字可能是多位数，比如11，100
                while (i < n && preorder.charAt(i) != ',') {
                    i++;
                }
                count++;
            }

        }
        return count == 0;
    }

    public String finalString(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        boolean head = true;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != 'i') {
                if (head) {
                    stack.offerLast(s.charAt(i));
                } else {
                    stack.offerFirst(s.charAt(i));
                }
            } else {
                head = !head;
            }
        }

        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            if (head) {
                sb.append(stack.pollFirst());
            } else {
                sb.append(stack.pollLast());
            }
        }
        return sb.toString();
    }


    class ThroneInheritance {

        Map<String, List<String>> edges; // <父亲名字,孩子们>
        Set<String> dead;

        String king;

        public ThroneInheritance(String kingName) {
            edges = new HashMap<>();
            dead = new HashSet<>();
            king = kingName;

        }

        public void birth(String parentName, String childName) {
            List<String> sons = edges.getOrDefault(parentName, new ArrayList<>());
            sons.add(childName);
            edges.put(parentName, sons);
        }

        public void death(String name) {
            dead.add(name);
        }

        public List<String> getInheritanceOrder() {
            List<String> ans = new ArrayList<>();
            preOrder(ans, king);
            return ans;
        }

        public void preOrder(List<String> ans, String name) {
            // 前序遍历
            if (!dead.contains(name)) {
                ans.add(name);
            }
            List<String> children = edges.getOrDefault(name, new ArrayList<>());
            for (String child : children) {
                preOrder(ans, child);
            }
        }

        public int minOperations(int[] nums) {
            int n = nums.length;
            Set<Integer> set = new HashSet<>();
            for (int num : nums) {
                set.add(num);
            }
            List<Integer> sortedUniqueNums = new ArrayList<>(set);
            Collections.sort(sortedUniqueNums);
            int ans = n;
            int j = 0;
            // 对每个left遍历，此时k =
            for (int i = 0; i < sortedUniqueNums.size(); i++) {
                int left = sortedUniqueNums.get(i);
                int right = left + n - 1;
                // 统计当前left 有多少num不用改变
                while (j < sortedUniqueNums.size() && sortedUniqueNums.get(j) <= right) {
                    ans = Math.min(ans, n - (j - i + 1));
                    j++;
                }
            }
            return ans;
        }
    }

    public String maximumBinaryString(String binary) {
        int len = binary.length();
        int count_0 = 0;
        int firstIndex = len;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = binary.charAt(i);
            if (c == '0') {
                count_0++;
                firstIndex = Math.min(firstIndex, i);
            }
            sb.append('1');
        }
        if (count_0 <= 1) {
            return binary;
        }
        sb.setCharAt(firstIndex + count_0 - 1, '0');
        return sb.toString();
    }


    class MyHashSet {

        private static final int base = 769;
        private LinkedList[] data;

        public MyHashSet() {
            data = new LinkedList[base];
            for (int i = 0; i < base; i++) {
                data[i] = new LinkedList<Integer>();
            }
        }

        public void add(int key) {
            int h = hash(key);
            Iterator<Integer> iterator = data[h].iterator();
            while (iterator.hasNext()) {
                Integer element = iterator.next();
                if (element == key) {
                    return;
                }
            }
            data[h].offerLast(key);
        }

        public void remove(int key) {
            int h = hash(key);
            Iterator<Integer> iterator = data[h].iterator();
            while (iterator.hasNext()) {
                Integer element = iterator.next();
                if (element == key) {
                    data[h].remove(element);
                    return;
                }
            }
        }

        public boolean contains(int key) {
            int h = hash(key);
            Iterator<Integer> iterator = data[h].iterator();
            while (iterator.hasNext()) {
                Integer element = iterator.next();
                if (element == key) {
                    return true;
                }
            }
            return false;
        }

        public int hash(int key) {
            return key % base;
        }
    }

    public int findChampion(int n, int[][] edges) {
        // 入度统计
        int[] inCount = new int[n];
        for (int[] edge : edges) {
            inCount[edge[1]]++;
        }
        int ans = -1;
        // 统计入度为0
        int inCount_0 = 0;
        for (int i = 0; i < n; i++) {
            if (inCount[i] == 0) {
                inCount_0++;
                // 有多个入度为0
                if (inCount_0 > 1) {
                    return -1;
                }
                ans = i;
            }
        }
        return ans;
    }

    public int lengthOfLongestSubstring(String s) {
        int left = 0;
        Map<Character, Integer> map = new HashMap<>();
        int ans = 0;
        for (int i = 0; i < s.length(); i++) {
            if (map.containsKey(s.charAt(i))) {
                left = Math.max(left, map.get(s.charAt(i)) + 1);
            }
            map.put(s.charAt(i), i);
            ans = Math.max(ans, i - left + 1);
        }
        return ans;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode prev = null;
        ListNode cur = head;
        while (cur.next != null) {
            // 记住下一个，使链表不至于断
            ListNode next = cur.next;
            // 反转
            cur.next = prev;
            // 指针向后移动
            prev = cur;
            cur = next;
        }
        cur.next = prev;
        return cur;
    }


    class DLinkedNode {
        int key;
        int value;
        DLinkedNode prev;
        DLinkedNode next;

        public DLinkedNode() {
        }

        public DLinkedNode(int _key, int _value) {
            key = _key;
            value = _value;
        }
    }

    private Map<Integer, DLinkedNode> cache = new HashMap<Integer, DLinkedNode>();
    private int size;
    private int capacity;
    private DLinkedNode head, tail;

//    public LRUCache(int capacity) {
//        this.capacity = capacity;
//        this.size = 0;
//        // 使用伪头部和伪尾部节点
//        this.head = new DLinkedNode();
//        this.tail = new DLinkedNode();
//        this.head.next = this.tail;
//        this.tail.prev = this.head;
//
//    }

    public int get(int key) {
        DLinkedNode dLinkedNode = cache.get(key);
        if (dLinkedNode == null) {
            return -1;
        }
        moveToHead(dLinkedNode);
        return dLinkedNode.value;

    }

    private void moveToHead(DLinkedNode dLinkedNode) {
        addToHead(dLinkedNode);
        removeNode(dLinkedNode);
    }

    private void removeNode(DLinkedNode dLinkedNode) {
        dLinkedNode.next.prev = dLinkedNode.prev;
        dLinkedNode.prev.next = dLinkedNode.next;
    }

    private void addToHead(DLinkedNode dLinkedNode) {
        dLinkedNode.prev = head;
        dLinkedNode.next = head.next;
        head.next.prev = dLinkedNode;
        head.next = dLinkedNode;
    }

    public void put(int key, int value) {
        DLinkedNode dLinkedNode = cache.get(key);
        // 不存在，放入，放在链表最前面
        if (dLinkedNode == null) {
            DLinkedNode node = new DLinkedNode(key, value);
            cache.put(key, node);
            addToHead(node);
            size++;
            // 容量溢出则删掉尾巴
            if (size > capacity) {
                DLinkedNode tail = removeTail();
                cache.remove(tail.key);
                --size;
            }
            //存在，覆盖，提到最前
        } else {
            dLinkedNode.value = value;
            moveToHead(dLinkedNode);
        }

    }

    private DLinkedNode removeTail() {
        DLinkedNode res = tail.prev;
        removeNode(res);
        return res;
    }

    public ListNode[] myReverse(ListNode head, ListNode tail) {
        ListNode prev = tail.next;
        ListNode p = head;
        while (p != tail) {
            // p在原链表上向后移动，先记录p下一个节点
            ListNode next = p.next;
            // 将p指向prev，将原来的下一个节点接在最前面
            p.next = prev;
            prev = p;
            p = next;
        }
        return new ListNode[]{tail, head};
    }

    public ListNode reverseKGroup(ListNode head, int k) {

        ListNode hair = new ListNode(0);
        hair.next = head;
        ListNode pre = hair;

        while (head != null) {
            ListNode tail = head;
            // 查看剩余部分长度是否大于等于k，不足k则证明以及反转完了，退出
            for (int i = 0; i < k; ++i) {
                tail = tail.next;
                if (tail == null) {
                    return hair.next;
                }
            }
            // 先记录tail的next，以免操作后取不到后续节点，链表断了
            ListNode nex = tail.next;
            // 反转操作
            ListNode[] ans = myReverse(head, tail);
            head = ans[0];
            tail = ans[1];
            // 重新接上
            pre.next = head;
            tail.next = nex;
            // 下一次pre 是上一次的tail
            pre = tail;
            // 下一次的head是上次的tail的next
            head = tail.next;
        }
        return hair.next;
    }


    public List<List<Integer>> threeSum(int[] nums) {

        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int r = nums.length - 1;
            for (int l = i + 1; l < nums.length - 1; l++) {
                if (l > i + 1 && nums[l] == nums[l - 1]) {
                    continue;
                }
                while (l < r) {
                    int sum = nums[i] + nums[l] + nums[r];
                    if (sum == 0) {
                        List<Integer> list = new ArrayList<>();
                        list.add(nums[i]);
                        list.add(nums[l]);
                        list.add(nums[r]);
                        ans.add(list);
                        break;
                    } else if (sum > 0) {
                        r--;
                    } else {
                        break;
                    }
                }
            }
        }
        return ans;
    }

    public int maxSubArray(int[] nums) {
        int ans = nums[0];
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1] + nums[i], nums[i]);
            ans = Math.max(ans, dp[i]);
        }
        return ans;
    }

    public int[] sortArray(int[] nums) {
        randomQuickSort(nums, 0, nums.length - 1);
        return nums;
    }

    private void randomQuickSort(int[] nums, int l, int r) {
        if (l < r) {
            int pos = partitioment(nums, l, r);
            randomQuickSort(nums, l, pos - 1);
            randomQuickSort(nums, pos + 1, r);
        }
    }

    private int partitioment(int[] nums, int l, int r) {
        // 随机生成一个在l和r之间的pos
        int i = new Random().nextInt(r - l + 1) + l;
        swap(nums, i, r);
        int index = l - 1;
        for (int j = l; j <= r - 1; j++) {
            if (nums[j] <= nums[r]) {
                index++;
                swap(nums, j, index);
            }
        }
        swap(nums, index + 1, r);
        return index + 1;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }


    public String longestPalindrome(String s) {
        int length = s.length();
        if (length < 2) {
            return s;
        }
        boolean[][] dp = new boolean[length][length];
        // 初始化，长度为1的都是回文串
        for (int i = 0; i < length; i++) {
            dp[i][i] = true;
        }
        int begin = 0;
        int maxLen = 1;
        char[] chars = s.toCharArray();

        for (int len = 2; len <= length; len++) {
            for (int i = 0; i < length; i++) {
                int j = len + i - 1;
                if (j >= len) {
                    break;
                }
                if (chars[i] != chars[j]) {
                    dp[i][j] = false;
                } else {
                    if (j - i < 3) {
                        dp[i][j] = true;
                    } else {
                        dp[i][j] = dp[i + 1][j - 1];
                    }
                }
                if (dp[i][j] && j - i - 1 > maxLen) {
                    begin = i;
                    maxLen = j - i - 1;
                }
            }
        }
        return s.substring(begin, begin + maxLen);
    }

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        if (root == null) {
            return ans;
        }
        queue.offer(root);
        int size = 1;
        int count = 0;
        while (!queue.isEmpty()) {
            List<Integer> nodes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode poll = queue.poll();
                nodes.add(poll.val);
                if (poll.left != null) {
                    queue.offer(poll.left);
                    count++;
                }
                if (poll.right != null) {
                    queue.offer(poll.right);
                    count++;
                }
            }
            ans.add(nodes);
            size = count;
            count = 0;
        }
        return ans;
    }

    public int search1(int[] nums, int target) {
        if (nums.length == 0) {
            return -1;
        }
        if (nums.length == 1) {
            return nums[0] == target ? 0 : -1;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            // 左边有序
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // 右边有序
                if (target <= nums[right] && target > nums[mid]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }

    /**
     * A description of the entire Java function.
     *
     * @param grid 2D array representing the grid
     * @return description of return value
     */
    public int numIslands1(char[][] grid) {
        int n = grid.length;
        int m = grid[0].length;
        int ans = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Queue<int[]> queue = new LinkedList<>();
                if (grid[i][j] == '1') {
                    ans++;
                    queue.offer(new int[]{i, j});
                    while (!queue.isEmpty()) {
                        int[] poll = queue.poll();
                        int x = poll[0];
                        int y = poll[1];
                        if (x - 1 >= 0) {
                            if (grid[x - 1][y] == '1') {
                                queue.offer(new int[]{x - 1, y});
                                grid[x - 1][y] = '0';
                            }
                        }
                        if (x + 1 < n) {
                            if (grid[x + 1][y] == '1') {
                                queue.offer(new int[]{x + 1, y});
                                grid[x + 1][y] = '0';
                            }
                        }
                        if (y - 1 >= 0) {
                            if (grid[x][y - 1] == '1') {
                                queue.offer(new int[]{x, y - 1});
                                grid[x][y - 1] = '0';
                            }
                        }
                        if (y + 1 < m) {
                            if (grid[x][y + 1] == '1') {
                                queue.offer(new int[]{x, y + 1});
                                grid[x][y + 1] = '0';
                            }
                        }
                    }
                }
            }
        }
        return ans;
    }

    public boolean isValid(String s) {
        char[] chars = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        for (Character c : chars) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }
                if ((c == ')' && stack.peek() == '(') || (c == ']' && stack.peek() == '[') || (c == '}' && stack.peek() == '{')) {
                    stack.pop();
                } else {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    public int maxProfit(int[] prices) {
        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] < min) {
                min = prices[i];
            } else if (prices[i] - min > max) {
                max = prices[i] - min;
            }
        }
        return max;
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int sum = m + n - 1;
        int index1 = m - 1;
        int index2 = n - 1;
        while (index1 >= 0 && index2 >= 0) {
            nums1[sum--] = nums1[index1] > nums2[index2] ? nums1[index1--] : nums2[index2--];
        }
        while (index2 > 0) {
            nums1[sum--] = nums2[index2--];
        }
    }

    public boolean hasCycle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                // 快慢相遇则是有环
                return true;
            }
        }
        return false;
    }

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();

        List<Integer> output = new ArrayList<>();
        for (int num : nums) {
            output.add(num);
        }
        int n = nums.length;
        backtrack(n, res, output, 0);
        return res;
    }

    private void backtrack(int n, List<List<Integer>> res, List<Integer> output, int first) {
        if (n == first) {
            res.add(new ArrayList<>(output));
        }
        for (int i = first; i < n; i++) {
            // 交换位置
            Collections.swap(output, first, i);
            // 递归找确定找下一个数
            backtrack(n, res, output, first + 1);
            // 恢复
            Collections.swap(output, first, i);
        }
    }


//    public TreeNode ans;
//    public Solution() {
//        this.ans = null;
//    }
//    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
//        dfs(root, p, q);
//        return ans;
//    }
//
//    private boolean dfs(TreeNode root, TreeNode p, TreeNode q) {
//        if (root == null) {
//            return false;
//        }
//        boolean left = dfs(root.left, p, q);
//        boolean right = dfs(root.right, p, q);
//        if ((left && right) || ((root == p || root == q) && (left || right))) {
//            ans = root;
//        }
//        return left || right || root == p || root == q;
//    }

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean isOrderLeft = true;
        while (!queue.isEmpty()) {
            int size = queue.size();
            Deque<Integer> level = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (isOrderLeft) {
                    level.offerLast(node.val);
                } else {
                    level.offerFirst(node.val);
                }
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            ans.add(new ArrayList<>(level));
            isOrderLeft = !isOrderLeft;
        }
        return ans;
    }


    public static String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) {
            return "";
        }
        // 初始化
        String prefix = strs[0];

        for (int i = 1; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }
        return prefix;
    }

//    public String longestCommonPrefix(String[] strs) {
//        if (strs.length == 0) {
//            return "";
//        }
//        String prefix = strs[0].substring(0,0);
//        int index = 0;
//        while (true){
//            String pre = strs[0].substring(0,index+1);
//            for(String str : strs){
//                if (index > str.length()){
//                    return prefix;
//                }
//
//                if (pre.equals(str.substring(0,index+1))){
//                    continue;
//                }else {
//                    return prefix;
//                }
//            }
//            prefix = pre;
//            index++;
//        }
//    }

    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> ans = new ArrayList<>();
        int l = 0, r = matrix[0].length - 1, u = 0, d = matrix.length - 1;
        while (true) {
            // l to r
            for (int i = l; i <= r; i++) {
                ans.add(matrix[u][i]);
            }
            // u to d
            if (u++ < d) break;
            for (int i = u; i < d; i++) {
                ans.add(matrix[i][r]);
            }
            // r to l
            if (r-- < l) break;
            for (int i = r; i >= l; i--) {
                ans.add(matrix[d][i]);
            }
            // d to u
            if (d-- < u) break;
            for (int i = d; i >= u; i--) {
                ans.add(matrix[i][l]);
            }
            if (l++ > r) break;
        }
        return ans;
    }


    public static void main(String[] args) {
        String[] list = {"flower", "flow", "flight"};
        System.out.println(longestCommonPrefix(list));

        System.out.println("abcd".indexOf("abcd"));
        System.out.println(Integer.parseInt("00001"));
        System.out.println("1.20".split("\\.")[0]);
    }


    public ListNode mergeKLists(ListNode[] lists) {
        ListNode ans = null;
        for (int i = 0; i < lists.length; i++) {
            ans = mergeTwoLists(ans, lists[i]);
        }
        return ans;
    }

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        if (list1 == null || list2 == null) {
            return list1 == null ? list2 : list1;
        }
        ListNode cur = new ListNode();
        ListNode root = cur;
        ListNode cur1 = list1;
        ListNode cur2 = list2;
        while (cur1 != null || cur2 != null) {
            if (cur1.val <= cur2.val) {
                cur.next = cur1;
                cur1 = cur1.next;
            } else {
                cur.next = cur2;
                cur2 = cur2.next;
            }
        }
        if (cur1 != null) {
            cur.next = cur1;
        }
        if (cur2 != null) {
            cur.next = cur2;
        }
        return root.next;
    }


    public int lengthOfLIS(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int n = nums.length;
        // dp[i] 是 包含nums[i]的最长上升子序列
        int[] dp = new int[n];
        dp[0] = 1;
        int ans = 1;
        for (int i = 1; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            ans = Math.max(ans, dp[i]);
        }
        return ans;
    }


    //
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }
        ListNode pa = headA, pb = headB;
        while (pa != pb) {
            pa = pa == null ? headB : pa.next;
            pb = pb == null ? headA : pb.next;
        }
        return pa;
    }

    public String addStrings(String num1, String num2) {
        int i = num1.length() - 1, j = num2.length() - 1;
        int add = 0;
        StringBuilder sb = new StringBuilder();
        while (i >= 0 || j >= 0 || add != 0) {
            int x = i >= 0 ? num1.charAt(i) - '0' : 0;
            int y = j >= 0 ? num2.charAt(j) - '0' : 0;
            int result = x + y + add;
            sb.append(result % 10);
            // 处理进位
            add = result / 10;
            i--;
            j--;
        }
        return sb.reverse().toString();
    }


    public void reorderList(ListNode head) {
        if (head == null) {
            return;
        }
        List<ListNode> list = new ArrayList<>();
        ListNode cur = head;
        while (cur.next != null) {
            list.add(cur);
            cur = cur.next;
        }
        int l = 0, r = list.size() - 1;
        while (l < r) {
            list.get(l).next = list.get(r);
            l++;
            if (l == r) {
                break;
            }
            list.get(r).next = list.get(l);
            r--;
        }
        list.get(l).next = null;

    }


    public ListNode detectCycle(ListNode head) {
        Set<ListNode> visited = new HashSet<>();
        while (head != null) {
            if (visited.contains(visited)) {
                return head;
            } else visited.add(head);
            head = head.next;
        }
        return null;
    }


    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> list = new ArrayList<>();
        for (int[] interval : intervals) {
            if (list.isEmpty() || list.get(list.size() - 1)[1] < interval[0]) {
                list.add(interval);
            } else {
                list.get(list.size() - 1)[1] = Math.max(list.get(list.size() - 1)[1], interval[1]);
            }
        }
        return list.toArray(new int[list.size()][]);
    }

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int temp = target - nums[i];
            if (map.containsKey(temp)) {
                return new int[]{map.get(temp), i};
            }
            map.put(nums[i], i);
        }
        return new int[0];
    }

    // 二分查找
    // 注释
    public int search2(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        // 二分查找
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            // 左边有序
            if (nums[mid] >= nums[l]) {
                if (target >= nums[l] && target < nums[mid]) {
                    r = mid - 1;
                } else {
                    l = mid + 1;
                }
            } else {
                // 右边有序
                if (target <= nums[r] && target > nums[mid]) {
                    l = mid + 1;
                } else {
                    r = mid - 1;
                }
            }
        }
        return -1;
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode first = head;
        ListNode second = dummy;
        for (int i = 0; i < n; i++) {
            first = first.next;
        }
        while (first != null) {
            first = first.next;
            second = second.next;
        }
        second.next = second.next.next;
        return dummy.next;
    }

    static final int SEG_COUNT = 4;
    List<String> ans1 = new ArrayList<String>();
    int[] segments = new int[SEG_COUNT];

    public List<String> restoreIpAddresses(String s) {
        dfs(s, 0, 0);
        return ans1;
    }

    public void dfs(String s, int segId, int segStart) {
        if (segId == SEG_COUNT) {
            if (segStart == s.length()) {
                StringBuilder ipAddr = new StringBuilder();
                for (int i = 0; i < SEG_COUNT; i++) {
                    ipAddr.append(segments[i]);
                    if (i != SEG_COUNT - 1) {
                        ipAddr.append('.');
                    }
                }
                ans1.add(ipAddr.toString());
            }
            return;
        }

        // 还没找到的四段ip 地址已经便利完了则退出
        if (segStart == s.length()) {
            return;
        }

        // 对于0必须单独处理
        if (s.charAt(segStart) == '0') {
            segments[segId] = 0;
            dfs(s, segId + 1, segStart + 1);
        }

        // 对于一般情况，枚举递归
        int addr = 0;
        for (int segEnd = segStart; segEnd < s.length(); ++segEnd) {
            addr = addr * 10 + (s.charAt(segEnd) - '0');
            if (addr > 0 && addr <= 255) {
                segments[segId] = addr;
                dfs(s, segId + 1, segEnd + 1);
            } else {
                break;
            }
        }
    }

    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        // dp[i][j] 表示以test1[i] 和 text2[j] 结尾 的最长公共子序列
        int ans = 0;
        int[][] dp = new int[m + 1][n + 1];
        // 初始化
        dp[0][0] = text1.charAt(0) == text2.charAt(0) ? 1 : 0;
        dp[0][1] = text1.charAt(0) == text2.charAt(1) ? 1 : 0;
        dp[1][0] = text1.charAt(1) == text2.charAt(0) ? 1 : 0;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i) == text2.charAt(j)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
                ans = Math.max(ans, dp[i][j]);
            }
        }
        return ans;
    }

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        inorder(root, ans);
        return ans;
    }

    private void inorder(TreeNode root, List<Integer> ans) {
        if (root == null) {
            return;
        }
        inorder(root.left, ans);
        ans.add(root.val);
        inorder(root.right, ans);
    }

    public ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        ListNode cur = dummy;
        while (cur.next != null && cur.next.next != null) {
            if (cur.next.val == cur.next.next.val) {
                int x = cur.next.val;
                // 重复元素不断向后移，直到不同，此时cur.next 指向新的数值不同的元素
                while (cur.next != null && cur.next.val == x) {
                    cur.next = cur.next.next;
                }
            } else {
                cur = cur.next;
            }
        }
        return dummy.next;
    }

    public int search(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int mid = (l + r) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                r = mid - 1;
            } else if (nums[mid] < target) {
                l = mid + 1;
            }
        }
        return -1;
    }


    public List<Integer> rightSideView(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        List<Integer> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode poll = queue.poll();
                if (i == size - 1) {
                    ans.add(poll.val);
                }
                if (poll.left != null) {
                    queue.offer(poll.left);
                }
                if (poll.right != null) {
                    queue.offer(poll.right);
                }
            }
        }
        return ans;
    }

    //
    public ListNode sortList(ListNode head) {
        return sortList(head, null);
    }

    public ListNode sortList(ListNode head, ListNode tail) {
        if (head == null) {
            return head;
        }
        if (head.next == tail) {
            head.next = null;
            return head;
        }
        ListNode slow = head, fast = head;
        // fast 每次走两步 ，slow走一步，fast走完，slow走到中间
        while (fast != tail) {
            slow = slow.next;
            fast = fast.next;
            if (fast != tail) {
                fast = fast.next;
            }
        }
        ListNode mid = slow;
        ListNode listNode1 = sortList(head, mid);
        ListNode listNode2 = sortList(mid, tail);
        ListNode merge = merge(listNode1, listNode2);
        return merge;
    }

    public ListNode merge(ListNode head1, ListNode head2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy, cur1 = head1, cur2 = head2;
        while (cur1 != null && cur2 != null) {
            if (cur1.val <= cur2.val) {
                cur.next = cur1;
                cur1 = cur1.next;
            } else {
                cur.next = cur2;
                cur2 = cur2.next;
            }
            cur = cur.next;
        }
        if (cur1 != null) {
            cur.next = cur1;
        }
        if (cur2 != null) {
            cur.next = cur2;
        }
        return dummy.next;
    }


    public void nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }
        if (i >= 0) {
            int j = nums.length - 1;
            while (j >= 0 && nums[i] >= nums[j]) {
                j--;
            }
            if(j >= 0){
                swap1(nums, i, j);
            }
        }
        reverse(nums, i + 1);
    }

    private void reverse(int[] nums, int i) {
        int l = i, r = nums.length - 1;
        while (l < r) {
            swap1(nums, l, r);
            l++;
            r--;
        }
    }

    private void swap1(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }


    Deque<Integer> inStack;
    Deque<Integer> outStack;

//    public MyQueue() {
//        inStack = new ArrayDeque<Integer>();
//        outStack = new ArrayDeque<Integer>();
//    }

    public void push(int x) {
        inStack.push(x);
    }

    public int pop() {
        if(outStack.isEmpty()){
            in2out();
        }
        return outStack.pop();
    }

    public int peek() {
        if(outStack.isEmpty()){
            in2out();
        }
        return outStack.peek();
    }

    public boolean empty() {
        return inStack.isEmpty() && outStack.isEmpty();
    }

    public void in2out(){
        while(!inStack.isEmpty()){
            outStack.push(inStack.pop());
        }
    }


    public int myAtoi(String s) {
        String trim = s.trim();
        char flag = trim.charAt(0);
        int l = 0;
        while (trim.charAt(l) == '0'){
            l++;
        }
        int index = l;
        while (index < trim.length() && (trim.charAt(index) > '0' || trim.charAt(index) <= '9')){
            index++;
        }
        String num = trim.substring(l, index);
        if (num.length() == 0){
            return 0;
        }



        return 0;
    }


    public int mySqrt(int x) {
        int l = 0,r = x;
        int ans = 0;
        while(l<=r){
            int m = l + (r-l)/2;
            if((long)m*m == x) {
                return m;
            }
            if ((long)m*m < x) {
                ans = m;
                l = m + 1;
            }
            if ((long)m*m > x) {
                r = m - 1;
            }
        }
        return ans;
    }


    public int climbStairs(int n) {
        int [] dp = new int[n+1];
        dp[1] = 1;
        if(n < 2){
            return dp[n];
        }
        dp[2] = 2;
        for (int i = 3; i <= n ;i++ ){
            dp[i]= dp[i-1] + dp[i-2];
        }
        return dp[n];
    }


    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        int carry = 0;
        while(l1 != null || l2 != null){
            int x  = l1 == null ? 0 : l1.val;
            int y  = l2 == null ? 0 : l2.val;
            int sum = x + y + carry;
            carry = sum / 10;
            cur.next = new ListNode(sum % 10);
            cur = cur.next;
            if (l1 != null){
                l1 = l1.next;
            }
            if (l2 != null){
                l2 = l2.next;
            }
        }
        if(carry > 0){
            cur.next = new ListNode(carry);
        }
        return dummy.next;
    }

    public int compareVersion(String version1, String version2) {
        String[] v1 = version1.split(".");
        String[] v2 = version2.split(".");
        int n = Math.max(v1.length, v2.length);
        for (int i = 0; i < n; i++) {
            int x =  i < v1.length ? Integer.parseInt(v1[i]) : 0;
            int y =  i < v2.length ? Integer.parseInt(v2[i]) : 0;
            if (x > y){
                return 1;
            }
            if (x < y){
                return -1;
            }
        }
        return 0;

    }





}

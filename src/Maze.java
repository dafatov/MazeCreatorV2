import java.util.*;

class Maze {
    private byte[] maze;
    private boolean[] way;
    private int rows, cols;

    Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        maze = new byte[rows * cols];
        way = new boolean[rows * cols];
        if (Main.LOAD) {
            loadMaze();
            if (Main.WITH_WAY) loadWay();
        } else {
            genMaze();
            genStartFinish();
        }
        //printMazeArray();
        new GUI(this);
    }

    int getRows() {
        return rows;
    }

    int getCols() {
        return cols;
    }

    byte[] getMaze() {
        return maze;
    }

    boolean[] getWay() {
        return way;
    }

    //

    private void loadMaze() {
        ArrayList<String> maze = FileManager.read(Main.FILE);

        for (int i = 0; i < this.maze.length; i++) {
            this.maze[i] = Byte.parseByte(Objects.requireNonNull(maze).get(i) ,2);
        }
    }

    private void loadWay() {
        String way = Objects.requireNonNull(FileManager.read(Main.WAY_FILE)).get(0);

        String[] cells = way.split(",");

        for (String cell : cells) {
            int index = Integer.parseInt(cell.substring(4)) - 1;
            this.way[index] = true;
        }
    }

    private void genStartFinish() {
        ArrayList<Integer> candidates = new ArrayList<>();

        for (int i = 0; i < maze.length; i++) {
            char[] binary = Integer.toBinaryString(maze[i]).toCharArray();
            int count1 = 0;

            for (int j : binary) {
                if (j == '1') {
                    count1++;
                }
            }
            if (count1 == 1) {
                candidates.add(i);
            }
        }


        Integer[] startFinish = checkDistance(candidates);

        System.err.println(startFinish[0]);
        System.err.println(startFinish[1]);

        if (new Random().nextBoolean()) {
            maze[startFinish[0]] += degree((byte) 5);
            maze[startFinish[1]] += degree((byte) 4);
        } else {
            maze[startFinish[0]] += degree((byte) 4);
            maze[startFinish[1]] += degree((byte) 5);
        }
    }

    private Integer[] checkDistance(ArrayList<Integer> candidates) {
        int maxDistance = 0;
        //int startFinish[] = new int[2];
        HashSet<Integer[]> candi = new HashSet<>();
        Random random = new Random();

        for (int i : candidates) {
            for (int j : candidates) {
                if (i < j) {
                    int tmp = getDistance(indexToCoord(i)[0], indexToCoord(i)[1], indexToCoord(j)[0], indexToCoord(j)[1]);
                    boolean first = random.nextBoolean();

                    if (tmp > maxDistance) {
//                        int index = random.nextInt(2);
//
//                        maxDistance = tmp;
//                        startFinish[index] = i;
//                        startFinish[1 - index] = j;
                        maxDistance = tmp;
                        candi.clear();
                        if (first) {
                            candi.add(new Integer[]{i, j});
                        } else {
                            candi.add(new Integer[]{j, i});
                        }
                    } else if (tmp == maxDistance) {
                        if (first) {
                            candi.add(new Integer[]{i, j});
                        } else {
                            candi.add(new Integer[]{j, i});
                        }
                    }
                }
            }
        }
        return randomIntArr(candi);
    }

    /*private int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }*/

    private int getDistance(int x1, int y1, int x2, int y2) {
        int[] currentCoordinates = {x1, y1};
        int counter = 0;

        while (currentCoordinates[0] != x2 | currentCoordinates[1] != y2) {
            double min = rows * cols;
//            Direction dir = null;
            HashSet<Direction> dir = new HashSet<>();
            HashSet<Direction> directions = validateDirection(coordToIndex(currentCoordinates));

            for (Direction d : directions) {
                int[] checkCoordinate = useDirection(indexToCoord(coordToIndex(currentCoordinates)), d);
                double distance = getDistanceDouble(checkCoordinate[0], checkCoordinate[1], x2, y2);

                if (distance < min) {
                    min = distance;
//                    dir = d;
                    dir.clear();
                    dir.add(d);
                } else if (distance == min) {
                    dir.add(d);
                }
            }
            if (dir.isEmpty()) {
                System.err.println("Error: getDistance (code:10");
                System.exit(10);
            }
            //System.out.println(dir.size());
            useDirection(currentCoordinates, randomDirection(dir));
            counter++;
        }
        return counter;
    }

    private double getDistanceDouble(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /*private void printMazeArray() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < Integer.toString(maze.length - 1).length() - Integer.toString(i).length(); j++) {
                //stringBuilder.append("0");
            }
            //stringBuilder.append(i).append(":");
            for (int j = 0; j < 2 - Byte.toString(maze[i]).length(); j++) {
                //stringBuilder.append("0");
            }
            //stringBuilder.append(maze[i]).append(":");
            for (int j = 0; j < 8 - Integer.toBinaryString(maze[i]).length(); j++) {
                stringBuilder.append("0");
            }
            stringBuilder.append(Integer.toBinaryString(maze[i]));
            if (i != maze.length - 1) stringBuilder.append('\n');
        }
        if (!false) System.out.println(stringBuilder.toString());
        FileManager.write(stringBuilder.toString());
    }*/

    private void genMaze() {
        int start = getRandomNullCell();
        int finish = getRandomNullCell();

        while (start == finish) {
            finish = getRandomNullCell();
        }
        maze = genMazeArray(findWay(start, finish));
        while (hasNull()) {
            maze = genMazeArray(findWay(getRandomNullCell(), getNotNullCells()));
        }
    }

    private int getRandomNullCell() {
        HashSet<Integer> randList = new HashSet<>();

        for (int i = 0; i < maze.length; i++) {
            if (maze[i] == 0) {
                randList.add(i);
            }
        }
        return randomInt(randList);
    }

    private HashSet<Integer> getNotNullCells() {
        HashSet<Integer> list = new HashSet<>();

        for (int i = 0; i < maze.length; i++) {
            if (maze[i] != 0) {
                list.add(i);
            }
        }
        return list;
    }

    private boolean hasNull() {
        boolean hasValue = false;

        for (byte tmp : maze) {
            if (tmp == 0) {
                hasValue = true;
                break;
            }
        }
        return hasValue;
    }

    private byte[] genMazeArray(Stack<Integer> way) {
        while (way.size() > 1) {
            int currentIndex = way.pop();
            int nextIndex = way.peek();
            byte[] tmp = valueMazeCell(indexToCoord(currentIndex), indexToCoord(nextIndex));

            maze[currentIndex] += degree(tmp[0]);
            maze[nextIndex] += degree(tmp[1]);
        }
        return maze;
    }

    private byte degree(byte value) {
        return (byte) Math.pow(2, value);
    }

    private byte[] valueMazeCell(int[] currentCoord, int[] nextCoord) {
        int x = currentCoord[0] - nextCoord[0];
        int y = currentCoord[1] - nextCoord[1];
        byte[] mode = {0, 0};

        if (x == 1 & y == 0) {
            //D->U
            mode[1] = 2;
        } else if (x == -1 & y == 0) {
            //U->D
            mode[0] = 2;
        } /*else if (x == 1 & y == -1) {
            //RU->LD
            mode[0] = 1;
            mode[1] = 4;
        } else if (x == -1 & y == 1) {
            //LD->RU
            mode[0] = 4;
            mode[1] = 1;
        } */ else if (x == 0 & y == -1) {
            //R->L
            mode[0] = 1;
            mode[1] = 3;
        } else if (x == 0 & y == 1) {
            //L->R
            mode[0] = 3;
            mode[1] = 1;
        } else {
            System.err.println("Error in valueMazeCell (code:2)");
            System.exit(2);
        }
        return mode;
    }

    private Stack<Integer> findWay(int startPosition, int finishPosition) {
        HashSet<Integer> finishPositions = new HashSet<>();

        finishPositions.add(finishPosition);
        return findWay(startPosition, finishPositions);
    }

    private Stack<Integer> findWay(int startPosition, HashSet<Integer> finishPosition) {
        int debug = 0;
        Stack<Integer> way = new Stack<>();
        int position = startPosition;
        way.push(position);

        while (!finishPosition.contains(position)) {
            position = coordToIndex(useDirection(indexToCoord(position), randomDirection(validateDirection(position))));
            while (way.contains(position)) {
                way.pop();
            }
            way.push(position);
            debug++;
            if (debug == rows * cols * 10000) {
                System.err.println("Error: findWay (code:4)");
                System.exit(4);
            }
        }
        //System.out.println(way);
        return way;
    }

    private static Direction randomDirection(HashSet<Direction> directions) {
        ArrayList<Direction> mas = new ArrayList<>(directions);
        Random random = new Random();
        int rand = random.nextInt(mas.size());

        return mas.get(rand);
    }

    private int randomInt(HashSet<Integer> integers) {
        ArrayList<Integer> mas = new ArrayList<>(integers);
        Random random = new Random();
        int rand = random.nextInt(mas.size());

        return mas.get(rand);
    }

    private Integer[] randomIntArr(HashSet<Integer[]> integers) {
        ArrayList<Integer[]> mas = new ArrayList<>(integers);
        Random random = new Random();
        int rand = random.nextInt(mas.size());

        return mas.get(rand);
    }

    private int[] useDirection(int[] coord, Direction direction) {
        switch (direction) {
            case U:
                coord[0]--;
                break;
            case D:
                coord[0]++;
                break;
            case R:
                coord[1]++;
                break;
            case L:
                coord[1]--;
                break;
            default:
                System.err.println("Error in direction (code: 1)");
                System.exit(1);
        }
        return coord;
    }

    private HashSet<Direction> validateDirection(int startPosition) {
        int[] coordinate = indexToCoord(startPosition);
        HashSet<Direction> directions = new HashSet<>();

        if (coordinate[0] > 0) {
            directions.add(Direction.U);
        }
        if (coordinate[0] < rows - 1) {
            directions.add(Direction.D);
        }
        if (coordinate[1] > 0) {
            directions.add(Direction.L);
        }
        if (coordinate[1] < cols - 1) {
            directions.add(Direction.R);
        }
        if (directions.isEmpty()) {
            System.err.println("Error: validateDirection (code:11)");
            System.exit(11);
        }
        return directions;
    }

    private int[] indexToCoord(int index) {
        int[] coord = new int[2];

        coord[0] = index / cols;
        coord[1] = index % cols;
        return coord;
    }

    private int coordToIndex(int[] coord) {
        return coord[0] * cols + coord[1];
    }

}


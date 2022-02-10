package com.vanzay.aom.game;

import static com.vanzay.aom.game.GameField.SIZE_X;
import static com.vanzay.aom.game.GameField.SIZE_Y;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Dijkstra {

    private final Point[][] parents;
    private final int[][] costs;
    private final SimplePriorityQueue<Point> queue = new SimplePriorityQueue<>();

    public Dijkstra(FieldCell[][] field) {
        parents = new Point[SIZE_Y][SIZE_X];
        costs = new int[SIZE_Y][SIZE_X];

        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                costs[i][j] = field[i][j].isFree() ? Integer.MAX_VALUE : -1;
            }
        }
    }

    public List<Point> findPath(Point from, Point to) {
        fillCosts(from, to);
        return buildPath(to);
    }

    private void fillCosts(Point from, Point to) {
        costs[from.y][from.x] = 0;
        queue.add(from, 0);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(to)) {
                break;
            }

            if (current.y > 0) {
                handleNeighbour(current, new Point(current.x, current.y - 1));
            }
            if (current.y < (SIZE_Y - 1)) {
                handleNeighbour(current, new Point(current.x, current.y + 1));
            }
            if (current.x > 0) {
                handleNeighbour(current, new Point(current.x - 1, current.y));
            }
            if (current.x < (SIZE_X - 1)) {
                handleNeighbour(current, new Point(current.x + 1, current.y));
            }
        }
    }

    private void handleNeighbour(Point current, Point next) {
        if (costs[next.y][next.x] == -1) {
            return;
        }

        int newCost = costs[current.y][current.x] + 1;
        if (newCost < costs[next.y][next.x]) {
            costs[next.y][next.x] = newCost;
            parents[next.y][next.x] = current;
            queue.add(next, newCost);
        }
    }

    private List<Point> buildPath(Point to) {
        Point parent = parents[to.y][to.x];
        if (parent == null) {
            return null;
        }

        int cost = costs[to.y][to.x];
        if ((cost == Integer.MAX_VALUE) || (cost < 0)) {
            return null;
        }

        List<Point> path = new ArrayList<>();
        path.add(to);
        while (parent != null) {
            path.add(0, parent);
            parent = parents[parent.y][parent.x];
        }
        return path;
    }
}

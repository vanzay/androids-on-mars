package com.vanzay.aom.game;

import android.graphics.Point;

import com.vanzay.aom.utils.RandomUtils;

import java.util.List;

public class GameField {

    public static final int SIZE_X = 9;
    public static final int SIZE_Y = 9;
    public static final int DISAPPEAR_CELLS_COUNT = 5;
    public static final int GREAT_DISAPPEAR_CELLS_COUNT = 9;

    private final FieldCell[][] field;
    private int freeCount = SIZE_X * SIZE_Y;
    private int frozenCount;
    private Point selected;

    public GameField() {
        field = new FieldCell[SIZE_Y][SIZE_X];
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                field[i][j] = new FieldCell();
            }
        }
    }

    public boolean isEmpty() {
        return freeCount == SIZE_X * SIZE_Y;
    }

    public boolean isFull() {
        return freeCount == 0;
    }

    public int getFreeCount() {
        return freeCount;
    }

    public int getFrozenCount() {
        return frozenCount;
    }

    public Point getSelected() {
        return selected;
    }

    public void setSelected(Point selected) {
        this.selected = selected;
    }

    public void addCells(List<FieldCell> cells) {
        for (FieldCell cell : cells) {
            Point point = findRandomFreePosition();
            if (point != null) {
                addCell(point, cell.getType());
                findAndMarkGroups(point);
            }
        }
    }

    private Point findRandomFreePosition() {
        int k = -1;
        int pos = RandomUtils.random(freeCount);

        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (field[i][j].isFree()) {
                    if (++k == pos) {
                        return new Point(j, i);
                    }
                }
            }
        }
        return null;
    }

    private void addCell(Point point, FieldCell.Type type) {
        FieldCell cell = field[point.y][point.x];
        cell.setType(type);
        cell.setAppearing(true);
        if (cell.isFrozen()) {
            frozenCount++;
        }
        freeCount--;
    }

    public FieldCell getCell(int x, int y) {
        return field[y][x];
    }

    public FieldCell getCell(Point point) {
        return field[point.y][point.x];
    }

    public void swap(Point one, Point two) {
        FieldCell tmpCell = field[one.y][one.x];
        field[one.y][one.x] = field[two.y][two.x];
        field[two.y][two.x] = tmpCell;
    }

    private boolean compareCells(FieldCell one, FieldCell two) {
        if (two.isFree() || two.isFrozen()) {
            return false;
        }
        return one.equals(two) || two.isSpecial();
    }

    private int countSimilarNeighbours(Point point, FieldCell cell, int stepX, int stepY) {
        int count = 0;
        for (int i = point.y + stepY, j = point.x + stepX; (i >= 0) && (i < SIZE_Y) && (j >= 0) && (j < SIZE_X); i += stepY, j += stepX) {
            if (compareCells(cell, field[i][j])) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private int checkAndMarkHorizontal(Point point, FieldCell cell) {
        if (cell == null || cell.isFree() || cell.isFrozen()) {
            return 0;
        }

        int leftCount = countSimilarNeighbours(point, cell, -1, 0);
        int rightCount = countSimilarNeighbours(point, cell, 1, 0);
        int count = leftCount + rightCount;

        if (count + 1 < DISAPPEAR_CELLS_COUNT) {
            return 0;
        }

        for (int i = point.x - leftCount; i <= point.x + rightCount; i++) {
            field[point.y][i].setDisappearing(true);
        }
        return count;
    }

    private int checkAndMarkVertical(Point point, FieldCell cell) {
        if (cell == null || cell.isFree() || cell.isFrozen()) {
            return 0;
        }

        int topCount = countSimilarNeighbours(point, cell, 0, -1);
        int bottomCount = countSimilarNeighbours(point, cell, 0, 1);
        int count = topCount + bottomCount;

        if (count + 1 < DISAPPEAR_CELLS_COUNT) {
            return 0;
        }

        for (int i = point.y - topCount; i <= point.y + bottomCount; i++) {
            field[i][point.x].setDisappearing(true);
        }
        return count;
    }

    private int checkAndMarkLeftTopRightBottom(Point point, FieldCell cell) {
        if (cell == null || cell.isFree() || cell.isFrozen()) {
            return 0;
        }

        int leftTopCount = countSimilarNeighbours(point, cell, -1, -1);
        int rightBottomCount = countSimilarNeighbours(point, cell, 1, 1);

        int count = leftTopCount + rightBottomCount;
        if (count + 1 < DISAPPEAR_CELLS_COUNT) {
            return 0;
        }

        for (int i = 1; i <= leftTopCount; i++) {
            field[point.y - i][point.x - i].setDisappearing(true);
        }
        for (int i = 0; i <= rightBottomCount; i++) {
            field[point.y + i][point.x + i].setDisappearing(true);
        }
        return count;
    }

    private int checkAndMarkRightTopLeftBottom(Point point, FieldCell cell) {
        if (cell == null || cell.isFree() || cell.isFrozen()) {
            return 0;
        }

        int rightTopCount = countSimilarNeighbours(point, cell, 1, -1);
        int leftBottomCount = countSimilarNeighbours(point, cell, -1, 1);
        int count = rightTopCount + leftBottomCount;

        if (count + 1 < DISAPPEAR_CELLS_COUNT) {
            return 0;
        }

        for (int i = 1; i <= rightTopCount; i++) {
            field[point.y - i][point.x + i].setDisappearing(true);
        }
        for (int i = 0; i <= leftBottomCount; i++) {
            field[point.y + i][point.x - i].setDisappearing(true);
        }
        return count;
    }

    private FieldCell findNonSpecialCell(Point point, int stepX, int stepY) {
        FieldCell cell = null;
        for (int i = point.y + stepY, j = point.x + stepX; (i >= 0) && (i < SIZE_Y) && (j >= 0) && (j < SIZE_X); i += stepY, j += stepX) {
            cell = field[i][j];
            if (!cell.isSpecial()) {
                break;
            }
        }
        return cell;
    }

    public int findAndMarkGroups(Point point) {
        FieldCell cell = field[point.y][point.x];
        if (cell.isSimple()) {
            return 1 + checkAndMarkHorizontal(point, cell)
                    + checkAndMarkVertical(point, cell)
                    + checkAndMarkLeftTopRightBottom(point, cell)
                    + checkAndMarkRightTopLeftBottom(point, cell);
        } else if (cell.isSpecial()) {
            return 1 + checkAndMarkHorizontal(point, findNonSpecialCell(point, -1, 0))
                    + checkAndMarkHorizontal(point, findNonSpecialCell(point, 1, 0))
                    + checkAndMarkVertical(point, findNonSpecialCell(point, 0, -1))
                    + checkAndMarkVertical(point, findNonSpecialCell(point, 0, 1))
                    + checkAndMarkLeftTopRightBottom(point, findNonSpecialCell(point, -1, -1))
                    + checkAndMarkLeftTopRightBottom(point, findNonSpecialCell(point, 1, 1))
                    + checkAndMarkRightTopLeftBottom(point, findNonSpecialCell(point, 1, -1))
                    + checkAndMarkRightTopLeftBottom(point, findNonSpecialCell(point, -1, 1));
        }
        return 0;
    }

    public List<Point> findPath(Point from, Point to) {
        return new Dijkstra(field).findPath(from, to);
    }

    public void highlightPath(List<Point> movingPath) {
        for (Point point : movingPath) {
            getCell(point).setHighlighted(true);
        }
    }

    public void tick() {
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                field[i][j].tick();
            }
        }
    }

    public void clearAppearingFlag() {
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (field[i][j].isAppearing()) {
                    field[i][j].setAppearing(false);
                }
            }
        }
    }

    public void clearHighlightedFlag() {
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (field[i][j].isHighlighted()) {
                    field[i][j].setHighlighted(false);
                }
            }
        }
    }

    public int getDisappearingCount() {
        int count = 0;

        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (field[i][j].isDisappearing()) {
                    count++;
                }
            }
        }

        return count;
    }

    public void clearDisappearedCells() {
        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (field[i][j].isDisappearing()) {
                    field[i][j].clear();
                    freeCount++;
                }
            }
        }
    }

    public void clearOneFrozenCell() {
        int index = RandomUtils.random(frozenCount);
        int k = -1;

        for (int i = 0; i < SIZE_Y; i++) {
            for (int j = 0; j < SIZE_X; j++) {
                if (field[i][j].isFrozen()) {
                    if (++k == index) {
                        field[i][j].setDisappearing(true);
                        frozenCount--;
                        return;
                    }
                }
            }
        }
    }
}

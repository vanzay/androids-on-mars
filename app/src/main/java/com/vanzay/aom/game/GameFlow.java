package com.vanzay.aom.game;

import static com.vanzay.aom.game.GameView.CELL_HEIGHT;
import static com.vanzay.aom.game.GameView.CELL_WIDTH;
import static com.vanzay.aom.game.GameView.MOVE_STEP_X;
import static com.vanzay.aom.game.GameView.MOVE_STEP_Y;
import static com.vanzay.aom.game.GameField.DISAPPEAR_CELLS_COUNT;
import static com.vanzay.aom.game.GameField.GREAT_DISAPPEAR_CELLS_COUNT;

import android.content.Intent;
import android.graphics.Point;

import com.vanzay.aom.GameActivity;
import com.vanzay.aom.PauseMenuActivity;
import com.vanzay.aom.R;
import com.vanzay.aom.sounds.SoundPlayer;
import com.vanzay.aom.storage.ScoreStorage;
import com.vanzay.aom.storage.SettingStorage;
import com.vanzay.aom.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class GameFlow implements Runnable {

    public enum State {
        INIT, ACTIVE, PAUSED, GAME_OVER, FINISHED
    }

    enum SubState {
        NONE, MOVING, APPEARING, DISAPPEARING
    }

    public static final int MILLISECONDS_PER_TICK = 100;
    public static final int LAMPS_OFF_PERIOD = 30;
    public static final int SCORE_PER_LEVEL = 300;
    public static final int INITIAL_CELLS_COUNT = 5;
    public static final int NEXT_CELLS_COUNT = 3;
    public static final int FROZEN_CELL_LIFETIME = 4; // player steps
    public static final int BONUS_BARRIER = 2; // unsuccessful player steps
    public static final int FREE_CELLS_FOR_BONUS = 40;
    public static final int FREE_CELLS_FOR_BONUS_STEP = 5;
    public static final int SCORE_MULTIPLIER = 3;
    public static final int GREAT_SCORE_MULTIPLIER = 100;
    public static final int ANIMATION_APPEARING_TICKS = 3;

    // Score and level
    private int score;
    private int level;
    private int scoreToEndLevel;
    private int freeCellsForBonus;
    private int bonuses;
    private int antiBonuses;

    // States
    private int frozenStepCount;
    private List<FieldCell> nextCells;
    private State state;
    private SubState subState;
    private Counter subStateCounter;

    // field
    private GameField field;
    private List<Point> movingPath;

    private final GameActivity context;

    public GameFlow(GameActivity context) {
        this.context = context;
        new Thread(this).start();
    }

    public int getScore() {
        return score;
    }

    public List<FieldCell> getNextCells() {
        return nextCells;
    }

    public GameField getField() {
        return field;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public SubState getSubState() {
        return subState;
    }

    public Counter getSubStateCounter() {
        return subStateCounter;
    }

    public List<Point> getMovingPath() {
        return movingPath;
    }

    @Override
    public void run() {
        while (state != State.FINISHED) {
            long time = System.currentTimeMillis();

            if (state == State.ACTIVE || state == State.GAME_OVER) {
                tick();
            }
            context.invalidate();

            time = System.currentTimeMillis() - time;

            if (time < MILLISECONDS_PER_TICK) {
                try {
                    Thread.sleep(MILLISECONDS_PER_TICK - time);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        ScoreStorage.saveScore(context, score);
        cleanup();
    }

    public void showPauseMenu() {
        context.startActivityForResult(new Intent(context, PauseMenuActivity.class),
                GameActivity.PAUSE_MENU_REQUEST);
        setState(State.PAUSED);
    }

    public void startGame() {
        setState(State.INIT);

        score = 0;
        level = 1;
        scoreToEndLevel = SCORE_PER_LEVEL;
        freeCellsForBonus = FREE_CELLS_FOR_BONUS;
        bonuses = 0;
        antiBonuses = 0;

        field = new GameField();
        field.addCells(generateNextCells(INITIAL_CELLS_COUNT));
        startAppearAnimation();

        nextCells = generateNextCells(NEXT_CELLS_COUNT);
        frozenStepCount = 0;

        setState(State.ACTIVE);
    }

    private void cleanup() {
        field = null;
        nextCells = null;
    }

    public void handleFieldCellClick(Point point) {
        if (field.getSelected() != null && field.getSelected().equals(point)) {
            field.setSelected(null);
        } else {
            FieldCell cell = field.getCell(point);
            if (cell.isSimple() || cell.isSpecial()) {
                field.setSelected(point);
            } else if (field.getSelected() != null && cell.isFree()) {
                movingPath = field.findPath(field.getSelected(), point);
                if (movingPath != null && movingPath.size() > 1) {
                    if (SettingStorage.isFastStepsEnabled()) {
                        field.swap(field.getSelected(), point);
                        handleStepDone(movingPath.get(movingPath.size() - 1));
                    } else {
                        field.highlightPath(movingPath);
                        subState = SubState.MOVING;
                        initMoving(movingPath.get(0), movingPath.get(1));
                    }
                    field.setSelected(null);
                }
            }
        }
    }

    private List<FieldCell> generateNextCells(int count) {
        count = Math.min(count, field.getFreeCount());
        List<FieldCell> cells = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            FieldCell.Type type = generateNextCellType();
            cells.add(new FieldCell(type));
            if (type == FieldCell.Type.MULTICOLOR) {
                bonuses = 0;
            } else if (type == FieldCell.Type.PINK) {
                antiBonuses = 0;
            }
        }
        return cells;
    }

    private FieldCell.Type generateNextCellType() {
        if (bonuses > BONUS_BARRIER
                && field.getFreeCount() < freeCellsForBonus
                && RandomUtils.random(2) == 0) {
            return FieldCell.Type.MULTICOLOR;
        } else if (antiBonuses > BONUS_BARRIER
                && RandomUtils.random(2) == 0) {
            return FieldCell.Type.PINK;
        }
        return FieldCell.SIMPLE_CELL_TYPES.get(RandomUtils.random(FieldCell.SIMPLE_CELL_TYPES.size()));
    }

    private void updateScore(int count) {
        if (count >= GREAT_DISAPPEAR_CELLS_COUNT) {
            score += GREAT_SCORE_MULTIPLIER * count;
            context.alert(context.getResources().getString(R.string.great));
            SoundPlayer.play(R.raw.great_score);
            // important condition: there is case when only frozen cell is deleting
        } else if (count >= DISAPPEAR_CELLS_COUNT) {
            score += SCORE_MULTIPLIER * count;
            SoundPlayer.play(R.raw.score);
        }

        if (score >= scoreToEndLevel) {
            level++;
            scoreToEndLevel = level * SCORE_PER_LEVEL;
            switch (level) {
                case 2:
                case 3:
                    freeCellsForBonus = FREE_CELLS_FOR_BONUS;
                    break;
                case 4:
                case 5:
                case 6:
                    freeCellsForBonus = FREE_CELLS_FOR_BONUS - FREE_CELLS_FOR_BONUS_STEP;
                    break;
                case 7:
                case 8:
                case 9:
                    freeCellsForBonus = FREE_CELLS_FOR_BONUS - (FREE_CELLS_FOR_BONUS_STEP << 1);
                    break;
                default:
                    freeCellsForBonus = FREE_CELLS_FOR_BONUS - (3 * FREE_CELLS_FOR_BONUS_STEP);
                    break;
            }
        }
    }

    private void startAppearAnimation() {
        subState = SubState.APPEARING;
        subStateCounter = new Counter(ANIMATION_APPEARING_TICKS);
    }

    private void startDisappearAnimation() {
        subState = SubState.DISAPPEARING;
        subStateCounter = new Counter(ANIMATION_APPEARING_TICKS);
    }

    private void initMoving(Point currentCellPos, Point nextCellPos) {
        FieldCell cell = field.getCell(currentCellPos);
        if (nextCellPos.x > currentCellPos.x) {
            subStateCounter = new MoveCounter(MOVE_STEP_X, 0, CELL_WIDTH / MOVE_STEP_X);
            cell.setState(FieldCell.State.MOVE_RIGHT);
        } else if (nextCellPos.x < currentCellPos.x) {
            subStateCounter = new MoveCounter(-MOVE_STEP_X, 0, CELL_WIDTH / MOVE_STEP_X);
            cell.setState(FieldCell.State.MOVE_LEFT);
        } else if (nextCellPos.y > currentCellPos.y) {
            subStateCounter = new MoveCounter(0, MOVE_STEP_Y, CELL_HEIGHT / MOVE_STEP_Y);
            cell.setState(FieldCell.State.MOVE_DOWN);
        } else {
            subStateCounter = new MoveCounter(0, -MOVE_STEP_Y, CELL_HEIGHT / MOVE_STEP_Y);
            cell.setState(FieldCell.State.MOVE_UP);
        }
    }

    private void handleStepDone(Point destPoint) {
        if (field.getFrozenCount() > 0) {
            frozenStepCount++;
        }
        if (frozenStepCount >= FROZEN_CELL_LIFETIME) {
            field.clearOneFrozenCell();
            startDisappearAnimation();
            frozenStepCount = 0;
        }

        int disappearingCount = field.findAndMarkGroups(destPoint);
        if (disappearingCount >= DISAPPEAR_CELLS_COUNT) {
            antiBonuses++;
            updateScore(disappearingCount);
            startDisappearAnimation();
        } else {
            bonuses++;
            field.addCells(nextCells);
            startAppearAnimation();
            nextCells = generateNextCells(NEXT_CELLS_COUNT);
        }
    }

    private void tick() {
        for (FieldCell cell : nextCells) {
            cell.tick();
        }

        field.tick();

        if (subStateCounter != null) {
            subStateCounter.increase();
            if (subStateCounter.completed()) {
                subStateCounter = null;

                switch (subState) {
                    case MOVING:
                        if (movingPath.size() > 1) {
                            Point currentCellPos = movingPath.remove(0);
                            Point nextCellPos = movingPath.get(0);

                            field.swap(currentCellPos, nextCellPos);

                            if (movingPath.size() > 1) {
                                initMoving(movingPath.get(0), movingPath.get(1));
                                break;
                            }
                        }

                        Point destPoint = movingPath.get(0);
                        field.getCell(destPoint).setState(FieldCell.State.IDLE);

                        field.clearHighlightedFlag();
                        movingPath = null;

                        handleStepDone(destPoint);
                        break;

                    case DISAPPEARING:
                        subState = SubState.NONE;
                        field.clearDisappearedCells();

                        if (field.isEmpty()) {
                            field.addCells(nextCells);
                            startAppearAnimation();
                            nextCells = generateNextCells(NEXT_CELLS_COUNT);
                        }
                        break;

                    case APPEARING:
                        subState = SubState.NONE;
                        field.clearAppearingFlag();

                        int count = field.getDisappearingCount();
                        if (count > 0) {
                            updateScore(count);
                            startDisappearAnimation();
                        } else if (field.isFull()) {
                            setState(State.GAME_OVER);
                            context.alert(context.getResources().getString(R.string.game_over));
                            SoundPlayer.play(R.raw.game_over);
                        }
                        break;
                }
            }
        }
    }
}

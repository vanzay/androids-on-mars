package com.vanzay.aom.game;

import static com.vanzay.aom.game.GameFlow.LAMPS_OFF_PERIOD;
import static com.vanzay.aom.images.ImageTools.ALIGN_RIGHT;
import static com.vanzay.aom.images.ImageTools.ALIGN_TOP;
import static com.vanzay.aom.images.Images.NUMBERS;
import static com.vanzay.aom.images.Images.TILES;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.vanzay.aom.GameActivity;
import com.vanzay.aom.R;
import com.vanzay.aom.images.ImageTools;
import com.vanzay.aom.images.Images;
import com.vanzay.aom.utils.RandomUtils;

public class GameView extends View {

    public static final int CELL_WIDTH = 32;
    public static final int CELL_HEIGHT = 32;
    public static final int FIELD_LEFT = 12;
    public static final int FIELD_TOP = 92;
    public static final int FIELD_RIGHT = FIELD_LEFT + (CELL_WIDTH + 1) * GameField.SIZE_X - 1;
    public static final int FIELD_BOTTOM = FIELD_TOP + (CELL_HEIGHT + 1) * GameField.SIZE_Y - 1;
    public static final int SCORE_POS_X = 308;
    public static final int SCORE_POS_Y = 426;
    public static final int SCORE_POS_X_STEP = 15;
    public static final int NEXT_CELL_TYPES_POS_X = 96;
    public static final int NEXT_CELL_TYPES_POS_Y = 32;
    public static final int NEXT_CELL_TYPES_POS_STEP = 48;
    public static final int LAMPS_POS_X = 232;
    public static final int LAMPS_POS_Y = 0;
    public static final int MENU_BUTTON_POS_X = 11;
    public static final int MENU_BUTTON_POS_Y = 424;
    public static final int MENU_BUTTON_LEFT = 11;
    public static final int MENU_BUTTON_TOP = 420;
    public static final int MENU_BUTTON_RIGHT = 154;
    public static final int MENU_BUTTON_BOTTOM = 474;
    public static final int MOVE_STEP_X = 8;
    public static final int MOVE_STEP_Y = 8;

    private final GameActivity context;
    private final GameFlow flow;

    private FlashMessage flashMessage;
    private Bitmap backgroundBitmap;
    private Rect backgroundClip;
    private Rect backgroundScaledSize;
    private boolean menuPressed;

    public GameView(Context context, GameFlow flow) {
        super(context);

        this.context = (GameActivity) context;
        this.flow = flow;

        flashMessage = new FlashMessage(this.context.getScale());
        backgroundBitmap = createBackground();
        menuPressed = false;
    }

    public void alert(String message) {
        flashMessage.alert(message);
    }

    private Bitmap createBackground() {
        Bitmap imgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_field);
        Bitmap bitmap = Bitmap.createBitmap(imgBitmap.getWidth(), imgBitmap.getHeight(), imgBitmap.getConfig());

        backgroundClip = new Rect(0, 0, imgBitmap.getWidth(), imgBitmap.getHeight());
        backgroundScaledSize = new Rect(0, 0, (int) (context.getScale() * imgBitmap.getWidth()), (int) (context.getScale() * imgBitmap.getHeight()));

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(imgBitmap, 0, 0, null);

        for (int i = 0; i < GameField.SIZE_Y; i++) {
            int y = FIELD_TOP + i * (CELL_HEIGHT + 1);
            for (int j = 0; j < GameField.SIZE_X; j++) {
                int x = FIELD_LEFT + j * (CELL_WIDTH + 1);
                int tile = TILES[RandomUtils.random(TILES.length)];
                ImageTools.drawOrigSize(canvas, x, y, tile, ALIGN_TOP | ImageTools.ALIGN_LEFT);
            }
        }

        return bitmap;
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawBitmap(backgroundBitmap, backgroundClip, backgroundScaledSize, null);

        if (flow.getState() == GameFlow.State.INIT || flow.getState() == GameFlow.State.FINISHED) {
            return;
        }

        drawNextCells(c);

        drawScore(c);

        if (flow.getSubState() == GameFlow.SubState.MOVING) {
            drawMovingPath(c);
        } else if (flow.getField().getSelected() != null) {
            ImageTools.draw(c, translateIndexToX(flow.getField().getSelected().x),
                    translateIndexToY(flow.getField().getSelected().y),
                    Images.ID_TILE_HL,
                    ALIGN_TOP | ImageTools.ALIGN_LEFT);
        }

        drawFieldCells(c);

        if (flashMessage.isVisible()) {
            flashMessage.draw(c);
        }

        if (menuPressed) {
            ImageTools.draw(c, MENU_BUTTON_POS_X, MENU_BUTTON_POS_Y,
                    Images.ID_MENU_PRESSED, ALIGN_TOP | ImageTools.ALIGN_LEFT);
        }

        if (flow.getState() != GameFlow.State.PAUSED && RandomUtils.random(LAMPS_OFF_PERIOD) == 0) {
            ImageTools.draw(c, LAMPS_POS_X, LAMPS_POS_Y,
                    Images.ID_LAMPS_OFF, ALIGN_TOP | ImageTools.ALIGN_LEFT);
        }
    }

    private void drawNextCells(Canvas c) {
        int x = NEXT_CELL_TYPES_POS_X;
        for (FieldCell cell : flow.getNextCells()) {
            cell.draw(c, x, NEXT_CELL_TYPES_POS_Y);
            x += NEXT_CELL_TYPES_POS_STEP;
        }
    }

    private void drawScore(Canvas c) {
        int x = SCORE_POS_X;
        int value = flow.getScore();
        do {
            int imageId = NUMBERS[value % 10];
            ImageTools.draw(c, x, SCORE_POS_Y, imageId, ALIGN_TOP | ALIGN_RIGHT);
            value /= 10;
            x -= SCORE_POS_X_STEP;
        } while (value != 0);
    }

    private void drawMovingPath(Canvas c) {
        for (int i = 0; i < GameField.SIZE_Y; i++) {
            for (int j = 0; j < GameField.SIZE_X; j++) {
                if (flow.getField().getCell(j, i).isHighlighted()) {
                    ImageTools.draw(c, translateIndexToX(j), translateIndexToY(i),
                            Images.ID_TILE_HL, ALIGN_TOP | ImageTools.ALIGN_LEFT);
                }
            }
        }
    }

    private void drawFieldCells(Canvas c) {
        Point current = flow.getSubState() == GameFlow.SubState.MOVING ? flow.getMovingPath().get(0) : null;
        for (int i = 0; i < GameField.SIZE_Y; i++) {
            int y = translateIndexToY(i);
            for (int j = 0; j < GameField.SIZE_X; j++) {
                int x = translateIndexToX(j);
                FieldCell cell = flow.getField().getCell(j, i);
                if (flow.getSubState() == GameFlow.SubState.MOVING && current != null && current.x == j && current.y == i) {
                    MoveCounter moveCounter = (MoveCounter) flow.getSubStateCounter();
                    cell.draw(c, x + moveCounter.getShiftX(), y + moveCounter.getShiftY());
                } else if (flow.getSubState() == GameFlow.SubState.APPEARING && cell.isAppearing()) {
                    // TODO move to FieldCell?
                    float radius = CELL_WIDTH * flow.getSubStateCounter().getProgress();
                    ImageTools.drawRounded(c, x, y, radius, cell.getIdleImageId(),
                            ALIGN_TOP | ImageTools.ALIGN_LEFT);
                } else if (flow.getSubState() == GameFlow.SubState.DISAPPEARING && cell.isDisappearing()) {
                    // TODO move to FieldCell?
                    float radius = CELL_WIDTH * (1 - flow.getSubStateCounter().getProgress());
                    ImageTools.drawRounded(c, x, y, radius, cell.getIdleImageId(),
                            ALIGN_TOP | ImageTools.ALIGN_LEFT);
                } else {
                    cell.draw(c, x, y);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (flow.getState() == GameFlow.State.GAME_OVER) {
            flow.setState(GameFlow.State.FINISHED);
            // do it in case of closing MainMenuActivity
            //context.startActivity(new Intent(context, MainMenuActivity.class));
            context.finish();
        } else if (flow.getState() == GameFlow.State.ACTIVE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (flow.getSubState() == GameFlow.SubState.NONE && isPointInGameField(x, y)) {
                        flow.handleFieldCellClick(new Point(translateXToIndex(x), translateYToIndex(y)));
                    } else if (menuPressed && isPointInMenuButton(x, y)) {
                        menuPressed = false;
                        flow.showPauseMenu();
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (isPointInMenuButton(x, y)) {
                        menuPressed = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    menuPressed = isPointInMenuButton(x, y);
                    break;
            }
        }
        return true;
    }

    private int translateIndexToX(int j) {
        return FIELD_LEFT + j * (CELL_WIDTH + 1);
    }

    private int translateIndexToY(int i) {
        return FIELD_TOP + i * (CELL_HEIGHT + 1);
    }

    private int translateYToIndex(float y) {
        return (int) ((y - FIELD_TOP * context.getScale()) / (CELL_HEIGHT * context.getScale() + 1));
    }

    private int translateXToIndex(float x) {
        return (int) ((x - FIELD_LEFT * context.getScale()) / (CELL_WIDTH * context.getScale() + 1));
    }

    private boolean isPointInGameField(float x, float y) {
        return x > FIELD_LEFT * context.getScale() && x < FIELD_RIGHT * context.getScale()
                && y > FIELD_TOP * context.getScale() && y < FIELD_BOTTOM * context.getScale();
    }

    private boolean isPointInMenuButton(float x, float y) {
        return x > MENU_BUTTON_LEFT * context.getScale() && x < MENU_BUTTON_RIGHT * context.getScale()
                && y > MENU_BUTTON_TOP * context.getScale() && y < MENU_BUTTON_BOTTOM * context.getScale();
    }
}

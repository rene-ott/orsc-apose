import com.aposbot.RasterOps;
import com.aposbot._default.IClient;
import com.aposbot._default.IPaintListener;

import java.awt.*;
import java.text.DecimalFormat;

public final class PaintListener
        implements IPaintListener {

    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 20;
    private static final int HP_FRONT = 0x3CDC3C;
    private static final int HP_BACK = 0x3CDC3C;
    private static final int PRAY_FRONT = 0x3CBEDC;
    private static final int PRAY_BACK = 0x3CBEDC;
    private static final int BOTTOM_GRAY = 0x0A0A03;
    private static final int BOTTOM_DARK = 0x002831;
    private static final int BOTTOM_MID = 0x00394A;
    private static final int BOTTOM_LIGHT = 0x005D7B;
    private static final PaintListener instance = new PaintListener();
    public static volatile boolean render_solid = true;
    public static volatile boolean render_textures = true;
    private final DecimalFormat dformat = new DecimalFormat("#,##0.0#");
    private final Dimension d = new Dimension();
    private IClient client;
    private boolean enabled = true;
    private double startingXP;
    private volatile boolean banned;
    private boolean resized = true;

    private PaintListener() {
    }

    static void init(IClient client) {
        instance.client = client;
    }

    public static void paint() {
        instance.onPaint();
    }

    public static void resetXp() {
        instance.resetDisplayedXp();
    }

    public static void toggle() {
        instance.setPaintingEnabled(!instance.isPaintingEnabled());
    }

    public static boolean isEnabled() {
        return instance.isPaintingEnabled();
    }

    public static void setEnabled(boolean enabled) {
        instance.setPaintingEnabled(enabled);
    }

    static IPaintListener get() {
        return instance;
    }

    private void drawStatBar(int x, int y,
                             double current, double base, int front, int back) {
        int rw = client.getGameWidth();
        int rh = client.getGameHeight();
        int[] pixels = client.getPixels();
        int width = (int) (current / base * BAR_WIDTH);

        y -= (BAR_HEIGHT / 2) + 4;
        RasterOps.fillRectAlpha(pixels, rw, rh,
                x, y, width, BAR_HEIGHT,
                160, front);
        RasterOps.fillRectAlpha(pixels, rw, rh,
                x + width, y, BAR_WIDTH - width, BAR_HEIGHT,
                65, back);
    }

    private void drawGradient() {
        int rw = client.getGameWidth();
        int rh = client.getGameHeight();
        int[] pixels = client.getPixels();
        int x = 512;
        int y = client.getGameHeight() - 13;

        RasterOps.fillRect(pixels, rw, rh, x, y, rw, 1, 0x0);
        y += 1;
        RasterOps.fillRect(pixels, rw, rh, x, y, rw, 11, BOTTOM_GRAY);
        y += 2;
        RasterOps.fillRect(pixels, rw, rh, x, y, rw, 7, BOTTOM_DARK);
        y += 1;
        RasterOps.fillRect(pixels, rw, rh, x, y, rw, 5, BOTTOM_MID);
        y += 1;
        RasterOps.fillRect(pixels, rw, rh, x, y, rw, 3, BOTTOM_LIGHT);
    }

    @Override
    public void onPaint() {
        if (banned) return;

        drawGradient();

        synchronized (d) {
            if (!resized) {
                client.resizeGame(d.width, d.height);
                resized = true;
            }
        }

        if (!enabled) return;

        final int hits = client.getCurrentLevel(3);
        final int base_hits = client.getBaseLevel(3);

        final int pray = client.getCurrentLevel(5);
        final int base_pray = client.getBaseLevel(5);

        int x, y;

        if (client.getGameWidth() >= (512 + 7 + BAR_WIDTH) &&
                client.getGameHeight() >= (346 + 84 + 25)) {
            x = client.getGameWidth() - BAR_WIDTH - 7;
            y = client.getGameHeight() - 84 - 25;
        } else {
            x = 7;
            y = 135;
        }

        int lineBeginX = x;


        String hits1 = String.format("Hits: %d", hits);
        int hits1Width = client.getGraphics().getFontMetrics().stringWidth(hits1);
        client.drawString(hits1, x, y, 1, 0xFFFFFF);
        x += hits1Width + 2;

        client.drawString("/", x, y, 1, 0xFF0000);
        x += 5;

        client.drawString(Integer.toString(base_hits), x, y, 1, 0xFFFFFF);
        x = lineBeginX;
        y += 17;

        String pray1 = String.format("Pray: %d", pray);
        int pray1Width = client.getGraphics().getFontMetrics().stringWidth(pray1);
        client.drawString(pray1, x, y, 1, 0xFFFFFF);
        x += pray1Width + 4;

        client.drawString("/", x, y, 1, 0xFF0000);
        x +=5;

        client.drawString(Integer.toString(base_pray), x, y, 1, 0xFFFFFF);
        y += 17;
        x = lineBeginX;

        client.drawString(String.format("Fatigue: %.2f%%",
                        client.getFatigue()),
                x, y, 1, 0xFFFFFF);
        y += 17;

        client.drawString("Tile: ", x, y, 1, 0xFFFFFF);
        x += 26;

        client.drawString("(", x, y, 1, 0xFF0000);
        x += 5;

        // See oli 15
        String posX = client.getLocalX() + client.getAreaX() + "";
        int posXWidth = client.getGraphics().getFontMetrics().stringWidth(posX);
        client.drawString(posX, x, y, 1, 0xFFFFFF);
        x += posXWidth + 2;

        client.drawString(",", x, y, 1, 0xFF0000);
        x += 5;

        String posY = client.getLocalY() + client.getAreaY() + "";
        int posYWidth = client.getGraphics().getFontMetrics().stringWidth(posY);
        client.drawString(posY, x, y, 1, 0xFFFFFF);
        x += posYWidth + 2;

        client.drawString(")", x, y, 1, 0xFF0000);
        y += 17;
        x = lineBeginX;

        client.drawString("XP: " +
                        dformat.format(getTotalXp() - startingXP),
                x, y, 1, 0xFFFFFF);

        ScriptListener.get().onPaintTick();
    }

    @Override
    public void resetDisplayedXp() {
        startingXP = getTotalXp();
    }

    @Override
    public boolean isPaintingEnabled() {
        return enabled;
    }

    @Override
    public void setPaintingEnabled(boolean b) {
        enabled = b;
    }

    private double getTotalXp() {
        double total = 0.0;
        final int len = StaticAccess.SKILL_NAMES.length;
        for (int i = 0; i < len; i++) {
            total += client.getExperience(i);
        }
        return total;
    }

    @Override
    public void setBanned(boolean b) {
        banned = b;
    }

    @Override
    public void doResize(int w, int h) {
        synchronized (d) {
            resized = false;
            d.width = w;
            d.height = h;
        }
    }

    @Override
    public void setRenderSolid(boolean b) {
        render_solid = b;
    }

    @Override
    public void setRenderTextures(boolean b) {
        render_textures = b;
    }
}

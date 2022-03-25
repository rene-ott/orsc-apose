import com.aposbot._default.IClient;
import com.aposbot._default.IScript;
import com.aposbot._default.IScriptListener;
import com.aposbot.common.BotProperties;
import com.aposbot.common.ReflectionUtil;
import com.aposbot.report.ReportDto;
import com.aposbot.report.ReportIntervalConverter;
import com.aposbot.report.ReportService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public final class ScriptListener
        implements IScriptListener {

    static final String ERROR_MESSAGE = "Error processing script. Send this output to the script's author:";
    private static final ScriptListener instance = new ScriptListener();
    private static ReportService reportService;

    private Extension ex;
    private long next;
    private boolean running;
    private IScript script;
    private String lastWord;
    private boolean newWord;
    private volatile boolean banned;

    private volatile boolean reporting;
    private volatile boolean reportScreenshot;

    private static long reportIntervalInMillis;
    private static long lastReportTimeInMillis = -1;

    private ScriptListener() {
    }

    static void init(Extension ex) {
        instance.ex = ex;
    }

    public static boolean isRunning() {
        return instance.isScriptRunning();
    }

    public static void setRunning(boolean b) {
        instance.setScriptRunning(b);
    }

    public static void runScript() {
        instance.onGameTick();
    }

    public static void message(boolean flag, String s1, int i1, String s2, int j1, int k1, String s3,
                               String s4) {

        instance.onGameMessage(flag, s1, i1, s2, j1, k1, s3, s4);
    }

    public static void playerDamagedCallback(final ta player)
    {
        instance.onPlayerDamaged(player);
    }

    public static void npcDamagedCallback(final ta npc)
    {
        instance.onNpcDamaged(npc);
    }

    public static void npcSpawnedCallback(final ta npc)
    {
        instance.onNpcSpawned(npc);
    }

    public static void npcProjectileDamagedCallback(final ta player)
    {
        instance.onNpcProjectileDamaged(player);
    }

    public static void deathCallback()
    {
        instance.onDeath();
    }

    public static void groundItemSpawnedCallback(final int groundItemIndex)
    {
        instance.onGroundItemSpawned(groundItemIndex);
    }

    public static void groundItemDespawnedCallback(final int id, final int localX, final int localY)
    {
        instance.onGroundItemDespawned(id, localX, localY);
    }

    static final IScriptListener get() {
        return instance;
    }

    private boolean isTempRenderingEnabled;
    private boolean isRenderingEnabled;

    @Override
    public void onGameTick() {
        if (banned) {
            return;
        }
        if (running) {

            if (reporting) {
                long timeDifferenceInMillis = System.currentTimeMillis() - lastReportTimeInMillis;

                if (timeDifferenceInMillis > reportIntervalInMillis - 1000 && !isTempRenderingEnabled) {
                    if (reporting && reportScreenshot) {
                        isRenderingEnabled = ex.isRendering();
                        isTempRenderingEnabled = true;
                        ex.setRendering(true);
                    }
                }

                if (timeDifferenceInMillis > reportIntervalInMillis) {
                    lastReportTimeInMillis = System.currentTimeMillis();
                    reportUserInformation();
                    if (reportScreenshot) {
                        ex.setRendering(isRenderingEnabled);
                        isTempRenderingEnabled = false;
                    }
                }
            }

            if (script.isSleeping()) {
                if (newWord && (script.getFatigue() == 0 || script.isTricking())) {
                    final String word = SleepListener.get().getGuess();
                    if (word != null && !word.equals(lastWord)) {
                        System.out.println("Sending guessed word: " + word);
                        ex.sendCAPTCHA(word);
                        lastWord = word;
                        newWord = false;
                    }
                }
                return;
            }
            if (System.currentTimeMillis() >= next) {
                try {
                    next = (script.main() + System.currentTimeMillis());
                } catch (final Throwable t) {
                    System.out.println(ERROR_MESSAGE);
                    t.printStackTrace();
                }
            }
        }
    }

    private void reportUserInformation() {
        Script script = (Script) this.script;

        IClient client = ReflectionUtil.getFieldValue(script, "client");
        Objects.requireNonNull(client);
        Extension extension = (Extension) client;

        ReportDto dto = ReportDto.create(
            AutoLogin.get().getUsername(),
            reportScreenshot ? getBase64EncodedScreenshot() : null,
            extension.getInventoryItems(),
            extension.getSkillLevels(),
            ReflectionUtil.getFieldValue(script, "bankViewTimestamp"),
            ReflectionUtil.getFieldValue(script, "viewedBankItems")
        );

        if (reportService == null) {
            reportService = ReportService.create();
        }
        reportService.sendReport(dto);
    }

    private String getBase64EncodedScreenshot() {
        final Image image = instance.ex.getImage();
        final BufferedImage b = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        final Graphics g = b.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(b, "png", baos);
        } catch (IOException e) {
            return null;
        }
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void onPaintTick() {
        if (running) {
            try {
                script.paint();
            } catch (final Throwable t) {
                System.out.println(ERROR_MESSAGE);
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onGameMessage(boolean flag, String s1, int i1, String s2, int j1, int k1, String s3,
                              String s4) {
        if (running) {
            if (s1 != null) {
                s1 = s1.replace((char) 160, ' ');
            }
            try {
                if (j1 == 1) {
                    script.onPrivateMessage(s2, s1, k1 == 1, k1 >= 2);
                } else if (j1 == 0 || j1 == 3) {
                    script.onServerMessage(s2);
                } else if (j1 == 4) {
                    script.onChatMessage(s2, s1, k1 == 1, k1 >= 2);
                } else if (j1 == 6) {
                    script.onTradeRequest(s1);
                }
            } catch (final Throwable t) {
                System.out.println(ERROR_MESSAGE);
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onKeyPress(int i) {
        if (running) {
            script.onKeyPress(i);
        }
    }

    @Override
    public void onNewSleepWord() {
        newWord = true;
    }

    @Override
    public void onPlayerDamaged(final Object player) {
        if (!running) {
            return;
        }

        try {
            script.onPlayerDamaged(player);
        } catch (final Throwable t) {
            System.out.println(ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    @Override
    public void onNpcDamaged(final Object npc) {
        if (!running) {
            return;
        }

        try {
            script.onNpcDamaged(npc);
        } catch (final Throwable t) {
            System.out.println(ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    @Override
    public void onNpcSpawned(final Object npc) {
        if (!running) {
            return;
        }

        try {
            script.onNpcSpawned(npc);
        } catch (final Throwable t) {
            System.out.println(ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    @Override
    public void onNpcProjectileDamaged(final Object player) {
        if (!running) {
            return;
        }

        try {
            script.onNpcProjectileDamaged(player);
        } catch (final Throwable t) {
            System.out.println(ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    @Override
    public void onDeath() {
        if (!running) {
            return;
        }

        try {
            script.onDeath();
        } catch (final Throwable t) {
            System.out.println(ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    @Override
    public void onGroundItemSpawned(final int groundItemIndex) {
        if (!running) {
            return;
        }

        try {
            script.onGroundItemSpawned(groundItemIndex);
        } catch (final Throwable t) {
            System.out.println(ERROR_MESSAGE);
            t.printStackTrace();
        }
    }

    @Override
    public void onGroundItemDespawned(final int id, final int localX, final int localY) {
        if (!running) {
            return;
        }

        try {
            script.onGroundItemDespawned(id, localX + this.ex.getAreaX(), localY + this.ex.getAreaY());
        } catch (final Throwable t) {
            System.out.println(ERROR_MESSAGE);
            t.printStackTrace();
        }
    }


    @Override
    public boolean isScriptRunning() {
        return running;
    }

    @Override
    public void setScriptRunning(boolean b) {
        if (b) {
            String duration = BotProperties.getReportInterval();
            reportIntervalInMillis = ReportIntervalConverter.convertToMillis(duration);
        } else {
            lastReportTimeInMillis = -1;
        }

        running = b;
    }

    @Override
    public void setIScript(IScript script) {
        this.script = script;
    }

    @Override
    public String getScriptName() {
        if (script == null) {
            return "null";
        }
        return script.toString();
    }

    @Override
    public boolean hasScript() {
        return script != null;
    }

    @Override
    public void setBanned(boolean b) {
        banned = b;
    }

    @Override
    public void setReporting(boolean b) {
        reporting = b;
    }

    @Override
    public void setReportScreenshot(boolean b) {
        reportScreenshot = b;
    }
}

package com.aposbot;

import com.aposbot._default.IClient;
import com.aposbot._default.IScript;
import com.aposbot._default.IScriptListener;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ScriptFrame extends Frame {

    static final File dir = new File("." + File.separator + "Scripts" + File.separator);
    private static final long serialVersionUID = -7439187170235715096L;
    private static final String PROCESSING_ERROR = "Error processing script. Send this output to the script's author:";
    private final java.awt.List displayed_list;
    private final TextField field;
    private TextField searchField;
    private final IClient client;
    private ScriptEngineManager manager;

    public ScriptFrame(IClient client) {
        super("Scripts");
        setFont(Constants.UI_FONT);

        this.client = client;

        setIconImages(Constants.ICONS);
        addWindowListener(new StandardCloseHandler(this, StandardCloseHandler.HIDE));

        Panel fpanel = new Panel();
        fpanel.setLayout(new GridLayout(1, 0));
        Label l = new Label("Parameters:");
        l.setFont(Constants.UI_FONT);
        fpanel.add(l);
        fpanel.add(field = new TextField(""));
        field.setFont(Constants.UI_FONT);
        add(fpanel, BorderLayout.NORTH);

        displayed_list = new java.awt.List();
        displayed_list.setFont(Constants.UI_FONT);
        final ScrollPane scroll = new ScrollPane();
        scroll.setFont(Constants.UI_FONT);
        scroll.add(displayed_list);
        add(scroll, BorderLayout.CENTER);

        // Search panel
        final Panel searchPanel = new Panel();
        searchPanel.setLayout(new GridLayout(1, 0));
        Label searchLabel = new Label("Search:");
        searchLabel.setFont(Constants.UI_FONT);
        searchPanel.add(searchLabel);

        searchField = new TextField("");
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    displayed_list.select(getMatchedScriptIndex());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        searchPanel.add(searchField);
        searchField.setFont(Constants.UI_FONT);

        final Panel buttonPanel = new Panel();

        final Button okButton = new Button("OK");
        okButton.setFont(Constants.UI_FONT);
        okButton.addActionListener(e -> new Thread(this::initScript, "ScriptInit").start());
        buttonPanel.add(okButton);

        final Button cancelButton = new Button("Cancel");
        cancelButton.setFont(Constants.UI_FONT);
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        final Panel southPanel = new Panel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(searchPanel);
        southPanel.add(buttonPanel);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(getSize());

        final Insets in = getInsets();

        setSize(in.right + in.left + 310, in.top + in.bottom + 240);
    }

    private int getMatchedScriptIndex() {
        String inputText = searchField.getText().toLowerCase();
        String[] scripts = displayed_list.getItems();
        if (scripts.length == 0)
            return -1;

        List<Object[]> matchedScripts = new ArrayList<>();

        for (int i = 0; i < scripts.length; i ++) {
            String curScriptName = scripts[i].toLowerCase();

            if (curScriptName.equals(inputText))
                return i;

            if (curScriptName.contains(inputText)) {
                matchedScripts.add(new Object[] { curScriptName, i});
            }
        }
        if (matchedScripts.isEmpty())
            return -1;

        if (matchedScripts.size() == 1)
            return (Integer) matchedScripts.get(0)[1];

        List<Object[]> sortedMatchedScripts = matchedScripts.stream().sorted((o1, o2) -> {
            return ((String)o1[0]).length() - ((String)o2[0]).length();
        }).collect(Collectors.toList());

        return (Integer)sortedMatchedScripts.get(0)[1];
    }

    public void initScript() {
        final IScriptListener listener = client.getScriptListener();
        if (listener.isScriptRunning()) {
            System.out.println("Stop the script you are running first.");
            return;
        }
        listener.setIScript(null);
        System.gc();

        final String selected_name = displayed_list.getSelectedItem();
        IScript script = null;
        if (selected_name.endsWith(".class")) {
            script = initJavaScript(selected_name);
        } else {
            script = initJSEScript(selected_name);
        }
        if (script == null) return;
        System.out.println("Selected script: " + script);
        try {
            script.init(field.getText());
        } catch (Throwable t) {
            System.out.println(PROCESSING_ERROR);
            t.printStackTrace();
            return;
        }
        field.setText("");
        client.getScriptListener().setIScript(script);
        System.out.println("Press the \"Start script\" button to start it.");
        setVisible(false);
    }

    public IScript initJSEScript(String name) {
        if (manager == null) {
            manager = new ScriptEngineManager();
        }
        int dot = name.indexOf('.');
        if (dot == -1) {
            System.out.println("Error: " + name + " has no file extension.");
            return null;
        }
        String ext = name.substring(dot + 1);
        ScriptEngine engine = manager.getEngineByExtension(ext);
        if (engine == null) {
            System.out.println("Error: No script engine found for " + ext);
            return null;
        }
        if (!(engine instanceof Invocable)) {
            System.out.println("Error: Engine for " + ext + " is not invocable.");
        }
        BufferedReader r = null;
        try {
            r = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(dir, name))
                            , Constants.UTF_8
                    )
            );
            engine.eval(r);
        } catch (Throwable t) {
            System.out.println("Error loading script:");
            t.printStackTrace();
            return null;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                }
            }
        }
        return client.createInvocableScript((Invocable) engine, name);
    }

    public IScript initJavaScript(String name) {
        Class<?> c;
        try {
            c = new ScriptClassLoader().loadClass(
                    name.substring(0, name.length() - 6)
            );
        } catch (final ClassNotFoundException t) {
            System.out.println("Error loading script:");
            t.printStackTrace();
            return null;
        }

        if (!IScript.class.isAssignableFrom(c)) {
            System.out.println("Error: " + name + " is not a valid Java script.");
            return null;
        }

        try {
            return (IScript) c.getConstructor(Class.forName("Extension")).newInstance(client);
        } catch (final Throwable t) {
            System.out.println("Failed to load script " + name + ":");
            t.printStackTrace();
        }
        return null;
    }

    private void update() {
        final String[] files = dir.list();
        if (files == null)
            return;

        final java.util.List<String> list = new ArrayList<>();

        int len = files.length;
        for (int i = 0; i < len; i++) {
            process(dir + File.separator + files[i], list);
        }
        Collections.sort(list);

        displayed_list.removeAll();
        for (final String str : list) {
            displayed_list.add(str);
        }
    }

    public void process(String filename, List<String> list) {
        File bonusdir = new File((filename));
        filename = filename.replace(dir.toString() + File.separator, "");
        if (bonusdir.isDirectory()) {
            String[] bonusFiles = bonusdir.list();
            if (bonusFiles != null) {
                for (int j = 0; j < bonusFiles.length; j++) {
                    process(File.separator + filename + File.separator + bonusFiles[j], list);
                }
            }
        }
        if (filename.endsWith(".java") ||
                filename.indexOf('$') != -1 ||
                filename.startsWith("_") ||
                filename.indexOf('.') == -1) {
            return;
        }
        list.add(filename);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            update();
            toFront();
            requestFocus();
        }
        super.setVisible(visible);
    }
}

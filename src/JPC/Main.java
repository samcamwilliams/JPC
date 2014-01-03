/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import MISP.Environment;
import MISP.BIFSystem;
import MISP.LObj;
import MISP.MISPException;
import MISP.MISPTests;
import MISP.StackTrace.Call;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;
import org.bff.javampd.events.*;
import org.bff.javampd.monitor.MPDStandAloneMonitor;
import org.bff.javampd.objects.MPDSong;

/**
 *
 * @author spine
 * 
 * This list is now basically out of date. The MISP system fucks most of these issues.
 * TODO:
 * Finish aliases. Write docs.
 * Add logging points
 * Process commands from command line arguments
 * Persistent logs
 * Turn input line yellow after a command is issued, but has not yet returned
 * Speed up switching tracks
 * 
 * POSSIBLES:
 * Document the code
 * GPLv3
 * Decouple MISP completely.
 * Config file
 * 
 */

public class Main extends javax.swing.JFrame {
    private UIState state;
    /**
     * Creates new form main
     */
    public Main() {
        state = UIState.getInstance();
        state.wrapper = new MPDWrapper();
        state.hooks = new ArrayList();
        state.buffers = new ArrayList();
        state.history = new ArrayList();
        state.bindings = new ArrayList();
        
        state.logger = new JPC.Logger();
        
        state.environment = new Environment();
        state.environment.AddBIFs(BIFUI.getAllVars());
        state.environment.AddBIFs(BIFInfo.getAllVars());
        
        File config = new File(System.getProperty("user.home") + "/.jpcrc");
        
        if(config.exists())
            state.environment.execute(new LObj("(include '" + config.getAbsolutePath() + "')"));
        
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (
            ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        
        startSystem();
    }
    
    private void startSystem() {
        initComponents();
        input.setFocusTraversalKeysEnabled(false);
        tabbed.setFocusTraversalKeysEnabled(false);
        
        state.tabbed = tabbed;
        state.input = input;
        state.output = output;
        state.panel = panel;
        
        state.buffers.add(
            new Buffer(
                scroll,
                playlist,
                state.wrapper.getStatus().getPlaylist()
            )
        );
        state.buffers.get(0).setName("Default");
        state.buffers.get(0).setSync(true);
        Buffer.activateBuffer(state, 0);
        
        state.setMode(UIState.Mode.COMMAND);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                state.wrapper.close();
            }
        });
        
        createMonitors();
        refresh();
        
        DefaultCaret c = (DefaultCaret)playlist.getCaret();
        c.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }
    
    private Runnable getUpdater() {
        
        Runnable runnable  = 
            new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            };
        
        return runnable;
    }

    private void createMonitors() {
        if(state.mpd == null) state.mpd = state.wrapper.getMPD();
        
        MPDStandAloneMonitor monitor = new MPDStandAloneMonitor(state.mpd) {};
        
        PlayerBasicChangeListener pbcl = new PlayerBasicChangeListener() {
            @Override
            public void playerBasicChange(PlayerBasicChangeEvent event) {
                SwingUtilities.invokeLater(getUpdater());
            }
        };
        TrackPositionChangeListener tpcl = new TrackPositionChangeListener() {
            @Override
            public void trackPositionChanged(TrackPositionChangeEvent event) {
                SwingUtilities.invokeLater(getUpdater());
                handleTriggers(event.getElapsedTime());
            }
        };
        
        monitor.addPlayerChangeListener(pbcl);
        monitor.addTrackPositionChangeListener(tpcl);
        monitor.addMPDErrorListener(new MPDErrorListener() {
            @Override
            public void errorEventReceived(MPDErrorEvent event) {
                System.out.println(event.getMsg());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

                createMonitors();
            }
        });
        state.updater = new Thread(monitor);
        state.updater.start();
    }
    
    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        mode_indicator = new javax.swing.JLabel();
        status = new javax.swing.JLabel();
        input = new javax.swing.JTextField();
        volume = new javax.swing.JLabel();
        now_playing = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        output = new javax.swing.JEditorPane();
        jLabel1 = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tabbed = new javax.swing.JTabbedPane();
        scroll = new javax.swing.JScrollPane();
        playlist = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JPC");

        panel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                panelKeyPressed(evt);
            }
        });

        mode_indicator.setText("Command");

        status.setText("jLabel2");

        input.setEnabled(false);
        input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputActionPerformed(evt);
            }
        });
        input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputKeyPressed(evt);
            }
        });

        volume.setText("jLabel3");

        now_playing.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        now_playing.setText("jLabel1");

        output.setEditable(false);
        output.setFocusable(false);
        jScrollPane2.setViewportView(output);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setText("Status:");

        time.setText("jLabel3");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText("Volume:");

        tabbed.setFocusable(false);
        tabbed.setOpaque(true);
        tabbed.setRequestFocusEnabled(false);
        tabbed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedStateChanged(evt);
            }
        });

        playlist.setEditable(false);
        playlist.setContentType("text/html"); // NOI18N
        playlist.setToolTipText(null);
        playlist.setFocusable(false);
        scroll.setViewportView(playlist);

        tabbed.addTab("1: Default", scroll);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(input)
                        .addGroup(panelLayout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(status)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(volume)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(time))
                        .addGroup(panelLayout.createSequentialGroup()
                            .addComponent(now_playing)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mode_indicator))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(tabbed))
                    .addContainerGap()))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                            .addComponent(now_playing)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(status)
                                .addComponent(jLabel2)
                                .addComponent(volume)
                                .addComponent(time)))
                        .addComponent(mode_indicator))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(tabbed)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void handleTriggers(long time) {
        Boolean trackChanged = false;
        
        MPDSong current = state.wrapper.getStatus().getNowPlaying();
        
        try {
            if(state.lastSong.getId() != current.getId()) {
                state.lastSong = state.wrapper.getStatus().getNowPlaying();
                trackChanged = true;
            }
        }
        catch(NullPointerException ex) {
            
        }
        
        ArrayList<Hook> to_remove = new ArrayList();
        
        for(Hook h: state.hooks)
            if(h.isActive(trackChanged, time)) {
                state.environment.execute(h.getCommand());
                if(h.isTemporary()) to_remove.add(h);
            }
        
        state.hooks.removeAll(to_remove);
    }
    
    private void inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputActionPerformed
        String in = input.getText();
        
        state.history_pos = -1;
        state.history.add(0, in);
        
        handle_exec(in);
    }//GEN-LAST:event_inputActionPerformed
    
    private void inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKeyPressed
        switch(evt.getKeyCode()) {
            case KeyEvent.VK_UP:
                if(state.history_pos + 1 < state.history.size()) {
                    state.history_pos++;
                    input.setText(state.history.get(state.history_pos));
                }
                break;
            case KeyEvent.VK_DOWN:
                if(state.history_pos > 0) {
                    state.history_pos--;
                    input.setText(state.history.get(state.history_pos));
                }
                else {
                    state.history_pos = -1;
                    input.setText("");
                }
                break;
            case KeyEvent.VK_TAB:
                    input.setText(autoComplete(input.getText()));
                break;
            case KeyEvent.VK_ESCAPE:
                state.input_buffer = "";
                state.setMode(UIState.Mode.COMMAND);
        }
    }//GEN-LAST:event_inputKeyPressed

    private void tabbedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedStateChanged
        if(state.tabbed != null) Buffer.activateBuffer(state, tabbed.getSelectedIndex());
    }//GEN-LAST:event_tabbedStateChanged

    private void panelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panelKeyPressed
        if(state.mode == UIState.Mode.INFO) {
            tabbed.remove(tabbed.getSelectedIndex());
            state.input_buffer = "";
            state.setMode(UIState.Mode.COMMAND);
            return;
        }
        
        int ascii = (int) evt.getKeyChar();
        if(!(ascii >= 32 && ascii < 128)) {
            switch (evt.getExtendedKeyCode()) {
                case KeyEvent.VK_ENTER:
                    state.input_buffer += "<Enter>";
                    break;
                case KeyEvent.VK_ALT:
                    state.input_buffer += "<Alt>";
                    break;
                case KeyEvent.VK_CONTROL:
                    state.input_buffer += "<Ctrl>";
                    break;
                case KeyEvent.VK_TAB:
                    state.input_buffer += "<Tab>";
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    if(!state.input_buffer.equals(""))
                        if(state.input_buffer.endsWith(">"))
                            state.input_buffer =
                                state.input_buffer.substring(0,
                                    state.input_buffer.lastIndexOf("<"));
                        else
                            state.input_buffer =
                                state.input_buffer.substring(0,
                                    state.input_buffer.length() - 1);
                    break;
                case KeyEvent.VK_ESCAPE:
                    state.input_buffer = "";
                    break;
            } 
        }
        else state.input_buffer += evt.getKeyChar();
        
        for(Binding b: state.bindings)
            if(b.matches(state.input_buffer)) {
                String com = b.getCom(state.input_buffer);

                state.input_buffer = "";
                
                if(b.isExec()) handle_exec(com);
                else {
                    state.setMode(UIState.Mode.INSERT);
                    int index = com.indexOf("$0");
                    input.setText(com.replace("$0", ""));
                    
                    if(index != -1) input.setCaretPosition(index);
                }
                return;
            }
        
        state.setMode(UIState.Mode.COMMAND);
        
    }//GEN-LAST:event_panelKeyPressed

    private void handle_exec(final String str) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String out = execute(autoComplete(str));
                output.setText(out);

                if(!state.error && state.mode != UIState.Mode.INFO) {
                    state.input_buffer = "";
                    state.setMode(UIState.Mode.COMMAND);
                }
            }
        });
    }
    
    private String execute(String in)  {
        in = in.trim();
        
        state.error = false;
        
        LObj obj;
        
        try {
            obj = new LObj(in);
        }
        catch (Exception ex) {
            Logger.getLogger(BIFSystem.class.getName()).log(Level.SEVERE, null, ex);
            state.error = true;
            return "Parser: " + formatException(ex);
        }
        
        if(!state.error) {            
            try {
                state.environment.resetTrace();
                LObj out = state.environment.unsafe_execute(obj);
                return out.toString();
            }
            catch (MISPException ex) {
                //String str = "";
                //ArrayList<Call> calls = ex.getTrace().getCalls();
                //for(int i = calls.size() - 1; i >= 0; i--) str += calls.get(i).toString() + "<br>";
                //state.showInfo("Exception", str);
                
                return "MISP Exception: " + ex.getReason();
            }
            catch (Exception ex) {
                Logger.getLogger(BIFSystem.class.getName()).log(Level.SEVERE, null, ex);
                return "Runtime: " + formatException(ex);
            }
        }
        
        return "Error: Unknown";
    }
    
    private String formatException(Exception ex) {
        return ex.toString() + " @ " + ex.getStackTrace()[0].toString();
    }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel mode_indicator;
    private javax.swing.JLabel now_playing;
    private javax.swing.JEditorPane output;
    private javax.swing.JPanel panel;
    private javax.swing.JEditorPane playlist;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JLabel status;
    private javax.swing.JTabbedPane tabbed;
    private javax.swing.JLabel time;
    private javax.swing.JLabel volume;
    // End of variables declaration//GEN-END:variables

    private void refresh() {
        MPDStatus mstatus = state.wrapper.getStatus();
        
        now_playing.setText(mstatus.getNowPlayingString());
        status.setText(mstatus.getStatus());
        volume.setText(mstatus.getVolume());
        
        if(mstatus.getNowPlaying() != null) {
           long elapsedTime = mstatus.getElapsedTime();
           int ttime = mstatus.getTrackLength();
           time.setText(formatTime((int)elapsedTime) + "/" + formatTime(ttime));
        }
        else time.setText("--/--");
        
        if(state.error) input.setBackground(new Color(232, 67, 67));
        else input.setBackground(Color.white);

        for(Buffer b: state.buffers) b.refresh(mstatus);

        switch (state.mode) {
            case COMMAND:
                mode_indicator.setText("Command");
                break;
            case INSERT:
                mode_indicator.setText("Insert");
                break;
            case INFO:
                mode_indicator.setText("Info");
                break;
        }
    }

    private String formatTime(int i) {
        int minutes = i / 60;
        int seconds = i % 60;
        String strseconds = Integer.toString(seconds);
        
        if(strseconds.length() < 2) strseconds = "0" + strseconds;
        
        return Integer.toString(minutes) + ":" + strseconds;
    }
    
    private String autoComplete(String text) {
        LObj o = new LObj(text);
        if(o.isSane()) return text;
        else return autoComplete(text += o.getExpected());
    }
}

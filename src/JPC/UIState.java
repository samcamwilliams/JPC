/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JPC;

import MISP.Environment;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.bff.javampd.MPD;
import org.bff.javampd.objects.MPDSong;

/**
 *
 * @author spine
 */
public class UIState {
    public enum Mode { INSERT, COMMAND, INFO };
    private static volatile UIState instance = null;
    private UIState(){}
    
    public Mode mode;
    public ArrayList<Buffer> buffers;
    public MPDWrapper wrapper;
    public MPD mpd;
    public Collection<MPDSong> search;
    public boolean refresh = true;
    public String old_pls = "";
    public ArrayList<String> history;
    public int history_pos = -1;
    public Boolean error = false;
    public Thread updater;
    public Logger logger;
    public ArrayList<Hook> hooks;
    public MPDSong lastSong = new MPDSong();
    public long lastTime = 0;
    public Environment environment;
    public String input_buffer = "";
    public ArrayList<Binding> bindings = new ArrayList<>();
    
    public JTabbedPane tabbed;
    public JEditorPane output;
    public JTextField input;
    public JPanel panel;
    
    public static UIState getInstance() {
        if (instance == null) {
            instance = new UIState();
        }
        return instance;
    }
    
    public void showInfo(String out) {
        showInfo("Info", out);
    }
    
    public void showInfo(String title, String out) {
        input.setFont(
                new Font(
                    input.getFont().getName(),
                    Font.BOLD,
                    input.getFont().getSize()
                )
            );
        
        JEditorPane output_pane = new javax.swing.JEditorPane();
        JScrollPane scroll = new javax.swing.JScrollPane();
        
        output_pane.setEditable(false);
        output_pane.setContentType("text/html");
        output_pane.setToolTipText(null);
        output_pane.setFocusable(false);
        output_pane.setText(out);
        scroll.setViewportView(output_pane);
        
        tabbed.addTab(title, scroll);
        tabbed.setSelectedComponent(scroll);
        
        setMode(Mode.INFO);
    }
    
    public void setMode(UIState.Mode m) {
        Boolean change = mode != m;
        mode = m;
        if(UIState.Mode.INFO == m && change) {
            input.setFont(
                    new Font(
                        input.getFont().getName(),
                        Font.BOLD,
                        input.getFont().getSize()
                    )
                );
            input.setText("Press any key to close this buffer.");
            input.setEnabled(false);
            tabbed.setEnabled(false);
            panel.requestFocus();
        }
        else if(UIState.Mode.COMMAND == m && !change) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(input_buffer.equals(""))
                        input.setText("Ready");
                    else
                        input.setText("Command Mode: " + input_buffer);
                }
            });
        }
        else if(UIState.Mode.COMMAND == m && change) {
            input.setFont(
                    new Font(
                        input.getFont().getName(),
                        Font.ITALIC,
                        input.getFont().getSize()
                    )
                );
            input.setText("Ready");
            input.setEnabled(false);
            tabbed.setEnabled(true);
            panel.requestFocus();
        }
        else if(UIState.Mode.INSERT == m && change) {
            tabbed.setEnabled(true);
            input.setEnabled(true);
            input.setFont(
                    new Font(
                        input.getFont().getName(),
                        Font.PLAIN,
                        input.getFont().getSize()
                    )
                );
            input.requestFocus();
            input.setText("");
        }
    }
}

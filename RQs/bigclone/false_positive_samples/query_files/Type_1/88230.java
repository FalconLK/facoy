import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class PIC implements ActionListener, KeyListener, Component {

    Project project;

    short loop = 0;

    short picwindow_update_loop = 0;

    boolean lst_available = false;

    boolean RA4_old_state = false;

    boolean RB0_old_state = false;

    boolean RB4_old_state = false;

    boolean RB5_old_state = false;

    boolean RB6_old_state = false;

    boolean RB7_old_state = false;

    boolean running = false;

    boolean remove_breakpoint_when_reached = false;

    CpuDebugWindow CPUDebug;

    RamDebugWindow RAMDebug;

    EEPROMDebugWindow EepromDebug;

    ProgramMemoryWindow progwindow;

    BreakpointDebugWindow bpwindow;

    StackDebugWindow stackwindow;

    PicWindow picwindow;

    WatchWindow watchers[] = new WatchWindow[5];

    IO porta;

    IO portb;

    Memory mem = new Memory(1024, 14, false);

    PICCPU cpu;

    String source_filename;

    String filename;

    String error_filename;

    String name = "PIC 16F84 Microcontroller";

    String[] pin_names = { "RB0", "RB1", "RB2", "RB3", "RB4", "RB5", "RB6", "RB7", "RA0", "RA1", "RA2", "RA3", "RA4" };

    String mpasm_path;

    String icprog_path;

    JButton PIC_button;

    JButton Build_button;

    JButton Program_button;

    JButton Code_button;

    JButton Step_button;

    JButton Stepover_button;

    JButton Stepout_button;

    JButton Restart_button;

    JButton Skip_button;

    JButton CPU_button;

    JButton RAM_button;

    JButton MEM_button;

    JButton EE_button;

    JButton Watch_button;

    JButton BP_button;

    JButton Stack_button;

    JButton AddBreakpoint_button;

    JButton DelBreakpoint_button;

    JButton watchdog_button;

    ImageIcon watchdog_on;

    ImageIcon watchdog_off;

    public PIC() {
    }

    public void init(Project parent, String data) {
        this.project = parent;
        if (data.charAt(1) != ':' || data.charAt(0) == '/') data = parent.getFilename().substring(0, Math.max(parent.getFilename().lastIndexOf('/'), parent.getFilename().lastIndexOf('\\'))) + '/' + data;
        this.source_filename = data;
        Workbench.desktop.addKeyListener(this);
        this.filename = data.substring(0, data.length() - 3) + "lst";
        try {
            RandomAccessFile piccfg = new RandomAccessFile("pic.cfg", "r");
            mpasm_path = piccfg.readLine();
            icprog_path = piccfg.readLine();
            piccfg.close();
        } catch (java.io.IOException ioe) {
        }
        JPanel panel = new JPanel();
        watchdog_on = new ImageIcon("images/watchdog_on.gif");
        watchdog_off = new ImageIcon("images/watchdog_off.gif");
        PIC_button = new JButton(new ImageIcon("images/pic.gif"));
        PIC_button.setBorder(BorderFactory.createEmptyBorder());
        PIC_button.setToolTipText("Show/Hide PIC");
        Code_button = new JButton(new ImageIcon("images/code.gif"));
        Code_button.setBorder(BorderFactory.createEmptyBorder());
        Code_button.setToolTipText("Edit program code");
        Build_button = new JButton(new ImageIcon("images/build.gif"));
        Build_button.setBorder(BorderFactory.createEmptyBorder());
        Build_button.setToolTipText("Save & Compile program code [F11]");
        Program_button = new JButton(new ImageIcon("images/program.gif"));
        Program_button.setBorder(BorderFactory.createEmptyBorder());
        Program_button.setToolTipText("Program the microcontroller");
        Step_button = new JButton(new ImageIcon("images/stepinto.gif"));
        Step_button.setBorder(BorderFactory.createEmptyBorder());
        Step_button.setToolTipText("Step into [F5]");
        Stepover_button = new JButton(new ImageIcon("images/stepover.gif"));
        Stepover_button.setBorder(BorderFactory.createEmptyBorder());
        Stepover_button.setToolTipText("Step over [F6]");
        Stepout_button = new JButton(new ImageIcon("images/stepout.gif"));
        Stepout_button.setBorder(BorderFactory.createEmptyBorder());
        Stepout_button.setToolTipText("Step out [F7]");
        Restart_button = new JButton(new ImageIcon("images/restart.gif"));
        Restart_button.setBorder(BorderFactory.createEmptyBorder());
        Restart_button.setToolTipText("Reset [F10]");
        Skip_button = new JButton(new ImageIcon("images/skip.gif"));
        Skip_button.setBorder(BorderFactory.createEmptyBorder());
        Skip_button.setToolTipText("Skip next instruction");
        CPU_button = new JButton(new ImageIcon("images/cpu.gif"));
        CPU_button.setBorder(BorderFactory.createEmptyBorder());
        CPU_button.setToolTipText("Show/Hide CPU Debugging");
        RAM_button = new JButton(new ImageIcon("images/ram.gif"));
        RAM_button.setBorder(BorderFactory.createEmptyBorder());
        RAM_button.setToolTipText("Show/Hide RAM Debugging");
        MEM_button = new JButton(new ImageIcon("images/mem.gif"));
        MEM_button.setBorder(BorderFactory.createEmptyBorder());
        MEM_button.setToolTipText("Show/Hide Program memory");
        EE_button = new JButton(new ImageIcon("images/ee.gif"));
        EE_button.setBorder(BorderFactory.createEmptyBorder());
        EE_button.setToolTipText("Show/Hide EEPROM memory");
        Watch_button = new JButton(new ImageIcon("images/rw.gif"));
        Watch_button.setBorder(BorderFactory.createEmptyBorder());
        Watch_button.setToolTipText("New Register Watch");
        BP_button = new JButton(new ImageIcon("images/bp.gif"));
        BP_button.setBorder(BorderFactory.createEmptyBorder());
        BP_button.setToolTipText("Show/Hide Breakpoints");
        Stack_button = new JButton(new ImageIcon("images/stack.gif"));
        Stack_button.setBorder(BorderFactory.createEmptyBorder());
        Stack_button.setToolTipText("Show/Hide Stack");
        AddBreakpoint_button = new JButton(new ImageIcon("images/addbp.gif"));
        AddBreakpoint_button.setBorder(BorderFactory.createEmptyBorder());
        AddBreakpoint_button.setToolTipText("Add breakpoint");
        DelBreakpoint_button = new JButton(new ImageIcon("images/delbp.gif"));
        DelBreakpoint_button.setBorder(BorderFactory.createEmptyBorder());
        DelBreakpoint_button.setToolTipText("Remove breakpoint");
        watchdog_button = new JButton(watchdog_off);
        watchdog_button.setBorder(BorderFactory.createEmptyBorder());
        watchdog_button.setToolTipText("Enable/Disable Watchdog Timer");
        panel.add(PIC_button);
        PIC_button.addActionListener(this);
        panel.add(Code_button);
        Code_button.addActionListener(this);
        if (mpasm_path != null) panel.add(Build_button);
        Build_button.addActionListener(this);
        if (icprog_path != null) panel.add(Program_button);
        Program_button.addActionListener(this);
        JButton separator3 = new JButton(new ImageIcon("images/separator.gif", " "));
        separator3.setBorder(BorderFactory.createEmptyBorder());
        panel.add(separator3);
        panel.add(Step_button);
        Step_button.addActionListener(this);
        panel.add(Stepover_button);
        Stepover_button.addActionListener(this);
        panel.add(Stepout_button);
        Stepout_button.addActionListener(this);
        panel.add(Restart_button);
        Restart_button.addActionListener(this);
        panel.add(Skip_button);
        Skip_button.addActionListener(this);
        JButton separator = new JButton(new ImageIcon("images/separator.gif", " "));
        separator.setBorder(BorderFactory.createEmptyBorder());
        panel.add(separator);
        panel.add(CPU_button);
        CPU_button.addActionListener(this);
        panel.add(RAM_button);
        RAM_button.addActionListener(this);
        panel.add(MEM_button);
        MEM_button.addActionListener(this);
        panel.add(EE_button);
        EE_button.addActionListener(this);
        panel.add(Watch_button);
        Watch_button.addActionListener(this);
        panel.add(BP_button);
        BP_button.addActionListener(this);
        panel.add(Stack_button);
        Stack_button.addActionListener(this);
        JButton separator2 = new JButton(new ImageIcon("images/separator.gif", " "));
        separator2.setBorder(BorderFactory.createEmptyBorder());
        panel.add(separator2);
        panel.add(AddBreakpoint_button);
        AddBreakpoint_button.addActionListener(this);
        panel.add(DelBreakpoint_button);
        DelBreakpoint_button.addActionListener(this);
        JButton separator4 = new JButton(new ImageIcon("images/separator.gif", " "));
        separator4.setBorder(BorderFactory.createEmptyBorder());
        panel.add(separator4);
        panel.add(watchdog_button);
        watchdog_button.addActionListener(this);
        parent.addToTab("PIC Microcontroller", panel, true);
        porta = new IO();
        portb = new IO();
        porta.setWidth(5);
        portb.setWidth(8);
        if (mem.load(filename)) lst_available = true;
        cpu = new PICCPU(mem);
        CPUDebug = new CpuDebugWindow(this, cpu.ram);
        RAMDebug = new RamDebugWindow(this, cpu.ram);
        EepromDebug = new EEPROMDebugWindow(this, cpu.eeprom);
        bpwindow = new BreakpointDebugWindow(this);
        cpu.DebugCPURegisters(CPUDebug);
        cpu.DebugFileRegisters(RAMDebug);
        cpu.DebugEEPROM(EepromDebug);
        progwindow = new ProgramMemoryWindow(this, this.filename, this.source_filename);
        picwindow = new PicWindow(porta, portb);
        stackwindow = new StackDebugWindow(this, cpu.stack);
        bpwindow.setProgramMemoryWindow(progwindow);
        updateDebugWindows();
        progwindow.GotoAddress(0);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == PIC_button) picwindow.ToggleVisible();
        if (evt.getSource() == Code_button && !running) {
            progwindow.EnableEditor(true);
            progwindow.setVisible(true);
        }
        if (evt.getSource() == Build_button) build();
        if (evt.getSource() == Program_button) {
            try {
                Thread.sleep(100);
                Runtime rt = Runtime.getRuntime();
                try {
                    rt.exec(icprog_path + " -l\"" + source_filename.substring(0, source_filename.length() - 3) + "hex\" -f3FF9");
                } catch (java.io.IOException ioe) {
                    System.out.println("Problem with ICProg");
                }
            } catch (java.lang.InterruptedException ie) {
            }
        }
        if (evt.getSource() == CPU_button) CPUDebug.ToggleVisible();
        if (evt.getSource() == RAM_button) RAMDebug.ToggleVisible();
        if (evt.getSource() == MEM_button) {
            progwindow.setVisible(true);
            progwindow.EnableEditor(false);
        }
        if (evt.getSource() == EE_button) EepromDebug.ToggleVisible();
        if (evt.getSource() == Step_button) {
            stepInto();
        }
        if (evt.getSource() == Stepover_button) {
            stepOver();
        }
        if (evt.getSource() == Stepout_button) {
            stepReturn();
        }
        if (evt.getSource() == Restart_button) reset();
        if (evt.getSource() == Skip_button && lst_available) {
            progwindow.EnableEditor(false);
            cpu.incrementPC();
            progwindow.GotoAddress(cpu.getPc());
        }
        if (evt.getSource() == BP_button) {
            bpwindow.ToggleVisible();
        }
        if (evt.getSource() == Stack_button) {
            stackwindow.ToggleVisible();
        }
        if (evt.getSource() == Watch_button) {
            int i = 0;
            while (i < 5) {
                if (watchers[i] == null) {
                    watchers[i] = new WatchWindow(this, progwindow.getSymbolTable(), cpu.ram);
                    break;
                } else if (watchers[i].isVisible() == false) {
                    watchers[i].setVisible(true);
                    break;
                }
                i++;
            }
        }
        if (evt.getSource() == AddBreakpoint_button) {
            bpwindow.addBreakpoint(progwindow.GetClickedAddress());
            progwindow.repaint();
        }
        if (evt.getSource() == DelBreakpoint_button) {
            bpwindow.removeBreakpoint(progwindow.GetClickedAddress());
            progwindow.repaint();
        }
        if (evt.getSource() == watchdog_button) {
            if (cpu.watchdog) {
                cpu.watchdog = false;
                watchdog_button.setIcon(watchdog_off);
            } else {
                cpu.watchdog = true;
                watchdog_button.setIcon(watchdog_on);
            }
        }
        String action = new String(evt.getActionCommand());
        if (action.equals("Run here")) {
            remove_breakpoint_when_reached = true;
            bpwindow.addBreakpoint(progwindow.GetClickedAddress());
            project.start();
        }
        if (action.equals("Set PC here")) {
            System.out.println(progwindow.GetClickedAddress());
            cpu.setPc(progwindow.GetClickedAddress());
            progwindow.repaint();
        }
        if (action.equals("Toggle breakpoint")) {
            if (bpwindow.isBreakpoint(progwindow.GetClickedAddress())) bpwindow.removeBreakpoint(progwindow.GetClickedAddress()); else bpwindow.addBreakpoint(progwindow.GetClickedAddress());
        }
    }

    public void addWatchWindow(WatchWindow watch) {
        int i = 0;
        while (i < 5) {
            if (watchers[i] == null) {
                watchers[i] = watch;
                break;
            }
            i++;
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F4) {
            project.stop();
            progwindow.EnableEditor(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            stepInto();
        }
        if (e.getKeyCode() == KeyEvent.VK_F6) {
            stepOver();
        }
        if (e.getKeyCode() == KeyEvent.VK_F7) {
            stepReturn();
        }
        if (e.getKeyCode() == KeyEvent.VK_F8) project.start();
        if (e.getKeyCode() == KeyEvent.VK_F9) project.stop();
        if (e.getKeyCode() == KeyEvent.VK_F11) build();
        if (e.getKeyCode() == KeyEvent.VK_F10) reset();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void step() {
        if (!cpu.sleep && lst_available) {
            cpu.cycles++;
            if ((cpu.ram.fetch(PICCPU.OPTION_REG) & 32) == 0) cpu.timer0();
            boolean porta_change = cpu.UpdateIO(porta, 0x05);
            boolean portb_change = cpu.UpdateIO(portb, 0x06);
            if (portb_change || porta_change) {
                if (running && picwindow_update_loop == 8 && picwindow.isVisible()) {
                    picwindow_update_loop = 0;
                    picwindow.repaint();
                }
                project.postPinUpdate(this);
                cpu.io_update_has_come = false;
            }
            cpu.eeprom();
            cpu.execute();
            cpu.incrementPC();
            cpu.check_interrupts();
            cpu.watchdog();
            porta_change = cpu.UpdateIO(porta, 0x05);
            portb_change = cpu.UpdateIO(portb, 0x06);
            if (portb_change || porta_change) {
                if (running && picwindow_update_loop == 8 && picwindow.isVisible()) {
                    picwindow_update_loop = 0;
                    picwindow.repaint();
                }
                project.postPinUpdate(this);
            }
            if (running == false) {
                progwindow.GotoAddress(cpu.getPc());
                updateDebugWindows();
            }
            if (running && loop == 0) {
                updateDebugWindows();
            }
            loop++;
            picwindow_update_loop++;
            for (int i = 0; i < 5; i++) if (watchers[i] != null && watchers[i].CheckBreak()) project.stop();
            if (!cpu.twoCycleInstruction && bpwindow.isBreakpoint(cpu.getPc())) {
                if (remove_breakpoint_when_reached) {
                    remove_breakpoint_when_reached = false;
                    bpwindow.removeBreakpoint(cpu.getPc());
                }
                project.stop();
            }
        }
        if (cpu.sleep) {
            cpu.cycles++;
            progwindow.SetEnabled(false);
            cpu.UpdateIO(porta, 0x05);
            cpu.UpdateIO(portb, 0x06);
            if (cpu.check_interrupts()) cpu.sleep = false;
            cpu.watchdog();
        }
    }

    void updateDebugWindows() {
        if (RAMDebug.isVisible()) cpu.DebugFileRegisters(RAMDebug);
        if (CPUDebug.isVisible()) cpu.DebugCPURegisters(CPUDebug);
        if (EepromDebug.isVisible()) cpu.DebugEEPROM(EepromDebug);
        for (int i = 0; i < 5; i++) if (watchers[i] != null) watchers[i].Update();
        if (picwindow.isVisible()) picwindow.repaint();
    }

    public void reset() {
        project.stop();
        if (mem.load(filename)) lst_available = true;
        progwindow.load(filename);
        int i = 0;
        while (watchers[i] != null) watchers[i++].updateSymbolTable(progwindow.getSymbolTable());
        cpu.reset();
        cpu.UpdateIO(porta, 0x05);
        cpu.UpdateIO(portb, 0x06);
        updateDebugWindows();
        progwindow.GotoAddress(cpu.getPc());
    }

    private void build() {
        if (mpasm_path == null) {
            Object options[] = { "   Yes   ", "    No    ", "  Help  " };
            int result = JOptionPane.showOptionDialog(Workbench.mainframe, "PIC Development Stuido does not have an inbuilt compiler so\nyou have to download one (i.e. MPASMWIN.EXE). Click on help for more info.\nDo you want to set the path to the compiler now?", "Path to compiler executable is not specified", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (result == 0) {
                JFileChooser lstchooser = new JFileChooser();
                lstchooser.setDialogTitle("Set path to compilator executable");
                if (lstchooser.showOpenDialog(Workbench.mainframe) == JFileChooser.APPROVE_OPTION) {
                    File file = lstchooser.getSelectedFile();
                    mpasm_path = file.getPath();
                    try {
                        RandomAccessFile piccfg = new RandomAccessFile("pic.cfg", "rw");
                        piccfg.writeBytes(mpasm_path);
                        piccfg.close();
                    } catch (java.io.IOException ioe) {
                    }
                }
            }
        }
        if (mpasm_path != null) {
            progwindow.save_source(source_filename);
            try {
                Thread.sleep(100);
                Runtime rt = Runtime.getRuntime();
                String runparams[] = { mpasm_path, "-p16F84", "\"" + source_filename + "\"" };
                try {
                    rt.exec(runparams);
                    Thread.sleep(100);
                } catch (java.io.IOException ioe) {
                    System.out.println("Problem with compilation");
                }
            } catch (java.lang.InterruptedException ie) {
            }
            reset();
        }
    }

    public void ioChangeNotify() {
        if (porta.state[4] != RA4_old_state) {
            RA4_old_state = porta.state[4];
            cpu.RA4_change_occured(porta.state[4]);
        }
        if (portb.state[0] != RB0_old_state) {
            RB0_old_state = portb.state[0];
            cpu.RB0_change_occured(portb.state[0]);
        }
        if (portb.state[4] != RB4_old_state) {
            RB4_old_state = portb.state[4];
            cpu.RB_port_change();
        }
        if (portb.state[5] != RB5_old_state) {
            RB5_old_state = portb.state[5];
            cpu.RB_port_change();
        }
        if (portb.state[6] != RB6_old_state) {
            RB6_old_state = portb.state[6];
            cpu.RB_port_change();
        }
        if (portb.state[7] != RB7_old_state) {
            RB7_old_state = portb.state[7];
            cpu.RB_port_change();
        }
        cpu.io_update_has_come = true;
    }

    public void setInput(int index, boolean state) {
        cpu.io_update_has_come = true;
        if (index < 8) portb.setInput(index, state); else porta.setInput(index - 8, state);
    }

    public boolean getOutput(int index) {
        if (index < 8) return portb.getOutput(index);
        return porta.getOutput(index - 8);
    }

    public String getName() {
        return name;
    }

    public String[] getPinNames() {
        return pin_names;
    }

    public void clock(int project_clock) {
        step();
    }

    public void starting() {
        progwindow.SetEnabled(false);
        progwindow.EnableEditor(false);
        running = true;
    }

    public void stopping() {
        running = false;
        progwindow.SetEnabled(true);
        updateDebugWindows();
        progwindow.GotoAddress(cpu.getPc());
    }

    private void stepInto() {
        if (lst_available && !project.isRunning()) {
            progwindow.EnableEditor(false);
            project.clock();
        }
    }

    private void stepOver() {
        if (lst_available && !project.isRunning()) {
            progwindow.EnableEditor(false);
            if (cpu.isCallInstruction(cpu.getPc())) {
                remove_breakpoint_when_reached = true;
                bpwindow.addBreakpoint(cpu.getPc() + 1);
                project.start();
            } else {
                project.clock();
            }
        }
    }

    private void stepReturn() {
        if (lst_available && !project.isRunning()) {
            progwindow.EnableEditor(false);
            if (cpu.stack.size() > 0) {
                int adr = ((Integer) cpu.stack.get(0)).intValue();
                bpwindow.addBreakpoint(adr);
                remove_breakpoint_when_reached = true;
                project.start();
            } else project.clock();
        }
    }
}

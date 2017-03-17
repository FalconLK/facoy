import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.lang.*;
import java.text.ParseException;

class ProjectProperties extends JFrame {

    public String getProjName() {
        return m_projName;
    }

    public String getProjRoot() {
        return m_projRoot;
    }

    public String getSrcRoot() {
        return m_srcRoot;
    }

    public String getLocRoot() {
        return m_locRoot;
    }

    public String getGlosRoot() {
        return m_glosRoot;
    }

    public String getTMRoot() {
        return m_tmRoot;
    }

    public String getInternal() {
        return m_projInternal;
    }

    public String getSrcLang() {
        return m_srcLang;
    }

    public String getLocLang() {
        return m_locLang;
    }

    public void reset() {
        m_projFile = "";
        m_projName = "";
        m_projRoot = "";
        m_projInternal = "";
        m_srcRoot = "";
        m_locRoot = "";
        m_glosRoot = "";
        m_tmRoot = "";
        m_srcLang = "";
        m_locLang = "";
    }

    class OTFileFilter extends javax.swing.filechooser.FileFilter {

        public String getDescription() {
            return "OmegaT project files";
        }

        public boolean accept(File f) {
            if (f.getName().endsWith(OConsts.PROJ_EXTENSION) == true) return true; else if (f.isDirectory() == true) return true; else return false;
        }
    }

    public boolean loadExisting() throws IOException, InterruptedIOException {
        reset();
        String curDir = CommandThread.core.getPreference(OConsts.PREF_CUR_DIR);
        JFileChooser pfc = new JFileChooser(curDir);
        pfc.setFileFilter(new OTFileFilter());
        pfc.setFileView(new ProjectFileView());
        int res = pfc.showOpenDialog(this);
        if (res == JFileChooser.CANCEL_OPTION) throw new InterruptedIOException();
        CommandThread.core.setPreference(OConsts.PREF_CUR_DIR, pfc.getSelectedFile().getParent());
        try {
            ProjFileReader pfr = new ProjFileReader();
            m_projName = pfc.getCurrentDirectory().getName();
            m_projRoot = pfc.getCurrentDirectory().getAbsolutePath() + File.separator;
            pfr.loadProjectFile(m_projRoot + OConsts.PROJ_FILENAME);
            m_srcRoot = pfr.getSource();
            m_locRoot = pfr.getTarget();
            m_glosRoot = pfr.getGlossary();
            m_tmRoot = pfr.getTM();
            m_projInternal = m_projRoot + OConsts.DEFAULT_INTERNAL + File.separator;
            m_srcLang = pfr.getSourceLang();
            m_locLang = pfr.getTargetLang();
            CommandThread.core.setPreference(OConsts.PREF_SRCLANG, m_srcLang);
            CommandThread.core.setPreference(OConsts.PREF_LOCLANG, m_locLang);
            m_projFile = m_projRoot + OConsts.PROJ_FILENAME;
            res = verifyProject();
            if (res != 0) {
                MNewProject prj = new MNewProject(this, m_projFile, res);
                boolean abort = false;
                while (true) {
                    prj.show();
                    if (m_dialogOK == false) {
                        abort = true;
                        break;
                    }
                    if ((res = verifyProject()) != 0) {
                        prj.setMessageCode(res);
                    } else {
                        buildProjFile();
                        break;
                    }
                }
                prj.dispose();
                if (abort == true) {
                    reset();
                    return false;
                }
            }
            return true;
        } catch (ParseException e) {
            reset();
            throw new IOException("Unable to read project file\n" + e);
        }
    }

    protected int verifyProject() throws IOException {
        int verificationCode = 0;
        File src = new File(m_srcRoot);
        File loc = new File(m_locRoot);
        File gls = new File(m_glosRoot);
        File tmx = new File(m_tmRoot);
        if (verifyLangCodes() == false) return 1;
        if (src.exists() && loc.exists() && gls.exists() && tmx.exists()) return 0;
        return 2;
    }

    protected boolean verifyLangCodes() {
        if (verifySingleLangCode(m_srcLang) == false) return false;
        if (verifySingleLangCode(m_locLang) == false) return false;
        return true;
    }

    public static boolean verifySingleLangCode(String code) {
        if (code.length() == 2) {
            if (Character.isLetter(code.charAt(0)) && Character.isLetter(code.charAt(1))) {
                return true;
            }
        } else if (code.length() == 5) {
            if (Character.isLetter(code.charAt(0)) && Character.isLetter(code.charAt(1)) && Character.isLetter(code.charAt(3)) && Character.isLetter(code.charAt(4))) {
                char c = code.charAt(2);
                if ((c == '-') || (c == '_')) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean createNew() {
        MNewProject newProj = new MNewProject(this, null, 0);
        if (newProj.dialogCancelled()) {
            newProj.dispose();
            return false;
        }
        newProj.show();
        newProj.dispose();
        return m_dialogOK;
    }

    public void buildProjFile() throws IOException {
        ProjFileReader pfr = new ProjFileReader();
        pfr.setTarget(m_locRoot);
        pfr.setSource(m_srcRoot);
        pfr.setTM(m_tmRoot);
        pfr.setGlossary(m_glosRoot);
        pfr.setSourceLang(m_srcLang);
        pfr.setTargetLang(m_locLang);
        pfr.writeProjectFile(m_projFile);
    }

    class MNewProject extends JDialog {

        public MNewProject(JFrame par, String projFileName, int msg) {
            super(par, true);
            m_dialogOK = false;
            setSize(650, 500);
            if (projFileName == null) reset();
            m_message = msg;
            if (projFileName == null) {
                m_srcLang = CommandThread.core.getPreference(OConsts.PREF_SRCLANG);
                m_locLang = CommandThread.core.getPreference(OConsts.PREF_LOCLANG);
                if (m_srcLang.equals("")) m_srcLang = "EN-US";
                if (m_locLang.equals("")) m_locLang = "ES";
            }
            m_browseTarget = 0;
            m_messageLabel = new JLabel();
            Box bMes = Box.createHorizontalBox();
            bMes.add(m_messageLabel);
            bMes.add(Box.createHorizontalGlue());
            m_srcRootLabel = new JLabel();
            Box bSrc = Box.createHorizontalBox();
            bSrc.add(m_srcRootLabel);
            bSrc.add(Box.createHorizontalGlue());
            m_srcBrowse = new JButton();
            bSrc.add(m_srcBrowse);
            m_srcRootField = new JTextField();
            m_srcRootField.setEditable(false);
            m_locRootLabel = new JLabel();
            Box bLoc = Box.createHorizontalBox();
            bLoc.add(m_locRootLabel);
            bLoc.add(Box.createHorizontalGlue());
            m_locBrowse = new JButton();
            bLoc.add(m_locBrowse);
            m_locRootField = new JTextField();
            m_locRootField.setEditable(false);
            m_glosRootLabel = new JLabel();
            Box bGlos = Box.createHorizontalBox();
            bGlos.add(m_glosRootLabel);
            bGlos.add(Box.createHorizontalGlue());
            m_glosBrowse = new JButton();
            bGlos.add(m_glosBrowse);
            m_glosRootField = new JTextField();
            m_glosRootField.setEditable(false);
            m_tmRootLabel = new JLabel();
            Box bTM = Box.createHorizontalBox();
            bTM.add(m_tmRootLabel);
            bTM.add(Box.createHorizontalGlue());
            m_tmBrowse = new JButton();
            bTM.add(m_tmBrowse);
            m_tmRootField = new JTextField();
            m_tmRootField.setEditable(false);
            m_srcLangLabel = new JLabel();
            Box bSL = Box.createHorizontalBox();
            bSL.add(m_srcLangLabel);
            bSL.add(Box.createHorizontalGlue());
            m_srcLangField = new JTextField();
            m_srcLangField.setText(m_srcLang);
            m_locLangLabel = new JLabel();
            Box bLL = Box.createHorizontalBox();
            bLL.add(m_locLangLabel);
            bLL.add(Box.createHorizontalGlue());
            m_locLangField = new JTextField();
            m_locLangField.setText(m_locLang);
            m_okButton = new JButton();
            m_cancelButton = new JButton();
            Box b = Box.createVerticalBox();
            b.add(bMes);
            b.add(bSrc);
            b.add(m_srcRootField);
            b.add(bLoc);
            b.add(m_locRootField);
            b.add(bGlos);
            b.add(m_glosRootField);
            b.add(bTM);
            b.add(m_tmRootField);
            b.add(bSL);
            b.add(m_srcLangField);
            b.add(bLL);
            b.add(m_locLangField);
            getContentPane().add(b, "North");
            Box b2 = Box.createHorizontalBox();
            b2.add(Box.createHorizontalGlue());
            b2.add(m_cancelButton);
            b2.add(Box.createHorizontalStrut(5));
            b2.add(m_okButton);
            getContentPane().add(b2, "South");
            m_okButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    doOK();
                }
            });
            m_cancelButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    doCancel();
                }
            });
            m_srcBrowse.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    m_browseTarget = 1;
                    doBrowseDirectoy();
                }
            });
            m_locBrowse.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    m_browseTarget = 2;
                    doBrowseDirectoy();
                }
            });
            m_glosBrowse.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    m_browseTarget = 3;
                    doBrowseDirectoy();
                }
            });
            m_tmBrowse.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    m_browseTarget = 4;
                    doBrowseDirectoy();
                }
            });
            if (projFileName == null) {
                NewDirectoryChooser ndc = new NewDirectoryChooser();
                String label;
                label = OStrings.PP_SAVE_PROJECT_FILE;
                ndc.setDialogTitle(label);
                String curDir = CommandThread.core.getPreference(OConsts.PREF_CUR_DIR);
                if (curDir != null) {
                    File dir = new File(curDir);
                    if (dir.exists() && dir.isDirectory()) {
                        ndc.setCurrentDirectory(dir);
                    }
                }
                int val = ndc.showSaveDialog(this);
                if (val != JFileChooser.APPROVE_OPTION) {
                    m_dialogCancelled = true;
                    return;
                }
                m_projRoot = ndc.getSelectedFile().getAbsolutePath() + File.separator;
                m_projFile = m_projRoot + OConsts.PROJ_FILENAME;
                CommandThread.core.setPreference(OConsts.PREF_CUR_DIR, ndc.getSelectedFile().getParent());
                m_projName = m_projFile.substring(m_projRoot.length());
                m_srcRoot = m_projRoot + OConsts.DEFAULT_SRC + File.separator;
                m_locRoot = m_projRoot + OConsts.DEFAULT_LOC + File.separator;
                m_glosRoot = m_projRoot + OConsts.DEFAULT_GLOS + File.separator;
                m_tmRoot = m_projRoot + OConsts.DEFAULT_TM + File.separator;
            } else {
                m_projFile = projFileName;
                m_projRoot = m_projFile.substring(0, m_projFile.lastIndexOf(File.separator));
            }
            m_projInternal = m_projRoot + OConsts.DEFAULT_INTERNAL + File.separator;
            m_srcRootField.setText(m_srcRoot);
            m_locRootField.setText(m_locRoot);
            m_glosRootField.setText(m_glosRoot);
            m_tmRootField.setText(m_tmRoot);
            m_srcLangField.setText(m_srcLang);
            m_locLangField.setText(m_locLang);
            updateUIText();
        }

        private void doBrowseDirectoy() {
            String title = "";
            switch(m_browseTarget) {
                case 1:
                    title = OStrings.PP_BROWSE_TITLE_SOURCE;
                    break;
                case 2:
                    title = OStrings.PP_BROWSE_TITLE_TARGET;
                    break;
                case 3:
                    title = OStrings.PP_BROWSE_TITLE_GLOS;
                    break;
                case 4:
                    title = OStrings.PP_BROWSE_TITLE_TM;
                    break;
                default:
                    return;
            }
            ;
            JFileChooser browser = new JFileChooser();
            String str = OStrings.PP_BUTTON_SELECT;
            browser.setApproveButtonText(str);
            browser.setDialogTitle(title);
            browser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String curDir = "";
            switch(m_browseTarget) {
                case 1:
                    curDir = CommandThread.core.getPreference(OConsts.PREF_SRC_DIR);
                    break;
                case 2:
                    curDir = CommandThread.core.getPreference(OConsts.PREF_LOC_DIR);
                    break;
                case 3:
                    curDir = CommandThread.core.getPreference(OConsts.PREF_GLOS_DIR);
                    break;
                case 4:
                    curDir = CommandThread.core.getPreference(OConsts.PREF_TM_DIR);
                    break;
            }
            ;
            if (curDir.equals("")) curDir = CommandThread.core.getPreference(OConsts.PREF_CUR_DIR);
            if (curDir.equals("") == false) {
                File dir = new File(curDir);
                if (dir.exists() && dir.isDirectory()) {
                    browser.setCurrentDirectory(dir);
                }
            }
            int res = browser.showOpenDialog(this);
            File dir = browser.getSelectedFile();
            if (dir == null) return;
            str = dir.getAbsolutePath() + File.separator;
            switch(m_browseTarget) {
                case 1:
                    CommandThread.core.setPreference(OConsts.PREF_SRC_DIR, browser.getSelectedFile().getParent());
                    m_srcRoot = str;
                    m_srcRootField.setText(m_srcRoot);
                    break;
                case 2:
                    CommandThread.core.setPreference(OConsts.PREF_LOC_DIR, browser.getSelectedFile().getParent());
                    m_locRoot = str;
                    m_locRootField.setText(m_locRoot);
                    break;
                case 3:
                    CommandThread.core.setPreference(OConsts.PREF_GLOS_DIR, browser.getSelectedFile().getParent());
                    m_glosRoot = str;
                    m_glosRootField.setText(m_glosRoot);
                    break;
                case 4:
                    CommandThread.core.setPreference(OConsts.PREF_TM_DIR, browser.getSelectedFile().getParent());
                    m_tmRoot = str;
                    m_tmRootField.setText(m_tmRoot);
                    break;
            }
            ;
        }

        private void doOK() {
            String str;
            m_srcRoot = m_srcRootField.getText();
            if (m_srcRoot.endsWith(File.separator) == false) m_srcRoot += File.separator;
            m_locRoot = m_locRootField.getText();
            if (m_locRoot.endsWith(File.separator) == false) m_locRoot += File.separator;
            m_glosRoot = m_glosRootField.getText();
            if (m_glosRoot.endsWith(File.separator) == false) m_glosRoot += File.separator;
            m_tmRoot = m_tmRootField.getText();
            if (m_tmRoot.endsWith(File.separator) == false) m_tmRoot += File.separator;
            m_srcLang = m_srcLangField.getText();
            m_locLang = m_locLangField.getText();
            if (verifyLangCodes() == false) {
                setMessageCode(1);
                return;
            }
            m_dialogOK = true;
            hide();
            m_browseTarget = 0;
        }

        private void doCancel() {
            m_dialogOK = false;
            hide();
        }

        public void setMessageCode(int n) {
            m_message = n;
        }

        public void updateUIText() {
            String str;
            str = OStrings.PP_CREATE_PROJ;
            setTitle(str);
            if (m_message == 0) {
                str = OStrings.PP_MESSAGE_CONFIGPROJ;
                m_messageLabel.setText(str);
            } else if (m_message == 1) {
                str = OStrings.PP_MESSAGE_BADLANG;
                m_messageLabel.setText(str);
            } else if (m_message == 2) {
                str = OStrings.PP_MESSAGE_BADPROJ;
                m_messageLabel.setText(str);
            }
            str = OStrings.PP_SRC_ROOT;
            m_srcRootLabel.setText(str);
            str = OStrings.PP_BUTTON_BROWSE_SRC;
            m_srcBrowse.setText(str);
            str = OStrings.PP_LOC_ROOT;
            m_locRootLabel.setText(str);
            str = OStrings.PP_BUTTON_BROWSE_TAR;
            m_locBrowse.setText(str);
            str = OStrings.PP_GLOS_ROOT;
            m_glosRootLabel.setText(str);
            str = OStrings.PP_BUTTON_BROWSE_GL;
            m_glosBrowse.setText(str);
            str = OStrings.PP_TM_ROOT;
            m_tmRootLabel.setText(str);
            str = OStrings.PP_BUTTON_BROWSE_TM;
            m_tmBrowse.setText(str);
            str = OStrings.PP_SRC_LANG;
            m_srcLangLabel.setText(str);
            str = OStrings.PP_LOC_LANG;
            m_locLangLabel.setText(str);
            Dimension orig = m_srcBrowse.getPreferredSize();
            Dimension tmp = m_locBrowse.getPreferredSize();
            orig.width = Math.max(orig.width, tmp.width);
            orig.height = Math.max(orig.height, tmp.height);
            tmp = m_glosBrowse.getPreferredSize();
            orig.width = Math.max(orig.width, tmp.width);
            orig.height = Math.max(orig.height, tmp.height);
            tmp = m_tmBrowse.getPreferredSize();
            orig.width = Math.max(orig.width, tmp.width);
            orig.height = Math.max(orig.height, tmp.height);
            m_srcBrowse.setPreferredSize(orig);
            m_locBrowse.setPreferredSize(orig);
            m_glosBrowse.setPreferredSize(orig);
            m_tmBrowse.setPreferredSize(orig);
            m_okButton.setText(OStrings.PP_BUTTON_OK);
            m_cancelButton.setText(OStrings.PP_BUTTON_CANCEL);
        }

        public boolean dialogCancelled() {
            return m_dialogCancelled;
        }

        private boolean m_dialogCancelled;

        private int m_browseTarget;

        public JLabel m_messageLabel;

        public int m_message;

        public JLabel m_srcRootLabel;

        public JTextField m_srcRootField;

        public JButton m_srcBrowse;

        public JLabel m_locRootLabel;

        public JTextField m_locRootField;

        public JButton m_locBrowse;

        public JLabel m_glosRootLabel;

        public JTextField m_glosRootField;

        public JButton m_glosBrowse;

        public JLabel m_tmRootLabel;

        public JTextField m_tmRootField;

        public JButton m_tmBrowse;

        public JLabel m_srcLangLabel;

        public JTextField m_srcLangField;

        public JLabel m_locLangLabel;

        public JTextField m_locLangField;

        public JButton m_okButton;

        public JButton m_cancelButton;
    }

    private String m_projName;

    private String m_projFile;

    private String m_projRoot;

    private String m_projInternal;

    private String m_srcRoot;

    private String m_locRoot;

    private String m_glosRoot;

    private String m_tmRoot;

    private String m_srcLang;

    private String m_locLang;

    protected boolean m_dialogOK;

    public static void main(String[] s) {
        ProjectProperties pp = new ProjectProperties();
        pp.createNew();
    }
}

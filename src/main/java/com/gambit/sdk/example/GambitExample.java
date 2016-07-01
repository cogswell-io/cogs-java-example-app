package com.gambit.sdk.example;

import com.gambit.sdk.GambitPushService;
import com.gambit.sdk.GambitSDKService;
import com.gambit.sdk.example.namespace.GambitRequestNamespace;
import com.gambit.sdk.example.namespace.GambitResponseNamespace;
import com.gambit.sdk.example.table.event.*;
import com.gambit.sdk.example.table.messages.GambitMessagesTableModel;
import com.gambit.sdk.example.table.messages.GambitSavedMessage;
import com.gambit.sdk.example.table.subscriptions.*;
import com.gambit.sdk.message.GambitMessage;
import com.gambit.sdk.request.GambitRequestEvent;
import com.gambit.sdk.response.GambitResponseEvent;
import com.gambit.tools.sdk.GambitResponse;
import com.gambit.tools.sdk.GambitToolsService;
import com.gambit.tools.sdk.request.GambitRequestClientSecret;
import com.gambit.tools.sdk.request.GambitRequestRandomUUID;
import com.gambit.tools.sdk.response.GambitResponseClientSecret;
import com.gambit.tools.sdk.response.GambitResponseRandomUUID;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.EdgedBalloonStyle;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.lang.Thread.UncaughtExceptionHandler;


public class GambitExample implements GambitPushService.GambitMessageListener {
    private JTabbedPane tabs;
    private JPanel panel;
    private JCheckBox setupCheckSave;
    private JTextField setupInputSecretKey;
    private JTextField setupInputAccessKey;
    private JTextPane generateTextPanel;
    private JButton generateButton;
    private JTextField generateResultClientSalt;
    private JButton generateButtonCopyClientSalt;
    private JTextField generateResultClientSecret;
    private JButton generateButtonCopyClientSecret;
    private JTextArea generateResultResponse;
    private JPanel generateResultPanel;
    private JTextField sendEventInputClientSalt;
    private JTextField sendEventInputClientSecret;
    private JButton sendEventButtonCopy;
    private JTextField sendEventInputTimestamp;
    private JButton sendEventButtonHelp;
    private JTextField sendEventInputNamespace;
    private JTextField sendEventInputEventName;
    private JButton sendEventButtonPopulate;
    private JButton sendEventButtonHelpPopulate;
    private GambitAttributeTable sendEventAttrTable;
    private JTextArea sendEventResponse;
    private JButton sendEventButton;
    private JTable receivedEventsTable;
    private JTextPane receivedEventsResponse;
    private JTextPane thisServiceIsAvailableTextPane;
    private JButton randomUUIDButtonGenerate;
    private JTextField randomUUIDInputResult;
    private JButton randomUUIDButtonCopyResult;
    private JTextArea randomUUIDResponse;
    private JButton randomUUIDButtonCopyResponse;
    private JPanel randomUUIDPanelResult;
    private JCheckBox sendEventCheckCurrentTimestamp;
    protected JComboBox sendEventComboDebug;
    protected JButton sendEventButtonHelpDebug;
    protected JTextField sendEventInputClientSaltSub;
    protected JTextField sendEventInputClientSecretSub;
    protected JTextField sendEventInputTimestampSub;
    protected JTextField sendEventInputNamespaceSub;
    protected JTextField sendEventInputEventNameSub;
    protected JCheckBox sendEventCheckCurrentTimestampSub;
    protected JComboBox sendEventComboDebugSub;
    protected JButton loadAttributesButton;
    protected GambitAttributeTable attributeSubscriptionTable;
    protected JButton saveSubscriptionButton;
    protected JTable sendSubscribtionTable;
    protected JButton sendEventButtonCopySub;
    protected JButton sendEventButtonHelpDebugSub;
    protected JTextField topicDescription;
    protected JLabel imageLogo;
    protected JLabel aboutMessage;
    protected JTextArea subscriptionResponse;
    protected JButton clearReceivedMessage;
    protected JButton copyFilePathButton;
    protected JLabel checkRememberFilePathLabel;
    protected JButton sendEventSubscriptionButtonHelp;

    static {
        final InputStream inputStream = GambitExample.class.getResourceAsStream("/logging.properties");

        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    "Could not load default logging.properties file: " + e.getMessage(), e);
        }
    }

    public static final String API_HOSTNAME = ((System.getenv("COGS_BASE_API_URL") == null) ? "api.cogswell.io" : System.getenv("COGS_BASE_API_URL"));

    protected static final String NON_DEFAULT_URL_TITLE = ((System.getenv("COGS_BASE_API_URL") == null) ? "" : "[" + System.getenv("COGS_BASE_API_URL") + "] ");

    public static final Logger getLogger() {
        //LogManager.getLogManager()

        Logger logger = Logger.getLogger(GambitExample.class.getName());

        return logger;
    }

    /**
     * Thread loop
     */
    protected final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Flag that determines whether we should read and save configuration. controller by setupCheckSave
     */
    protected boolean saveConfig = false;

    /**
     * Gambit access key, from config or entered in setupInputAccessKey
     */
    protected String accessKey;

    /**
     * Gambit secret key, from config or entered in setupInputSecretKey
     */
    protected String secretKey;

    /**
     * Flag that indicates whether to use current time stamp or read the value of
     * sendEventInputTimestamp. Controlled by sendEventCheckCurrentTimestamp
     */
    protected boolean sendEventWithCurrentTimestamp = false;

    protected boolean sendSubscribptiontWithCurrentTimestamp = false;

    /**
     * Gambit client salt, from config or entered in sendEventInputClientSalt
     */
    protected String sendEventClientSalt;

    /**
     * Gambit client salt, from config or entered in sendEventInputClientSecret
     */
    protected String sendEventClientSecret;

    /**
     * Event name, from config or entered in sendEventInputEventName
     */
    protected String sendEventEventName;

    /**
     * Event namespace name, from config or entered in sendEventInputNamespace
     */
    protected String sendEventNamespace;

    /**
     * Subscription name space
     */
    protected String sendSubscriptionNamespace;

    /**
     * Subscription topic name
     */
    protected String subscriptionTopicName;

    /**
     * Debug Directive value, from config or entered in sendEventComboDebug
     */
    protected String sendEventDebugDirective;


    /**
     * Manage the table data for event namespace attributes
     */
    protected GambitAttributeTableModel sendEventAttrTableModel;


    /**
     * Manage the table data for event namespace attributes on the subscriptions tab
     */
    protected GambitAttributeTableModel attributeSubscriptionTableModel;


    /**
     * Manage the table data for event topic subscriptions
     */
    protected GambitSubscriptionTableModel sendEventSubscriptionsTableModel;

    /**
     * Custom Helper Model that supplies the GambitAttributeTableModel
     * with capability of defining different TableCellEditor instances for different rows.
     * <p/>
     * Used on send event tab.
     */
    protected GambitAttributeRowEditorModel sendEventAttrRowEditorModel;

    /**
     * Custom Helper Model that supplies the GambitAttributeTableModel
     * with capability of defining different TableCellEditor instances for different rows.
     * <p/>
     * Used on subscriptions tab.
     */
    protected GambitAttributeRowEditorModel attributeSubscriptionRowEditorModel;

    /**
     * Manage the table data for the received messages table
     */
    protected GambitMessagesTableModel receivedMessagesTableModel;

    /**
     * Hold the received messages data after being read from the configuration
     */
    protected ArrayList<GambitSavedMessage> receivedMessagesArray;

    /**
     * Collection of push services
     */
    protected Map<Integer, GambitPushService> pushServices = new HashMap<>();

    /**
     * Collection of push service configurations
     */
    protected Map<String, GambitSavedSubscription> topicSubscriptionData = new HashMap<>();

    /**
     * The directory to store the configuration file
     */
    protected String tempDir = System.getProperty("java.io.tmpdir");

    /**
     * The filename to store the configuration
     */
    protected String filePath = tempDir + File.separator + "gambit" + File.separator + "config.json";

    /**
     * We start the app, init it, let it loose, and then add few very important lines of code, that everyone
     * using this SDK should also use. That is, properly shutting down the SDK and ToolsSDK run loops.
     *
     * @param args command line arguments, unused
     */
    public static void main(String[] args) {
        class LoggingExceptionHandler implements UncaughtExceptionHandler {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                getLogger().log(Level.SEVERE,
                        "Unhandled exception in Thread-" + Thread.currentThread().getId() +
                        " '" + Thread.currentThread().getName() + "' : " + e.getMessage(), e);
            }
        }

        Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler());
        System.setProperty("sun.awt.exception.handler", LoggingExceptionHandler.class.getName());

        JFrame frame = new JFrame(NON_DEFAULT_URL_TITLE + "Cogs Demo");

        GambitExample app = new GambitExample();

        GambitToolsService.getInstance().setEndpointUrl(API_HOSTNAME);
        GambitSDKService.getInstance().setEndpointUrl(API_HOSTNAME);

        frame.setContentPane(app.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        app.init();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                app.executor.shutdown();
                GambitToolsService.getInstance().finish();
                GambitSDKService.getInstance().finish();
            }
        });
    }


    /**
     * Application constructor that executes IJ's auto-generated UI and reads the config file.
     */
    public GambitExample() {

        //do not touch, this initializes the interface; IntelliJ IDEA stuff
        $$$setupUI$$$();

        //read the configuration
        readConfig();
    }

    /**
     * Set all view states and attach all view listeners.
     */
    public void init() {

        toggleConfigDependantElements();

        setupCheckSave.setSelected(saveConfig);
        setupInputAccessKey.setText(accessKey);
        setupInputSecretKey.setText(secretKey);

        generateResultClientSalt.setText(sendEventClientSalt);
        generateResultClientSecret.setText(sendEventClientSecret);

        sendEventInputClientSalt.setText(sendEventClientSalt);
        sendEventInputClientSecret.setText(sendEventClientSecret);
        sendEventInputNamespace.setText(sendEventNamespace);
        sendEventInputEventName.setText(sendEventEventName);

        sendEventInputClientSaltSub.setText(sendEventClientSalt);
        sendEventInputClientSecretSub.setText(sendEventClientSecret);

        //Call push services from saved subscriptions
        for (GambitSavedSubscription item : topicSubscriptionData.values()) {
            try {
                callPushServiceFromSavedSubscription(item);
            } catch (Throwable error) {
                String description = "unknown subscription";

                if (item != null && item.getSubscribtion() != null &&
                    item.getSubscribtion().getTopicDescription() != null)
                {
                    description = " subscription '" + item.getSubscribtion().getTopicDescription() + "'";
                }

                getLogger().log(Level.SEVERE, "Error resuming " + description + " : " + error.getMessage(), error);
            }
        }

        String checkSaveText =
                "<html> Remember keys. Note: Only use on a secure system. " +
                        "Stored as plain text at: " +
                        filePath +
                        "</html>";
        setupCheckSave.setText(checkSaveText);

        copyFilePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(filePath);
            }
        });

        setupCheckSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (setupCheckSave.isSelected()) {
                    saveConfig = true;
                } else {
                    saveConfig = false;
                    clearConfig();
                }
                saveConfig();
            }
        });

        setupInputAccessKey.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                accessKey = setupInputAccessKey.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                accessKey = setupInputAccessKey.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                accessKey = setupInputAccessKey.getText();
                saveConfig();
                toggleConfigDependantElements();
            }
        });

        setupInputSecretKey.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                secretKey = setupInputSecretKey.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                secretKey = setupInputSecretKey.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                secretKey = setupInputSecretKey.getText();
                saveConfig();
                toggleConfigDependantElements();
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        Future<GambitResponse> future = GambitToolsService.getInstance()
                                .requestClientSecret(
                                        new GambitRequestClientSecret.Builder(
                                                accessKey,
                                                secretKey
                                        )
                                );

                        GambitResponseClientSecret response;
                        try {
                            response = (GambitResponseClientSecret) future.get();

                            generateResultClientSalt.setText(response.getClientSalt());
                            generateResultClientSecret.setText(response.getClientSecret());

                            sendEventClientSalt = response.getClientSalt();
                            sendEventClientSecret = response.getClientSecret();
                            saveConfig();

                            generateResultResponse.setWrapStyleWord(false);
                            generateResultResponse.setLineWrap(true);

                            try {
                                JSONObject responseBodyJSON = new JSONObject(response.getRawBody());
                                generateResultResponse.setText(responseBodyJSON.toString(4));
                            } catch (JSONException e) {
                                getLogger().log(Level.WARNING,
                                        "Error parsing the client secret response: " + e.getMessage(), e);
                                generateResultResponse.setText(response.getRawBody());
                            }

                            generateResultPanel.setVisible(true);
                        } catch (Throwable ex) {
                            getLogger().log(Level.WARNING,
                                    "Error processing the client secret response: " + ex.getMessage(), ex);

                            StringWriter sw = new StringWriter();
                            ex.printStackTrace(new PrintWriter(sw));
                            String stackTrace = sw.toString();

                            generateResultResponse.setText(stackTrace);
                        }

                        return null;
                    }
                };

                try {
                    job.execute();
                } catch (Throwable ex) {
                    getLogger().log(Level.SEVERE,
                            "Error executing client secret request: " + ex.getMessage(), ex);

                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    String stackTrace = sw.toString();

                    generateResultResponse.setText(stackTrace);
                }

            }
        });

        String messageEventButtonHelp = "<html>Must be formatted in ISO-8601 format:<br/>YYYY-MM-DDThh:mm:ssTZD<br/><br/>Example:<br/>2016-01-07T22:23:24+00:00</html>";
        createBaloonMessage(sendEventButtonHelp,
                messageEventButtonHelp);
        createBaloonMessage(sendEventButtonHelpPopulate,
                "<html>Namespace, as well as secret key and access key from the setup tab are required to use this.\n" +
                        "<br/>\n" +
                        "<br/>\n" +
                        "Will populate the below table with your defined schema from the argument namespace.\n" +
                        "<br/>\n" +
                        "<br/>\n" +
                        "If the table is already populated:\n" +
                        "<br/>\n" +
                        "* New attributes not already existing in the table are added as new rows.\n" +
                        "<br/>\n" +
                        "* Attribute rows in the table which do not match the schema are removed.\n" +
                        "<br/>\n" +
                        "* Attribute rows which exist in the table and match existing attributes defined in the schema\n" +
                        "will be left alone and their values preserved.<html>");
        String messageEventDebug = "https://aviatainc.atlassian.net/wiki/display/GAM/Debug+Directives\n" +
                "for info on what each of these does.";
        createBaloonMessage(sendEventButtonHelpDebug, messageEventDebug);


        sendEventInputClientSalt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sendEventClientSalt = sendEventInputClientSalt.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendEventClientSalt = sendEventInputClientSalt.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendEventClientSalt = sendEventInputClientSalt.getText();
                saveConfig();
                toggleConfigDependantElements();
            }
        });

        sendEventInputClientSecret.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sendEventClientSecret = sendEventInputClientSecret.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendEventClientSecret = sendEventInputClientSecret.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendEventClientSecret = sendEventInputClientSecret.getText();
                saveConfig();
                toggleConfigDependantElements();
            }
        });

        sendEventInputNamespace.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sendEventNamespace = sendEventInputNamespace.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendEventNamespace = sendEventInputNamespace.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendEventNamespace = sendEventInputNamespace.getText();
                saveConfig();
                toggleConfigDependantElements();
            }
        });

        sendEventInputEventName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sendEventEventName = sendEventInputEventName.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendEventEventName = sendEventInputEventName.getText();
                saveConfig();
                toggleConfigDependantElements();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendEventEventName = sendEventInputEventName.getText();
                saveConfig();
                toggleConfigDependantElements();
            }
        });

        sendEventComboDebug.addItem(new String("No debug directive"));
        sendEventComboDebug.addItem(new String("echo-as-message"));
        sendEventComboDebug.addItem(new String("trigger-all-campaigns-no-delays"));

        if (sendEventDebugDirective != null) {
            sendEventComboDebug.setSelectedItem(sendEventDebugDirective);
        }

        sendEventComboDebug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendEventDebugDirective = null;

                if (sendEventComboDebug.getSelectedIndex() > 0) {
                    sendEventDebugDirective = sendEventComboDebug.getSelectedItem().toString();
                }

                saveConfig();
            }
        });

        sendEventButtonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendEventInputClientSalt.setText(generateResultClientSalt.getText());
                sendEventInputClientSecret.setText(generateResultClientSecret.getText());
            }
        });

        sendEventCheckCurrentTimestamp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                sendEventWithCurrentTimestamp = sendEventCheckCurrentTimestamp.isSelected();

                if (sendEventWithCurrentTimestamp) {
                    sendEventInputTimestamp.setEnabled(false);
                    sendEventInputTimestamp.setText("");
                } else {
                    sendEventInputTimestamp.setEnabled(true);
                }
            }
        });

        generateButtonCopyClientSalt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(generateResultClientSalt.getText());
            }
        });
        generateButtonCopyClientSecret.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(generateResultClientSecret.getText());
            }
        });

        //programmatically invoke action listener and set default state...
        if (!sendEventCheckCurrentTimestamp.isSelected()) {
            sendEventCheckCurrentTimestamp.doClick();
        }

        sendEventResponse.setWrapStyleWord(false);
        sendEventResponse.setLineWrap(true);

        sendEventAttrRowEditorModel = new GambitAttributeRowEditorModel();
        sendEventAttrTable.setRowEditorModel(sendEventAttrRowEditorModel);

        sendEventAttrTableModel = new GambitAttributeTableModel();
        sendEventAttrTableModel.setRowEditorModel(sendEventAttrRowEditorModel);

        sendEventAttrTable.setModel(sendEventAttrTableModel);

        //this trick here is to force the table to commit changes whenever the cell editor loses focus
        sendEventAttrTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        sendEventAttrTable.getTableHeader().setReorderingAllowed(false);
        sendEventAttrTable.setRowSelectionAllowed(false);
        sendEventAttrTable.setColumnSelectionAllowed(false);
        sendEventAttrTable.setRowHeight(20);

        sendEventButtonPopulate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {

                        GambitRequestNamespace.Builder builder = new GambitRequestNamespace.Builder(
                                accessKey,
                                secretKey,
                                sendEventNamespace
                        );

                        try {
                            Future<com.gambit.sdk.GambitResponse> future = executor.submit(builder.build());

                            GambitResponseNamespace response = (GambitResponseNamespace) future.get();

                            sendEventAttrTableModel.setData(response.getAttributes());

                            TimeZone tz = TimeZone.getDefault();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                            df.setTimeZone(tz);

                            String responseDateHeader = "On " + df.format(new Date()) + ":\n";

                            sendEventResponse.setText(responseDateHeader);

                            try {
                                JSONObject responseBodyJSON = new JSONObject(response.getRawBody());

                                sendEventResponse.setText(sendEventResponse.getText() + responseBodyJSON.toString(4));
                            } catch (JSONException e) {
                                getLogger().log(Level.WARNING,
                                        "Error parsing the namespace schema response: " + e.getMessage(), e);
                                sendEventResponse.setText(sendEventResponse.getText() + response.getRawBody());
                            }

                            sendEventResponse.setRows(sendEventResponse.getLineCount());

                        } catch (Throwable ex) {
                            getLogger().log(Level.WARNING,
                                    "Error processing the namespace schema response: " + ex.getMessage(), ex);

                            TimeZone tz = TimeZone.getDefault();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                            df.setTimeZone(tz);

                            String responseDateHeader = "On " + df.format(new Date()) + ":\n";

                            StringWriter sw = new StringWriter();
                            ex.printStackTrace(new PrintWriter(sw));
                            String stackTrace = sw.toString();

                            sendEventResponse.setText(responseDateHeader + stackTrace);
                            sendEventResponse.setRows(sendEventResponse.getLineCount());
                        }

                        return null;
                    }
                };

                try {
                    job.execute();
                } catch (Throwable ex) {
                    getLogger().log(Level.SEVERE,
                            "Error executing the namespace schema request: " + ex.getMessage(), ex);

                    TimeZone tz = TimeZone.getDefault();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                    df.setTimeZone(tz);

                    String responseDateHeader = "On " + df.format(new Date()) + ":\n";

                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    String stackTrace = sw.toString();

                    sendEventResponse.setText(responseDateHeader + stackTrace);
                    sendEventResponse.setRows(sendEventResponse.getLineCount());
                }
            }
        });

        sendEventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {

                        //prepare attributes

                        LinkedList<GambitAttribute> tableData = sendEventAttrTableModel.getData();
                        Iterator<GambitAttribute> tableDataIterator = tableData.iterator();

                        LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>();

                        while (tableDataIterator.hasNext()) {
                            GambitAttribute attr = tableDataIterator.next();

                            try {
                                Object value = attr.getTypeCorrectedValue();
                                if (value != null) {
                                    attributes.put(attr.getName(), value);
                                }
                            } catch (Throwable e) {
                                getLogger().log(Level.SEVERE, "Invalid value for attribute " +
                                        attr.getName() + " : " + e.getMessage(), e);
                                // TODO: throw exception?
                            }
                        }

                        GambitRequestEvent.Builder builder = new GambitRequestEvent.Builder(accessKey, sendEventClientSalt, sendEventClientSecret);
                        builder.setEventName(sendEventEventName);
                        builder.setNamespace(sendEventNamespace);
                        builder.setAttributes(attributes);

                        if (sendEventCheckCurrentTimestamp.isSelected()) {
                            builder.setTimestamp(DatatypeConverter.printDateTime(new GregorianCalendar()));
                        }

                        builder.setForwardAsMessage(true);

                        if (sendEventDebugDirective != null) {
                            builder.setDebugDirective(sendEventDebugDirective);
                        }

                        try {
                            Future<com.gambit.sdk.GambitResponse> future = GambitSDKService.getInstance().sendGambitEvent(builder);

                            GambitResponseEvent response = (GambitResponseEvent) future.get();

                            TimeZone tz = TimeZone.getDefault();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                            df.setTimeZone(tz);

                            String responseDateHeader = "On " + df.format(new Date()) + ":\n";

                            sendEventResponse.setText(responseDateHeader);

                            try {
                                JSONObject responseBodyJSON = new JSONObject(response.getRawBody());

                                sendEventResponse.setText(sendEventResponse.getText() + responseBodyJSON.toString(4));
                            } catch (JSONException e) {
                                getLogger().log(Level.WARNING,
                                        "Error parsing the event response: " + e.getMessage(), e);
                                sendEventResponse.setText(sendEventResponse.getText() + response.getRawBody());
                            }

                            sendEventResponse.setRows(sendEventResponse.getLineCount());

                        } catch (Throwable ex) {
                            getLogger().log(Level.WARNING,
                                    "Error processing the event response: " + ex.getMessage(), ex);
                            getLogger().log(Level.SEVERE, null, ex);

                            TimeZone tz = TimeZone.getDefault();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                            df.setTimeZone(tz);

                            String responseDateHeader = "On " + df.format(new Date()) + ":\n";

                            StringWriter sw = new StringWriter();
                            ex.printStackTrace(new PrintWriter(sw));
                            String stackTrace = sw.toString();

                            sendEventResponse.setText(responseDateHeader + stackTrace);
                            sendEventResponse.setRows(sendEventResponse.getLineCount());
                        }

                        return null;
                    }
                };

                try {
                    job.execute();
                } catch (Throwable ex) {
                    getLogger().log(Level.SEVERE,
                            "Error executing the event request: " + ex.getMessage(), ex);

                    TimeZone tz = TimeZone.getDefault();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                    df.setTimeZone(tz);

                    String responseDateHeader = "On " + df.format(new Date()) + ":\n";

                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    String stackTrace = sw.toString();

                    sendEventResponse.setText(responseDateHeader + stackTrace);
                    sendEventResponse.setRows(sendEventResponse.getLineCount());
                }
            }
        });


        // SUBSCRIBTION PART

        sendEventInputNamespaceSub.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sendSubscriptionNamespace = sendEventInputNamespaceSub.getText();
                manageSubscriptionNamespace();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sendSubscriptionNamespace = sendEventInputNamespaceSub.getText();
                manageSubscriptionNamespace();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sendSubscriptionNamespace = sendEventInputNamespaceSub.getText();
                manageSubscriptionNamespace();
            }
        });

        topicDescription.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                subscriptionTopicName = topicDescription.getText();
                manageSubscriptionTopic();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                subscriptionTopicName = topicDescription.getText();
                manageSubscriptionTopic();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                subscriptionTopicName = topicDescription.getText();
                manageSubscriptionTopic();
            }
        });

        sendEventButtonCopySub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendEventInputClientSaltSub.setText(generateResultClientSalt.getText());
                sendEventInputClientSecretSub.setText(generateResultClientSecret.getText());
            }
        });

        //Get attribtute from Namespace service

        attributeSubscriptionRowEditorModel = new GambitAttributeRowEditorModel();
        attributeSubscriptionTable.setRowEditorModel(attributeSubscriptionRowEditorModel);

        attributeSubscriptionTableModel = new GambitAttributeTableModel();
        attributeSubscriptionTableModel.setRowEditorModel(attributeSubscriptionRowEditorModel);

        attributeSubscriptionTable.setModel(attributeSubscriptionTableModel);

        //this trick here is to force the table to commit changes whenever the cell editor loses focus
        attributeSubscriptionTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        attributeSubscriptionTable.getTableHeader().setReorderingAllowed(false);
        attributeSubscriptionTable.setRowSelectionAllowed(false);
        attributeSubscriptionTable.setColumnSelectionAllowed(false);
        attributeSubscriptionTable.setRowHeight(20);

        loadAttributesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {

                        //clear the Topic Description, if one is already present.
                        topicDescription.setText("");
                        //enable the Namespace field if it is disabled (would be disabled if the user previously double-clicked on the topic table below to load that topic subscription.
                        sendEventInputNamespaceSub.setEnabled(true);

                        GambitRequestNamespace.Builder builder = new GambitRequestNamespace.Builder(
                                accessKey,
                                secretKey,
                                sendSubscriptionNamespace
                        );

                        try {
                            Future<com.gambit.sdk.GambitResponse> future = executor.submit(builder.build());

                            GambitResponseNamespace response = (GambitResponseNamespace) future.get();

                            ArrayList<GambitAttribute> subscriptionsAttributes = response.getAttributes();
                            attributeSubscriptionTableModel.setData(subscriptionsAttributes);

                            //enable subscription save button if exist attribute (least 1 attribute)

                            TimeZone tz = TimeZone.getDefault();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                            df.setTimeZone(tz);

                            String responseDateHeader = "On " + df.format(new Date()) + ":\n";

                        } catch (Throwable ex) {
                            //TODO check somehow -> is exist namespace !? and show popup - by specification
                            getLogger().log(Level.SEVERE,
                                    "Error loading attributes for namespace '" + sendSubscriptionNamespace +
                                    "' : " + ex.getMessage(), ex);
                        }

                        return null;
                    }
                };

                try {
                    job.execute();
                } catch (Throwable ex) {
                    getLogger().log(Level.SEVERE,
                            "Error executing attribute load job for namespace '" + sendSubscriptionNamespace +
                             "' : " + ex.getMessage(), ex);
                }
            }
        });


        sendEventSubscriptionsTableModel = new GambitSubscriptionTableModel();
        sendSubscribtionTable.setModel(sendEventSubscriptionsTableModel);

        sendSubscribtionTable.getTableHeader().setReorderingAllowed(false);
        sendSubscribtionTable.setRowSelectionAllowed(true);
        sendSubscribtionTable.setColumnSelectionAllowed(false);
        sendSubscribtionTable.setRowHeight(20);

        sendSubscribtionTable.getColumn("Actions").setCellRenderer(new GambitSubscriptionButtonRenderer());
        sendSubscribtionTable.getColumn("Actions").setCellEditor(new GambitSubscriptionButtonEditor(new JCheckBox()));

        sendSubscribtionTable.setPreferredScrollableViewportSize(sendSubscribtionTable.getPreferredSize());
        sendSubscribtionTable.getColumnModel().getColumn(2).setPreferredWidth(70);

        if (topicSubscriptionData.size() > 0) {
            sendEventSubscriptionsTableModel.setData(topicSubscriptionData.values());
        }


        // TODO SAVE

        saveSubscriptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        try {
                            //prepare attributes

                            // Wrap this in a new LinkedList since using the supplied list ends up as a shared reference
                            // between all instances of GambitSavedSubscription.
                            LinkedList<GambitAttribute> tableAttributesData = new LinkedList<>(attributeSubscriptionTableModel.getData());
                            if (tableAttributesData == null) return null;

                            String subscribptionTimestamp = null;

                            GambitSavedSubscription savedSubscribtionItem = new GambitSavedSubscription(
                                    new GambitSubscription(
                                            sendEventInputNamespaceSub.getText(),
                                            subscriptionTopicName,
                                            sendEventInputClientSaltSub.getText(),
                                            sendEventInputClientSecretSub.getText(),
                                            tableAttributesData
                                    ));

                            //push
                            GambitPushService service = callPushServiceFromSavedSubscription(savedSubscribtionItem);
                            int row = pushServices.size();
                            pushServices.put(row, service);

                            topicSubscriptionData.put(savedSubscribtionItem.getSubscribtion().getTopicDescription(), savedSubscribtionItem);

                            saveConfig();

                            ArrayList<GambitSavedSubscription> tableData = new ArrayList<GambitSavedSubscription>();
                            tableData.addAll(topicSubscriptionData.values());

                            sendEventSubscriptionsTableModel.setData(tableData);

                            //clear attribute data and topic description
                            attributeSubscriptionTableModel.clearData();
                            topicDescription.setText("");

                        } catch (Throwable ex) {
                            getLogger().log(Level.SEVERE, "Error saving the configuration: " + ex.getMessage(), ex);
                        }

                        return null;
                    }
                };

                try {
                    job.execute();
                } catch (Throwable ex) {
                    getLogger().log(Level.SEVERE, "Error executing the configuration save job: " + ex.getMessage(), ex);
                }
            }
        });

        sendSubscribtionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();

                GambitSavedSubscription savedSubscription = sendEventSubscriptionsTableModel.getSavedSubscriction(row);

                //Delete subscription row
                if (column == 2) {
                    // terminate the websocket connection for that subscribed topic.
                    if (pushServices.containsKey(row)) {
                        GambitPushService ser = pushServices.get(row);
                        if (ser != null) {

                            //TODO must call unregister push not close socket !???
                            ser.shutdown();
                        }
                    }
                    sendEventSubscriptionsTableModel.removeRow(row);
                    sendEventSubscriptionsTableModel.fireTableDataChanged();
                    topicSubscriptionData.remove(savedSubscription.getSubscribtion().getTopicDescription());
                    saveConfig();
                    return;
                }

                //populate the CIID attributes, topic description and namespace
                topicDescription.setText(savedSubscription.getSubscribtion().getTopicDescription());
                attributeSubscriptionTableModel.setData(new ArrayList<>(savedSubscription.getSubscribtion().getAttributes()));
            }
        });

        // RANDOM UUID TAB

        randomUUIDButtonGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        Future<GambitResponse> future = GambitToolsService.getInstance()
                                .requestRandomUUID(
                                        new GambitRequestRandomUUID.Builder(
                                                accessKey,
                                                secretKey
                                        )
                                );

                        GambitResponseRandomUUID response;
                        try {
                            response = (GambitResponseRandomUUID) future.get();

                            randomUUIDInputResult.setText(response.getUUID());

                            randomUUIDResponse.setWrapStyleWord(false);
                            randomUUIDResponse.setLineWrap(true);

                            try {
                                JSONObject responseBodyJSON = new JSONObject(response.getRawBody());

                                randomUUIDResponse.setText(responseBodyJSON.toString(4));
                            } catch (JSONException e) {
                                getLogger().log(Level.WARNING,
                                        "Error parsing the random UUID response: " + e.getMessage(), e);
                                randomUUIDResponse.setText(response.getRawBody());
                            }

                        } catch (Throwable ex) {
                            getLogger().log(Level.WARNING,
                                    "Error processing the random UUID response: " + ex.getMessage(), ex);
                        }

                        return null;
                    }
                };

                try {
                    job.execute();
                } catch (Throwable ex) {
                    getLogger().log(Level.SEVERE,
                            "Error executing random UUID request: " + ex.getMessage(), ex);
                }
            }
        });

        randomUUIDButtonCopyResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(randomUUIDInputResult.getText());
            }
        });

        randomUUIDButtonCopyResponse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(randomUUIDResponse.getText());
            }
        });

        clearReceivedMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                receivedMessagesTableModel.clearData();
                receivedEventsResponse.setText("");
                saveConfig();
            }
        });

        receivedMessagesTableModel = new GambitMessagesTableModel(topicSubscriptionData);

        receivedEventsTable.setModel(receivedMessagesTableModel);

        receivedEventsTable.getTableHeader().setReorderingAllowed(false);
        receivedEventsTable.setRowSelectionAllowed(true);
        receivedEventsTable.setColumnSelectionAllowed(false);
        receivedEventsTable.setRowHeight(20);
        receivedEventsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    if (receivedEventsTable.getSelectedRow() > -1) {
                        GambitSavedMessage selectedMessage = receivedMessagesTableModel.getData().get(receivedEventsTable.getSelectedRow());

                        receivedEventsResponse.setText(new JSONObject(selectedMessage.getMessage().getRawMessage()).toString(4));
                    }
                }
            }
        });

        if (receivedMessagesArray != null && !receivedMessagesArray.isEmpty()) {
            receivedMessagesTableModel.setData(receivedMessagesArray);
        }
        String version;
        String build = null;
        try {
            Properties props = new Properties();
            InputStream is = GambitExample.class.getResourceAsStream("/version.properties");
            props.load(is);
            is.close();
            version = props.getProperty("artifactMajorVersion") + "." + props.getProperty("artifactBuildNumber");

            String gitHash = props.getProperty("gitHash");
            String buildTime = props.getProperty("buildTime");
            if (gitHash != null && buildTime != null) {
                build = gitHash + " " + buildTime;
            }
        } catch (Throwable e) {
            getLogger().log(Level.WARNING, "Could not fetch version info: " + e.getMessage(), e);
            version = "--";
        }

        //ABOUT TAB
        aboutMessage.setText("<html><div style='text-align: center;'>COGS Test App" +
                "<br/>" +
                "<br/>" +
                "Version: " + version + "" +
                "<br/>" +
                ((build != null) ? "Build: " + build + "<br/>" : "") +
                "<br/>" +
                "<a href='http:\\www.cogswell.io'>www.cogswell.io</a>" +
                "<br/>" +
                "by Aviata" +
                "<br/>" +
                "www.aviatainc.com" +
                "<br/>" +
                "</div><html>");
        aboutMessage.setVerticalAlignment(SwingConstants.CENTER);

    }

    /**
     * Launch a push service instance from saved configuration.
     *
     * @param savedSubscription Push Service configuration.
     * @return A Push Service instance
     */
    private GambitPushService callPushServiceFromSavedSubscription(GambitSavedSubscription savedSubscription) {

        GambitSubscription curSubscr = savedSubscription.getSubscribtion();

        GambitPushService service = tryToStartPushService(
                accessKey,
                curSubscr.getClientSalt(),
                curSubscr.getClientSecret(),
                curSubscr.getNamespace(),
                curSubscr.getAttributes(),
                curSubscr.getTopicDescription());

        return service;

    }

    /**
     * Create a tool tip baloon.
     *
     * @param button  Component that triggers the baloon.
     * @param message Message to be displayed.
     */
    private void createBaloonMessage(JButton button, String message) {

        EdgedBalloonStyle style = new EdgedBalloonStyle(Color.WHITE, Color.BLUE);

        final BalloonTip balloonTip = new BalloonTip(
                button,
                new JLabel(message),
                style,
                BalloonTip.Orientation.LEFT_BELOW,
                BalloonTip.AttachLocation.SOUTHEAST,
                5, 5,
                true
        );

        balloonTip.setCloseButton(BalloonTip.getDefaultCloseButton(), false);
        balloonTip.setVisible(false);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                balloonTip.setVisible(true);
            }
        });
    }

    /**
     * Determine the view state of the "load attributes" button on the subscriptions tab
     */
    private void manageSubscriptionNamespace() {
        if (sendSubscriptionNamespace != null && !sendSubscriptionNamespace.isEmpty()) {
            loadAttributesButton.setEnabled(true);
        } else {
            loadAttributesButton.setEnabled(false);
        }
    }

    /**
     * Determine the view state of the "save" button on the subscriptions tab
     */
    private void manageSubscriptionTopic() {
        //enable subscription save button if exist attribute (least 1 attribute) and exist topic description
        Collection<GambitAttribute> attributes = attributeSubscriptionTableModel.getData();
        if (subscriptionTopicName != null
                && !subscriptionTopicName.isEmpty()
                && attributes != null
                && attributes.size() > 0) {
            saveSubscriptionButton.setEnabled(true);
            return;
        }

        saveSubscriptionButton.setEnabled(false);
    }

    /**
     * Helper method to put text in the clip board
     *
     * @param content to be copied
     */
    protected void copyToClipboard(String content) {
        StringSelection stringSelection = new StringSelection(content);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    /**
     * Read the configuration and populate properties to be used runtime
     */
    protected void readConfig() {
        File file = new File(filePath);

        if (file.exists() && file.canRead()) {
            try {
                JSONTokener tokener = new JSONTokener(new FileInputStream(file));
                JSONObject json = new JSONObject(tokener);

                if (json.has("save") && json.getBoolean("save")) {
                    saveConfig = true;
                }
                if (json.has("access_key")) {
                    accessKey = json.getString("access_key");
                }
                if (json.has("secret_key")) {
                    secretKey = json.getString("secret_key");
                }
                if (json.has("client_salt")) {
                    sendEventClientSalt = json.getString("client_salt");
                }
                if (json.has("client_secret")) {
                    sendEventClientSecret = json.getString("client_secret");
                }
                if (json.has("namespace")) {
                    sendEventNamespace = json.getString("namespace");
                }
                if (json.has("event_name")) {
                    sendEventEventName = json.getString("event_name");
                }
                if (json.has("debug_directive")) {
                    sendEventDebugDirective = json.getString("debug_directive");
                }

                if (json.has("messages")) {

                    JSONArray messages = json.getJSONArray("messages");

                    receivedMessagesArray = new ArrayList<>();

                    for (Object obj : messages) {
                        JSONObject messageJson = (JSONObject) obj;
                        receivedMessagesArray.add(GambitSavedMessage.fromJson(messageJson));
                    }
                }

                if (json.has("subscriptions")) {

                    JSONArray subscriptions = json.getJSONArray("subscriptions");
                    topicSubscriptionData.clear();

                    for (Object obj : subscriptions) {
                        JSONObject subscriptionJson = (JSONObject) obj;
                        GambitSavedSubscription savedSubscription = GambitSavedSubscription.fromJson(subscriptionJson);
                        topicSubscriptionData.put(savedSubscription.getSubscribtion().getTopicDescription(), savedSubscription);
                    }
                }

            } catch (Throwable e) {
                getLogger().log(Level.WARNING, "Error reading the configuration:" + e.getMessage(), e);
            }
        }
    }

    /**
     * Write configuration out to a file.  Note that this is in a temporary location so may get lost during reboot.
     */
    protected void saveConfig() {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        if (!file.exists() && file.canWrite()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Error saving creating the configuration file: " + e.getMessage(), e);

                return;
            }
        }

        JSONObject config = new JSONObject();
        if (saveConfig) {
            config.put("save", true);
            config.put("access_key", accessKey + "");
            config.put("secret_key", secretKey + "");
        }

        if (sendEventClientSalt != null) {
            config.put("client_salt", sendEventClientSalt);
        }
        if (sendEventClientSecret != null) {
            config.put("client_secret", sendEventClientSecret);
        }
        if (sendEventNamespace != null) {
            config.put("namespace", sendEventNamespace);
        }
        if (sendEventEventName != null) {
            config.put("event_name", sendEventEventName);
        }

        if (sendEventDebugDirective != null) {
            config.put("debug_directive", sendEventDebugDirective);
        }

        if (receivedMessagesTableModel != null && receivedMessagesTableModel.getData() != null) {
            LinkedList<GambitSavedMessage> messages = receivedMessagesTableModel.getData();

            Iterator<GambitSavedMessage> i = messages.descendingIterator();

            JSONArray messagesArray = new JSONArray();

            while (i.hasNext()) {
                GambitSavedMessage message = i.next();

                messagesArray.put(message.toJson());
            }

            config.put("messages", messagesArray);
        }

        if (sendEventSubscriptionsTableModel != null && sendEventSubscriptionsTableModel.getData() != null) {

            JSONArray subscriptionArray = new JSONArray();
            for (GambitSavedSubscription item : topicSubscriptionData.values()) {
                subscriptionArray.put(item.toJson());
            }

            config.put("subscriptions", subscriptionArray);
        }

        //

        try {
            FileWriter writer = new FileWriter(file);

            writer.write(config.toString());
            writer.close();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Error writing the configuration file: " + e.getMessage(), e);
        }
    }

    /**
     * Clear the configuration file.
     */
    protected void clearConfig() {
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Toggle state of buttons based on current data entered.
     */
    protected void toggleConfigDependantElements() {
        if (accessKey != null && !accessKey.equals("") && secretKey != null && !secretKey.equals("")) {
            generateButton.setEnabled(true);
            randomUUIDButtonGenerate.setEnabled(true);

            if (sendEventClientSalt != null && !sendEventClientSalt.equals("") && sendEventClientSecret != null && !sendEventClientSecret.equals("") && sendEventNamespace != null && !sendEventNamespace.equals("")) {
                sendEventButtonPopulate.setEnabled(true);

                if (sendEventEventName != null && !sendEventEventName.equals("")) {
                    sendEventButton.setEnabled(true);
                } else {
                    sendEventButton.setEnabled(false);
                }
            } else {
                sendEventButtonPopulate.setEnabled(false);
                sendEventButton.setEnabled(false);
            }
        } else {
            generateButton.setEnabled(false);
            sendEventButtonPopulate.setEnabled(false);
            sendEventButton.setEnabled(false);
            randomUUIDButtonGenerate.setEnabled(false);
        }
    }

    /**
     * Validate information and attempt to open a web socket for push notifications.
     */
    protected GambitPushService tryToStartPushService(
            final String accessKey,
            final String clientSalt,
            final String clientSecret,
            final String namespace,
            LinkedList<GambitAttribute> attributes,
            final String topicDescription) {

        if (accessKey == null || accessKey.isEmpty()) {
            return null;
        }
        if (secretKey == null || secretKey.isEmpty()) {
            return null;
        }
        if (clientSalt == null || clientSalt.isEmpty()) {
            return null;
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            return null;
        }
        if (namespace == null || namespace.isEmpty()) {
            return null;
        }
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }

        SwingWorker job = new SwingWorker() {
            GambitPushService service = null;

            @Override
            protected GambitPushService doInBackground() throws Exception {

                try {

                    LinkedHashMap<String, Object> ciidAttributes = new LinkedHashMap<>();

                    for (GambitAttribute attr : attributes) {
                        if (attr.isCiid() && attr.getValue() != null && !attr.getValue().isEmpty()) {
                            ciidAttributes.put(attr.getName(), attr.getTypeCorrectedValue());
                        }
                    }

                    if (ciidAttributes.isEmpty()) {
                        getLogger().log(Level.SEVERE, "No CIID attributes set. Unable to start push instance");
                        return null;
                    }

                    GambitSDKService.getInstance().setGambitMessageListener(GambitExample.this);

                    service = GambitSDKService.getInstance().startPushService(
                            new GambitPushService.Builder(accessKey, clientSalt, clientSecret, topicDescription)
                                    .setNamespace(namespace)
                                    .setAttributes(ciidAttributes)
                    );

                } catch (Throwable ex) {
                    getLogger().log(Level.SEVERE, "Error establishing push WebSocket: " + ex.getMessage(), ex);
                }

                return service;
            }
        };

        try {
            job.execute();

            return (GambitPushService) job.get();
        } catch (Throwable ex) {
            getLogger().log(Level.SEVERE, "Error executing push WebSocket request: " + ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * The {@link GambitMessage listener} that delivers push messages from all available WebSocket instances.
     *
     * @param builder To identify the push service instance
     * @param message The actual message received
     */
    @Override
    public void onGambitMessage(GambitPushService.Builder builder, GambitMessage message) {
        GambitSavedMessage savedMessage = new GambitSavedMessage(new Date(), message, builder.getTopicDescription());

        receivedMessagesTableModel.getData().push(savedMessage);
        receivedMessagesTableModel.fireTableDataChanged();

        saveConfig();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.setMaximumSize(new Dimension(-1, -1));
        panel.setMinimumSize(new Dimension(-1, -1));
        panel.setPreferredSize(new Dimension(960, 600));
        tabs = new JTabbedPane();
        panel.add(tabs, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 10, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMaximumSize(new Dimension(-1, -1));
        panel1.setMinimumSize(new Dimension(-1, -1));
        panel1.setPreferredSize(new Dimension(-1, -1));
        panel1.setRequestFocusEnabled(true);
        tabs.addTab("Setup", panel1);
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(3, 5, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        setupInputAccessKey = new JTextField();
        setupInputAccessKey.setText("");
        panel1.add(setupInputAccessKey, new GridConstraints(1, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(740, -1), null, 0, false));
        setupCheckSave = new JCheckBox();
        setupCheckSave.setActionCommand("setupSave");
        setupCheckSave.setAlignmentX(0.5f);
        setupCheckSave.setContentAreaFilled(false);
        setupCheckSave.setHorizontalAlignment(2);
        setupCheckSave.setHorizontalTextPosition(4);
        setupCheckSave.setLabel("<html>Remember keys. Note: Only use on a secure system.</html>");
        setupCheckSave.setSelected(false);
        setupCheckSave.setText("<html>Remember keys. Note: Only use on a secure system.</html>");
        panel1.add(setupCheckSave, new GridConstraints(0, 1, 1, 8, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        copyFilePathButton = new JButton();
        copyFilePathButton.setText("Copy Path");
        panel1.add(copyFilePathButton, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setAlignmentX(0.0f);
        label1.setOpaque(true);
        label1.setText("Access Key");
        label1.putClientProperty("html.disable", Boolean.FALSE);
        panel1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JLabel label2 = new JLabel();
        label2.setText("Secret Key");
        panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        setupInputSecretKey = new JTextField();
        setupInputSecretKey.setColumns(1);
        panel1.add(setupInputSecretKey, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(740, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs.addTab("Generate Client Key", panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Generate Key"));
        generateButton = new JButton();
        generateButton.setHorizontalAlignment(0);
        generateButton.setText("Generate Client Keys");
        panel3.add(generateButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("<html>Generate using the secret_key: <secret_key> entered from the Setup tab. </html>");
        panel3.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(640, -1), new Dimension(640, -1), 1, false));
        generateTextPanel = new JTextPane();
        generateTextPanel.setContentType("text/html");
        generateTextPanel.setEditable(false);
        generateTextPanel.setEnabled(true);
        generateTextPanel.setText("<html>\n  <head>\n    \n  </head>\n  <body>\n    Information on keys:<br><a href=\"https://aviatainc.atlassian.net/wiki/display/COGS/Web+App+User%27s+Manual#WebAppUsersManual-Keys\">https://aviatainc.atlassian.net/wiki/display/COGS/Web+App+User%27s+Manual#WebAppUser'sManual-Keys \n    </a>\n  </body>\n</html>\n");
        panel3.add(generateTextPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, 150), null, 1, false));
        generateResultPanel = new JPanel();
        generateResultPanel.setLayout(new GridLayoutManager(6, 2, new Insets(0, 0, 0, 0), -1, -1));
        generateResultPanel.setVisible(true);
        panel2.add(generateResultPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        generateResultPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Results"));
        final JLabel label4 = new JLabel();
        label4.setText("Client Salt");
        generateResultPanel.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        generateResultClientSalt = new JTextField();
        generateResultClientSalt.setEditable(false);
        generateResultPanel.add(generateResultClientSalt, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        generateButtonCopyClientSalt = new JButton();
        generateButtonCopyClientSalt.setText("Copy");
        generateResultPanel.add(generateButtonCopyClientSalt, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Client Secret");
        generateResultPanel.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        generateResultClientSecret = new JTextField();
        generateResultClientSecret.setEditable(false);
        generateResultClientSecret.setText("");
        generateResultPanel.add(generateResultClientSecret, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 1, false));
        generateButtonCopyClientSecret = new JButton();
        generateButtonCopyClientSecret.setText("Copy");
        generateResultPanel.add(generateButtonCopyClientSecret, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Raw Response");
        generateResultPanel.add(label6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        generateResultPanel.add(scrollPane1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        generateResultResponse = new JTextArea();
        generateResultResponse.setEditable(false);
        generateResultResponse.setPreferredSize(new Dimension(-1, 120));
        scrollPane1.setViewportView(generateResultResponse);
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setMaximumSize(new Dimension(-1, -1));
        panel4.setMinimumSize(new Dimension(-1, -1));
        panel4.setPreferredSize(new Dimension(-1, -1));
        tabs.addTab("Subscriptions", panel4);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Event Setup"));
        final JLabel label7 = new JLabel();
        label7.setText("Client Salt");
        panel5.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputClientSaltSub = new JTextField();
        panel5.add(sendEventInputClientSaltSub, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Client Secret");
        panel5.add(label8, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputClientSecretSub = new JTextField();
        sendEventInputClientSecretSub.setText("");
        panel5.add(sendEventInputClientSecretSub, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        sendEventButtonCopySub = new JButton();
        sendEventButtonCopySub.setText("Copy from generated keys");
        panel5.add(sendEventButtonCopySub, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Namespace");
        panel5.add(label9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputNamespaceSub = new JTextField();
        sendEventInputNamespaceSub.setText("");
        panel5.add(sendEventInputNamespaceSub, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        topicDescription = new JTextField();
        topicDescription.setText("");
        panel5.add(topicDescription, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Topic Description");
        panel5.add(label10, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.setVisible(true);
        panel4.add(panel6, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(270);
        splitPane1.setOrientation(0);
        panel6.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel7);
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setDividerLocation(118);
        splitPane2.setOrientation(0);
        panel7.add(splitPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setAutoscrolls(true);
        splitPane2.setRightComponent(scrollPane2);
        sendSubscribtionTable = new JTable();
        scrollPane2.setViewportView(sendSubscribtionTable);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane2.setLeftComponent(panel8);
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setVisible(true);
        panel8.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        attributeSubscriptionTable = new GambitAttributeTable();
        attributeSubscriptionTable.setColumnSelectionAllowed(false);
        attributeSubscriptionTable.setMaximumSize(new Dimension(-1, -1));
        scrollPane3.setViewportView(attributeSubscriptionTable);
        saveSubscriptionButton = new JButton();
        saveSubscriptionButton.setEnabled(false);
        saveSubscriptionButton.setText("Save");
        panel8.add(saveSubscriptionButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(panel9);
        final JScrollPane scrollPane4 = new JScrollPane();
        panel9.add(scrollPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        subscriptionResponse = new JTextArea();
        subscriptionResponse.setLineWrap(true);
        subscriptionResponse.setWrapStyleWord(false);
        scrollPane4.setViewportView(subscriptionResponse);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel10, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loadAttributesButton = new JButton();
        loadAttributesButton.setEnabled(false);
        loadAttributesButton.setText("New");
        panel10.add(loadAttributesButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel10.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel11.setMaximumSize(new Dimension(-1, -1));
        panel11.setMinimumSize(new Dimension(-1, -1));
        panel11.setPreferredSize(new Dimension(-1, -1));
        tabs.addTab("Send Event", panel11);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel12.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Event Setup"));
        final JLabel label11 = new JLabel();
        label11.setText("Client Salt");
        panel12.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputClientSalt = new JTextField();
        panel12.add(sendEventInputClientSalt, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Client Secret");
        panel12.add(label12, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputClientSecret = new JTextField();
        sendEventInputClientSecret.setText("");
        panel12.add(sendEventInputClientSecret, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        sendEventButtonCopy = new JButton();
        sendEventButtonCopy.setText("Copy from previous tab");
        panel12.add(sendEventButtonCopy, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Timestamp");
        panel12.add(label13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputTimestamp = new JTextField();
        sendEventInputTimestamp.setText("");
        panel12.add(sendEventInputTimestamp, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        sendEventButtonHelp = new JButton();
        sendEventButtonHelp.setEnabled(true);
        sendEventButtonHelp.setHorizontalAlignment(2);
        sendEventButtonHelp.setMargin(new Insets(0, 0, 0, 0));
        sendEventButtonHelp.setText("?");
        sendEventButtonHelp.setToolTipText("");
        panel12.add(sendEventButtonHelp, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Namespace");
        panel12.add(label14, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputNamespace = new JTextField();
        sendEventInputNamespace.setText("");
        panel12.add(sendEventInputNamespace, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        sendEventCheckCurrentTimestamp = new JCheckBox();
        sendEventCheckCurrentTimestamp.setText("<html>Use current timestamp</html>");
        panel12.add(sendEventCheckCurrentTimestamp, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Event Name");
        panel12.add(label15, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventInputEventName = new JTextField();
        sendEventInputEventName.setText("");
        panel12.add(sendEventInputEventName, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(150, -1), null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Debug Directive");
        panel12.add(label16, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventComboDebug = new JComboBox();
        panel12.add(sendEventComboDebug, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), null, null, 0, false));
        sendEventButtonHelpDebug = new JButton();
        sendEventButtonHelpDebug.setText("?");
        panel12.add(sendEventButtonHelpDebug, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel13.setVisible(true);
        panel11.add(panel13, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSplitPane splitPane3 = new JSplitPane();
        splitPane3.setDividerLocation(240);
        splitPane3.setOrientation(0);
        panel13.add(splitPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JScrollPane scrollPane5 = new JScrollPane();
        scrollPane5.setAutoscrolls(true);
        splitPane3.setRightComponent(scrollPane5);
        sendEventResponse = new JTextArea();
        sendEventResponse.setAutoscrolls(false);
        sendEventResponse.setEditable(false);
        sendEventResponse.setLineWrap(true);
        sendEventResponse.setMaximumSize(new Dimension(-1, -1));
        sendEventResponse.setMinimumSize(new Dimension(-1, -1));
        sendEventResponse.setPreferredSize(new Dimension(-1, 360));
        sendEventResponse.setRows(20);
        sendEventResponse.setText("");
        sendEventResponse.setVisible(true);
        scrollPane5.setViewportView(sendEventResponse);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane3.setLeftComponent(panel14);
        final JScrollPane scrollPane6 = new JScrollPane();
        scrollPane6.setVisible(true);
        panel14.add(scrollPane6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sendEventAttrTable = new GambitAttributeTable();
        sendEventAttrTable.setColumnSelectionAllowed(false);
        sendEventAttrTable.setMaximumSize(new Dimension(-1, -1));
        scrollPane6.setViewportView(sendEventAttrTable);
        sendEventButton = new JButton();
        sendEventButton.setText("Send Event");
        panel14.add(sendEventButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel11.add(panel15, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sendEventButtonPopulate = new JButton();
        sendEventButtonPopulate.setText("Populate");
        panel15.add(sendEventButtonPopulate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel15.add(spacer5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        sendEventButtonHelpPopulate = new JButton();
        sendEventButtonHelpPopulate.setMargin(new Insets(0, 0, 0, 0));
        sendEventButtonHelpPopulate.setText("?");
        panel15.add(sendEventButtonHelpPopulate, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs.addTab("Received Messages", panel16);
        clearReceivedMessage = new JButton();
        clearReceivedMessage.setText("Clear");
        panel16.add(clearReceivedMessage, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(300, -1), 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel17, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSplitPane splitPane4 = new JSplitPane();
        splitPane4.setDividerLocation(300);
        splitPane4.setOrientation(0);
        panel17.add(splitPane4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane4.setLeftComponent(panel18);
        final JScrollPane scrollPane7 = new JScrollPane();
        panel18.add(scrollPane7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        receivedEventsTable = new JTable();
        scrollPane7.setViewportView(receivedEventsTable);
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane4.setRightComponent(panel19);
        final JScrollPane scrollPane8 = new JScrollPane();
        panel19.add(scrollPane8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 100), new Dimension(-1, 320), null, 0, false));
        receivedEventsResponse = new JTextPane();
        receivedEventsResponse.setEditable(false);
        scrollPane8.setViewportView(receivedEventsResponse);
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabs.addTab("Random UUID", panel20);
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel20.add(panel21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel21.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Generate UUID"));
        thisServiceIsAvailableTextPane = new JTextPane();
        thisServiceIsAvailableTextPane.setText("This service is available for you to use to securely generate version 4 UUIDs (https://en.wikipedia.org/wiki/Universally_unique_identifier) to use for whatever purpose fits your business need. ");
        panel21.add(thisServiceIsAvailableTextPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel21.add(spacer6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel20.add(spacer7, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("<html>Generate using the secret_key: <secret_key> entered from the Setup tab. </html>");
        panel20.add(label17, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        randomUUIDButtonGenerate = new JButton();
        randomUUIDButtonGenerate.setText("Generate");
        panel20.add(randomUUIDButtonGenerate, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        randomUUIDPanelResult = new JPanel();
        randomUUIDPanelResult.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        randomUUIDPanelResult.setVisible(true);
        panel20.add(randomUUIDPanelResult, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("UUID");
        randomUUIDPanelResult.add(label18, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        randomUUIDInputResult = new JTextField();
        randomUUIDInputResult.setEditable(false);
        randomUUIDPanelResult.add(randomUUIDInputResult, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        randomUUIDButtonCopyResult = new JButton();
        randomUUIDButtonCopyResult.setText("Copy");
        randomUUIDPanelResult.add(randomUUIDButtonCopyResult, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        randomUUIDButtonCopyResponse = new JButton();
        randomUUIDButtonCopyResponse.setText("Copy");
        randomUUIDPanelResult.add(randomUUIDButtonCopyResponse, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane9 = new JScrollPane();
        randomUUIDPanelResult.add(scrollPane9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        randomUUIDResponse = new JTextArea();
        randomUUIDResponse.setEditable(false);
        randomUUIDResponse.setMaximumSize(new Dimension(-1, -1));
        randomUUIDResponse.setPreferredSize(new Dimension(-1, 50));
        scrollPane9.setViewportView(randomUUIDResponse);
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabs.addTab("About", panel22);
        imageLogo = new JLabel();
        imageLogo.setBackground(new Color(-1));
        imageLogo.setEnabled(true);
        imageLogo.setIcon(new ImageIcon(getClass().getResource("/logo_only_blue.png")));
        imageLogo.setText("");
        imageLogo.setVerticalAlignment(1);
        imageLogo.setVerticalTextPosition(1);
        panel22.add(imageLogo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(200, 200), new Dimension(200, 186), null, 0, false));
        aboutMessage = new JLabel();
        aboutMessage.setFont(new Font(aboutMessage.getFont().getName(), aboutMessage.getFont().getStyle(), 16));
        aboutMessage.setText(" COGS Test App");
        aboutMessage.setVerticalAlignment(0);
        panel22.add(aboutMessage, new GridConstraints(1, 0, 2, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel22.add(spacer8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        label1.setLabelFor(setupInputAccessKey);
        label2.setLabelFor(setupInputSecretKey);
        label16.setLabelFor(sendEventComboDebug);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}

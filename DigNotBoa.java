import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DigNotBoa extends JFrame {

    private final List<Notice> noticeBoard = new ArrayList<>();
    private int nextId = 1;

    private JTextField titleField;
    private JTextField postedByField;
    private JTextArea contentArea;
    private JTable noticeTable;
    private DefaultTableModel tableModel;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton exitButton;

    public DigNotBoa() {
        setTitle("Digital Notice Board System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("DIGITAL NOTICE BOARD", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(heading, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        JLabel formTitle = new JLabel("Notice Details");
        formTitle.setFont(new Font("Arial", Font.BOLD, 20));
        formPanel.add(formTitle, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Notice Title:");
        titleField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(titleLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);

        JLabel postedByLabel = new JLabel("Posted By:");
        postedByField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(postedByLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(postedByField, gbc);

        JLabel contentLabel = new JLabel("Content:");
        contentArea = new JTextArea(8, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        inputPanel.add(contentLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(contentScroll, gbc);
        formPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        addButton = new JButton("ADD NOTICE");
        updateButton = new JButton("UPDATE NOTICE");
        deleteButton = new JButton("DELETE NOTICE");
        clearButton = new JButton("CLEAR");
        exitButton = new JButton("EXIT");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(formPanel, BorderLayout.WEST);

        String[] columns = {"ID", "Title", "Content", "Posted By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        noticeTable.setRowHeight(30);
        noticeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        noticeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        noticeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScroll = new JScrollPane(noticeTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel tableTitle = new JLabel("All Notices");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 20));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> createNotice());
        updateButton.addActionListener(e -> updateNotice());
        deleteButton.addActionListener(e -> deleteNotice());
        clearButton.addActionListener(e -> clearFields());

        exitButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to exit?", "Exit",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        noticeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = noticeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    titleField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    contentArea.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    postedByField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        noticeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = noticeTable.rowAtPoint(e.getPoint());
                if (selectedRow >= 0) {
                    showNoticeDetails(selectedRow);
                }
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    private void createNotice() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String postedBy = postedByField.getText().trim();

        if (title.isEmpty() || content.isEmpty() || postedBy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Notice notice = new Notice(nextId++, title, content, postedBy);
        noticeBoard.add(notice);
        addNoticeToTable(notice);

        JOptionPane.showMessageDialog(this,
                "Notice posted successfully!\nNotice ID: " + notice.getId()
                        + "\nDate Posted: " + notice.getPostedDate(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
    }

    private void readAllNotices() {
        tableModel.setRowCount(0);
        for (Notice notice : noticeBoard) {
            addNoticeToTable(notice);
        }
    }

    private Notice findNoticeById(int id) {
        for (Notice notice : noticeBoard) {
            if (notice.getId() == id) return notice;
        }
        return null;
    }

    private void updateNotice() {
        int selectedRow = noticeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notice from the table.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String newTitle = titleField.getText().trim();
        String newContent = contentArea.getText().trim();
        String newPostedBy = postedByField.getText().trim();

        if (newTitle.isEmpty() || newContent.isEmpty() || newPostedBy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Notice notice = findNoticeById(id);
        if (notice != null) {
            notice.setTitle(newTitle);
            notice.setContent(newContent);
            notice.setPostedBy(newPostedBy);
            readAllNotices();
            JOptionPane.showMessageDialog(this, "Notice ID " + id + " updated successfully!",
                    "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        }
    }

    private void deleteNotice() {
        int selectedRow = noticeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notice to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Notice ID " + id + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            Notice notice = findNoticeById(id);
            if (notice != null) {
                noticeBoard.remove(notice);
                readAllNotices();
                JOptionPane.showMessageDialog(this, "Notice ID " + id + " deleted successfully!",
                        "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        }
    }

    private void addNoticeToTable(Notice notice) {
        tableModel.addRow(new Object[]{
                notice.getId(), notice.getTitle(), notice.getContent(), notice.getPostedBy()
        });
    }

    private void clearFields() {
        titleField.setText("");
        contentArea.setText("");
        postedByField.setText("");
        noticeTable.clearSelection();
        titleField.requestFocus();
    }

    private void showNoticeDetails(int row) {
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String title = tableModel.getValueAt(row, 1).toString();
        String content = tableModel.getValueAt(row, 2).toString();
        String postedBy = tableModel.getValueAt(row, 3).toString();

        Notice notice = findNoticeById(id);
        if (notice == null) return;

        JFrame noticeFrame = new JFrame("Digital Notice Board - Notice Details");
        noticeFrame.setSize(750, 600);
        noticeFrame.setLocationRelativeTo(this);
        noticeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(new Color(12, 20, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 35, 65));
        header.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel heading = new JLabel("OFFICIAL NOTICE", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(Color.WHITE);
        header.add(heading, BorderLayout.CENTER);
        detailPanel.add(header, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(15, 20));
        card.setBackground(new Color(25, 40, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 220), 2),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        JLabel idLabel = new JLabel("NOTICE ID: " + id);
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idLabel.setForeground(new Color(150, 190, 220));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        titleLabel.setForeground(new Color(0, 220, 220));

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setOpaque(false);
        topPanel.add(idLabel);
        topPanel.add(titleLabel);

        JTextArea noticeContent = new JTextArea(content);
        noticeContent.setFont(new Font("Arial", Font.PLAIN, 18));
        noticeContent.setForeground(Color.WHITE);
        noticeContent.setBackground(new Color(25, 40, 70));
        noticeContent.setLineWrap(true);
        noticeContent.setWrapStyleWord(true);
        noticeContent.setEditable(false);
        noticeContent.setFocusable(false);
        noticeContent.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JScrollPane scrollPane = new JScrollPane(noticeContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(25, 40, 70));

        JLabel dateLabel = new JLabel("DATE POSTED: " + notice.getPostedDate());
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setForeground(new Color(0, 220, 220));

        JLabel postedLabel = new JLabel("PUBLISHED BY: " + postedBy);
        postedLabel.setFont(new Font("Arial", Font.BOLD, 15));
        postedLabel.setForeground(new Color(150, 190, 220));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 15));
        contentPanel.setBackground(new Color(25, 40, 70));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(dateLabel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(postedLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(12, 20, 40));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        centerPanel.add(card, BorderLayout.CENTER);

        detailPanel.add(centerPanel, BorderLayout.CENTER);
        noticeFrame.add(detailPanel);
        noticeFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DigNotBoa::new);
    }
}

class Notice {
    private final int id;
    private String title;
    private String content;
    private String postedBy;
    private final LocalDate postedDate;

    public Notice(int id, String title, String content, String postedBy) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postedBy = postedBy;
        this.postedDate = LocalDate.now();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getPostedBy() { return postedBy; }
    public String getPostedDate() {
        return postedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
}

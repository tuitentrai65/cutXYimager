import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageRegionSelector extends JFrame {
    private BufferedImage image;
    private ImagePanel imagePanel;
    private List<Rectangle> regions = new ArrayList<>();
    private List<Integer> alignXList = new ArrayList<>();
    private List<Integer> alignYList = new ArrayList<>();
    private Rectangle currentRect = null;
    private double scaleX = 1.0, scaleY = 1.0;
    private int selectedRegionIndex = -1;
    private Point dragStart = null;
    private Point dragOffset = null;

    private JTextField fieldCutX, fieldCutY, fieldSizeX, fieldSizeY, fieldAlignX, fieldAlignY;

    public ImageRegionSelector() {
        setTitle("chọn size ảnh -test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        imagePanel = new ImagePanel();
        imagePanel.setPreferredSize(new Dimension(600, 600));
        add(new JScrollPane(imagePanel), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel topButtonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton loadButton = new JButton("Tải Ảnh");
        loadButton.addActionListener(e -> loadImage());
        JButton exportButton = new JButton("Xuất SQL");
        exportButton.addActionListener(e -> updateOutputFields());
        JButton deleteButton = new JButton("Xoá Vùng Đang Chọn");
        deleteButton.addActionListener(e -> deleteSelectedRegion());

        topButtonPanel.add(loadButton);
        topButtonPanel.add(exportButton);
        topButtonPanel.add(deleteButton);

        JPanel sqlInputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        sqlInputPanel.setBorder(BorderFactory.createTitledBorder("Nhập Dữ Liệu SQL"));

        fieldCutX = new JTextField();
        fieldCutY = new JTextField();
        fieldSizeX = new JTextField();
        fieldSizeY = new JTextField();
        fieldAlignX = new JTextField();
        fieldAlignY = new JTextField();

        sqlInputPanel.add(new JLabel("bigCutX:"));
        sqlInputPanel.add(fieldCutX);
        sqlInputPanel.add(new JLabel("bigCutY:"));
        sqlInputPanel.add(fieldCutY);
        sqlInputPanel.add(new JLabel("bigSizeX:"));
        sqlInputPanel.add(fieldSizeX);
        sqlInputPanel.add(new JLabel("bigSizeY:"));
        sqlInputPanel.add(fieldSizeY);
        sqlInputPanel.add(new JLabel("bigAlignX:"));
        sqlInputPanel.add(fieldAlignX);
        sqlInputPanel.add(new JLabel("bigAlignY:"));
        sqlInputPanel.add(fieldAlignY);

        JButton loadSQLButton = new JButton("Load SQL");
        loadSQLButton.addActionListener(e -> loadFromSQLInput());

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(loadSQLButton, BorderLayout.NORTH);
        bottomPanel.add(sqlInputPanel, BorderLayout.CENTER);

        rightPanel.add(topButtonPanel, BorderLayout.NORTH);
        rightPanel.add(bottomPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                image = ImageIO.read(chooser.getSelectedFile());
                int panelWidth = imagePanel.getWidth();
                int panelHeight = imagePanel.getHeight();
                scaleX = panelWidth * 1.0 / image.getWidth();
                scaleY = panelHeight * 1.0 / image.getHeight();
                imagePanel.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateOutputFields() {
        fieldCutX.setText(getXList().toString());
        fieldCutY.setText(getYList().toString());
        fieldSizeX.setText(getWidthList().toString());
        fieldSizeY.setText(getHeightList().toString());
        fieldAlignX.setText(alignXList.toString());
        fieldAlignY.setText(alignYList.toString());
    }

    private void loadFromSQLInput() {
        try {
            List<Integer> xs = parseList(fieldCutX.getText());
            List<Integer> ys = parseList(fieldCutY.getText());
            List<Integer> ws = parseList(fieldSizeX.getText());
            List<Integer> hs = parseList(fieldSizeY.getText());
            List<Integer> ax = parseList(fieldAlignX.getText());
            List<Integer> ay = parseList(fieldAlignY.getText());

            if (xs.size() != ys.size() || xs.size() != ws.size() || xs.size() != hs.size()) {
                JOptionPane.showMessageDialog(this, "Danh sách X, Y, Width, Height không khớp kích thước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            regions.clear();
            alignXList.clear();
            alignYList.clear();

            for (int i = 0; i < xs.size(); i++) {
                regions.add(new Rectangle(xs.get(i), ys.get(i), ws.get(i), hs.get(i)));
                alignXList.add(i < ax.size() ? ax.get(i) : 0);
                alignYList.add(i < ay.size() ? ay.get(i) : 0);
            }

            imagePanel.repaint();
            updateOutputFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý đầu vào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedRegion() {
        if (selectedRegionIndex >= 0 && selectedRegionIndex < regions.size()) {
            regions.remove(selectedRegionIndex);
            alignXList.remove(selectedRegionIndex);
            alignYList.remove(selectedRegionIndex);
            selectedRegionIndex = -1;
            imagePanel.repaint();
            updateOutputFields();
        }
    }

    private List<Integer> parseList(String text) {
        text = text.replaceAll("[\\[\\]]", "");
        String[] parts = text.split(",");
        List<Integer> result = new ArrayList<>();
        for (String s : parts) {
            s = s.trim();
            if (!s.isEmpty()) result.add(Integer.parseInt(s));
        }
        return result;
    }

    private List<Integer> getXList() {
        List<Integer> list = new ArrayList<>();
        for (Rectangle r : regions) list.add(r.x);
        return list;
    }

    private List<Integer> getYList() {
        List<Integer> list = new ArrayList<>();
        for (Rectangle r : regions) list.add(r.y);
        return list;
    }

    private List<Integer> getWidthList() {
        List<Integer> list = new ArrayList<>();
        for (Rectangle r : regions) list.add(r.width);
        return list;
    }

    private List<Integer> getHeightList() {
        List<Integer> list = new ArrayList<>();
        for (Rectangle r : regions) list.add(r.height);
        return list;
    }

    private class ImagePanel extends JPanel {
        public ImagePanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Point click = e.getPoint();
                    selectedRegionIndex = -1;
                    dragOffset = null;

                    for (int i = 0; i < regions.size(); i++) {
                        Rectangle r = regions.get(i);
                        Rectangle scaled = new Rectangle(
                            (int) (r.x * scaleX),
                            (int) (r.y * scaleY),
                            (int) (r.width * scaleX),
                            (int) (r.height * scaleY)
                        );
                        if (scaled.contains(click)) {
                            selectedRegionIndex = i;
                            dragOffset = new Point(click.x - scaled.x, click.y - scaled.y);
                            repaint();
                            return;
                        }
                    }

                    dragStart = e.getPoint();
                    currentRect = null;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (selectedRegionIndex != -1) {
                        dragOffset = null;
                        return;
                    }

                    if (dragStart != null) {
                        int x1 = (int) (Math.min(dragStart.x, e.getX()) / scaleX);
                        int y1 = (int) (Math.min(dragStart.y, e.getY()) / scaleY);
                        int x2 = (int) (Math.max(dragStart.x, e.getX()) / scaleX);
                        int y2 = (int) (Math.max(dragStart.y, e.getY()) / scaleY);
                        Rectangle r = new Rectangle(x1, y1, x2 - x1, y2 - y1);
                        regions.add(r);
                        alignXList.add(0);
                        alignYList.add(0);
                        currentRect = null;
                        dragStart = null;
                        repaint();
                        updateOutputFields();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (selectedRegionIndex != -1 && dragOffset != null) {
                        Rectangle r = regions.get(selectedRegionIndex);
                        int newX = (int) ((e.getX() - dragOffset.x) / scaleX);
                        int newY = (int) ((e.getY() - dragOffset.y) / scaleY);
                        r.setLocation(newX, newY);
                        repaint();
                        updateOutputFields();
                    } else if (dragStart != null) {
                        int x = Math.min(dragStart.x, e.getX());
                        int y = Math.min(dragStart.y, e.getY());
                        int w = Math.abs(dragStart.x - e.getX());
                        int h = Math.abs(dragStart.y - e.getY());
                        currentRect = new Rectangle(x, y, w, h);
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int w = (int) (image.getWidth() * scaleX);
                int h = (int) (image.getHeight() * scaleY);
                g.drawImage(image, 0, 0, w, h, null);
            }
            for (int i = 0; i < regions.size(); i++) {
                Rectangle r = regions.get(i);
                int x = (int) (r.x * scaleX);
                int y = (int) (r.y * scaleY);
                int w = (int) (r.width * scaleX);
                int h = (int) (r.height * scaleY);
                g.setColor(i == selectedRegionIndex ? Color.ORANGE : Color.RED);
                g.drawRect(x, y, w, h);
            }
            if (currentRect != null) {
                g.setColor(Color.BLUE);
                g.drawRect(currentRect.x, currentRect.y, currentRect.width, currentRect.height);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageRegionSelector::new);
    }
}

package org.chunkmancompletionist.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChunkMapWindow extends JFrame {

    private JPanel contentPanel;
    private Point origin;

    public ChunkMapWindow() {
        setTitle("Pannable JFrame Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw content here
                g.setColor(Color.BLUE);
                g.fillRect(50, 50, 200, 200);  // Example content
            }
        };

        contentPanel.setPreferredSize(new Dimension(1600, 1200)); // Larger than the frame to enable panning

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Mouse listener to handle panning
        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origin = e.getPoint();
            }
        });

        contentPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null) {
                    JViewport viewport = scrollPane.getViewport();
                    Point viewPos = viewport.getViewPosition();
                    int dx = origin.x - e.getX();
                    int dy = origin.y - e.getY();
                    viewPos.translate(dx, dy);
                    contentPanel.scrollRectToVisible(new Rectangle(viewPos, viewport.getSize()));
                }
            }
        });
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            PannableFrame frame = new PannableFrame();
//            frame.setVisible(true);
//        });
//    }
}
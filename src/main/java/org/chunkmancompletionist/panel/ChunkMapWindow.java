package org.chunkmancompletionist.panel;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import org.chunkmancompletionist.types.ChunkTaskCalculateMessage;
import org.chunkmancompletionist.types.ChunkTaskCalculatorMessage;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@Singleton
@Slf4j
public class ChunkMapWindow extends JFrame {

    private JPanel contentPanel;
    private Point origin;

    private EventBus eventBus;

    @Inject
    public ChunkMapWindow(EventBus eventBus) {
        this.eventBus = eventBus;

        log.info("map window created");

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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                eventBus.post(new ChunkTaskCalculateMessage("You need to work"));
            }
        });


    }

    @Subscribe
    public void react(ChunkTaskCalculatorMessage event) {
        log.info(event.getName());
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            PannableFrame frame = new PannableFrame();
//            frame.setVisible(true);
//        });
//    }
}
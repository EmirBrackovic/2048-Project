package igrica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Emir2048 extends JPanel {

    private Polje[] Polja;
    boolean pobjeda = false;
    boolean poraz = false;
    int rezultat = 0;

    public Emir2048() {
        setPreferredSize(new Dimension(350, 420));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resetIgre();
                }
                if (!KrajDaNe()) {
                    poraz = true;
                }

                if (!pobjeda && !poraz) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();
                            break;
                        case KeyEvent.VK_RIGHT:
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            up();
                            break;
                    }
                }

                if (!pobjeda && !KrajDaNe()) {
                    poraz = true;
                }

                repaint();
            }
        });
        resetIgre();
    }

    public void resetIgre() {
        rezultat = 0;
        pobjeda = false;
        poraz = false;
        Polja = new Polje[4 * 4];
        for (int i = 0; i < Polja.length; i++) {
            Polja[i] = new Polje();
        }
        novoPolje();
        novoPolje();
    }

    public void left() {
        boolean dodavanjePolje = false;
        for (int i = 0; i < 4; i++) {
            Polje[] line = getLine(i);
            Polje[] merged = mergeLine(moveLine(line));
            setLine(i, merged);
            if (!dodavanjePolje && !compare(line, merged)) {
                dodavanjePolje = true;
            }
        }

        if (dodavanjePolje) {
            novoPolje();
        }
    }

    public void right() {
        Polja = rotate(180);
        left();
        Polja = rotate(180);
    }

    public void up() {
        Polja = rotate(270);
        left();
        Polja = rotate(90);
    }

    public void down() {
        Polja = rotate(90);
        left();
        Polja = rotate(270);
    }

    private Polje poljeTo(int x, int y) {
        return Polja[x + y * 4];
    }

    private void novoPolje() {
        List < Polje > list = slobodnoMjesto();
        if (!slobodnoMjesto().isEmpty()) {
            int index = (int)(Math.random() * list.size()) % list.size();
            Polje emptyTime = list.get(index);
            emptyTime.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List < Polje > slobodnoMjesto() {

        final List < Polje > list = new ArrayList < Polje > (16);
        for (Polje t: Polja) {
            if (t.isEmpty()) {
                list.add(t);
            }
        }
        return list;
    }

    private boolean zauzeto() {
        return slobodnoMjesto().size() == 0;
    }

    boolean KrajDaNe() {
        if (!zauzeto()) {
            return true;
        }
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Polje t = poljeTo(x, y);
                if ((x < 3 && t.value == poljeTo(x + 1, y).value) || ((y < 3) && t.value == poljeTo(x, y + 1).value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compare(Polje[] prvo, Polje[] drugo) {
        if (prvo == drugo) {
            return true;
        } else if (prvo.length != drugo.length) {
            return false;
        }

        for (int i = 0; i < prvo.length; i++) {
            if (prvo[i].value != drugo[i].value) {
                return false;
            }
        }
        return true;
    }

    private Polje[] rotate(int angle) {
        Polje[] novaPolja2 = new Polje[4 * 4];
        int pivremeX = 3, pivremeY = 3;
        if (angle == 90) {
            pivremeY = 0;
        } else if (angle == 270) {
            pivremeX = 0;
        }

        double rad = Math.toRadians(angle);
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int newX = (x * cos) - (y * sin) + pivremeX;
                int newY = (x * sin) + (y * cos) + pivremeY;
                novaPolja2[(newX) + (newY) * 4] = poljeTo(x, y);
            }
        }
        return novaPolja2;
    }

    private Polje[] moveLine(Polje[] staraLinija) {
        LinkedList < Polje > l = new LinkedList < Polje > ();
        for (int i = 0; i < 4; i++) {
            if (!staraLinija[i].isEmpty())
                l.addLast(staraLinija[i]);
        }
        if (l.size() == 0) {
            return staraLinija;
        } else {
            Polje[] novaLinija = new Polje[4];
            ensureSize(l, 4);
            for (int i = 0; i < 4; i++) {
                novaLinija[i] = l.removeFirst();
            }
            return novaLinija;
        }
    }

    private Polje[] mergeLine(Polje[] staraLinija) {
        LinkedList < Polje > list = new LinkedList < Polje > ();
        for (int i = 0; i < 4 && !staraLinija[i].isEmpty(); i++) {
            int num = staraLinija[i].value;
            if (i < 3 && staraLinija[i].value == staraLinija[i + 1].value) {
                num *= 2;
                rezultat += num;
                int kraljPobjede = 2048;
                if (num == kraljPobjede) {
                    pobjeda = true;
                }
                i++;
            }
            list.add(new Polje(num));
        }
        if (list.size() == 0) {
            return staraLinija;
        } else {
            ensureSize(list, 4);
            return list.toArray(new Polje[4]);
        }
    }

    private static void ensureSize(java.util.List < Polje > l, int s) {
        while (l.size() != s) {
            l.add(new Polje());
        }
    }

    private Polje[] getLine(int index) {
        Polje[] result = new Polje[4];
        for (int i = 0; i < 4; i++) {
            result[i] = poljeTo(i, index);
        }
        return result;
    }

    private void setLine(int index, Polje[] re) {
        System.arraycopy(re, 0, Polja, index * 4, 4);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                crtajPolje(g, Polja[x + y * 4], x, y);
            }
        }
    }

    private void crtajPolje(Graphics g2, Polje Polje, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        int value = Polje.value;
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        g.setColor(Polje.getBackground());
        g.fillRoundRect(xOffset, yOffset, Polje_SIZE, Polje_SIZE, 14, 14);
        g.setColor(Polje.getForeground());
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);
        g.setFont(font);

        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        if (value != 0)
            g.drawString(s, xOffset + (Polje_SIZE - w) / 2, yOffset + Polje_SIZE - (Polje_SIZE - h) / 2 - 2);

        if (pobjeda || poraz) {
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(78, 139, 202));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
            if (pobjeda) {
                g.drawString("Cestitam! Pobjedili ste :)", 70, 150);
            }
            if (poraz) {
                g.drawString("Kraj igre!", 53, 135);

            }
            if (pobjeda || poraz) {
                g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
                g.setColor(new Color(128, 128, 128, 128));
                g.drawString("Prtisini Esc da nastavis igru", 80, getHeight() - 40);
            }
        }
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
        g.drawString("Rezultat: " + rezultat, 200, 365);

    }

    private static int offsetCoors(int arg) {
        return arg * (polja2_MARGIN + Polje_SIZE) + polja2_MARGIN;
    }

    static class Polje {
        int value;

        public Polje() {
            this(0);
        }

        public Polje(int num) {
            value = num;
        }

        public boolean isEmpty() {
            return value == 0;
        }

        public Color getForeground() {
            return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
        }

        public Color getBackground() {
            switch (value) {
                case 2:
                    return new Color(0xebecf7);
                case 4:
                    return new Color(0xc8caed);
                case 8:
                    return new Color(0x9579f2);
                case 16:
                    return new Color(0x7663f5);
                case 32:
                    return new Color(0x4f3dad);
                case 64:
                    return new Color(0x271a69);
                case 128:
                    return new Color(0x130d33);
                case 256:
                    return new Color(0x0b0047);
                case 512:
                    return new Color(0x011175);
                case 1024:
                    return new Color(0x0023ff);
                case 2048:
                    return new Color(0xeb75df);
            }
            return new Color(0xcdc1b4);
        }
    }
    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Times New Roman";
    private static final int Polje_SIZE = 64;
    private static final int polja2_MARGIN = 16;

}
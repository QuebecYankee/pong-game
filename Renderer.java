package pong;

import java.awt.Graphics;
import javax.swing.JPanel;

public class Renderer extends JPanel{
	private static final long serialVersionUID = 1;
	
	@Override
	protected void paintComponent(Graphics g) {
		Pong.pong.repaint(g);
	}
}
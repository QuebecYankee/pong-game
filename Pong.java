package pong;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Pong implements ActionListener, KeyListener {
	
	//horizontalSpeeds is a collection of valid speeds that won't make the ball move so many pixels that it won't hit the paddles anymore
	//speed of ball is used to set the amount of pixels for the ball to move per frame
	//hits is a count used to change horizontalSpeedOfBall according to the horizontalSpeeds array
	public int[] horizontalSpeeds = {2, 3, 4, 5, 6, 6, 6, 6, 10, 10, 12, 12, 12, 15, 15, 15, 15, 15, 20, 20, 25, 25, 30, 30, 50, 50, 60, 60}; //2, 3, 4, 5, 6, 6, 6, 6, 10, 10, 12, 12, 12, 15, 15, 15, 15, 15, 20, 20, 25, 25, 30, 30, 50, 50, 60, 60, 75, 75, 100
	public int horizontalSpeedOfBall = horizontalSpeeds[0];
	public int verticalSpeedOfBall = 0;
	public int hits = 0;
	
	public static Pong pong;
	public Renderer renderer;
	public Random rand = new Random();
	public final int WIDTH = 800, HEIGHT = 800;
	public Rectangle ball, leftPaddle, rightPaddle;
	public boolean ballIsGoingRight = true, ballIsGoingLeft = false;
	public boolean ballIsGoingUp = (rand.nextInt(2) == 0), ballIsGoingDown = !ballIsGoingUp;
	public int leftScore = 0, rightScore = 0;
	public boolean gameHasStarted = false, paused = false;
	
	public Pong() {
		JFrame jframe = new JFrame();
		
		URL iconURL = getClass().getResource("/img/pong icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		jframe.setIconImage(icon.getImage());
		
		Timer timer = new Timer(20, this);
		renderer = new Renderer();

		jframe.add(renderer);
		jframe.setTitle("Pong");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT+39);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);

		
		ball = new Rectangle((WIDTH / 2) - 10, (HEIGHT / 2) - 15, 30, 30);
		leftPaddle = new Rectangle(50, (HEIGHT / 2) - 150, 30, 300);
		rightPaddle = new Rectangle(WIDTH - 20 - 20 - 50, (HEIGHT / 2) - 150, 30, 300);
		
		timer.start();
	}
	
	public void repaint(Graphics g) {
		//color background black
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
				
		//color ball white
		g.setColor(Color.white);
		g.fillRect(ball.x, ball.y, ball.width, ball.height);
		
		//color paddles white
		g.setColor(Color.white);
		g.fillRect(leftPaddle.x, leftPaddle.y, leftPaddle.width, leftPaddle.height);
		g.fillRect(rightPaddle.x, rightPaddle.y, rightPaddle.width, rightPaddle.height);
		
		
		//start screen
		if (!gameHasStarted) {
			g.setColor(Color.lightGray);
			g.setFont(new Font("Arial", 1, 60));
			g.drawString("Press Space to Start", WIDTH / 2 - 300, 100);
		} //score board
		else if (paused) {
			g.setColor(Color.lightGray);
			g.setFont(new Font("Arial", 1, 60));
			g.drawString("Paused", WIDTH / 2 - 108, 100);
		}
		else {
			g.setColor(Color.lightGray);
			g.setFont(new Font("Arial", 1, 60));
			g.drawString(String.valueOf(leftScore), leftPaddle.x, 100);
			g.drawString(String.valueOf(rightScore), rightPaddle.x, 100);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean ballIsTouchingLeftY = (ball.y+ball.height > leftPaddle.y && ball.y < leftPaddle.y+leftPaddle.height);
		boolean ballIsTouchingLeftX = (ball.x == leftPaddle.x + leftPaddle.width);
		boolean ballIsTouchingRightY = (ball.y+ball.height > rightPaddle.y && ball.y < rightPaddle.y+rightPaddle.height);
		boolean ballIsTouchingRightX = (ball.x+ball.width == rightPaddle.x);
		boolean ballIsTouchingTopOfScreen = (ball.y == 0);
		boolean ballIsTouchingBottomOfScreen = (ball.y+ball.height == HEIGHT);
		
		if (gameHasStarted && !paused) {
			//if ball is touching a paddle turn it around and change the ball speed
			if (ballIsGoingLeft && ballIsTouchingLeftX && ballIsTouchingLeftY) {
				ballIsGoingRight = true;
				ballIsGoingLeft = false;
				
				//choose if ball goes up or down
				if (rand.nextInt(2) == 0) {
					ballIsGoingUp = false;
					ballIsGoingDown = true;
				}
				else {
					ballIsGoingUp = true;
					ballIsGoingDown = false;
				}
				
				//set speeds
				if (hits+1 < horizontalSpeeds.length) {
					horizontalSpeedOfBall = horizontalSpeeds[++hits];
					verticalSpeedOfBall = pickRandomVerticalSpeed();
				}
			}
			else if (ballIsGoingRight && ballIsTouchingRightX && ballIsTouchingRightY) {
				ballIsGoingRight = false;
				ballIsGoingLeft = true;
				
				//choose if ball goes up or down
				if (rand.nextInt(2) == 0) {
					ballIsGoingUp = false;
					ballIsGoingDown = true;
				}
				else {
					ballIsGoingUp = true;
					ballIsGoingDown = false;
				}
				
				//set speeds
				if (hits+1 < horizontalSpeeds.length) {
					horizontalSpeedOfBall = horizontalSpeeds[++hits];
					verticalSpeedOfBall = pickRandomVerticalSpeed();
				}
			}
			
			//if ball is out of the window iterate correct score and restart game
			if (ball.x <= 0) {
				rightScore++;
				reset();
			}
			else if (ball.x >= WIDTH) {
				leftScore++;
				reset();
			}
			
			//move the ball sideways
			if (ballIsGoingRight) {
				ball.x += horizontalSpeedOfBall;
			}
			else if (ballIsGoingLeft) {
				ball.x -= horizontalSpeedOfBall;
			}
			
			//if ball is touching the top or bottom of the screen flip it's vertical motion
			if (ballIsTouchingTopOfScreen || ball.y + ball.height < ball.height) {
				ballIsGoingUp = false;
				ballIsGoingDown = true;
				verticalSpeedOfBall = pickRandomVerticalSpeed();
			}
			else if (ballIsTouchingBottomOfScreen || ball.y + ball.height > HEIGHT) {
				ballIsGoingUp = true;
				ballIsGoingDown = false;
				verticalSpeedOfBall = pickRandomVerticalSpeed();
			}
			
			//move the ball vertically if necessary
			if (ballIsGoingUp) {
				ball.y -= verticalSpeedOfBall;
			}
			else if (ballIsGoingDown) {
				ball.y += verticalSpeedOfBall;
			}
		}
		
		renderer.repaint();
	}
	
	public int pickRandomVerticalSpeed() {
		//find distance to work with
		//get arrayList of factors of distanceToWork less than or equal to 50 to avoid ridiculously fast speeds
		//return a random element
		
		int speed, distanceToWork, maxSpeed = 10;
		
		if (ballIsGoingDown) {
			distanceToWork = HEIGHT - ball.y + ball.height;
		}
		else { //if ball is going up
			distanceToWork = ball.y - 0;
		}
		
		ArrayList<Integer> factors = new ArrayList<Integer>();
		
		for(int a = 1; a <= distanceToWork/2 && a <= maxSpeed; a++) {
			if (distanceToWork % a == 0) {
				factors.add(a);
			}
		}
		
		speed = factors.get(rand.nextInt(factors.size()));
		
		System.out.println("distance: " + distanceToWork);
		System.out.println("speed: " + speed);
		return speed;
	}
	
	public void reset() {
		hits = 0;
		horizontalSpeedOfBall = horizontalSpeeds[0];
		verticalSpeedOfBall = 0;
		ballIsGoingRight = true;
		ballIsGoingLeft = false;
		ball.x = (WIDTH / 2) - 10;
		ball.y = (HEIGHT / 2) - 15;
		leftPaddle.x = 50;
		leftPaddle.y = (HEIGHT / 2) - 150;
		rightPaddle.x = (WIDTH - 20 - 20 - 50);
		rightPaddle.y = (HEIGHT / 2) - 150;
		ballIsGoingUp = (rand.nextInt(2) == 0);
		ballIsGoingDown = !ballIsGoingUp;
	}
	public static void main(String args[]) {
		pong = new Pong();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int ammountPaddleMovesPerClick = 50;
		
		if (gameHasStarted) {
			if (e.getKeyCode() == KeyEvent.VK_W && leftPaddle.y > 0) {
				leftPaddle.y -= ammountPaddleMovesPerClick;
			}
			else if (e.getKeyCode() == KeyEvent.VK_S && leftPaddle.y+leftPaddle.height < HEIGHT) {
				leftPaddle.y += ammountPaddleMovesPerClick;
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP && rightPaddle.y > 0) {
				rightPaddle.y -= ammountPaddleMovesPerClick;
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN && rightPaddle.y+leftPaddle.height < HEIGHT) {
				rightPaddle.y += ammountPaddleMovesPerClick;
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (gameHasStarted && !paused)
				paused = true;
			else if (gameHasStarted && paused)
				paused = false;
			else 
				gameHasStarted = true;
		}
	}
}

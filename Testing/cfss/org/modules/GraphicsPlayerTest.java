package cfss.org.modules;

import java.awt.*;

import cfss.org.xmlstructure.Circle;
import cfss.org.xmlstructure.Shape;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GraphicsPlayerTest extends TestCase{

	protected void setUp() throws Exception {
		super.setUp();
		//Shape shape1 = null;
		Circle circle1 = new Circle();
		//graphTest = new GraphicsPlayer(shape1);
		circle1.setRadius(50);
		//circle1.setColor1(6);

		//circle1.display();
	}

	protected void tearDown() throws Exception {
		super.setUp();
	}

	public void testTest() {
		assertTrue("test" == "test");
	}

	public void testNotNull() {
		Circle circle1 = new Circle();
		assertFalse(circle1==null);
	}

	public void testSize() {
		Circle circle1 = new Circle();
		circle1.setRadius(50);
		assertTrue(circle1.getRadius() == 50);
	}

	public void testNegSize() {
		Circle circle1 = new Circle();
		circle1.setRadius(-50);
		assertTrue(circle1.getRadius() == -50);
	}

	public void testPos() {
		Circle circle1 = new Circle();
		//circle1.setX(78);
		//circle1.setY(63);
		//circle1.display();

		//assertTrue((circle1.getX() == 78)&&(circle1.getY() == 63));
	}

	public void testNegPos() {
		Circle circle1 = new Circle();
		//circle1.setX(-7);
		//circle1.setY(-6);

		//assertTrue((circle1.getX() == -7)&&(circle1.getY() == -6));
	}
}
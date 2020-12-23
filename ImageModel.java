import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

class ImageModel extends Canvas {

    BufferedImage photo;

    public ImageModel(BufferedImage image){
        photo = image;
    }

    public void paint(Graphics g) {
		// draw boundary
		g.setColor(Color.gray);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		// compute the offset of the image.
		int xoffset = (getWidth() - photo.getWidth()) / 2;
		int yoffset = (getHeight() - photo.getHeight()) / 2;
		g.drawImage(photo, xoffset, yoffset, this);
	}
    
}
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

public class Filter extends Frame implements ActionListener, ItemListener {

    BufferedImage inputImage,outputImage;
    ImageModel inputCanvas,outputCanvas;
    int imageWidth, imageHeight;

    public ArrayList<JSpinner> inputs = new ArrayList<JSpinner>();

    JSpinner kernelMultiplier;  
    JComboBox<String> filterList;

    public Filter(){

        JFrame frame = new JFrame("Image Filters");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //reading image from file
        try { 
            inputImage = ImageIO.read(new File("Lenna.png"));
            outputImage = ImageIO.read(new File("Lenna.png"));
        } catch (IOException e) {
        } 

        //getting image height and width
        imageWidth = inputImage.getWidth();
		imageHeight = outputImage.getHeight();

        inputCanvas = new ImageModel(inputImage);
        outputCanvas = new ImageModel(outputImage);

        //adding image to panel
        Panel imagePanel = new Panel();
        imagePanel.setLayout(new GridLayout(1, 2, 10, 10));
        imagePanel.add(outputCanvas);
           

        //adding 3X3 kernel input boxes
        JPanel kernelPanel = new JPanel(new GridLayout(3,3, 10, 30));

        JSpinner input00 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input00);
        JSpinner input01 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input01);
        JSpinner input02 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input02);

        JSpinner input10 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input10);
        JSpinner input11 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input11);
        JSpinner input12 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input12);

        JSpinner input20 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input20);
        JSpinner input21 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input21);
        JSpinner input22 = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
        inputs.add(input22);

        kernelPanel.add(input00);
        kernelPanel.add(input01);
        kernelPanel.add(input02);
        kernelPanel.add(input10);
        kernelPanel.add(input11);
        kernelPanel.add(input12);
        kernelPanel.add(input20);
        kernelPanel.add(input21);
        kernelPanel.add(input22);

        kernelPanel.setPreferredSize(new Dimension(200, 200));   

        //Buttons
        JPanel buttonPanel = new JPanel();

        
        //List with some filters
        String[] filters = {"None", "Sharpen", "Box Flur", "Gaussian blur", "Emboss", "Outline"};

        filterList = new JComboBox<>(filters);
        filterList.setSelectedIndex(0);
        filterList.addItemListener(this); 

        //adding filter list to bottom button panel
        buttonPanel.add(filterList);

        JLabel label1 = new JLabel("Multiplier");
        buttonPanel.add(label1);

        //kernel Multiplier
        kernelMultiplier = new JSpinner(new SpinnerNumberModel(1, 0, 1, 0.01));
        kernelMultiplier.setPreferredSize(new Dimension(70, 25)); 
        buttonPanel.add(kernelMultiplier);

        Button button = new Button("Reset");
        button.addActionListener(this);
        buttonPanel.add(button);
        
        button = new Button("Apply");
        button.addActionListener(this);
        buttonPanel.add(button);
    

        //adding components to frame
        frame.add(imagePanel,BorderLayout.CENTER);
        frame.add(kernelPanel,BorderLayout.WEST);
        frame.add(buttonPanel,BorderLayout.SOUTH);
        

        //Displaying window
        frame.setSize(imageWidth*2+100, imageHeight+100); 
        frame.setVisible(true);
        
    }

    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            Object source = event.getSource();
            if (source instanceof JComboBox) { //action listener for dropdown list with filters
                JComboBox cb = (JComboBox)source;
                String selectedItem = (String)cb.getSelectedItem();

                int[] kernel;
                double multiplierValue = 1;

                if(selectedItem.equals("Gaussian blur")){
                    kernel = new int[]{1,2,1,2,4,2,1,2,1};
                    multiplierValue = 0.0625;
                }else if(selectedItem.equals("Sharpen")){
                    kernel = new int[]{0,-1,0,-1,5,-1,0,-1,0};
                }else if(selectedItem.equals("Box Flur")){
                    kernel = new int[]{1,1,1,1,1,1,1,1,1};
                    multiplierValue = 0.111;
                }else if(selectedItem.equals("Emboss")){
                    kernel = new int[]{-2,-1,0,-1,1,1,0,1,2};
                }else if(selectedItem.equals("Outline")){
                    kernel = new int[]{-1,-1,-1,-1,8,-1,-1,-1,-1};
                }else{
                    kernel = new int[]{0,0,0,0,0,0,0,0,0};
                }

                //looping through inputs and setting choosen values
                for (int i = 0; i < inputs.size(); i++) {
                    JSpinner inputSpinner = inputs.get(i);
                    inputSpinner.setValue(kernel[i]);
                }
                //setting multiplier value
                kernelMultiplier.setValue(multiplierValue);
                
            } 
        } 
    }


    public void actionPerformed(ActionEvent e) {
       

        if ( ((Button)e.getSource()).getLabel().equals("Reset") ) {
            
            //looping through output image and overwriting it with original image
			for ( int y=0, i=0 ; y < imageHeight; y++ )
				for ( int x=0 ; x < imageWidth; x++, i++ ) {
					Color clr = new Color(inputCanvas.photo.getRGB(x, y));
					int red = clr.getRed();
					int green = clr.getGreen();
					int blue = clr.getBlue();
					outputCanvas.photo.setRGB(x, y, (new Color(red, green, blue)).getRGB());
				}
			outputCanvas.repaint();

        }else{
 
            int[][] kernel = new int[3][3]; 

            //looping through inputs to place them in a 3x3 kernel 
            for (int i = 0; i < inputs.size(); i++) {

                JSpinner inputSpinner = inputs.get(i);

                int value = (Integer)inputSpinner.getValue();

                //calculating the row and column input value should be placed in
                int row = i / 3;
                int column = i % 3;

                kernel[row][column] = value;
            }

            
			for (int y=0; y < imageHeight; y++) {
				for (int x=0; x < imageWidth; x++) {

                    int startingX = x - 1;
                    int startingY = y - 1;

                    int sumRed = 0;
                    int sumGreen = 0;
                    int sumBlue = 0;

                    //looping through 3x3 neighborhood
                    for(int i = 0; i < 3; i++){
                        for(int j = 0; j < 3; j++){
            
                            int xValue = startingX + j;
                            int yValue = startingY + i;

                            //checking if x and y is within image
                            boolean legal = true;

                            if(xValue < 0 || yValue < 0){
                                legal = false;
                            }

                            if(xValue >= imageWidth || yValue >= imageHeight){
                                legal = false;
                            }

                            if(legal){
                                Color allColors = new Color(inputCanvas.photo.getRGB(xValue, yValue));
                                int red = allColors.getRed();
                                int green = allColors.getGreen();
                                int blue = allColors.getBlue();

                                sumRed += red * kernel[j][i];
                                sumGreen += green * kernel[j][i];
                                sumBlue += blue * kernel[j][i];
                            }

                        }
                    } 

                    double multiplierValue = (Double)kernelMultiplier.getValue();
                    sumRed = (int)(sumRed * multiplierValue);
                    sumGreen = (int)(sumGreen * multiplierValue);
                    sumBlue = (int)(sumBlue * multiplierValue);

                    //ensuring values are not over min and max color values
                    sumRed = sumRed > 255 ? 255 : sumRed < 0 ? 0 : sumRed; 
                    sumGreen = sumGreen > 255 ? 255 : sumGreen < 0 ? 0 : sumGreen; 
                    sumBlue = sumBlue > 255 ? 255 : sumBlue < 0 ? 0 : sumBlue; 
					
					outputCanvas.photo.setRGB(x, y, (new Color(sumRed, sumGreen, sumBlue)).getRGB());
				}
            }
			outputCanvas.repaint();

        }
    }


    public static void main(String[] args) {
        new Filter();
    }

}
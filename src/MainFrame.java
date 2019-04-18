import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLOutput;

class MainFrame extends JFrame implements ActionListener
{

        // ecranul principal
        private JFrame frame;

        // butoanele pentru tastatura calculatorului
        private JButton[] buttons;

        // eticheta pentru afisare text
        private JLabel label;

        // panouri utilizate
        private JPanel calculatorPanel;
        private JPanel outputPanel;

        //text field that displays the output of the calculator
        private JTextField accumulatorField; // rezultatul curent
        private JTextField outputField; // toate calculele efectuate

        //vreau sa imi setez fontul pentru textField
        private Font myFont = new Font("Verdana",Font.BOLD,20);

        //variabile folosite pentru calculator
        private String accumulator = "";
        private String outputAccumulator ="";
        private String toWrite = "";
        private Double firstOperand = 0.0;
        private double residualValue = 0.0;

        String operator = "";
        private boolean dotPressed = false;
        private boolean started = false;
        private boolean isClicked = false;
        private boolean selfOperatorPreviouslyOn = false;





        // main class
        MainFrame()
        {

            // initializez variabilele utilizate
            initializeVariables();


            //adaug casutele de text in outputPanel
            outputPanel.add(outputField, BorderLayout.NORTH);
            outputPanel.add(accumulatorField, BorderLayout.SOUTH);

            // adaug butoanele si eticheta in panel;
            addButtonsToPanel(buttons, calculatorPanel);
            calculatorPanel.add(label);

            // set background and layout of panels
            calculatorPanel.setBackground(Color.lightGray);
            calculatorPanel.setLayout(new GridLayout(0,4 ));


            //adaug panourile in frame
            frame.add(calculatorPanel, BorderLayout.CENTER);
            frame.add(outputPanel, BorderLayout.NORTH);


            // set the size of frame
            frame.setSize(300, 300);

            //show the frame on screen
            frame.setVisible(true);

            addActionListeners();
        }



    public void actionPerformed(ActionEvent e)
    {

        String action = e.getActionCommand();


        if(isDigit(action))
        {
            updateAccumulator(action);
        }

        if(isArithmeticOperator(action))
        {
            updateValue(action);
        }

        if (isSelfOperator(action))
        {

            if(firstOperand != 0.0 && outputAccumulator.isEmpty()) //afisez rezultatul dupa apasarea unui operator
            {
                outputAccumulator = firstOperand.toString();
            }
            updateSelfOperator(action);
            dotPressed = false;
        }

        if(isEqualOperator(action))
        {
            updateDisplay(operator);
            accumulator = "";
            dotPressed = false;
        }
        else
        {
            residualValue = 0;
        }

        if(action.equals(".") && !dotPressed)
        {
            if(accumulator.isEmpty())
            {
                accumulator = "0";
            }
                accumulator += action;
                accumulatorField.setText(accumulator);
                dotPressed = true;
        }

        if(action.equals("<--"))
        {
            deleteOneDigit();
        }

        if(action.equals("CE"))
        {
            accumulator = "";
            accumulatorField.setText("0");
            dotPressed = false;
        }

        if(action.equals("C"))
        {
            firstOperand = 0.0;
            residualValue = 0.0;
            accumulator = "";
            outputAccumulator = "";
            operator = "";
            accumulatorField.setText("0");
            outputField.setText("");
            dotPressed = false;
            started = false;
            isClicked = false;
            toWrite = "";
            selfOperatorPreviouslyOn = false;
        }
    }



    void addButtonsToPanel (JButton[] buttons, JPanel panel)
        {
            int i;

            for (i = 0; i < buttons.length; i++)
            {

                panel.add(buttons[i]);
            }
        }



    public void initializeOperator(String action)
    {

        //salvez operatorul
        operator = action;

        //actualizez outputField-ul
        outputAccumulator += accumulator;
        outputField.setText(outputAccumulator + " ");

        //incarc in primul operand
        if(!accumulator.isEmpty())
        {
            firstOperand = Double.parseDouble(accumulator);
        }

        //resetez acumulatorul
        accumulator = "";
    }



    public void updateSelfOperator(String action)
    {

        Double currentValue;


        if(outputField.getText().isEmpty() && !accumulator.isEmpty())
        {
            outputField.setText(accumulator);
        }


        if(!accumulator.isEmpty()) { // toate functiile actioneaza asupra acumulatorului
            currentValue  = Double.parseDouble(accumulator);
        }
        else
        {
            currentValue = firstOperand;
        }

        if(!selfOperatorPreviouslyOn)
        {
            toWrite = accumulatorField.getText();
        }

        if(action.equals("1/x"))
        {
            if(currentValue != 0)
            {
                toWrite = " 1/(" + toWrite + ") ";// + outputAccumulator + ") ";
                currentValue = 1 / currentValue;
            }
            else
            {
                accumulator = "Cannot divide by zero";
                //blochez butoanele
                accumulatorField.setText(accumulator);
                return;
            }
        }

        if(action.equals("√"))
        {
            if(currentValue >= 0)
            {
                toWrite = " √(" + toWrite + ") ";//+ outputAccumulator + ") ";
                currentValue = Math.sqrt(currentValue);
            }
            else
            {
                accumulator = "";
                outputAccumulator = "";
                //blochez butoanele
                accumulatorField.setText("Invalid input");
                outputField.setText("");
                started = false;
                return;
            }
        }
        if(action.equals("x²"))
        {
            toWrite = " sqr(" + toWrite + ") " ;//+ outputAccumulator + ") ";
            currentValue *= currentValue;
        }

        if(action.equals("±"))
        {
            toWrite = " negate(" + toWrite + ") ";//+ outputAccumulator + ") ";
            currentValue = -currentValue;
        }

        if(outputAccumulator.length() >= 1 &&
                !isArithmeticOperator(Character.toString(outputAccumulator.charAt(outputAccumulator.length() - 1))))
        {
            outputAccumulator +=  " " + operator;
        }

        accumulator = currentValue.toString();
        accumulatorField.setText(accumulator);
        outputField.setText(outputAccumulator + toWrite);

        selfOperatorPreviouslyOn = true;
    }


    public void updateOperator(String action)
    {

        if(accumulator != "" && isDigit(accumulator)) //isDigit verifica daca este numar
        {
            Double currentValue = Double.parseDouble(accumulator);
            firstOperand = computeCalculation(currentValue);
        }

        // salvez operatia pe care urmeaza sa o efectuez
        operator = action;
        if(selfOperatorPreviouslyOn) // pur estetic, pentru afisare
        {
            selfOperatorPreviouslyOn = false;
            outputAccumulator += toWrite;
        }
        else
        {
            outputAccumulator += accumulator;
        }

        outputField.setText(outputAccumulator + " " + operator + " "); //

        accumulatorField.setText(Double.toString(firstOperand)); // afisez rezultatul curent
        accumulator = ""; // am efectuat operatia, deci resetez acumulatorul intern
        isClicked = true; // marchez faptul ca am un operand de scris
    }

    // functie care determina daca am introdus o cifra
    public boolean isDigit(String action)
    {
        boolean isDigit = true;

        try
        {
            Double num = Double.parseDouble(action);
        }
        catch (NumberFormatException e)
        {
            isDigit = false;
        }

        return isDigit;
    }

    // functie care imi incarca acumulatorul la introducerea unei constante numerice
    public void updateAccumulator(String action)
    {
        int i;
        for (i = 0; i <= 9; i ++)
        {
            if(action.equals(Integer.toString(i)))
            {
                if(!accumulator.equals("0"))
                {
                    accumulator += i;
                }
                accumulatorField.setText(accumulator);
                if(isClicked)
                {
                    outputAccumulator += " " + operator + " ";
                    outputField.setText(outputAccumulator);
                    isClicked = false;
                }
            }
        }
    }


    //action listeners pentru butoanele calculatorului
    public void addActionListeners()
    {

        for (int i = 0; i < buttons.length; i++)
        {

            buttons[i].addActionListener(this);
        }
    }

    //constructor pentru butoanele calculatorului
    public JButton[] createCalculator()
    {
        JButton[] buttons = new JButton[24];

        buttons[0] = new JButton("%");
        buttons[1] = new JButton("√");
        buttons[2] = new JButton("x²");
        buttons[3] = new JButton("1/x");

        buttons[4] = new JButton("CE");
        buttons[5] = new JButton("C");
        buttons[6] = new JButton("<--");
        buttons[7] = new JButton("/");

        buttons[8] = new JButton("7");
        buttons[9] = new JButton("8");
        buttons[10] = new JButton("9");
        buttons[11] = new JButton("*");

        buttons[12] = new JButton("4");
        buttons[13] = new JButton("5");
        buttons[14] = new JButton("6");
        buttons[15] = new JButton("-");

        buttons[16] = new JButton("1");
        buttons[17] = new JButton("2");
        buttons[18] = new JButton("3");
        buttons[19] = new JButton("+");

        buttons[20] = new JButton("±");
        buttons[21] = new JButton("0");
        buttons[22] = new JButton(".");
        buttons[23] = new JButton("=");

        return buttons;
    }

    // functie auxiliara - determin daca operatia se face asupra primului operand
    public boolean isSelfOperator(String action)
    {

        return action.equals("x²") || action.equals("1/x") || action.equals("√") || action.equals("±");
    }

    // functie auxiliara - determin daca sunt necesari 2 operanzi
    public boolean isArithmeticOperator(String action)
    {

        return action.equals("+") || action.equals("-") || action.equals("*")
                || action.equals("/") || action.equals("%");
    }

    // functie auxiliara - determin daca am apasat butonul "="
    public boolean isEqualOperator(String action)
    {
        if(action.equals("="))
        {
            return true;
        }

        return false;

    }

    //functie pentru calcularea valorii curente - putea fi void, dar este mai explicit sa returneze valoarea
    public double computeCalculation(double currentValue)
    {
        if (operator.equals("+"))
        {
            firstOperand += currentValue;
        }
        if(operator.equals("-"))
        {
            firstOperand -= currentValue;
        }
        if(operator.equals("*"))
        {
            firstOperand *= currentValue;
        }
        if(operator.equals("/"))
        {
            if(currentValue != 0)
            {
                firstOperand /= currentValue;
            }
            else
            {
                accumulator = "Cannot divide by zero";
                //blochez butoanele
                outputField.setText(accumulator);
            }
        }
        if(operator.equals("%"))
        {
            firstOperand %= currentValue;
        }
            return firstOperand;
    }




    // actualizez afisajul
    public void updateDisplay (String action)
    {

        if((residualValue == 0) && (!accumulator.isEmpty())) // retin valoarea inainte de efectuarea functiei
        {
            residualValue = Double.parseDouble(accumulator);
        }
        else
        {
            accumulator = Double.toString(residualValue);
        }


        if(!accumulator.isEmpty())
        {
            if(isSelfOperator(operator))
            {
                updateSelfOperator(action);
            }
            if(isArithmeticOperator(action))
            {
                updateOperator(action);
            }
        }

        accumulator = Double.toString(residualValue); // in acumulator retin valoarea pe care urmeaza sa o adaug
        accumulatorField.setText(Double.toString(firstOperand));
        outputAccumulator = "";
        outputField.setText(outputAccumulator);

    }


    public void deleteOneDigit()
    {
        if(!accumulator.isEmpty())
        {
           accumulator = accumulator.substring(0, accumulator.length() - 1);
           accumulatorField.setText(accumulator);
        }
    }


    void initializeVariables()
    {
        // create a new frame to store text field and button
        frame = new JFrame("Calculator");

        // create a vector to store the calculator components
        buttons = createCalculator();

        // create a label to display text
        label = new JLabel("panel label");


        // creates text fields to display the output of the calculator
        accumulatorField = new JTextField("0");
        accumulatorField.setPreferredSize(new Dimension(300, 40));
        accumulatorField.setFont(myFont);

        // create a new panel where it adds the buttons
        calculatorPanel = new JPanel();
        outputPanel = new JPanel(new BorderLayout());


        outputField = new JTextField();
        outputField.setPreferredSize(new Dimension(300, 20));
        outputField.setEditable(false);

    }

    public void updateValue (String action)
    {
        /*if(outputAccumulator.isEmpty() && !accumulator.isEmpty())
        {
            outputAccumulator = accumulator;
        }*/
        if(outputAccumulator.isEmpty() && firstOperand != 0)
        {
            outputAccumulator = firstOperand.toString();
        }

        if(!started) //daca nu am facut nicio operatie pana acum
        {
            started = true;
            initializeOperator(action);
            outputField.setText(outputAccumulator + " " + operator + " ");
            updateOperator(action);
        }
        else // inseamna ca trebuie sa efectuez o operatie aritmetica cu operatorul salvat
        {
            updateOperator(action);
        }

        dotPressed = false;
    }
}







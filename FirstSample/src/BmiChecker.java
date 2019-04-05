/**
 * This software is produced under the GNU GPL Version 3, 29 June 2007
 * See the attached README file for the complete license.
 */

import java.text.DecimalFormat;
import java.util.Scanner;


/**
 * A command line program that prompts the user for his/her measurements and calculates his/her BMI.
 * @author Jeffrey Blomgren
 * @author Hizzi Moh
 *
 */
public class BmiChecker {

	enum UnitOfMeasure
	{
		Metric, Imperial, Unknown;
	}
	public static final String UOM_FIRST_PROMPT_TEXT = "Would you like to input your measurements in Metric (cm/kg) or Imperial (in/lb)? [Imperial]: ";
	public static final String UOM_SECOND_PROMPT_TEXT = "Please specify \"Metric\" or \"Imperial\" [Imperial]: ";
	public static final String HEIGHT_PROMPT_IMPERIAL = "Please specify your height in inches: ";
	public static final String HEIGHT_PROMPT_METRIC = "Please specify your height in centimeters: ";
	public static final String WEIGHT_PROMPT_IMPERIAL = "Please specify your weight in pounds: ";
	public static final String WEIGHT_PROMPT_METRIC= "Please specify your weight in kilos: ";
	public static final int MAX_HEIGHT_CENTIMETERS = 300;
	public static final int MAX_WEIGHT_KILOS = 1000;
	public static final double INCHES_TO_CENTIMETERS = 2.54;
	public static final double POUNDS_TO_KILOS = 1.0/2.205;
	
	/**
	 * @param args Not Used
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Hello!  This program will calculate your BMI based upon your height/weight.");
		Scanner myScanner = new Scanner(System.in);
		try
		{
		
			//Ask the user if he wants to work in Imperial or Metric.  The default entry of Imperial
			//Just means that if the user hits [Enter] without typing anything, we'll default to Imperial.
			UnitOfMeasure chosenUnits = promptForUom(UOM_FIRST_PROMPT_TEXT, myScanner, UnitOfMeasure.Imperial);
			//If the user gave us a bad answer, then the above function will return "Unknown".
			//Therefore, this loop will only execute if the user gives us a bad answer.
			while (chosenUnits == UnitOfMeasure.Unknown)
			{
				chosenUnits = promptForUom(UOM_SECOND_PROMPT_TEXT, myScanner, UnitOfMeasure.Imperial);
			}
			
			double heightInCentimeters = -1;
			double weightInKilos = -1;
			
			switch(chosenUnits)
			{
			case Metric:
				System.out.print(HEIGHT_PROMPT_METRIC);
				heightInCentimeters = myScanner.nextDouble();
				System.out.print(WEIGHT_PROMPT_METRIC);
				weightInKilos = myScanner.nextDouble();
				break;
			case Imperial:
				System.out.print(HEIGHT_PROMPT_IMPERIAL);
				heightInCentimeters = myScanner.nextDouble() * INCHES_TO_CENTIMETERS;
				System.out.print(WEIGHT_PROMPT_IMPERIAL);
				weightInKilos = myScanner.nextDouble() * POUNDS_TO_KILOS;
				break;
			default:
				throw new IllegalArgumentException("User has specified an supported Unit of Measure: " + chosenUnits);	
			}
			
			if (heightInCentimeters <= 0 || heightInCentimeters > MAX_HEIGHT_CENTIMETERS )
			{
				System.out.println("Illegal value entered for height.");
				throw new IllegalArgumentException("Specified input height value of " + heightInCentimeters +
						                           " is outside the allowed range of (0:" + MAX_HEIGHT_CENTIMETERS +
						                           "].");
			}
			
			if (weightInKilos <= 0 || weightInKilos > MAX_WEIGHT_KILOS )
			{
				System.out.println("Illegal value entered for weight.");
				throw new IllegalArgumentException("Specified input weight value of " + weightInKilos +
						                           " is outside the allowed range of (0:" + MAX_WEIGHT_KILOS +
						                           "].");
			}
			
			DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
			
			System.out.println("Now calculating your BMI.");
			double bmi = calculateBmi(heightInCentimeters, weightInKilos);
			System.out.println("Height: " + oneDecimalPlace.format(heightInCentimeters) + "cm");
			System.out.println("Weight: " + oneDecimalPlace.format(weightInKilos) + "kg");
			System.out.println("BMI:    " + oneDecimalPlace.format(bmi));
			System.out.println("You are: " + getBmiCategory(bmi));
		}
		finally
		{
			myScanner.close();
		}

	}
	
	/**
	 * Prompts the user ONCE for a unit of measure contained within the UnitOfMeasure enum.
	 * Scolds the user if they provide a wrong value, but does not prompt again.
	 * @param promptText The text to prompt the user with (e.g. "Please enter a unit of measure")
	 * @param theScanner The scanner which the user will provide input on.
	 * @return The unit of measure that the user specified, or Unknown if the user's input
	 *         could not be understood.
	 */
	public static UnitOfMeasure promptForUom(String promptText, Scanner theScanner)
	{
		//Yes, this function is just for people who are too lazy to provide a default UnitOfMeasure
		return promptForUom(promptText, theScanner, UnitOfMeasure.Unknown);
	}
	
	/**
	 * Prompts the user ONCE for a unit of measure contained within the UnitOfMeasure enum.
	 * Scolds the user if they provide a wrong value, but does not prompt again.
	 * If the user provides no input and just presses [Enter], then the defaultAnswer is returned.
	 * @param promptText The text to prompt the user with (e.g. "Please enter a unit of measure")
	 * @param theScanner The scanner which the user will provide input on.
	 * @param defaultAnswer Answer to return if the user just presses [Enter]
	 * @return The unit of measure that the user specified, or Unknown if the user's input
	 *         could not be understood.
	 */
	public static UnitOfMeasure promptForUom(String promptText, Scanner theScanner, UnitOfMeasure defaultAnswer)
	{
		if (theScanner == null)
		{
			throw new IllegalArgumentException("Scanner provided to promptForUom is NULL.  Prompt Text: " + promptText);
		}
		
		String str = "";
		System.out.print(promptText);
		str = theScanner.nextLine().trim();
		
		if (str.isEmpty())
		{
			return defaultAnswer;
		}
		else
		{
			for (UnitOfMeasure uom : UnitOfMeasure.values())
			{
				if (str.equalsIgnoreCase(uom.toString()))
				{
					return uom;
				}
			}
		}
		
		//If we get to the end of the function, then the user did not provide a sensible value.
		//So we will have to generate an error message.  Start by saying that the value was not recognized.
		System.out.print("Input value " + str + " is not recognized.  ");
		
		//Then create a list of the values that we recognize.  I've chosen to generate
		//this list dynamically, but I could've just hard-coded it.
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		for (UnitOfMeasure uom : UnitOfMeasure.values())
		{
			if (uom != UnitOfMeasure.Unknown)
			{
				sb.append(uom.toString());
				sb.append(", ");
			}
		}
		
		sb.setLength(sb.length() - 2);
		sb.append(" }");
		System.out.println("Here is a list of allowed values: " + sb.toString());
		
		return UnitOfMeasure.Unknown;
	}
	
	/**
	 * Calculates a Body Mass Index (BMI) based upon an entered height/weight.  No input validation is done.
	 * @param heightInCentimeters A human's height in centimeters.
	 * @param weightInKilograms A human's weight in kilograms.
	 * @return The human's calculate Body Mass Index
	 */
	public static double calculateBmi(double heightInCentimeters, double weightInKilograms)
	{
		//BMI formula is really simple!  Weight / Height^2
		//(For SI-units, Kilograms and Meters)
		double heightInMeters = heightInCentimeters / 100.0;
		return weightInKilograms/heightInMeters/heightInMeters;
	}
	
	/**
	 * Returns a string indicating what category the provided BMI is in.
	 * @param bmi The BMI to assess
	 * @return A string describing the cateogry the BMI is in (for example, "Normal Weight (18.5-25)")
	 */
	public static String getBmiCategory(double bmi)
	{
		if (bmi < 18.5)
		{
			return "Underweight (0-18.5)";
		}
		else if (bmi < 25)
		{
			return "Normal Weight (18.5-25)";
		}
		else if (bmi < 30)
		{
			return "Overweight (25-30)";
		}
		else
		{
			return "Obese (30+)";
		}
	}
}

import java.io.*;
import java.util.*;
/**
 * @author Ian Francis
 * Date 10/15/2024
 */
public class PasswordHandler 
{
	private static final String SPECIAL_CHARS = "!@#$%^&*()";
	
	public static void main(String[] args) throws Exception {
		Scanner IO = new Scanner(System.in);
		int option = 0;
		while(option != 5)
		{
			option = welcomePrompt(IO);
			switch(option){
			case 1: createPassword(IO); break;
			case 2: changePassword(IO); break;
			case 3: 
				System.out.print("Enter a password:");			
				if(isValid(IO.nextLine().trim()))
					System.out.println("Password is valid\n");
				else
					System.out.println("Password is not valid\n");
				break;
			case 4: 
				System.out.printf("\nSuggested Password: %s\n", suggestPassword()); break;
			}
		}
		IO.close();
	}

	private static int welcomePrompt(Scanner sc) {
		System.out.println("*/\\*Password Handler*/\\*");
		System.out.println("1. Create a password");
		System.out.println("2. Change your password");
		System.out.println("3. Check to see if a password is valid");
		System.out.println("4. Get a suggested password");
		System.out.println("5. Quit");
		System.out.print("Choose a number (1-5) ");
		String selection = sc.nextLine().trim();
		int num;
		try {
			num = Integer.parseInt(selection);
		}
		catch(NumberFormatException e) {
			System.out.println("\nSelection must be a number between 1 and 5\n");
			return welcomePrompt(sc);
		}
		if(num < 1 || num > 5) {
			System.out.println("\nSelection must be a number between 1 and 5\n");
			return welcomePrompt(sc);
		}
		return num;
	}

	/**
	 * When this method is called the file pwds.txt is created with a new valid password,
     * or if pwds.txt already exists nothing happens.
	 */
	public static void createPassword(Scanner s) throws IOException{
		try {
			File f = new File("pwds.txt");
			
		    if (f.createNewFile()) {
		    	System.out.println("File created!");
		    	System.out.println("Enter your password:");		    
		    	String pw = s.nextLine().trim(); // .trim()?
		    
		    	while (!(isValid(pw))) {
		    		System.out.println("This is not a valid password! Please enter a valid password:");
		    		pw = s.nextLine().trim();
		    	}
		    
		    	BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		    	writer.write(pw);
		    	writer.newLine();
		    	writer.close();
		    }
		    else
		    	System.out.println("File already exists.");
		}
		catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}

	/**
	 * When this method is called the file pwds.txt is accessed to update a new valid password,
     * or if pwds.txt doesn't exists nothing happens.
	 */
	public static void changePassword(Scanner s) {
		File f = new File("pwds.txt");
		ArrayList<String> passwords = passwordArray();

		if (!f.exists()) {
			System.out.println("File does not exist.");
			return;
		}
		
		if (passwords.isEmpty()) {
			System.out.println("There are no previous passwords stored.");
			return;
		}
		
		System.out.println("Enter your current password.");
		String currentPassword = s.nextLine().trim();
		
		if (!currentPassword.equals(passwords.get(passwords.size() - 1))) {
			System.out.println("Current password is incorrect.");
			return;
		}
		
		while (true) {
            System.out.print("Enter your new password: ");
            String newPassword = s.nextLine().trim();

            if (passwords.contains(newPassword)) {
                System.out.println("The new password has been used before. Please choose a different password.");
                continue;
            }

            if (!isValid(newPassword)) {
                System.out.println("The new password is invalid.");
                continue;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, true))) {
                writer.write(newPassword);
                writer.newLine();
            }
            catch (IOException e) {
                System.out.println("Error writing to the password file.");
                return;
            }

            System.out.println("Password has been changed successfully.");
            break;
        }
	}

	/**
	 * When this method is called password is checked to ensure that it is valid.
	 */
	public static boolean isValid(String password) {
		String uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowers = "abcdefghijklmnopqrstuvwxyz";
		String nums = "0123456789";
		
		boolean cUppers = false;
		boolean cLowers = false;
		boolean cNums = false;
		boolean cSpecials = false;
		boolean length = (password.length() >= 6 && password.length() <= 10);
		boolean used = passwordArray().contains(password);

		String separated = "";
		
		for (int i = 0; i < password.length(); i++) {
			separated += " ";
			separated += password.charAt(i);
		}
		
		StringTokenizer tokenizer = new StringTokenizer(separated, " ");	
		
		while (tokenizer.hasMoreTokens()) {
			String temp = tokenizer.nextToken();
			if (uppers.contains(temp)) cUppers = true;
			if (lowers.contains(temp)) cLowers = true;
			if (nums.contains(temp)) cNums = true;
			if (SPECIAL_CHARS.contains(temp)) cSpecials = true;	
			if (cUppers && cLowers && cNums && cSpecials) break;
			if (!uppers.contains(temp) && !lowers.contains(temp) && !nums.contains(temp) && !SPECIAL_CHARS.contains(temp)) {
				System.out.println("Contains character that is not allowed.");
				return false;
			}
		}
		
		if (!cUppers) System.out.println("Must contain an uppercase letter.");
		if (!cLowers) System.out.println("Must contain a lowecase letter.");
		if (!cNums) System.out.println("Must contain a number.");
		if (!cSpecials) System.out.println("Must contain a special character (!@#$%^&*()).");
		if (!length) System.out.println("Must be between 6 and 10 characters.");
		if (used) System.out.println("Password has already been used.");

		return (cUppers && cLowers && cNums && cSpecials && length && !used);
	}
	
	/**
	 * When this method is called a valid password is randomly generated and returned.
	 */
	public static String suggestPassword() {
		String pw = "";
		int pwLength = (int)(Math.random() * 5) + 6;
		
		ArrayList<Integer> arr = randNumbs(pwLength); 
		
		int capIndx = arr.get(0);
		int lowerIndx = arr.get(1);
		int specialIndx = arr.get(2);
		int numbIndx = arr.get(3);
		
		for (int i = 0; i < pwLength; i++) {
			if (i == capIndx) {
				char capital = (char)((int)(Math.random() * 26) + 65);
				pw += capital;
			}
			else if (i == lowerIndx) {
				char lower = (char)((int)(Math.random() * 26) + 97);
				pw += lower;
			}
			else if (i == specialIndx) {
				char special = SPECIAL_CHARS.charAt((int)(Math.random() * 8));
				pw += special;
			}
			else if (i == numbIndx) {
				char numb = (char)((int)(Math.random() * 10) + 48);
				pw += numb;
			}
			else {
				int charType = (int)(Math.random() * 4);
				if (charType == 0) {
					char capital = (char)((int)(Math.random() * 26) + 65);
					pw += capital;
				}
				else if (charType == 1) {
					char lower = (char)((int)(Math.random() * 26) + 97);
					pw += lower;
				}
				else if (charType == 2) {
					char special = SPECIAL_CHARS.charAt((int)(Math.random() * 8));
					pw += special;
				}
				else {
					char number = (char)((int)(Math.random() * 10) + 48);
					pw += number;
				}
			}
		}
		return pw; //quiets compiler
	}

	/*
	 * Returns an array the same length as pwLength that has its indexes randomly shuffled
	 */
	private static ArrayList<Integer> randNumbs(int pwLength) {
		ArrayList<Integer> arr = new ArrayList<>();
	    
	    for (int i = 0; i < pwLength; i++) arr.add(i);
	
	    Collections.shuffle(arr);
	    
		return arr;
	}
	
	/*
	 * Returns array with all the passwords on "pwds.txt" in it, last index has most recent PW
	 */
	private static ArrayList<String> passwordArray() {
		File f = new File("pwds.txt");
		ArrayList<String> passwords = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String currentPW;
			while ((currentPW = reader.readLine()) != null) {
				passwords.add(currentPW);
			}
			reader.close();
		}
		catch (Exception e) {}
		
		return passwords;
	}
}


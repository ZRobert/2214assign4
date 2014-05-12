/*********************************************************
* Main.java (Assignment4: Hashing)
* Author: Robert Payne
* Date: 8/8/2012
* Class: ITCS 2214-021

* Purpose: To read in a list of words and hash them to
* a hash table with as few collisions as possible! This
* implementation also has some testing features to 
* manually test the output by looking at the contents of
* the hash table in hashtable.txt and by being able to
* use the console to search the hash table to see how
* many rehashes the program needs to find the target.
/*********************************************************/
import java.util.Scanner;
import java.io.*;

public class Main {

/*
*	 
===========================================================
	Fields
---------------------------------------------------------*/ 
	public static String[] hashTable = new String[50000]; 
	public static int collisions = 0;
/*
*	main()	 
===========================================================
	Main opens the word file and reads in each of the words
	until there are no words left to read in. As each word
	is read in, main will call the hashing methods and 
	keep track of the indexes between them. After the keys
	have been hashed to the hash table, main outputs the
	table to a file and calls a driver method that allows
	the user to search the hashtable.
---------------------------------------------------------*/ 
	public static void main(String[] args)throws IOException {	
	 	
		Scanner keyboard = new Scanner(System.in);
		
		File openFile = new File("words.txt");
		Scanner inFile = new Scanner(openFile);
		String temp = null;
	
		int index;			//index for the hashTable
		int counter;		//counter for the thirdHashingFunction
		boolean found = false; //used for checking for an
									//available space for the key
									//during quadratic probing
		
			
		while(inFile.hasNext()) {
			
			//initializing variables
			//for a new key	
			found = false;
			temp = inFile.nextLine();
			temp = temp.trim();
			index = 31;
			counter = 1;
				
			//first hash attempt
			index = hashFunction(temp);

				
			if(hashTable[index] != null){
				
				//add a collision after the first test if
				//the index is not empty
				collisions++;					
				index = secondHashFunction(index, temp);
				
				//if there is something in index after
				//second hash function, then increase
				//the collision counter
				if(hashTable[index] != null) {
					collisions++;
				do{
				
					//calling the quadratic probing function
					index = thirdHashFunction(index, counter);
					
					if(hashTable[index]== null)
						found = true;
					
					//if avaiable space not found, then add a collision 
					else
						collisions++;
					
					counter++;
				
				}while(!found);
			}
		}		
		
		//it's empty, so adding to the hashtable
		hashTable[index] = temp;			
	}//end of the hashing
	
	//number of collisions output	
	System.out.println("Collisions: " + collisions);

//to delete half of the hash table		
//	for(int i = 0; i < 24999; i++)
//		hashTable[i] = null;
	
	//close word.txt	
	inFile.close();
	//for console control
	driver();
	//output the hashtable to hashtable.txt
	outFile();
	
	}//end of main

/*
*	hashFunction(word)	 
===========================================================
	This is the first hashing function called for each
	new word being hashed to the hash table. This 
	function takes the word and get's it's length and
	sets the initial indexing value to 31.
---------------------------------------------------------*/
	public static int hashFunction (String word) {

		//length of the word being hashed
		int length = word.length();
		//starting number for hash index calculation
		int index = 31;
		
		//loop to include all of the characters
		//from the string into the calculation
		//to make it unique per word
		for(int i = 0; i < length; i++)
		{
			index+= (int)word.charAt(i); //adds index and char together 
			index+= (index<<8);	//does a left shift on index and stores it in index
			index+= (index>>6);	//does a right shift on index and stores it in index
		}
		
		
		index+=(index<<3); //left shift 3
		index^=(index>>11);	//right shift 11
		index+=(index<<15);

		index = index % 50000;	//mod by array length to keep in bounds
		return Math.abs(index); //absolute value error handling		
	}

/*
*	secondHashFunction(index, word)	 
===========================================================
	Second hash function takes the word being hashed and
	the last index calculated by the first hash function
	that did not work, and does some further manipulations
	to the index.
---------------------------------------------------------*/
	public static int secondHashFunction(int index, String word) {
		
		//length of the word being hashed
		int length = word.length();
		
		//looping through the string:
		//shifts c left 3 and ORs it with itself
		//unsigned shifted right 28 and ORs it
		//with 16 bit character from the string
		for(int i = 0; i < length; i++)
			index = (index <<3) ^ (index>>>28) ^ word.charAt(i);
		
		index = (index + 1) % 50000; //mod to keep in bounds	
		return Math.abs(c);  //absolute value to keep from having a neg index		
	}
	
/*
*	thirdHashFunction(index, count)	 
===========================================================
	This is a quadratic hashing function. It starts with
	the index that was left over from the second hashing
	function and it uses a counting loop variable named
	count and squares it. This hash will loop until the
	key finds a spot in the hash table. I found it 
	exceedingly difficult to get a hash working without
	either a linear or quadratic function for probing.
---------------------------------------------------------*/
	public static int thirdHashFunction(int c, int count)
	{	
		//quadratic hash last resort
		c = c + count * count;

		c %= 50000;
		return c;
	}
	

/*
*	driver()	 
===========================================================
	Driver simply gives the user the ability to search
	for elements in the hashTable while being able
	to cross-reference the hashtable.txt file.
---------------------------------------------------------*/
	public static void driver()throws IOException {
	
		Scanner keyboard = new Scanner(System.in);
		boolean found;
		String input;
		
		System.out.print("Enter a command: ");
		input = keyboard.nextLine();
		
		do{
			found = false;
			System.out.print(input);
			switch(input.charAt(0)){
			
				case 'f':
					
					System.out.print("Enter a string to search for: ");
					input = keyboard.nextLine();
					found = search(input);
					if(found)
						System.out.println(input + " was found!");
					else
						System.out.println(input + " was not found!");
					break;
						
				case 'o':
					System.out.println("Output to file...");
					outFile();
					System.out.println("Output complete");
					break;
					
				default:
					System.out.println("Invalid input");
			}
			
			System.out.print("Enter a command: ");
			input = keyboard.nextLine();
			
		
		}while(input.charAt(0) != 'q');
	}
/*
*	outFile()	 
===========================================================
	Writes the contents of the hashTable into the
	hashtable.txt file. Writes them in 10 columns
	with all the null spaces for easier visualization
	of how the hashing functions are distributing the
	keys across the table for hole analysis.
---------------------------------------------------------*/
	public static void outFile()throws IOException {
	
		PrintWriter outFile = new PrintWriter("hashtable.txt");

	 
  		for(int i = 0; i < 49999; i++){    
			outFile.print(hashTable[i] + "   ");
  			if((i% 10)== 9)
			{
				outFile.println();
			}
		}    
		
		outFile.close();
	}	
/*
*	search(word)	 
===========================================================
	Called from the driver function. Takes a string as
	an argument and checks to see if the hashTable 
	contains the file. It searches the table by making
	a hash code just like when inserting into the table.
	It also reports how many checks were needed to come
	up with a hash code that finds the target.
---------------------------------------------------------*/	
	public static boolean search(String word) {

		int index = 31;
		int counter = 1;
		boolean found = false;
	
		index = hashFunction(word);
		System.out.println("INDEX: " + index);
	
		if(hashTable[index] != null)
			if(hashTable[index].compareTo(word) == 0 ) {
			
				System.out.println("Found on the first check");
				return true;
			}
	
		index = secondHashFunction(index, word);
	
		if (hashTable[index] != null) {
	 
			if(hashTable[index].compareTo(word) == 0){
			
				System.out.println("Found on the second check");
				return true;
		
			}
		}	
	
		do{
			
			index = thirdHashFunction(index, counter);
		
			if(hashTable[index] != null){
				if(hashTable[index].compareTo(word) == 0){	
				
					System.out.println("Found on the " + (counter + 2) + " check."); //sagest with half of table deleted
					return true;
				}
			}
			
			counter++;

			if(counter > 1000)
				return false;
	
			}while(!found);
		
		return false;
	
	}
}
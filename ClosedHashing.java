package edu.cofc.csci230;

/**
 * 
 * Closed hashing data structure using linear probing.
 * 
 * @author CSCI 230: Data Structures and Algorithms Fall 2017
 *
 */
public class ClosedHashing extends HashTable { 

	/* private instance variables */
	private String[] hash_table;

	/**
	 * Constructor
	 */
	public ClosedHashing( int hash_function ) {

		this.hash_function = hash_function;

	} // end constructor

	/**
	 * Initialize the hash table
	 * 
	 * The number of elements in the hash table is equal to 2 x the number of words 
	 * in the word list.
	 * 
	 */
	public void initialize() {

		hash_table = new String[ 2*words.size() ];

		for ( int i=0; i < words.size(); i++ ) {

			hash_table[i] = null;

		}

	} // end initialize() method



	/**
	 * Search for key in the hash table.
	 * 
	 * In this implementation, a lazy character "^" (at the beginning of the 
	 * string value) is used to indicate a collision has occurred. The number 
	 * of lazy characters indicate the number of collisions,e.g. "^^" would 
	 * indicate two collisions have occurred.
	 * 
	 * Exceptions: If the key does not exist in the hash table, then throw 
	 *             a HashTableKeyException
	 * 
	 * return: The number of linear probes needed to find the key in the 
	 * 		   hash table, e.g. 1 if no probing, n if probed n times to
	 *         find an open location.
	 * 
	 * @param key
	 * @return
	 */
	public int search( String key ) throws HashTableKeyException {

		int probes = 0;

		/* ----------------------------------
		 * TODO: Put your solution here
		 * ----------------------------------
		 */

		boolean found = false;
		int hashKey = calcHash(key);


		if (hash_table[hashKey] == null) {
			probes ++;

			throw new HashTableKeyException("Key not found! Probes = " + probes);

		}else {
			
			String wordWithoutCarrots = hash_table[hashKey].replaceAll("\\^", "");
			if(wordWithoutCarrots.compareTo(key) == 0) {

				probes++;

			}else { 

				//count the number of carrots
				int numberOfCollisions = hash_table[hashKey].lastIndexOf('^') + 1;
				int numberEqualHashFound = 0;
				int i = hashKey + 1;

				while((numberEqualHashFound < numberOfCollisions) && i < hash_table.length && !found) {

					if(hash_table[i] != null && !found) {

						String wordWithoutCarrotsAtPos = hash_table[i].replaceAll("\\^", "");
						int hashOfWord = calcHash(wordWithoutCarrotsAtPos);

						if (hashOfWord == hashKey) {

							numberEqualHashFound++;
							probes++;


							if (wordWithoutCarrotsAtPos.compareTo(key) == 0) found = true;

						}
						i++;
					}
				}

				if(numberEqualHashFound < numberOfCollisions && !found) {
					int j = 0;
					while((numberEqualHashFound < numberOfCollisions) && j < hashKey) {

						if (hash_table[j] != null) {

							String wordWithoutCarrotAtPos = hash_table[j].replaceAll("\\^", "");
							int hashOfWord = calcHash(wordWithoutCarrotAtPos);

							if (hashOfWord == hashKey) {
								
								numberEqualHashFound++;
								probes++;


								if (wordWithoutCarrotAtPos.compareTo(key) == 0) found = true;
							}
						}
						j++;

					}//end while
				}//end if

			}//end else

		}//end else
		
		if (!found) throw new HashTableKeyException("Probes: " + probes);


		return probes;


	} // end search() method

	/**
	 * Insert key into hash table
	 * 
	 * In this implementation, a lazy character "^" (at the beginning of the 
	 * string value) is used to indicate a collision has occurred. The number 
	 * of lazy characters indicate the number of collisions,e.g. "^^" would 
	 * indicate two collisions have occurred.
	 * 
	 * Exceptions: Duplicate key values are not allowed e.g., if "dog" is 
	 * 		       already exists in the hash table, then another 
	 * 			   "dog" key cannot be inserted. In this instance, throw a
	 * 			   HashTableKeyException.
	 * 
	 * @param key
	 */
	public void insert( String key ) throws HashTableKeyException {

		/* ----------------------------------
		 * TODO: Put your solution here
		 * ----------------------------------
		 */

		int hash = calcHash(key);


		if( hash_table[hash] == null ) {

			hash_table[hash] = key;

		}else {

			String word = hash_table[hash].replaceAll("\\^", "");
			if (word.compareTo(key) == 0) {

				throw new HashTableKeyException("Key already exists!");
				
			}else {
				//if there was a collision:
				if (hash_table[hash].charAt(0) == '^') { 
					
					int numberOfCollisions = hash_table[hash].lastIndexOf('^') + 1;
					int numberEqualHashFound = 0;
					int i = hash + 1;
					
					//start from middle of table and go to the end
					//stop if we have found all of the collisions
					//here we are just checking to make sure that the key isnt already in the list
					while((numberEqualHashFound < numberOfCollisions) && i < hash_table.length) {
						
						if(hash_table[i] != null) { //check the hash if theres a string
					
							String wordWithoutCarrotsAtPos = hash_table[i].replaceAll("\\^", "");
							int hashOfWord = calcHash(wordWithoutCarrotsAtPos);
							if (hashOfWord == hash) {
								numberEqualHashFound++; //have the same hash so increment
								//if the word is the same as the key then throw exception
								if (wordWithoutCarrotsAtPos.compareTo(key) == 0) throw new HashTableKeyException("Key already exists!");
							}

						}
						i++;
					}
					//only need to do this from beginning to middle if we havent found all the collisions
					if(numberEqualHashFound < numberOfCollisions) { //I could take this out but it's 10:54 and i'm afraid to
						//mess anything up
						int j = 0;
						//essentially do the same thing but from beginning 
						while((numberEqualHashFound < numberOfCollisions) && j < hash) {
							if (hash_table[j] != null) {
								String wordWithoutCarrotAtPos = hash_table[j].replaceAll("\\^", "");
								int hashOfWord = calcHash(wordWithoutCarrotAtPos);

								if (hashOfWord == hash) {
									numberEqualHashFound++;
									if (wordWithoutCarrotAtPos.compareTo(key) == 0) throw new HashTableKeyException("Key already exists!");
								}

							}
							j++;

						}
					}

				}

				//now we know for sure that the word isn't in the list so we may proceed in adding

				hash_table[hash] = "^" +  hash_table[hash]; //add a carrot bc we know we have collision
				
				boolean added = false;
				int k = hash;
				//the same logic as loops for checking for duplicates but now looking for a place to put the key (empty spot)
				while( k < hash_table.length && !added) {
					k++;
					if (hash_table[k] == null) {
						hash_table[k] = key;

						added = true;

					}

				}

				if(!added) {
					int j = 0;
					while(j < hash && !added) {
						if (hash_table[j] == null) {
							hash_table[j] = key;
							added = true;


						}
						j++;

					}

				}
			}

		}


	}// end insert() method

	/**
	 * Delete a key from the hash table. 
	 * 
	 * In this implementation, a lazy character "^" (at the beginning of the 
	 * string value) is used to indicate a collision has occurred. The number 
	 * of lazy characters indicate the number of collisions,e.g. "^^" would 
	 * indicate two collisions have occurred. Every successful deletion 
	 * should remove one "^" symbol. 
	 * 
	 * Exceptions: if they key does not exist in the hash table then throw
	 * 			   a HashTableKeyException
	 * 
	 * return: The number of probes needed to find the key in the hash table,
	 *         e.g. 1 if the key was the first element in the list, n if it was 
	 *         the very last element in the list, where n is the number of elements 
	 *         in the list.
	 * 
	 * @param key
	 * @return
	 */
	public int delete ( String key ) throws HashTableKeyException {

		int probes = 0;

		/* ----------------------------------
		 * TODO: Put your solution here
		 * ----------------------------------
		 */

		try {

			this.search(key); // if it's not in the table, this will throw HTK

			int hashKey = calcHash(key);

			String word = hash_table[hashKey].replaceAll("\\^", "");

			int numberOfCollisions = hash_table[hashKey].lastIndexOf('^') + 1;

			if(word.compareTo(key) == 0) { //word at this location is our delete key

				probes++;
				StringBuilder newString = new StringBuilder();

				for(int i = 0; i < numberOfCollisions; i++) {

					newString.append("^");

				}

				hash_table[hashKey] = newString.toString();

			}else{ //there was a collision and the key is somewhere else
				probes++;
				int numberEqualHashFound = 0;
				int i = hashKey + 1;
				boolean found = false;
				//look for values from the hash to the end, stop if we find 
				//all of the words with an equal hash
				while((numberEqualHashFound < numberOfCollisions) && i < hash_table.length && !found) {
					
					
					if(hash_table[i] != null) {
						//calculate the hash
						String wordWithoutCarrotsAtPos = hash_table[i].replaceAll("\\^", "");
						int hashOfWord = calcHash(wordWithoutCarrotsAtPos);
						
						if (hashOfWord == hashKey) {
							
							numberEqualHashFound++;

							
							if (wordWithoutCarrotsAtPos.compareTo(key) == 0) {
								
								probes++;
								
								StringBuilder newString = new StringBuilder();

								for(int j = 0; j < (numberOfCollisions - 1); j++) {

									newString.append("^");

								}
								
								hash_table[hashKey] = newString + word;
								found = true;
								
							}
						}
						i++;
					}
				}
				if((numberEqualHashFound < numberOfCollisions) && !found) {
					int j = 0;
					while((numberEqualHashFound < numberOfCollisions) && j < hashKey && !found) {
						
						if (hash_table[j] != null) {

							String wordWithoutCarrotAtPos = hash_table[j].replaceAll("\\^", "");
							int hashOfWord = calcHash(wordWithoutCarrotAtPos);

							if (hashOfWord == hashKey) {
								numberEqualHashFound++;
								probes++;
								if (wordWithoutCarrotAtPos.compareTo(key) == 0) {
									
									StringBuilder newString = new StringBuilder();

									for(int k = 0; k < (numberOfCollisions - 1); k++) {

										newString.append("^");

									}
									
									hash_table[hashKey] = newString + word;
									found = true;
									
								}
								
							}

						}
						j++;

					}
				}

			}
		}catch(HashTableKeyException e) {
			probes++;
		}
		
		return probes;

} // end delete() method

/**
 * See page 271 in supplemental course textbook (chapter is provided as PDF 
 * on OAKS in content section).
 * 
 * Also, refer to your lecture notes. Note, for closed hashing, m is 
 * the number of locations in the hash table.
 * 
 * @return
 */
public double loadFactor() {

	/* ----------------------------------
	 * TODO: Put your solution here
	 * ----------------------------------
	 */

	//QUESTION: What if there's an index with a ^ and no word
	double n = 0.0;
	for (int i = 0; i < hash_table.length; i++) {
		if ( hash_table[i] != null ) {
			n++;
		}

	}


	return (double) n / hash_table.length;

} // end loadFactor() method


/**
 * See equation (7.5) on page 273 in supplemental course textbook (chapter 
 * is provided as PDF on OAKS in content section).
 * 
 * @return
 */
public double successfulSearches() {

	/* ----------------------------------
	 * TODO: Put your solution here
	 * ----------------------------------
	 */

	return .5 * (1.0 + (1.0/ (1-this.loadFactor())));

} // end successfulSearches() method

/**
 * See equation (7.5) on page 273 in supplemental course textbook (chapter 
 * is provided as PDF on OAKS in content section).
 * 
 * @return
 */
public double unsuccessfulSearches() {

	/* ----------------------------------
	 * TODO: Put your solution here
	 * ----------------------------------
	 */

	return .5 * ( 1.0 + 1.0 / Math.pow(1 - this.loadFactor(), 2) );

} // end unsuccessfulSearches() method


/**
 * 
 * @param args
 */
public static void main( String[] args ) {

	ClosedHashing hashDS = new ClosedHashing( HashTable.HASH_FUNC1 );
	System.out.printf("\n%s ----------------------------------\n", "Closed Hashing: FUNC1" );

	if ( hashDS.loadWords() ) {

		hashDS.initialize();

		System.out.printf( "Number of words in list = %d\n", words.size() );

		/* ------------------------------------------------
		 * TODO:
		 * ------------------------------------------------
		 * 1) Insert each word into hash data structure
		 * 2) Calculate load factor value and print to 
		 *    console (using System.printf or System.println)
		 * 3) Calculate successful searches value and print to 
		 *    console (using System.printf or System.println)
		 * 4) Calculate unsuccessful searches value and print to 
		 *    console (using System.printf or System.println)
		 * 5) For each word in words list, search in the hashDS, 
		 *    and print mean probe value to console (using System.printf or System.println)
		 * 6) For a word that does not exist in hashDS, search in 
		 *    the hashDS, print the probe value to console (using System.printf or System.println)
		 * 7) For each word in words list, delete word in the hashDS, 
		 *    and print mean probe value to console (using System.printf or System.println)
		 * 8) For a word that does not exist in hashDS, delete in 
		 *    the hashDS, print the probe value to console (using System.printf or System.println)
		 * 
		 */

		for(int i = 0; i < words.size(); i++) {
			try {
				hashDS.insert(words.get(i));
			}catch(HashTableKeyException e) {}
		}


		//2) Calculate load factor value and print to 
		//console (using System.printf or System.println)
		System.out.println("Load Factor:\t\t" + hashDS.loadFactor());

		//3) Calculate successful searches value and print to 
		//console (using System.printf or System.println)
		System.out.println("Successful Searches:\t" + hashDS.successfulSearches());

		//4) Calculate unsuccessful searches value and print to 
		//console (using System.printf or System.println)
		System.out.println("Unsuccessful Searches:\t" + hashDS.unsuccessfulSearches());

		//5) For each word in words list, search in the hashDS, 
		//and print mean probe value to console (using System.printf or System.println)

		double probes = 0;
		for (int i = 0; i < words.size(); i++) {
			try {
				probes += hashDS.search(words.get(i));
			}catch(HashTableKeyException e) {}
		}

		double meanSearchProbes = probes / words.size();
		System.out.println("Mean Search Probes:\t" + meanSearchProbes);

		//6) For a word that does not exist in hashDS, search in 
		//the hashDS, print the probe value to console (using System.printf or System.println)

		try {
			hashDS.search("kendall");
			System.out.println("Search Failed");
		}catch(HashTableKeyException e) {
			System.out.println("Number of probles in Search = " +e.getMessage());
		}
		
		 //7) For each word in words list, delete word in the hashDS,
		 //and print mean probe value to console (using System.printf or System.println)
		double probesDelete = 0;
		for (int j = 0; j < words.size(); j++) {
			
			try {
				probesDelete += hashDS.delete(words.get(j));
			}catch(HashTableKeyException e) {
			}
			
		}
		
		double meanProbesDelete = probesDelete / words.size();
		System.out.println("Mean probes for Delete:\t" + meanProbesDelete);
		
		
		//8) For a word that does not exist in hashDS, delete in
		//the hashDS, print the probe value to console (using System.printf or System.println)
		try {
			System.out.println("Probes for Delete not found:\t" + hashDS.delete("Kendall"));
		}catch(HashTableKeyException e) {
			System.out.println(e.getMessage());
		}

	} else {

		System.out.println("Failed to load words from text file" );
	}

	// ------------------------------------------------
	// Repeat for second hash function

	System.out.printf("\n%s ----------------------------------\n", "Closed Hashing: FUNC2" );
	hashDS = new ClosedHashing( HashTable.HASH_FUNC2 );

	if ( hashDS.loadWords() ) {

		hashDS.initialize();

		System.out.printf( "Number of words in list = %d\n", words.size() );

		/* ------------------------------------------------
		 * TODO:
		 * ------------------------------------------------
		 * 1) Insert each word into hash data structure
		 * 2) Calculate load factor value and print to 
		 *    console (using System.printf or System.println)
		 * 3) Calculate successful searches value and print to 
		 *    console (using System.printf or System.println)
		 * 4) Calculate unsuccessful searches value and print to 
		 *    console (using System.printf or System.println)
		 * 5) For each word in words list, search in the hashDS, 
		 *    and print mean probe value to console (using System.printf or System.println)
		 * 6) For a word that does not exist in hashDS, search in 
		 *    the hashDS, print the probe value to console (using System.printf or System.println)
		 * 7) For each word in words list, delete word in the hashDS, 
		 *    and print mean probe value to console (using System.printf or System.println)
		 * 8) For a word that does not exist in hashDS, delete in 
		 *    the hashDS, print the probe value to console (using System.printf or System.println)
		 * 
		 */

		for(int i = 0; i < words.size(); i++) {
			try {
				hashDS.insert(words.get(i));
			}catch(HashTableKeyException e) {}
		}


		//2) Calculate load factor value and print to 
		//console (using System.printf or System.println)
		System.out.println("Load Factor:\t\t" + hashDS.loadFactor());

		//3) Calculate successful searches value and print to 
		//console (using System.printf or System.println)
		System.out.println("Successful Searches:\t" + hashDS.successfulSearches());

		//4) Calculate unsuccessful searches value and print to 
		//console (using System.printf or System.println)
		System.out.println("Unsuccessful Searches:\t" + hashDS.unsuccessfulSearches());

		//5) For each word in words list, search in the hashDS, 
		//and print mean probe value to console (using System.printf or System.println)

		double probes = 0;
		for (int i = 0; i < words.size(); i++) {
			try {
				probes += hashDS.search(words.get(i));
			}catch(HashTableKeyException e) {}
		}

		double meanSearchProbes = probes / words.size();
		System.out.println("Mean Search Probes:\t" + meanSearchProbes);

		//6) For a word that does not exist in hashDS, search in 
		//the hashDS, print the probe value to console (using System.printf or System.println)

		try {
			hashDS.search("kendall");
			System.out.println("Search Failed");
		}catch(HashTableKeyException e) {
			System.out.println("Number of probes in Search = "+ e.getMessage());
		}
		

		 //7) For each word in words list, delete word in the hashDS,
		 //and print mean probe value to console (using System.printf or System.println)
		double probesDelete = 0;
		for (int j = 0; j < words.size(); j++) {
			
			try {
				probesDelete += hashDS.delete(words.get(j));
			}catch(HashTableKeyException e) {
			}
			
		}
		
		double meanProbesDelete = probesDelete / words.size();
		System.out.println("Mean probes for Delete:\t" + meanProbesDelete);
		
		
		//8) For a word that does not exist in hashDS, delete in
		//the hashDS, print the probe value to console (using System.printf or System.println)
		try {
			System.out.println("Probes for Delete not found:\t" + hashDS.delete("Kendall"));
		}catch(HashTableKeyException e) {
			System.out.println(e.getMessage());
		}
	} else {

		System.out.println("Failed to load words from text file" );
	}

} // end main() method

} // end ClosedHashing class definition
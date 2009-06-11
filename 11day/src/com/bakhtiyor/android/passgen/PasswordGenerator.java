/*
 * Copyright (c) 2009 Bakhtiyor Khodjayev (http://www.bakhtiyor.com/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bakhtiyor.android.passgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PasswordGenerator {
	public final static String LOWER_CASE = "abcdefghijklmnopqrstuvwyxz";
	public final static String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWYXZ";
	public final static String NUMBERS = "0123456789";
	public final static String PUNCTUATIONS = "~!@#$%^&*()-+='\",.?";
	@SuppressWarnings("serial")
	public final static Map<Character, String> PHONETIC_ALPHABET = new HashMap<Character, String>() {
		{
			put('a', "Alpha");
			put('b', "Bravo");
			put('c', "Charlie");
			put('d', "Delta");
			put('e', "Echo");
			put('f', "Foxtrot");
			put('g', "Golf");
			put('h', "Hotel");
			put('i', "India");
			put('j', "Juliet");
			put('k', "Kilo");
			put('l', "Lima");
			put('m', "Mike");
			put('n', "November");
			put('o', "Oscar");
			put('p', "Papa");
			put('q', "Quebec");
			put('r', "Romeo");
			put('s', "Sierra");
			put('t', "Tango");
			put('u', "Uniform");
			put('v', "Victor");
			put('w', "Whiskey");
			put('x', "X-ray");
			put('y', "Yankee");
			put('z', "Zulu");

			put('A', "ALPHA");
			put('B', "BRAVO");
			put('C', "CHARLIE");
			put('D', "DELTA");
			put('E', "ECHO");
			put('F', "FOXTROT");
			put('G', "GOLF");
			put('H', "HOTEL");
			put('I', "INDIA");
			put('J', "JULIET");
			put('K', "KILO");
			put('L', "LIMA");
			put('M', "MIKE");
			put('N', "NOVEMBER");
			put('O', "OSCAR");
			put('P', "PAPA");
			put('Q', "QUEBEC");
			put('R', "ROMEO");
			put('S', "SIERRA");
			put('T', "TANGO");
			put('U', "UNIFORM");
			put('V', "VICTOR");
			put('W', "WHISKEY");
			put('X', "X-RAY");
			put('Y', "YANKEE");
			put('Z', "ZULU");

			put('0', "Zero");
			put('1', "One");
			put('2', "Two");
			put('3', "Three");
			put('4', "Four");
			put('5', "Five");
			put('6', "Six");
			put('7', "Seven");
			put('8', "Eight");
			put('9', "Nine");

			put('~', "Tilda");
			put('!', "Exclamation Mark");
			put('@', "At Symbol");
			put('#', "Pound Sign");
			put('$', "Dollar Sign");
			put('%', "Percent");
			put('^', "Carrot Symbol");
			put('&', "Ampersand");
			put('*', "Asterisk");
			put('(', "Left Parenthesis");
			put(')', "Right Parenthesis");
			put('-', "Minus Sign");
			put('+', "Plus Sign");
			put('=', "Equals Sign");
			put('\'', "Apostrophe");
			put('"', "Quote");
			put(',', "Comma");
			put('.', "Period");
			put('?', "Question Mark");
		}
	};

	public String generatePassword(int length, boolean includeLowerCase, boolean includeUpperCase,
			boolean includeNumbers, boolean includePunctuations) {
		String base = "";
		if (includeLowerCase) {
			base += LOWER_CASE;
		}
		if (includeUpperCase) {
			base += UPPER_CASE;
		}
		if (includeNumbers) {
			base += NUMBERS;
		}
		if (includePunctuations) {
			base += PUNCTUATIONS;
		}
		String result = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			result += base.charAt(random.nextInt(base.length()));
		}
		return result;
	}

	public int getPasswordStrength(String password) {
		boolean includeLowerCase = false;
		boolean includeUpperCase = false;
		boolean includeNumbers = false;
		boolean includePunctuations = false;
		for (int i = 0; i < password.length(); i++) {
			CharSequence cs = String.valueOf(password.charAt(i));
			if (LOWER_CASE.contains(cs)) {
				includeLowerCase = true;
			} else if (UPPER_CASE.contains(cs)) {
				includeUpperCase = true;
			} else if (NUMBERS.contains(cs)) {
				includeNumbers = true;
			} else if (PUNCTUATIONS.contains(cs)) {
				includePunctuations = true;
			}
		}
		float bitStrength = 0;
		if (includeNumbers && includeLowerCase && includeUpperCase && includePunctuations) {
			bitStrength = 6.55f;
		} else if (includeNumbers && includeLowerCase && includeUpperCase) {
			bitStrength = 5.95f;
		} else if (includeNumbers && includeLowerCase) {
			bitStrength = 5.17f;
		} else if (includeLowerCase) {
			bitStrength = 4.7f;
		} else if (includeNumbers) {
			bitStrength = 3.32f;
		}
		return Math.round(bitStrength * password.length());
	}

	public String getPhonetic(String str) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < str.length(); i++) {
			if (PHONETIC_ALPHABET.get(str.charAt(i)) != null) {
				result.add(PHONETIC_ALPHABET.get(str.charAt(i)));
			}
		}
		return !result.isEmpty() ? join(result, " ") : "";
	}

	private String join(Collection<?> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
			if (iter.hasNext()) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}
}

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
package com.bakhtiyor.android.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


class RssParser extends DefaultHandler {
	private class Item {
		String code;
		Double rate;
	}

	private static final Pattern codePattern = Pattern.compile("^([A-Z]{3})\\/(?:.*)$");
	private static final Pattern ratePattern = Pattern
			.compile("^(?:[.\\d\\w\\s]*)=([.\\s\\d]+)(?:.*)$");
	private final Map<String, Double> rates = new HashMap<String, Double>();
	private final StringBuilder text = new StringBuilder();
	private final InputStream instream;
	private Item item;

	public RssParser(InputStream instream) {
		super();
		this.instream = instream;
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		text.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (localName.equalsIgnoreCase("item")) {
			if (item != null && item.code != null && item.rate != null) {
				rates.put(item.code, item.rate);
			}
			item = null;
		} else if (item != null && localName.equalsIgnoreCase("title")) {
			item.code = getCode(text.toString().trim());
		} else if (item != null && localName.equalsIgnoreCase("description")) {
			item.rate = getRate(text.toString().trim());
		}
		text.setLength(0);
	}

	public Currency parse(String code) throws ParserConfigurationException, SAXException,
			IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		if (spf != null) {
			sp = spf.newSAXParser();
			sp.parse(instream, this);
		}
		if (!rates.isEmpty())
			return new Currency(code, rates);
		return null;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException {
		if (localName.equalsIgnoreCase("item")) {
			item = new Item();
		}
	}

	private String getCode(String value) {
		Matcher m = codePattern.matcher(value);
		if (m.find())
			return m.group(1);
		return null;
	}

	private Double getRate(String value) {
		Matcher m = ratePattern.matcher(value);
		if (m.find())
			return Double.parseDouble(m.group(1).trim());
		return null;
	}

}
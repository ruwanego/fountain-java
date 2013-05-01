package com.thp.fountain;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class FNParser {
	public static final Pattern DIRECTIVE_PATTERN = Pattern.compile("^([^\\t\\s][^:]+):([\\t\\s]*$)");
	public static final Pattern INLINE_PATTERN  = Pattern.compile("^([^\\t\\s][^:]+):\\s*([^\\t\\s].*$)");
	public static final Pattern CONTENT_PATTERN = Pattern.compile("");

	private List<FNElement> elements;
	private Map<String,String> titlePage;
	private String contents;

	/**
	 * Creates empty FNParser
	 */
	public FNParser() {
		elements = new ArrayList<FNElement>();
		titlePage = new HashMap<String, String>();
	}

	/**
	 * Creates FNParser and parses the fountain formatted
	 * string into a title page and list of FNElements
	 * @param nContents A fountain formatted string
	 */
	public FNParser(final String nContents) {
		this();
		parseContents(nContents);
	}

	/**
	 * Creates FNParser and parses the fountain formatted
	 * file into a title page and list of FNElements
	 * @param nFile A fountain formatted file
	 */
	public FNParser(final File nFile) {
		this();
		try {
			final Scanner reader = new Scanner(nFile);
			StringBuilder file = new StringBuilder();

			while (reader.hasNextLine()) {
				file.append(String.format("%s\n", reader.nextLine()));
			}

			reader.close();

			parseContents(file.toString());
		} catch (Exception e) {
			System.err.printf("Could not read file %s%n", nFile);
		}
	}

	/**
	 * Given a string of fountain, parse into a title page and
	 * a list of FNElements
	 * @param nContents A fountain formatted string
	 */
	public void parseContents(final String nContents) {
		contents = nContents;

		// Trim newlines
		contents = contents.trim();
		// Uniform line endings
		contents = contents.replaceAll("\\r\\n|\\r|\\n", "\n");
		contents = String.format("%s\n\n", contents);

		// Find first blank line
		final int firstBlankLine = contents.indexOf("\n\n");
		String topOfDocument = contents.substring(0, firstBlankLine);

		////////////////////////////////////////////////////////////
		// TITLE PAGE
		////////////////////////////////////////////////////////////
		// Is there a title page present?
		boolean foundTitlePage = false;
		String openKey = "";
		StringBuilder openValues = new StringBuilder();
		String[] topLines = topOfDocument.split("\n");

		for (String line : topLines) {
			final Matcher directive = DIRECTIVE_PATTERN.matcher(line);
			final Matcher inline = INLINE_PATTERN.matcher(line);
			final Matcher content = CONTENT_PATTERN.matcher(line);
			if (line.equals("") || directive.matches()) {
				foundTitlePage = true;
				// We want to add the key and contents to our map
				if (!openKey.equals("")) {
					titlePage.put(openKey, openValues.toString());
					openValues = new StringBuilder();
				}

				openKey = directive.group(1);
				if (openKey.equalsIgnoreCase("author")) {
					openKey = "authors";
				}
			} else if (inline.matches()) {
				foundTitlePage = true;
				if (!openKey.equals("")) {
					titlePage.put(openKey, openValues.toString());
					openValues = new StringBuilder();
				}

				openKey = inline.group(1);
				openValues.append(inline.group(2));
			} else if (foundTitlePage) {
				openValues.append(String.format("\n%s", line.trim()));
			}
		}

		if (foundTitlePage && !(openValues.length() == 0 && titlePage.size() == 0)) {
			if (!openKey.equals("")) {
				titlePage.put(openKey, openValues.toString());
				openValues = new StringBuilder();
				openKey = "";
			}
			contents = contents.replace(topOfDocument, "");
		}

		////////////////////////////////////////////////////////////
		// BODY
		////////////////////////////////////////////////////////////
		String[] lines = contents.split("\n");
		String[] transitions = {"CUT TO:", "FADE OUT.", "SMASH CUT TO:", "CUT TO BLACK.", "MATCH CUT TO:"};
		Arrays.sort(transitions);
		int newlinesBefore = 0;
		int index = -1;
		boolean isCommentBlock = false;
		boolean isInsideDialogueBlock = false;
		StringBuilder commentText = new StringBuilder();

		for (String line : lines) {
			index++;

			// Blank line
			if (line.equals("") || (line.matches("^\\s*$") && !isCommentBlock)) {
				isInsideDialogueBlock = false;
				newlinesBefore++;
				continue;
			}

			// Open boneyard
			if (line.matches("^\\/\\*.*")) {
				String text = line.replaceAll("^\\/\\*", "");

				// Single line boneyard
				if (line.matches(".*\\*\\/\\s*$")) {
					text = text.replaceAll("\\*\\/\\s*$", "");
					isCommentBlock = false;
					FNElement element = new FNElement(FNTypes.BONEYARD, text);
					elements.add(element);
					newlinesBefore = 0;
				} else {
					isCommentBlock = true;
					commentText.append(String.format("%s\n", text));
				}
				continue;
			}

			// Close boneyard
			if (line.matches(".*\\*\\/\\s*$")) {
				String text = line.replaceAll("\\*\\/\\s*$", "");
				if(!text.equals("")) {
					commentText.append(text.trim());
				}
				isCommentBlock = false;
				FNElement element = new FNElement(FNTypes.BONEYARD, commentText.toString());
				commentText = new StringBuilder();
				newlinesBefore = 0;
				continue;
			}

			// Inside boneyard
			if(isCommentBlock) {
				commentText.append(String.format("%s\n", line));
				continue;
			}

			// Page break
			if(line.matches("^={3,}\\s*$")) {
				FNElement element = new FNElement(FNTypes.PAGE_BREAK, line);
				elements.add(element);
				newlinesBefore = 0;
				continue;
			}

			// Synopsis
			if(newlinesBefore > 0 && line.trim().startsWith("=")) {
				String text = line.replaceAll("^\\s*={1}", "");
				FNElement element = new FNElement(FNTypes.SYNOPSIS, text);
				elements.add(element);
				continue;
			}

			// Comment
			if(newlinesBefore > 0 && line.matches("^\\s*\\[{2}\\s*([^\\]\\n])+\\s*\\]{2}\\s*$")) {
				String text = line.replace("[[", "");
				text = text.replace("]]", "");
				FNElement element = new FNElement(FNTypes.COMMENT, text);
				elements.add(element);
				continue;
			}

			// Section heading
			if(newlinesBefore > 0 && line.trim().startsWith("#")) {
				newlinesBefore = 0;
				String text = line.replaceAll("^\\s*#+", "");
				int depth = line.length() - text.length();

				if (text.trim().equals("")) {
					System.err.printf("Error in section heading on line #%d%n", (index+1));
					continue;
				}

				FNElement element = new FNElement(FNTypes.SECTION_HEADING, text);
				element.setSectionDepth(depth);
				elements.add(element);
				continue;
			}

			// Forced scene heading
			if(line.length() > 1 && line.charAt(0) == '.' && line.charAt(1) != '.') {
				newlinesBefore = 0;
				String sceneNumber = null;
				String text = null;
				Pattern hasSceneNum = Pattern.compile("\\.(.*)#([^\\n#]*?)#\\s*$");
				Matcher matchSceneNum = hasSceneNum.matcher(line);
				if (matchSceneNum.matches()) {
					sceneNumber = matchSceneNum.group(2);
					text = matchSceneNum.group(1).trim();
				} else {
					text = line.substring(1).trim();
				}

				FNElement element = new FNElement(FNTypes.SCENE_HEADING, text);
				if (sceneNumber != null) {
					element.setSceneNumber(sceneNumber);
				}
				elements.add(element);
				continue;
			}

			// Other scene headings
			if(line.matches("(?i)^(INT|EXT|EST|I\\/??E)[\\.\\-\\s].*")) {
				newlinesBefore = 0;
				String sceneNumber = null;
				String text = null;
				Pattern hasSceneNum = Pattern.compile("(.*)#([^\\n#]*?)#\\s*$");
				Matcher matchSceneNum = hasSceneNum.matcher(line);
				if (matchSceneNum.matches()) {
					sceneNumber = matchSceneNum.group(2);
					text = matchSceneNum.group(1).trim();
				} else {
					text = line;
				}

				FNElement element = new FNElement(FNTypes.SCENE_HEADING, text);
				if (sceneNumber != null) {
					element.setSceneNumber(sceneNumber);
				}
				elements.add(element);
				continue;
			}

			// Forced transitions
			if (line.charAt(0) == '>') {
				// Centered text
				if (line.length() > 1 && line.charAt(line.length() - 1) == '<') {
					String text = line.substring(1, line.length() - 1);
					FNElement element = new FNElement(FNTypes.ACTION, text);
					element.setCentered(true);
					elements.add(element);
					newlinesBefore = 0;
					continue;
				} else {
					String text = line.substring(1).trim();
					FNElement element = new FNElement(FNTypes.TRANSITION, text);
					elements.add(element);
					newlinesBefore = 0;
					continue;
				}
			}

			// Transitions
			if(transitions[Arrays.binarySearch(transitions, line.trim().toUpperCase())].equals(line.trim().toUpperCase()) || line.trim().endsWith("TO:")) {
				newlinesBefore = 0;
				FNElement element = new FNElement(FNTypes.TRANSITION, line);
				elements.add(element);
				continue;
			}

			// Character
			if (newlinesBefore > 0 && line.matches("^[^a-z]+$")) {
				int nextIndex = index + 1;
				if (nextIndex < lines.length) {
					String nextLine = lines[nextIndex];
					if (!nextLine.trim().equals("")) {
						newlinesBefore = 0;
						FNElement element = new FNElement(FNTypes.CHARACTER, line);

						if (line.trim().endsWith("^")) {
							element.setDualDialog(true);
							boolean foundPreviousCharacter = false;
							int search = elements.size() - 1;
							while((search >= 0) && !foundPreviousCharacter) {
								FNElement prevElement = elements.get(search);
								if (prevElement.getElementType() == FNTypes.CHARACTER) {
									prevElement.setDualDialog(true);
									foundPreviousCharacter = true;
								}
								search--;
							}
						}

						elements.add(element);
						isInsideDialogueBlock = true;
						continue;
					}
				}
			}

			// Dialogue and Parentheticals
			if (isInsideDialogueBlock) {
				// Find the type of element
				if(newlinesBefore == 0 && line.trim().startsWith("(")) {
					FNElement element = new FNElement(FNTypes.PARENTHETICAL, line);
					elements.add(element);
					continue;
				} else {
					// Check to see if we had dialogue, append if needed
					int lastIndex = elements.size() - 1;
					FNElement prevElement = elements.get(lastIndex);
					if (prevElement.getElementType() == FNTypes.DIALOGUE) {
						String text = String.format("%s\n%s", prevElement.getElementText(), line);
						prevElement.setElementText(text);
					} else {
						FNElement element = new FNElement(FNTypes.DIALOGUE, line);
						elements.add(element);
					}
					continue;
				}
			}

			// For inter elements lines that aren't separated by blank lines
			if (newlinesBefore == 0 && elements.size() > 0) {
				// Merge with previous action
				int lastIndex = elements.size() - 1;
				FNElement prevElement = elements.get(lastIndex);
				String text = String.format("%s\n%s", prevElement.getElementText(), line);
				prevElement.setElementText(text);
				newlinesBefore = 0;
				continue;
			} else {
				FNElement element = new FNElement(FNTypes.ACTION, line);
				elements.add(element);
				newlinesBefore = 0;
				continue;
			}
		}
	}

}
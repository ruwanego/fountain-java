
package com.thp.fountain;

public class FNElement {
	private FNTypes elementType;
	private String elementText;
	private boolean isCentered;
	private String sceneNumber;
	private boolean isDualDialog;
	private int sectionDepth;

	/**
	 * Creates an empty FNElement
	 */
	public FNElement() {
		elementType = FNTypes.NONE;
		elementText = "";
		isCentered = false;
		isDualDialog = false;
		sceneNumber = null;
		sectionDepth = 0;
	}

	/**
	 * Creates a FNElement of the given Type with the given Text
	 * @param nElementType The type of this FNElement
	 * @param nElementText The content of this FNElement
	 */
	public FNElement(final FNTypes nElementType, final String nElementText) {
		this();
		elementType = nElementType;
		elementText = nElementText;
	}

	/**
	 * Returns a String that describes this FNElement.  The String returned
	 * is "element formatting: element text"
	 *
	 * @return the String describing this FNElement
	 */
	public String description() {
		final String textOutput = elementText;
		final StringBuffer typeOutput = new StringBuffer();
		typeOutput.append(elementType);
		if (isCentered) {
			typeOutput.append(" (centered)");
		} else if (isDualDialog) {
			typeOutput.append(" (dual dialogue)");
		} else if (sectionDepth > 0) {
			typeOutput.append(String.format(" (%d)", sectionDepth));
		}

		return String.format("%s: %s", typeOutput, textOutput);
	}

	/**
	 * Sets the section depth for the FNElement
	 * @param nSectionDepth The depth of FNElement
	 */
	public void setSectionDepth(final int nSectionDepth) {
		sectionDepth = nSectionDepth;
	}

	/**
	 * Get the section depth of this FNElement
	 * @return The section depth
	 */
	public int getSectionDepth() {
		return sectionDepth;
	}

	/**
	 * Sets the Element text to a new element text
	 * @param nElementText The new element text for the FNElement
	 */
	public void setElementText(final String nElementText) {
		elementText = nElementText;
	}

	/**
	 * Gets the Element text of this element
	 * @return The text of element
	 */
	public String getElementText() {
		return elementText;
	}

	/**
	 * Sets the Element Type to a new element type
	 * @param nElementType The new element type for the FNElement
	 */
	public void setElementType(final FNTypes nElementType) {
		elementType = nElementType;
	}

	/**
	 * Gets the Element type of this element
	 * @return The type of element
	 */
	public FNTypes getElementType() {
		return elementType;
	}

	/**
	 * Returns the current scene number, null otherwise
	 * @return Current scene number or null
	 */
	public String getSceneNumber() {
		return sceneNumber;
	}

	/**
	 * Sets the scene number
	 * @param nSceneNumber The new scene number
	 */
	public void setSceneNumber(final String nSceneNumber) {
		sceneNumber = nSceneNumber;
	}

	/**
	 * Sets both the element type and element text
	 * @param nElementType The new element type
	 * @param nElementText The new content for the element
	 */
	public void setElement(final FNTypes nElementType, final String nElementText) {
		setElementType(nElementType);
		setElementText(nElementText);
	}

	/**
	 * Sets the text to centered or not centered
	 * @param nIsCentered Centers text if true
	 */
	public void setCentered(final boolean nIsCentered) {
		isCentered = nIsCentered;
	}

	/**
	 * Sets the text to dual dialog or not
	 * @param nIsDualDialog Means dual dialog if true
	 */
	public void setDualDialog(final boolean nIsDualDialog) {
		isDualDialog = nIsDualDialog;
	}

	/**
	 * Returns a String that describes this FNElement.  The String returned
	 * is "element formatting: element text"
	 *
	 * @return the String describing this FNElement
	 */
	public String toString() {
		return description();
	}
}
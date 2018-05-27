package com.bornander.libgdx;

public class LabelledInt implements CharSequence {
	private final char[] characters;
	private final int maxWidth;
	private final int labelWidth;
	private final boolean blankLeadingZeros = true;
	private final boolean rightAlign = false;
	private int value;
	
	public LabelledInt(String label, int maxWidth) {
		this.maxWidth = maxWidth;
		this.labelWidth = label.length();
		characters = new char[label.length() + maxWidth];
		for(int i = 0; i < characters.length; ++i) {
			characters[i] = i < label.length() ? label.charAt(i) : ' ';
		}
	}
	
	public void setValue(int value) {
		this.value = value;
		int divisor = 1;
		for(int i = 0; i < maxWidth; ++i) {
			int v = (value / divisor) % 10;
			characters[characters.length - (i + 1)] = (char)('0' + v);
			divisor *= 10;
		}
		
		if (blankLeadingZeros) {
			for(int i = labelWidth; i < characters.length; ++i){
				if (characters[i] != '0')
					break;
				characters[i] = ' ';
			}
		}
		
		if (!rightAlign) {
			for(int i = labelWidth; i < characters.length - 1; ++i){
				if (characters[i] == ' ') {
					characters[i] = characters[i + 1];
					characters[i + 1] = ' ';
				}
			}
		}
	}
	
	public void add(int delta) {
		setValue(value + delta);
	}

	@Override
	public char charAt(int index) {
		return characters[index];
	}

	@Override
	public int length() {
		return characters.length;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		throw new IllegalStateException("Method not implemented");
	}	
}

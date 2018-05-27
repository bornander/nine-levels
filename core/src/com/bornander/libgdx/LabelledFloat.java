package com.bornander.libgdx;

public class LabelledFloat implements CharSequence {
	private final char[] characters;
	private final int maxWidth;
	private final int decimals;
	private final int labelWidth;
	private final boolean blankLeadingZeros = true;
	private final boolean rightAlign = false;
	private float value;	
	
	public LabelledFloat(String label, int maxWidth, int decimals) {
		this.labelWidth = label.length();
		this.maxWidth = maxWidth;
		this.decimals = decimals;
		characters = new char[label.length() + maxWidth];
		for(int i = 0; i < characters.length; ++i) {
			characters[i] = i < label.length() ? label.charAt(i) : ' ';
		}
	}
	
	public void setValue(float value) {
	    int scaledValue = (int)Math.round(value * Math.pow(10, decimals));		
		int divisor = 1;
		int position = characters.length - 1;
		for(int i = 0; i < maxWidth - 1; ++i) {
			if (i == decimals)
				characters[position--] = '.';
			int v = (scaledValue / divisor) % 10;
			characters[position--] = (char)('0' + v);
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
	
	public void add(float delta) {
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

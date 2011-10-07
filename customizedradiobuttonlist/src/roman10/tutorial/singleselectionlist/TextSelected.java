package roman10.tutorial.singleselectionlist;

public class TextSelected implements Comparable<TextSelected>{
    
	private String mText = "";
	private boolean mSelected;
	
	public TextSelected(String text, boolean selected) {
		mText = text;
		mSelected = selected;
	}
	
	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	public boolean getSelected() {
		return mSelected;
	}
	
	public void setSelected(boolean selected) {
		mSelected = selected;
	}

	//@Override
	public int compareTo(TextSelected other) {
		if(this.mText != null)
			return this.mText.compareTo(other.getText()); 
		else 
			throw new IllegalArgumentException();
	}
}

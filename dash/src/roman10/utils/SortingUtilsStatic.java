package roman10.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import roman10.ui.iconifiedtextselectedlist.IconifiedTextSelected;



public class SortingUtilsStatic {
	public static void sortRecordsByAZAsc(List<IconifiedTextSelected> _records) {
		Comparator<IconifiedTextSelected> comparator = new Comparator<IconifiedTextSelected>() {
			public int compare(IconifiedTextSelected arg0,
					IconifiedTextSelected arg1) {
				return arg0.compareTo(arg1);
			}
		};
		Collections.sort(_records, comparator);
	}
	
	public static void sortRecordsByAZDesc(List<IconifiedTextSelected> _records) {
		Comparator<IconifiedTextSelected> comparator = new Comparator<IconifiedTextSelected>() {
			public int compare(IconifiedTextSelected arg0,
					IconifiedTextSelected arg1) {
				return arg0.compareTo(arg1)*-1;
			}
		};
		Collections.sort(_records, comparator);
	}
}

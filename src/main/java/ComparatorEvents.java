import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.util.Comparator;

public class ComparatorEvents implements Comparator<Component> {
    @Override
    public int compare(Component component, Component t1) {
        String dt1 = component.getProperty("DTSTART").getValue();
        int year1 = Integer.parseInt(dt1.substring(0, 4));
        int month1 = Integer.parseInt(dt1.substring(4, 6));
        int day1 = Integer.parseInt(dt1.substring(6));
        //System.out.print(year1 + " " + month1 + " " + day1);
        String dt2 = t1.getProperty("DTSTART").getValue();
        int year2 = Integer.parseInt(dt2.substring(0, 4));
        int month2 = Integer.parseInt(dt2.substring(4, 6));
        int day2 = Integer.parseInt(dt2.substring(6));
        //System.out.print(" VERSUS " + year2 + " " + month2 + " " + day2);

        int comp = Integer.compare(year1, year2);
        if( comp == 0 ) {
            comp = Integer.compare(month1, month2);
            if( comp == 0 ) {
                comp = Integer.compare(day1, day2);
            }
        }

        return comp;
    }
}

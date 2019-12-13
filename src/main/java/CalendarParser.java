import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.MapTimeZoneCache;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class CalendarParser {
    private static String[] mois = new String[]{"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Décembre"};

    public static void main(String[] args) throws IOException, ParserException, URISyntaxException {
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache.class.getName());

        CalendarParser cp = new CalendarParser();

        File file = cp.getFileFromResources("basic.ics");
        URL url = new URL("https://www.google.com/calendar/ical/fr.french%23holiday%40group.v.calendar.google.com/public/basic.ics");
        FileUtils.copyURLToFile(url, file);

        FileInputStream fin = new FileInputStream(file);

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(fin);

        java.util.Calendar today = java.util.Calendar.getInstance();
        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.clear(java.util.Calendar.MINUTE);
        today.clear(java.util.Calendar.SECOND);

        List<Component> eventsToday = getFilteredList(calendar, today);

        eventsToday.sort(new ComparatorEvents());

        System.out.println("Date du jour : " + today.getTime());
        System.out.println("Le jour férié le plus proche d'aujourd'hui : " +
                eventsToday.get(0).getProperty("SUMMARY").getValue() +
                " le " + eventsToday.get(0).getProperty("DTSTART").getValue());

        System.out.println("Le mois qui a le plus de jours fériés : " + getPlusJourFeries(eventsToday, today));
    }

    // get file from classpath, resources folder
    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            return new File(fileName);
        } else {
            return new File(resource.getFile());
        }
    }

    private static List getFilteredList(Calendar calendar, java.util.Calendar today) {
        /* PERIODE DE TEMPS */
        java.util.Calendar end = java.util.Calendar.getInstance();
        end.set(java.util.Calendar.MONTH, 12);
        end.set(java.util.Calendar.DAY_OF_MONTH, 31);
        end.set(java.util.Calendar.YEAR, today.get(java.util.Calendar.YEAR) + 1);
        end.set(java.util.Calendar.HOUR_OF_DAY, 0);
        end.clear(java.util.Calendar.MINUTE);
        end.clear(java.util.Calendar.SECOND);
        /* FIN PERIODE DE TEMPS */

        Period period = new Period(new DateTime(today.getTime()), new DateTime(end.getTime()));
        Filter filter = new Filter(new PeriodRule(period));

        return (List) filter.filter(calendar.getComponents(Component.VEVENT));
    }

    private static String getPlusJourFeries(List<Component> eventsToday, java.util.Calendar today) {
        int[] cptJourFerie = new int[12];
        for (Component c:
                eventsToday) {
            String date = c.getProperty("DTSTART").getValue();
            int year = Integer.parseInt(date.substring(0, 4));
            if(year == (today.get(java.util.Calendar.YEAR) + 1)) {
                int month = Integer.parseInt(date.substring(4, 6)) - 1;
                int day = Integer.parseInt(date.substring(6));
                if( day != 0 && day != 6 )
                    cptJourFerie[month]++;
            }
        }

        int cpt = 0, max = 0;
        for (int i = 0; i < 12; i++) {
            System.out.println(mois[i] + " : " + cptJourFerie[i] + " jours fériés");
            if( max < cptJourFerie[i] ) {
                max = cptJourFerie[i];
                cpt = i;
            }
        }
        return mois[cpt];
    }
}
